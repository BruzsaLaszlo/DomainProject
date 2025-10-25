package bruzsal.dnsmanagement.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public String getCurrentUsername() {
        // 1. Lekérdezzük az aktuális biztonsági kontextust.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ellenőrizzük, hogy van-e érvényes hitelesítés
        if (authentication == null || !authentication.isAuthenticated()) {
            return "Vendég felhasználó (nincs hitelesítve)";
        }

        // 2. Lekérdezzük a Principal objektumot, ami a felhasználó adatai.
        // Ez a legjobb mód, mivel kezeli, ha a Principal string (pl. csak username) vagy UserDetails.
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            // Ha a Principal UserDetails típusú (a Spring standard)
            return userDetails.getUsername();
        } else {
            // Más típusú Principal esetén (pl. String)
            if (principal != null) {
                return principal.toString();
            }
            throw new IllegalArgumentException("Principal objektum null értékű");
        }
    }
}