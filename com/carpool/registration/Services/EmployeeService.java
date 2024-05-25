package com.carpool.registration.Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.carpool.registration.Entity.Employee;
import com.carpool.registration.Interf.EmployeeRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private JavaMailSender mailSender;

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
    public Optional<Employee> findEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
    public List<Employee> findAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> findAllDrivers() {
        return employeeRepository.findByUserType("rider");
    }

    public List<Employee> findAllPassengers() {
        return employeeRepository.findByUserType("passenger");
    }

    public void sendSMS(String phoneNumber, String message) {
        // Twilio credentials
        String ACCOUNT_SID = "";
        String AUTH_TOKEN = "";

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message.creator(
            new PhoneNumber(phoneNumber),
            new PhoneNumber("+"),
            message
        ).create();
    }
    public void notifyUser(Employee employee) {
        String message;
        if ("rider".equalsIgnoreCase(employee.getUserType())) {
            message = String.format(" Registration Successful! You registered as a rider with ID: %d, Name: %s, Vehicle Number: %s", employee.getId(), employee.getName(), employee.getVehicleNumber());
        } else {
            message = String.format(" Registration Successful! You registered as a passenger with ID: %d, Name: %s", employee.getId(), employee.getName());
        }
        sendSMS(employee.getPhoneNumber(), message);
        notifyUserByEmail(employee, message);
    }
    public void updateNotification(Employee employee) {
        String message;
        if ("rider".equalsIgnoreCase(employee.getUserType())) {
            message = String.format(" Update Successful! You updated your rider details with ID: %d, Name: %s, Vehicle Number: %s", employee.getId(), employee.getName(), employee.getVehicleNumber());
        } else {
            message = String.format(" Update Successful! You updated your passenger details with ID: %d, Name: %s", employee.getId(), employee.getName());
        }
        sendSMS(employee.getPhoneNumber(), message);
        notifyUserByEmail(employee, message);
    }


    public void notifyUserByEmail(Employee employee, String message) {
        if (isValidEmail(employee.getEmail())) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(employee.getEmail());
            mailMessage.setSubject("Notification from Carpool Registration");
            mailMessage.setText(message);
            mailSender.send(mailMessage);
        } else {
            System.out.println("Invalid email: " + employee.getEmail());
        }
    }

    private boolean isValidEmail(String email) {
        // A basic pattern to validate email addresses
        Pattern pattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        return pattern.matcher(email).matches();
    }
}
