package com.bankcore.accounts.controller;

import com.bankcore.accounts.AbstractIntegrationTest;
import com.bankcore.accounts.DataProvider;
import com.bankcore.accounts.repository.AccountRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;


import java.io.IOException;
import java.util.List;
import java.util.UUID;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountsControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository repository;

    private static final MockWebServer mockWebServer = new MockWebServer();

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("ms-customers.url", () -> {
            try {
                mockWebServer.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return mockWebServer.url("/").toString();
        });
    }

    @BeforeEach
    void setupData() {
        repository.deleteAll();
        repository.saveAll(List.of(DataProvider.createDummyAccount(), DataProvider.createDummyAccount(), DataProvider.createDummyAccount()));
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @AfterAll
    static void stopServer() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnAccountsWhenCustomerAuthorizedAndActive() throws Exception {

        UUID id = UUID.fromString(DataProvider.CUSTOMER_TEST_UUID);

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                        "id": "%s",
                        "exists":true,
                        "isActive": true
                        }
                        """.formatted(id)).addHeader("Content-Type", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/accounts").with(
                        user(id.toString())
                                .roles("CUSTOMER")
                )).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$").isNotEmpty());

    }

    @Test
    void shouldReturn403WhenCustomerAuthorizedAndInactive() throws Exception {

        UUID id = UUID.fromString(DataProvider.CUSTOMER_TEST_UUID);

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                        "id": "%s",
                        "exists":true,
                        "isActive": false
                        }
                        """.formatted(id)).addHeader("Content-Type", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/accounts").with(
                        user(id.toString())
                                .roles("CUSTOMER")
                )).andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.name").value("Forbidden"))
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.description").value("You do not have permission to access this resource."))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {

        UUID id = UUID.fromString(DataProvider.CUSTOMER_TEST_UUID);

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                        "id": "%s",
                        "exists":false,
                        "isActive": false
                        }
                        """.formatted(id)).addHeader("Content-Type", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/accounts").with(
                        user(id.toString())
                                .roles("CUSTOMER")
                )).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.name").value("Not Found"))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.description").value("Customer not found for id: " + DataProvider.CUSTOMER_TEST_UUID))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    @Test
    void shouldReturnNoAccountsWhenCustomerAuthorizedAndActive() throws Exception {

        repository.deleteAll();
        UUID id = UUID.fromString(DataProvider.CUSTOMER_TEST_UUID);

        mockWebServer.enqueue(new MockResponse()
                .setBody("""
                        {
                        "id": "%s",
                        "exists": true,
                        "isActive": true
                        }
                        """.formatted(id)).addHeader("Content-Type", MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/accounts").with(
                        user(id.toString())
                                .roles("CUSTOMER")
                )).andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    void shouldReturn502WhenExternalServiceFails() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        mockMvc.perform(get("/api/accounts").with(
                        user(DataProvider.CUSTOMER_TEST_UUID).roles("CUSTOMER")
                )).andDo(print())
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.name").value("Bad Gateway"))
                .andExpect(jsonPath("$.code").value(502))
                .andExpect(jsonPath("$.description").value("Error communicating with Customer Service"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn403WhenWrongRole() throws Exception {

        mockMvc.perform(get("/api/accounts").with(
                        user(DataProvider.CUSTOMER_TEST_UUID).roles("ADMIN")
                )).andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/accounts"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


}
