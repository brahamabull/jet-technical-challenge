package com.coding.challenge.test.testIT;

import com.coding.challenge.entity.Employee;
import com.coding.challenge.enums.Role;
import com.coding.challenge.payload.request.LoginRequest;
import com.coding.challenge.payload.request.RegisterRequest;
import com.coding.challenge.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class EmployeeControllerUserPrivilageITTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void getAllEmployeeItTest() throws Exception {

        RegisterRequest request = createRequest();
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
        loginRequest.setEmail("Test2@someemail.com");
        loginRequest.setPassword("test123");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String bearerToken = JsonPath.read(result.getResponse().getContentAsString(), "$.access_token");
        this.mockMvc.perform(MockMvcRequestBuilders.get("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Access Denied"));
    }

    @Test
    public void createEmployeeITTest() throws Exception {

        RegisterRequest request = createRequest();
        request.setEmail("Test3@someemail.com");
        request.setFirstname("TestFirst3");
        request.setLastname("Lastname3");
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
        loginRequest.setEmail("Test3@someemail.com");
        loginRequest.setPassword("test123");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String bearerToken = JsonPath.read(result.getResponse().getContentAsString(), "$.access_token");
        mapper.registerModule(new JavaTimeModule());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(createEmployee()))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("Test Employee 1"))
                .andExpect(jsonPath("$.email").value("testemployee1@someemail.com"));
    }

    @Test
    public void updateEmployeeITTest() throws Exception {
        RegisterRequest request = createRequest();
        request.setEmail("Test4@someemail.com");
        request.setFirstname("TestFirst4");
        request.setLastname("Lastname4");
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
        loginRequest.setEmail("Test4@someemail.com");
        loginRequest.setPassword("test123");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String bearerToken = JsonPath.read(result.getResponse().getContentAsString(), "$.access_token");
        mapper.registerModule(new JavaTimeModule());
        Employee employee = createEmployee();
        employee.setEmail("testemployee2@someemail.com");
        employee.setFullName("Test Employee 2");
        MvcResult employeeResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(employee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("Test Employee 2"))
                .andExpect(jsonPath("$.email").value("testemployee2@someemail.com"))
                .andExpect(jsonPath("$.uuid").isNotEmpty()).andReturn();

        String uuid = JsonPath.read(employeeResult.getResponse().getContentAsString(), "$.uuid");
        Employee updatedEmployee = createEmployee();
        updatedEmployee.setUuid(null);
        updatedEmployee.setEmail("testemployee3@someemail.com");
        updatedEmployee.setFullName("Test Employee 23");
        this.mockMvc.perform(MockMvcRequestBuilders.put("/employees/" + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(updatedEmployee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Access Denied"));

    }

    @Test
    public void deleteITTest() throws Exception {
        RegisterRequest request = createRequest();
        request.setEmail("Test5@someemail.com");
        request.setFirstname("TestFirst5");
        request.setLastname("Lastname5");
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
        loginRequest.setEmail("Test5@someemail.com");
        loginRequest.setPassword("test123");
        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(loginRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String bearerToken = JsonPath.read(result.getResponse().getContentAsString(), "$.access_token");
        mapper.registerModule(new JavaTimeModule());
        Employee employee = createEmployee();
        employee.setEmail("testEmail5@someemail.com");
        employee.setFullName("TestFirstname5 TestLastname5");
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/employees/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(employee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Access Denied"));
    }

    private RegisterRequest createRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("Test2@someemail.com");
        registerRequest.setFirstname("TestFirst2");
        registerRequest.setLastname("Lastname2");
        registerRequest.setPassword("test123");
        registerRequest.setRole(Role.USER);
        return registerRequest;
    }

    private Employee createEmployee() {
        Employee employee = new Employee();
        employee.setEmail("testemployee1@someemail.com");
        employee.setBirthday(LocalDate.of(1989, 07, 07));
        employee.setFullName("Test Employee 1");
        employee.setHobbies(Arrays.asList("Reading", "Music"));
        return employee;
    }
}
