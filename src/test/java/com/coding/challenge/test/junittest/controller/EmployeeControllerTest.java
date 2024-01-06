package com.coding.challenge.test.junittest.controller;

import com.coding.challenge.controller.EmployeeController;
import com.coding.challenge.entity.Employee;
import com.coding.challenge.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private EmployeeService employeeService;

    @Test
    public void getAllEmployeesTestEmptyResult() {

        when(employeeService.getAllEmployees()).thenReturn(new ArrayList<>());
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<List<Employee>> listResponseEntity = employeeController.getAllEmployees();
        assertThat(listResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(listResponseEntity.getBody().isEmpty());
    }

    @Test
    public void getAllEmployeesTestWithResult() {

        when(employeeService.getAllEmployees()).thenReturn(Collections.singletonList(createEmployee()));
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<List<Employee>> listResponseEntity = employeeController.getAllEmployees();
        assertThat(listResponseEntity.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat((long) Objects.requireNonNull(listResponseEntity.getBody()).size()).isEqualTo(Long.parseLong("1"));
    }

    @Test
    public void getEmployeeByIdTest() {
        when(employeeService.getEmployeeById(anyString())).thenReturn(createEmployee());
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<Employee> response = employeeController.getEmployeeById("1a-2b-3c-4e-5d");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(Objects.requireNonNull(response.getBody()).getEmail()).isEqualTo("johncena@someemail.com");
    }

    @Test
    public void createEmployeeTest() {
        Employee employee = createEmployee();
        when(employeeService.createEmployee(any())).thenReturn(employee);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<Employee> response = employeeController.createEmployee(employee);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        assertThat(Objects.requireNonNull(response.getBody()).getEmail()).isEqualTo("johncena@someemail.com");
    }

    @Test
    public void createEmployeeWithDuplicateEmail() {
        Employee employee2 = createEmployee();
        when(employeeService.createEmployee(any())).thenThrow(RuntimeException.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        assertThrows(RuntimeException.class, () -> employeeController.createEmployee(employee2)) ;
    }

    @Test
    public void updateEmployeeTest() {

        String uuid = UUID.randomUUID().toString();
        Employee employee = createEmployee();
        employee.setFullName("Term Cena");
        when(employeeService.updateEmployee(anyString(), any())).thenReturn(employee);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<Employee> response = employeeController.updateEmployee(uuid, employee);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(Objects.requireNonNull(response.getBody()).getFullName()).isEqualTo("Term Cena");
    }

    @Test
    public void updateEmployeeTestNotFound() {

        String uuid = UUID.randomUUID().toString();
        Employee employee = createEmployee();
        employee.setFullName("Term Cena");
        when(employeeService.updateEmployee(anyString(), any())).thenReturn(null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<Employee> response = employeeController.updateEmployee(uuid, employee);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
    }

    @Test
    public void deleteEmployeeTest() {
        String uuid = UUID.randomUUID().toString();
        when(employeeService.deleteEmployee(anyString())).thenReturn(true);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<Void> response = employeeController.deleteEmployee(uuid);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
    }

    @Test
    public void deleteEmployeeTestEmployeeNotFound() {
        String uuid = UUID.randomUUID().toString();
        when(employeeService.deleteEmployee(anyString())).thenReturn(false);
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        ResponseEntity<Void> response = employeeController.deleteEmployee(uuid);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(404));
    }

    private Employee createEmployee() {
        Employee employee = new Employee();
        employee.setEmail("johncena@someemail.com");
        employee.setBirthday(LocalDate.of(1989, 07, 07));
        employee.setFullName("John Cena");
        employee.setUuid(UUID.randomUUID().toString());
        employee.setHobbies(Arrays.asList("Reading", "Music"));
        return employee;
    }
}
