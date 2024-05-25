package com.carpool.registration.Controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.carpool.registration.Entity.Employee;
import com.carpool.registration.Services.EmployeeService;

@RestController
@RequestMapping("/api")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/register")
    public ResponseEntity<?> registerEmployee(@RequestBody Employee employee) {
        Optional<Employee> existingEmployee = employeeService.findEmployeeById(employee.getId());
        if (existingEmployee.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("{\"status\":\"the user already exists\"}");
        }
        Employee savedEmployee = employeeService.saveEmployee(employee);
        employeeService.notifyUser(savedEmployee);
        return ResponseEntity.ok(savedEmployee);
    }

    @GetMapping("/update/findById/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.findEmployeeById(id);
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"status\":\"user not found\"}");
    }

    @PutMapping("/update/findById/change")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee employee) {
        Optional<Employee> existingEmployee = employeeService.findEmployeeById(employee.getId());
        if (existingEmployee.isPresent()) {
            Employee updatedEmployee = employeeService.saveEmployee(employee);
            Employee savedEmployee = employeeService.saveEmployee(employee);
            employeeService.updateNotification(savedEmployee);
            return ResponseEntity.ok(updatedEmployee);
        }
       
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"status\":\"user not found\"}");
    }

    //Admin Credentials
    @GetMapping("/admin/allUsers")
    public ResponseEntity<List<Employee>> getAllUsers() {
        List<Employee> users = employeeService.findAllEmployees();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/findById/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.findEmployeeById(id);
        if (employee.isPresent()) {
            return ResponseEntity.ok(employee.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"status\":\"user not found\"}");
    }

    @GetMapping("/admin/allDrivers")
    public ResponseEntity<List<Employee>> getAllDrivers() {
        List<Employee> drivers = employeeService.findAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/admin/allPassengers")
    public ResponseEntity<List<Employee>> getAllPassengers() {
        List<Employee> passengers = employeeService.findAllPassengers();
        return ResponseEntity.ok(passengers);
    }
}
