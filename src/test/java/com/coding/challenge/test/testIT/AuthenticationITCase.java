package com.coding.challenge.test.testIT;

import com.coding.challenge.enums.Role;
import com.coding.challenge.payload.request.LoginRequest;
import com.coding.challenge.payload.request.RefreshTokenRequest;
import com.coding.challenge.payload.request.RegisterRequest;
import com.coding.challenge.service.AuthenticationService;
import com.coding.challenge.service.JwtService;
import com.coding.challenge.service.RefreshTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import jakarta.servlet.http.Cookie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DirtiesContext
@EmbeddedKafka(partitions = 1, bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@ActiveProfiles("test")
public class AuthenticationITCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @Test
    public void registerUserItTest() throws Exception {
        RegisterRequest request = createRequest();
        ObjectMapper mapper = new ObjectMapper();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").isNotEmpty())
                .andExpect(jsonPath("$.email").value("johncena@someemail.com"));
    }

    @Test
    public void authenticateUserItTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("johncena@someemail.com");
        loginRequest.setPassword("test123");
        ObjectMapper mapper = new ObjectMapper();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").isNotEmpty())
                .andExpect(jsonPath("$.email").value("johncena@someemail.com"));
    }

    @Test
    public void refreshTokenItTest() throws Exception {
        RegisterRequest request = createRequest2();
        ObjectMapper mapper = new ObjectMapper();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("tripleh@someemail.com");
        loginRequest.setPassword("test123");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String refreshToken = JsonPath.read(result.getResponse().getContentAsString(), "$.refresh_token");

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken(refreshToken);
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(refreshTokenRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                .andExpect(jsonPath("$.access_token").isNotEmpty())
                .andExpect(jsonPath("$.refresh_token").isNotEmpty())
                .andExpect(jsonPath("$.token_type").isNotEmpty());
    }


    @Test
    public void refreshTokenCookieITest() throws Exception {
        RegisterRequest request = createRequest();
        request.setEmail("kane@someemail.com");
        request.setFirstname("Kane");
        request.setLastname("Kane");
        ObjectMapper mapper = new ObjectMapper();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("kane@someemail.com");
        loginRequest.setPassword("test123");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String refreshToken = JsonPath.read(result.getResponse().getContentAsString(), "$.refresh_token");
        Cookie[] cookies = new Cookie[]{
                new Cookie("refresh-jwt-cookie", refreshToken)
        };
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh-token-cookie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookies)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void logoutITest() throws Exception {
        RegisterRequest request = createRequest2();
        request.setEmail("cmpunk@someemail.com");
        request.setFirstname("CM");
        request.setLastname("Punk");
        ObjectMapper mapper = new ObjectMapper();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cmpunk@someemail.com");
        loginRequest.setPassword("test123");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    private RegisterRequest createRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("johncena@someemail.com");
        registerRequest.setFirstname("John");
        registerRequest.setLastname("Cena");
        registerRequest.setPassword("test123");
        registerRequest.setRole(Role.ADMIN);
        return registerRequest;
    }

    private RegisterRequest createRequest2() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("tripleh@someemail.com");
        registerRequest.setFirstname("Triple");
        registerRequest.setLastname("Helmsely");
        registerRequest.setPassword("test123");
        registerRequest.setRole(Role.ADMIN);
        return registerRequest;
    }
}
