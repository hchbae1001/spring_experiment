package bae.springexperiment.config.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Auth {

    public static AuthInformation getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthInformation authInformation = null;
        if (authentication != null && authentication.getPrincipal() instanceof AuthInformation) {
            authInformation = (AuthInformation) authentication.getPrincipal();
        }
        if (authInformation == null) {
            log.warn("Authentication information is null.");
        }
        return authInformation;
    }

    public static Long getMemberId() {
        AuthInformation authInformation = getAuthentication();
        if (authInformation == null) {
            log.error("Failed to get member ID because authentication information is null.");
            throw new IllegalStateException("Authentication information is not available.");
        }
        return authInformation.member_id();
    }
}
