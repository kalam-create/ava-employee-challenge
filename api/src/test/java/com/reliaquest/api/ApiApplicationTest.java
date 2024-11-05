package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.common.Constants;
import com.reliaquest.api.controller.impl.*;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.request.CreateEmployeeRequest;
import com.reliaquest.api.request.DeleteEmployeeRequest;
import com.reliaquest.api.service.ApiService;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.api.util.FileUtil;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

/**
 * Integration Test covering all Employee API endpoints
 * Employee and API Service are injected and all the interactions to Server API are mocked.
 */
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@AutoConfigureMockMvc
@SpringBootTest
class ApiApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    RestTemplate restTemplate;

    @InjectMocks
    private EmployeeController controller;

    @Value("${api.base.uri}")
    private String apiBaseUrl;

    private static JsonNode allEmployeeJson;
    private static JsonNode singleEmployeeJson;
    private static List<String> empNames;

    /**
     * Initializing data required for integration tests
     *
     * @throws IOException
     */
    @BeforeAll
    public static void init() throws IOException {
        allEmployeeJson = FileUtil.readJSON("employeelist-data.json");
        assertNotNull(allEmployeeJson);
        singleEmployeeJson = FileUtil.readJSON("employee-data.json");
        assertNotNull(singleEmployeeJson);
        empNames = Arrays.asList(
                "Jann Graham",
                "Tamesha Greenholt",
                "Keneth Lindgren",
                "Refugio Frami PhD",
                "Dane Stark V",
                "Dr. Leonora Cruickshank",
                "Mr. Columbus Monahan",
                "Mavis Gerhold I",
                "Marlin King",
                "Mr. Nestor Donnelly");

    }

    @Test
    void getAllEmployeesTest() throws Exception {
        mockGetAllSuccess();
        mockMvc.perform(get("/employee")).andExpect(status().isOk()).andDo(result -> {
            List<Employee> list = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals(50, list.size());
        });
    }

    @Test
    void getEmployeesByNameSearchTest() throws Exception {
        mockGetAllSuccess();
        mockMvc.perform(get("/employee/search/and")).andExpect(status().isOk()).andDo(result -> {
            List<Employee> list = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
            assertEquals(3, list.size());
        });
    }

    @Test
    void getEmployeeByIdTest() throws Exception {
        mockSingleEmployee();
        mockMvc.perform(get("/employee/7e44d58c-730e-404a-841e-44bcc9dd87bd"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    Employee emp = mapper.readValue(result.getResponse().getContentAsString(), Employee.class);
                    assertEquals(emp.getName(), "Micheal Hand");
                    assertEquals(emp.getSalary(), 397028);
                    assertEquals(emp.getAge(),63);
                    assertEquals(emp.getTitle(), "Technology Director");
                    assertEquals(emp.getEmail(), "tickleme_pink@company.com");
                });
    }

    @Test
    void getHighestSalaryOfEmployeesTest() throws Exception {
        mockGetAllSuccess();
        mockMvc.perform(get("/employee/highestSalary"))
                .andExpect(status().isOk())
                .andDo(result -> assertEquals("493025", result.getResponse().getContentAsString()));
    }

    @Test
    void getTopTenHighestEarningEmployeeNamesTest() throws Exception {
        mockGetAllSuccess();
        mockMvc.perform(get("/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andDo(result -> {
                    List<String> list =
                            mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});
                    assertEquals(10, list.size());
                    assertIterableEquals(empNames, list);
                });
    }

    @Test
    void createEmployeeTest() throws Exception {
        CreateEmployeeRequest createRequest =
                new CreateEmployeeRequest("Scott Farrell", 89277, 27, "Corporate Sales Agent", "vagram@company.com");
        String requestBody = mapper.writeValueAsString(createRequest);
        when(restTemplate.postForEntity(apiBaseUrl + "/employee", createRequest, JsonNode.class))
                .thenReturn(ResponseEntity.ok(singleEmployeeJson));
        mockMvc.perform(post("/employee").content(requestBody).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    Employee emp = mapper.readValue(result.getResponse().getContentAsString(), Employee.class);
                    assertEquals(emp.getName(), "Micheal Hand");
                    assertEquals(emp.getSalary(), 397028);
                    assertEquals(emp.getAge(),63);
                    assertEquals(emp.getTitle(), "Technology Director");
                    assertEquals(emp.getEmail(), "tickleme_pink@company.com");
                });
    }

    @Test
    void deleteEmployeeByIdTest() throws Exception {
        mockSingleEmployee();
        when(restTemplate.exchange(
                        apiBaseUrl + Constants.EMPLOYEE_URI,
                        HttpMethod.DELETE,
                        new HttpEntity<>(new DeleteEmployeeRequest("Scott Farrell")),
                        JsonNode.class))
                .thenReturn(ResponseEntity.ok(singleEmployeeJson));

        mockMvc.perform(delete("/employee/7e44d58c-730e-404a-841e-44bcc9dd87bd"))
                .andExpect(status().isOk())
                .andDo(result ->
                        assertEquals("Micheal Hand", result.getResponse().getContentAsString()));
    }

    /**
     * Mock Get All Employee request to Server API
     */
    private void mockGetAllSuccess() {
        when(restTemplate.getForEntity(apiBaseUrl + Constants.EMPLOYEE_URI, JsonNode.class))
                .thenReturn(ResponseEntity.ok(allEmployeeJson));
    }

    /**
     * Mock Get Employee by ID request to Server API
     */
    private void mockSingleEmployee() {
        when(restTemplate.getForEntity(
                        apiBaseUrl + Constants.EMPLOYEE_URI + "/7e44d58c-730e-404a-841e-44bcc9dd87bd",
                        JsonNode.class))
                .thenReturn(ResponseEntity.ok(singleEmployeeJson));
    }
}
