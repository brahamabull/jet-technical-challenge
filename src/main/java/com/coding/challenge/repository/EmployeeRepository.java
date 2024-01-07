package com.coding.challenge.repository;

import com.coding.challenge.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Employee findByEmail(String email);
}