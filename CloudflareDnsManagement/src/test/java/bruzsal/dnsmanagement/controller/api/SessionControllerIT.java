package bruzsal.dnsmanagement.controller.api;

import bruzsal.dnsmanagement.controller.session.UserSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(
        controllers = SessionController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class SessionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserSession userSession;


    // --- login teszt ---
    @Test
    void login_ShouldSetUsernameAndReturnOk() throws Exception {
        String username = "testUser";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/login")
                        .param("username", username)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged in: " + username));

        verify(userSession, times(1)).setUsername(username);
    }

    // --- getLoggedInUser teszt ---
    @Test
    void getLoggedInUser_ShouldReturnCurrentUsername() throws Exception {
        String expectedUsername = "loggedInUser";
        when(userSession.getUsername()).thenReturn(expectedUsername);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/session/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged in as: " + expectedUsername));

        verify(userSession, times(1)).getUsername();
    }

    // --- setApiToken teszt ---
    @Test
    void setApiToken_ShouldSetApiTokenAndReturnOk(@Value("${cloudflare.api-token}") String apiToken) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/api-token")
                        .param("apiToken", apiToken)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("API token set"));

        verify(userSession, times(1)).setApiToken(apiToken);
    }

    // --- setZoneId teszt ---
    @Test
    void setZoneId_ShouldSetApiTokenWithZoneIdValueAndReturnOk() throws Exception {
        String zoneId = "testZoneId";

        // A teszt a controller megadott viselkedését ellenőrzi, ahol setApiToken van hívva:
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/zone-id")
                        .param("zoneId", zoneId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().string("ZoneID set"));

        verify(userSession, times(1)).setZoneId(zoneId);
    }

    // --- clearSession teszt ---
    @Test
    void clearSession_ShouldInvalidateSessionAndReturnOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/session/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Session cleared"));
    }
}