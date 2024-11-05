package com.reliaquest.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;

/**
 * Utility class to load dummy records from resource
 */
public class FileUtil {
    public static JsonNode readJSON(String filename) throws IOException {
        URL resource = FileUtil.class.getClassLoader().getResource(filename);
        return new ObjectMapper().readTree(resource);
    }
}
