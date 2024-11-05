package com.reliaquest.api.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.common.Constants;
import com.reliaquest.api.exception.ErrorResponseBuilder;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.Response;
import com.reliaquest.api.request.CreateEmployeeRequest;
import com.reliaquest.api.util.EmployeeProcessor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmployeeService implements IEmployeeService {

    private final ApiService apiService;

    private final EmployeeProcessor employeeProcessor = new EmployeeProcessor();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public EmployeeService(ApiService apiService) {
		this.apiService = apiService;
    	
    }
    
    
    /**
     * Fetch all employee list from server API.
     *
     * @return List of Employees
     */
    @Override
    public List<Employee> getAllEmployees() {
        log.info("Fetching All employees from server API");
        ResponseEntity<JsonNode> responseEntity = apiService.get(Constants.EMPLOYEE_URI);
        return processResponse(responseEntity, new TypeReference<List<Employee>>() {});
    }

    /**
     * Fetches Employee with provided string
     *
     * @param searchString String to search in employee names
     * @return List of Employees
     */
    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        String searchStringLowerCase = searchString.toLowerCase();
        List<Employee> employeeList = getAllEmployees();
        return employeeProcessor.getAllEmployeesWithMatchingName(employeeList, searchStringLowerCase);
    }

    /**
     * Fetches Employee with given Id
     *
     * @param id Employee ID
     * @return Employee
     */
    @Override
    public Employee getEmployeeById(String id) {
        log.info("Fetching Employee with given ID: {}", id);
        ResponseEntity<?> responseEntity = apiService.get(Constants.EMPLOYEE_URI + "/" + id);
        return processResponse(responseEntity, new TypeReference<>() {});
    }

    /**
     * Fetches highest salary out of all Employees
     *
     * @return Highest salary
     */
    @Override
    public Integer getHighestSalaryOfEmployees() {
        List<Employee> employeeList = getAllEmployees();
        return employeeProcessor.getHighestSalaryOfAllEmployees(employeeList);
    }

    /**
     * Fetches top ten employees with Highest salary
     *
     * @return List of Employee names
     */
    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        List<Employee> employeeList = getAllEmployees();
        List<String> sortedEmployeeNames = employeeProcessor.getEmployeesNamesSortedBySalary(employeeList);
        return sortedEmployeeNames.subList(0, Math.min(sortedEmployeeNames.size(), 10));
    }

    /**
     * Create employee with required parameters
     *
     * @param employeeRequest Employee parameters to create the new Employee
     * @return Employee
     */
    @Override
    public Employee createEmployee(CreateEmployeeRequest employeeRequest) {
        log.info("Creating employee with provided parameters: {}", employeeRequest);
        ResponseEntity<JsonNode> responseEntity = apiService.post(Constants.EMPLOYEE_URI, employeeRequest);
        return processResponse(responseEntity, new TypeReference<>() {});
    }

    /**
     * Delete employee given its Employee ID
     *
     * @param id Employee ID
     * @return Employee name
     */
    @Override
    public String deleteEmployeeById(String id) {
        Employee employee = getEmployeeById(id);
        String employeeName = employee.getName();
        log.info("Deleting employee record with given ID: {}", id);
        apiService.delete(Constants.EMPLOYEE_URI, employeeName);
        return employeeName;
    }

    /**
     * Helps in processing response sent from server API
     *
     * @param responseEntity Entity returned by Server API
     * @param responseType   Object Type of Entity returned
     * @return Parsed entity
     */
    private <T> T processResponse(ResponseEntity<?> responseEntity, TypeReference<T> responseType) {
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            Response response = objectMapper.convertValue(responseEntity.getBody(), Response.class);
            if (Constants.SUCCESS_MSG.equalsIgnoreCase(response.status())) {
                return objectMapper.convertValue(response.data(), responseType);
            } else {
                throw new RuntimeException(Constants.INTERNAL_SERVER_ERROR);
            }
        } else {
            throw ErrorResponseBuilder.exceptionByStatus(responseEntity);
        }
    }
}
