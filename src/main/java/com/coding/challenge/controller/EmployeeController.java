package com.coding.challenge.controller;

import com.coding.challenge.entity.Employee;
import com.coding.challenge.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Tag(name = "Employee", description = "Employee CRUD Operations APIs - Secured")
@RestController
@RequestMapping("/employees")
@Slf4j
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasRole('ADMIN')")
    @Operation(
            description = "This endpoint require a valid JWT, ADMIN role with READ_PRIVILEGE",
            summary = "This Endpoint is used to get the details of all employees",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("Request for getting all employees info Received");
        List<Employee> employees = employeeService.getAllEmployees();
        if (CollectionUtils.isEmpty(employees)) {
            log.error("No Records are present for Employees");
            return ResponseEntity.ok(new ArrayList<>());
        } else {
            log.info("Fetching of Records for All Employee is Completed");
            return ResponseEntity.ok(employees);
        }
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('READ_PRIVILEGE') and hasRole('ADMIN')")
    @Operation(
            description = "This endpoint require a valid JWT, ADMIN role with READ_PRIVILEGE",
            summary = "This Endpoint is used to Get the Details of employee using specific uuid",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String uuid) {
        log.info("Request for getting employee info with id {} Received", uuid);
        Employee employee = employeeService.getEmployeeById(uuid);
        if (Objects.nonNull(employee)) {
            return ResponseEntity.ok(employee);
        } else {
            log.error("No Data for Given Id {}", uuid);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_PRIVILEGE') and hasAnyRole('ADMIN','USER')")
    @Operation(
            description = "This endpoint require a valid JWT, ADMIN or USER role with WRITE_PRIVILEGE",
            summary = "This Endpoint is used to Create an Employee Record",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        log.info("Request for Creation of Employee Received with these Dataset : Email {}, Fullname {}, " +
                "Birthday {}, Hobbies {}", employee.getEmail(), employee.getFullName(), employee.getBirthday(),
                employee.getHobbies());
        Employee createdEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.status(201).body(createdEmployee);
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('UPDATE_PRIVILEGE') and hasAnyRole('ADMIN','USER')")
    @Operation(
            description = "This endpoint require a valid JWT, ADMIN or USER role with UPDATE_PRIVILEGE",
            summary = "This Endpoint is used to Update an Employee Record",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<Employee> updateEmployee(@PathVariable String uuid, @RequestBody Employee employee) {
        log.info("Request for Update of Employee Info Received with these Dataset : Email {}, Fullname {}, " +
                        "Birthday {}, Hobbies {}", employee.getEmail(), employee.getFullName(), employee.getBirthday(),
                employee.getHobbies());
        Employee updatedEmployee = employeeService.updateEmployee(uuid, employee);
        if (updatedEmployee != null) {
            return ResponseEntity.ok(updatedEmployee);
        } else {
            log.error("No Record match to Update the Employee Info");
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('DELETE_PRIVILEGE') and hasRole('ADMIN')")
    @Operation(
            description = "This endpoint require a valid JWT, ADMIN role with DELETE_PRIVILEGE",
            summary = "This Endpoint is used to Delete an Employee Record",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Unauthorized / Invalid Token",
                            responseCode = "401"
                    )
            }
    )
    public ResponseEntity<Void> deleteEmployee(@PathVariable String uuid) {
        log.info("Request for Deletion of Employee with Id {} is Received", uuid);
        boolean deleted = employeeService.deleteEmployee(uuid);
        if (deleted) {
            log.info("Employee with Id {} Record Deleted Succesfully", uuid);
            return ResponseEntity.noContent().build();
        } else {
            log.error("Employee with Id {} is not found for Deletion", uuid);
            return ResponseEntity.notFound().build();
        }
    }
}
