package com.coding.challenge.test.junittest.service;

import com.coding.challenge.entity.Employee;
import com.coding.challenge.events.KafkaProducerService;
import com.coding.challenge.repository.EmployeeRepository;
import com.coding.challenge.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @InjectMocks
    EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Captor
    ArgumentCaptor<String> kafkaCaptor;

    @Test
    public void getAllEmployeesServiceTestNoResult() {
        when(employeeRepository.findAll()).thenReturn(new ArrayList<>());
        List<Employee> list = employeeService.getAllEmployees();
        assertThat(list.isEmpty());
    }

    @Test
    public void getAllEmployeesServiceTestResults() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(createEmployee()));
        List<Employee> list = employeeService.getAllEmployees();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void createEmployeeServiceTest() {
        when(employeeRepository.findByEmail(anyString())).thenReturn(null);
        Employee employee = employeeService.createEmployee(createEmployee());

        verify(kafkaProducerService, times(1)).publishEmployeeEvent(Mockito.anyString(), Mockito.anyString());
        verify(employeeRepository, times(1)).save(Mockito.any(Employee.class));
        verify(employeeRepository, times(1)).findByEmail(Mockito.anyString());
        assertThat(Objects.nonNull(employee.getUuid()));
    }

    @Test
    public void createEmployeeServiceTestDuplicateEmail() {
        when(employeeRepository.findByEmail(anyString())).thenReturn(createEmployee());
        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(createEmployee()));

        verify(kafkaProducerService, times(0)).publishEmployeeEvent(Mockito.anyString(), Mockito.anyString());
        verify(employeeRepository, times(0)).save(Mockito.any(Employee.class));
        verify(employeeRepository, times(1)).findByEmail(Mockito.anyString());
    }

    @Test
    public void updateEmployeeTest() {

        String uuid = UUID.randomUUID().toString();
        Employee employee = createEmployee();
        Employee employee1 = createEmployee();
        employee1.setEmail("someoneelse@email.com");
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee1));
        Employee updatedEmp = employeeService.updateEmployee(uuid, employee);
        verify(kafkaProducerService, times(1)).publishEmployeeEvent(Mockito.anyString(), Mockito.anyString());
        verify(employeeRepository, times(1)).save(Mockito.any(Employee.class));
        verify(employeeRepository, times(1)).findById(Mockito.anyString());
        assertThat(updatedEmp.getEmail()).isEqualTo("johncena@someemail.com");
    }

    @Test
    public void updateEmployeeTestFailedForSameEmail() {

        String uuid = UUID.randomUUID().toString();
        Employee employee = createEmployee();
        Employee employee1 = createEmployee();
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee1));

        assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(uuid, employee));
        verify(kafkaProducerService, times(0)).publishEmployeeEvent(Mockito.anyString(), Mockito.anyString());
        verify(employeeRepository, times(0)).save(Mockito.any(Employee.class));
        verify(employeeRepository, times(1)).findById(Mockito.anyString());
    }

    @Test
    public void deleteEmployee() {
        String uuid = UUID.randomUUID().toString();
        Employee employee = createEmployee();
        when(employeeRepository.findById(anyString())).thenReturn(Optional.of(employee));
        employeeService.deleteEmployee(uuid);
        verify(kafkaProducerService, times(1)).publishEmployeeEvent(Mockito.anyString(), Mockito.anyString());
        verify(kafkaProducerService).publishEmployeeEvent(anyString(), kafkaCaptor.capture());
        assertThat(kafkaCaptor.getValue()).isEqualTo(uuid);
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
