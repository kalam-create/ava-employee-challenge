package com.reliaquest.api.controller.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.reliaquest.api.controller.IEmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.CreateEmployeeRequest;
import com.reliaquest.api.service.EmployeeService;

/**
 * Controller class that provides definition to all the endpoints
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {

	@Autowired
    private EmployeeService employeeService;

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employeeList = employeeService.getEmployeesByNameSearch(searchString);
        return ResponseEntity.ok(employeeList);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> top10HighlyPaidEmployees = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok(top10HighlyPaidEmployees);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        String name = employeeService.deleteEmployeeById(id);
        return ResponseEntity.ok(name);
    }

	@Override
	public ResponseEntity<Employee> createEmployee(CreateEmployeeRequest employeeRequest) {
        Employee employee = employeeService.createEmployee(employeeRequest);
        return ResponseEntity.ok(employee);    
	}
}
