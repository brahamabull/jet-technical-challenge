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
public class EmployeeControllerItTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeService employeeService;

    @Test
    public void getAllEmployeeItTest() throws Exception {

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
                .andExpect(status().isOk()).andReturn();

        String bearerToken = JsonPath.read(result.getResponse().getContentAsString(), "$.access_token");
        this.mockMvc.perform(MockMvcRequestBuilders.get("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void createEmployeeITTest() throws Exception {

        RegisterRequest request = createRequest2();
        request.setEmail("larsen@someemail.com");
        request.setFirstname("larsen");
        request.setLastname("toubro");
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
        loginRequest.setEmail("larsen@someemail.com");
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
                .andExpect(jsonPath("$.fullName").value("John Cena"))
                .andExpect(jsonPath("$.email").value("johncena@someemail.com"));
    }

    @Test
    public void createEmployeeDuplicateEmailITTest() throws Exception {
        RegisterRequest request = createRequest2();
        request.setEmail("kurtangle@someemail.com");
        request.setFirstname("Kurt");
        request.setLastname("Angle");
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
        loginRequest.setEmail("kurtangle@someemail.com");
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
        Employee emp = createEmployee();
        emp.setEmail("johncena1234@someemail.com");
        emp.setFullName("John Cena Real");
        mapper.registerModule(new JavaTimeModule());
        this.mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(emp))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty()).andReturn();

        this.mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(emp))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().string("Email must be unique"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateEmployeeITTest() throws Exception {
        RegisterRequest request = createRequest2();
        request.setEmail("dwayenjohnson@someemail.com");
        request.setFirstname("Dwayane");
        request.setLastname("Johnson");
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
        loginRequest.setEmail("dwayenjohnson@someemail.com");
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
        employee.setEmail("testEmail@someemail.com");
        employee.setFullName("TestFirstname TestLastname");
        MvcResult employeeResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(employee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("TestFirstname TestLastname"))
                .andExpect(jsonPath("$.email").value("testEmail@someemail.com"))
                .andExpect(jsonPath("$.uuid").isNotEmpty()).andReturn();

        String uuid = JsonPath.read(employeeResult.getResponse().getContentAsString(), "$.uuid");
        Employee updatedEmployee = createEmployee();
        updatedEmployee.setUuid(null);
        updatedEmployee.setEmail("testEmailLstNm@someemail.com");
        updatedEmployee.setFullName("TestFirstname Lastname");
        this.mockMvc.perform(MockMvcRequestBuilders.put("/employees/" + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(updatedEmployee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("TestFirstname Lastname"))
                .andExpect(jsonPath("$.email").value("testEmailLstNm@someemail.com"))
                .andExpect(jsonPath("$.uuid").isNotEmpty()).andReturn();

    }

    @Test
    public void deleteITTest() throws Exception {
        RegisterRequest request = createRequest2();
        request.setEmail("rickflair@someemail.com");
        request.setFirstname("Rick");
        request.setLastname("Flair");
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
        loginRequest.setEmail("rickflair@someemail.com");
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
        employee.setEmail("testEmail1@someemail.com");
        employee.setFullName("TestFirstname1 TestLastname1");
        MvcResult employeeResult = this.mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(employee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.fullName").value("TestFirstname1 TestLastname1"))
                .andExpect(jsonPath("$.email").value("testEmail1@someemail.com"))
                .andExpect(jsonPath("$.uuid").isNotEmpty()).andReturn();

        String uuid = JsonPath.read(employeeResult.getResponse().getContentAsString(), "$.uuid");
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/employees/" + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(employee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/employees/" + uuid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(employee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteITTestNoEmployeeFound() throws Exception {
        RegisterRequest request = createRequest2();
        request.setEmail("rickflair@someemail.com");
        request.setFirstname("Rick");
        request.setLastname("Flair");
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
        loginRequest.setEmail("rickflair@someemail.com");
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
        employee.setEmail("testEmail1@someemail.com");
        employee.setFullName("TestFirstname1 TestLastname1");
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/employees/" + UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .content(mapper.writeValueAsString(employee))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
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

    private Employee createEmployee() {
        Employee employee = new Employee();
        employee.setEmail("johncena@someemail.com");
        employee.setBirthday(LocalDate.of(1989, 07, 07));
        employee.setFullName("John Cena");
        employee.setHobbies(Arrays.asList("Reading", "Music"));
        return employee;
    }

}
