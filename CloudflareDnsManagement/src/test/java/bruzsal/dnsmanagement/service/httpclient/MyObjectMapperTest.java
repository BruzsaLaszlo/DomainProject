package bruzsal.dnsmanagement.service.httpclient;

import bruzsal.dnsmanagement.dto.CloudflareErrorDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;


class MyObjectMapperTest {
    final MyObjectMapper om = new MyObjectMapper();

    @Test
    void testReadValue() {
        final String json = """
                {
                   "result":null,
                   "success":false,
                   "errors":[{
                         "code":1004,
                         "message":"DNS Validation Error",
                         "error_chain":[
                            {
                               "code":9005,
                               "message":"Content for A record must be a valid IPv4 address."
                            }]
                      }],
                   "messages":[]
                }""";

        CloudflareErrorDto cloudflareErrorDto = om.readValue(json, CloudflareErrorDto.class);

        assertFalse(cloudflareErrorDto.success());
    }

}