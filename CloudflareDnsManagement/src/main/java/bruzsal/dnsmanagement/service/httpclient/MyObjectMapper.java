package bruzsal.dnsmanagement.service.httpclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Slf4j
@Component
public class MyObjectMapper {

    private final ObjectMapper om;

    public MyObjectMapper() {
        this.om = new ObjectMapper().findAndRegisterModules();
    }

    public String writeValueAsString(Object value) {
        try {
            return om.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("Can not create json object from: {}", value);
            throw new IllegalArgumentException(e);
        }
    }

    public <T> T readValue(String content, Class<T> valueType) {
        try {
            return om.readValue(content, valueType);
        } catch (JsonMappingException jme) {
            log.error("Mapping failed to class: {} , from json: {}", valueType.getName(), content);
            throw new IllegalArgumentException(jme);
        } catch (JsonProcessingException jpe) {
            throw new IllegalArgumentException(jpe);
        }
    }

    public <T> T readValue(String json, TypeReference<T> typeReference) {
        try {
            return om.readValue(json, typeReference);
        } catch (JsonMappingException jme) {
            log.error("Mapping failed to class: {} , from json: {}", typeReference.getType().getTypeName(), json);
            throw new IllegalArgumentException(jme);
        } catch (JsonProcessingException jpe) {
            throw new IllegalArgumentException(jpe);
        }
    }

    public String writeValueAsStringPretty(String value) {
        try {
            JsonNode jsonNode = om.readTree(value);
            return om.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
        } catch (JsonProcessingException | RuntimeException _) {
            log.error("Can not create pretty json object from: {}", value);
        }
        return value;
    }

}
