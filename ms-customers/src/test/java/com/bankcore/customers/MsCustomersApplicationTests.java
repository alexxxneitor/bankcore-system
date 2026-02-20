package com.bankcore.customers;

import com.bankcore.customers.dto.requests.RegisterRequest;
import com.bankcore.customers.repository.UserRepository;
import com.bankcore.customers.utils.CustomerStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MsCustomersApplicationTests {

    @Test
	void contextLoads() {}

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ObjectMapper objectMapper;

    private final RegisterRequest firstsUserRegister =
            RegisterRequest.builder()
                    .dni("1234567890")
                    .firstName("John")
                    .lastName("Doe")
                    .email("johndoe@email.com")
                    .password("Password123!")
                    .atmPin("1234")
                    .phone("+573001234567")
                    .address("123 Main St")
                    .build();

    @BeforeEach
    void setUp() throws Exception{
        userRepository.deleteAll();

        String ResponseRegister =
                mockMvc
                        .perform(
                                post("/api/auth/register")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(firstsUserRegister)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(result -> {
                            String id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
                            UUID.fromString(id);
                        })
                        .andExpect(jsonPath("$.dni").value(firstsUserRegister.getDni()))
                        .andExpect(jsonPath("$.email").value(firstsUserRegister.getEmail()))
                        .andExpect(jsonPath("$.fullName").value(firstsUserRegister.getFirstName() + " " + firstsUserRegister.getLastName()))
                        .andExpect(jsonPath("$.status").value(CustomerStatus.ACTIVE.name()))
                        .andExpect(jsonPath("$.createdDate").exists())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();
    }

}
