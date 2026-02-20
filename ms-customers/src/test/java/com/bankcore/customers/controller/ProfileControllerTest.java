package com.bankcore.customers.controller;

import com.bankcore.customers.AbstractIntegrationTest;
import com.bankcore.customers.DataProvider;
import com.bankcore.customers.model.UserEntity;
import com.bankcore.customers.repository.UserRepository;
import com.bankcore.customers.service.UserManagementImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@ActiveProfiles("test")
public class ProfileControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserManagementImpl userManagement;

    @BeforeEach
    public void init() {
        UserEntity user = DataProvider.createMockUser();
        userRepository.save(user);
    }

    @Test
    @WithMockUser(
            username = DataProvider.EMAIL,
            roles = DataProvider.CUSTOMER_ROLE
    )
    void shouldReturnProfileWhenAuthorized() throws Exception {
        mockMvc.perform(get("/api/customers/me"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = DataProvider.EMAIL, roles = "ADMIN")
    void shouldReturn403WhenWrongRole() throws Exception {

        mockMvc.perform(get("/api/customers/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn401WhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/customers/me"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



}
