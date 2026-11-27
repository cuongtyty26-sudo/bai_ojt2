package com.example.baiojt2.controller;

import com.example.baiojt2.config.SecurityConfig;
import com.example.baiojt2.repository.UserAccountRepository;
import com.example.baiojt2.security.RoleBasedAuthenticationFailureHandler;
import com.example.baiojt2.security.RoleBasedAuthenticationSuccessHandler;
import com.example.baiojt2.service.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerTest {

    @MockBean private CustomUserDetailsService userDetailsService;
    @MockBean private RoleBasedAuthenticationSuccessHandler successHandler;
    @MockBean private RoleBasedAuthenticationFailureHandler failureHandler;
    @MockBean private UserAccountRepository userAccountRepository;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void homePageIsAccessibleWithoutAuthentication() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("index"));
	}
}
