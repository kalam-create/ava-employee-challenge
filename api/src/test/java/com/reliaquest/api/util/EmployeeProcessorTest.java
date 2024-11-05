package com.reliaquest.api.util;

import com.reliaquest.api.model.Employee;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit tests to cover Employee Processor
 */
@SpringBootTest
public class EmployeeProcessorTest {

    @Autowired
    private EmployeeProcessor employeeProcessor;

    private static List<Employee> employeeList;
    private static Employee employee1;
    private static Employee employee2;

    @BeforeAll
    public static void setUp() throws IOException, URISyntaxException {

        employee1 = new Employee(
                UUID.randomUUID(), "Candace Berge", 292934, 34, "Human Consulting", "magik_mike@compnay.com");
        employee2 = new Employee(
                UUID.randomUUID(),
                "Harry Torp",
                4592934,
                67,
                "Community-Services Orchestrator",
                "zaam-dox@compnay.com");

        employeeList = new ArrayList<>();
        employeeList.add(employee1);
        employeeList.add(employee2);
    }

    @Test
    public void testAllEmployeesWithMatchingNames() {
        List<Employee> matchedEmployee = employeeProcessor.getAllEmployeesWithMatchingName(employeeList, "and");

        Assertions.assertEquals(1, matchedEmployee.size());
        Assertions.assertEquals(employee1, matchedEmployee.get(0));
    }

    @Test
    public void testGetHighestSalaryOfALlEmployees() {
        Integer highestSalary = employeeProcessor.getHighestSalaryOfAllEmployees(employeeList);

        Assertions.assertEquals(4592934, highestSalary);
    }

    @Test
    public void testGetEmployeesNamesSortedBySalary() {
        List<String> sortedEmployeeNames = employeeProcessor.getEmployeesNamesSortedBySalary(employeeList);

        Assertions.assertEquals(2, sortedEmployeeNames.size());
        Assertions.assertEquals(employee2.getName(), sortedEmployeeNames.get(0));
        Assertions.assertEquals(employee1.getName(), sortedEmployeeNames.get(1));
    }
}
