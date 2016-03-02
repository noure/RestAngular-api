package fr.example.springboot.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import fr.example.springboot.domain.User;

public class SecurityUtil {

    public static User currentUser() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth == null) {
            return null;
        }

        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        return (User) auth.getPrincipal();
    }
}