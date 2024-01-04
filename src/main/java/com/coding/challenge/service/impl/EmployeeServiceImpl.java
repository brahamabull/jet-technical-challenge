package com.coding.challenge.service.impl;

import com.coding.challenge.entity.Employee;
import com.coding.challenge.events.KafkaProducerService;
import com.coding.challenge.repository.EmployeeRepository;
import com.coding.challenge.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public List<Employee> getAllEmployees() {
        log.info("Getting the List of All Employees");
        return employeeRepository.findAll();
    }

    @Override
    public Employee getEmployeeById(String uuid) {
        Optional<Employee> employee = employeeRepository.findById(uuid);
        return employee.orElse(null);
    }

    @Override
    public Employee createEmployee(Employee employee) {
        // Check for uniqueness of email
        if (Objects.nonNull(employeeRepository.findByEmail(employee.getEmail()))) {
            log.error("Duplicate Email Id Found. Email Id Already Exists for {}", employee.getEmail());
            throw new RuntimeException("Email must be unique");
        }

        // Generate UUID and save the employee
        String uuid = UUID.randomUUID().toString();
        employee.setUuid(uuid);
        employeeRepository.save(employee);

        // Publish create event
        kafkaProducerService.publishEmployeeEvent("EmployeeCreated", uuid);

        return employee;
    }

    @Override
    public Employee updateEmployee(String uuid, Employee employee) {
        Employee emp = getEmployeeById(employee.getUuid());
        // Check if the employee exists
        if (Objects.isNull(emp)) {
            return null;
        }

        // Check for uniqueness of email (excluding the current employee)
        if (emp.getEmail().equalsIgnoreCase(employee.getEmail())) {
            throw new RuntimeException("Email must be unique.");
        }

        // Update the employee
        employee.setUuid(uuid);
        employeeRepository.save(employee);

        // Publish update event
        kafkaProducerService.publishEmployeeEvent("EmployeeUpdated", uuid);

        return employee;
    }

    @Override
    public boolean deleteEmployee(String uuid) {
        // Check if the employee exists
        if (Objects.isNull(getEmployeeById(uuid))) {
            return false;
        }

        // Publish delete event
        kafkaProducerService.publishEmployeeEvent("EmployeeDeleted", uuid);
        return true;
    }
}
