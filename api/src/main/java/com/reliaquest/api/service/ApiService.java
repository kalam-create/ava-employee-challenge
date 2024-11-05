package com.reliaquest.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.reliaquest.api.request.CreateEmployeeRequest;
import com.reliaquest.api.request.DeleteEmployeeRequest;

/**
 * Component class to handle interaction with Server API
 */
@Component
public class ApiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.base.uri}")
    private String apiBaseUri;

    /**
     * Makes GET call to Server API endpoint
     */
    public ResponseEntity<JsonNode> get(String endpointUri) {
        return restTemplate.getForEntity(apiBaseUri + endpointUri, JsonNode.class);
    }

    /**
     * Makes post call to Server API endpoint with given post body
     */
    public ResponseEntity<JsonNode> post(String endpointUri, CreateEmployeeRequest employeeRequest) {
        return restTemplate.postForEntity(apiBaseUri + endpointUri, employeeRequest, JsonNode.class);
    }

    /**
     * Makes delete call to Server API endpoint with employee name to be deleted
     */
    public ResponseEntity<JsonNode> delete(String endpointUri, String name) {
        HttpEntity<DeleteEmployeeRequest> employeeDeleteRequest = new HttpEntity<>(new DeleteEmployeeRequest(name));
        return restTemplate.exchange(
                apiBaseUri + endpointUri, HttpMethod.DELETE, employeeDeleteRequest, JsonNode.class);
    }
}
