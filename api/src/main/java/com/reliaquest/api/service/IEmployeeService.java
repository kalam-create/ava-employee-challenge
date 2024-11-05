package com.reliaquest.api.service;

import java.util.List;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.CreateEmployeeRequest;

public interface IEmployeeService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(CreateEmployeeRequest employeeInput);

    String deleteEmployeeById(String id);
}
