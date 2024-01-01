package com.coding.challenge.service;

import com.coding.challenge.entity.Employee;

import java.util.List;

public interface EmployeeService {
    List<Employee> getAllEmployees();

    Employee getEmployeeById(String uuid);

    Employee createEmployee(Employee employee);

    Employee updateEmployee(String uuid, Employee employee);

    boolean deleteEmployee(String uuid);
}
