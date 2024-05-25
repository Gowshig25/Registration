package com.carpool.registration.Interf;
import org.springframework.data.jpa.repository.JpaRepository;

import com.carpool.registration.Entity.Employee;
import java.util.List;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByUserType(String userType);
}
