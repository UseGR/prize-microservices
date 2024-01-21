package galeev.prizeservice.util;

import org.springframework.core.io.ClassPathResource;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.JsonNode;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonUtils {
    public final static ObjectMapper objectMapper = new ObjectMapper();

    public static String loadResourceData(String resourceFile) throws IOException {
        return IOUtils.toString(new ClassPathResource(resourceFile).getInputStream(), StandardCharsets.UTF_8.displayName());
    }

    public static JsonNode loadResourceDataAsJson(String resourceFile) throws IOException {
        return convertStringToJson(loadResourceData(resourceFile));
    }

    public static JsonNode convertStringToJson(String src) throws IOException {
        return objectMapper.readTree(src);
    }
}
