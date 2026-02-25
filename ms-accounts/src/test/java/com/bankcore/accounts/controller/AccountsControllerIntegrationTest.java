package com.bankcore.accounts.controller;

import com.bankcore.accounts.AbstractIntegrationTest;
import com.bankcore.accounts.DataProvider;
import com.bankcore.accounts.client.CustomerClientImpl;
import com.bankcore.accounts.model.AccountEntity;
import com.bankcore.accounts.repository.AccountRepository;
import com.bankcore.accounts.service.AccountManagementImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClient;


import java.io.IOException;
import java.util.List;
import java.util.UUID;


import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountsControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository repository;

    @Autowired
    private AccountManagementImpl service;

    private static MockWebServer mockWebServer;

    @BeforeEach
    void setupData() {
        repository.deleteAll();
        repository.saveAll(List.of(DataProvider.createDummyAccount(), DataProvider.createDummyAccount(), DataProvider.createDummyAccount()));
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {

        try {
            mockWebServer = new MockWebServer();
            mockWebServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        registry.add("ms-customers.url",
                () -> mockWebServer.url("/").toString());
    }

    @AfterAll
    static void stopServer() throws IOException {
        mockWebServer.shutdown();
    }

    @Autowired
    Environment env;

    @Test
    void debugProperty() {
        System.out.println(env.getProperty("ms-customers.url"));
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


}
