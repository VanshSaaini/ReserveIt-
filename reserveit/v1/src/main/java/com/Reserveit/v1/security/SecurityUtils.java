package com.Reserveit.v1.security;

import com.Reserveit.v1.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Small helper for pulling the currently authenticated User out of the SecurityContext. */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AppUserPrincipal currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserPrincipal principal)) {
            throw new org.springframework.security.access.AccessDeniedException("No authenticated user.");
        }
        return principal;
    }

    public static User currentUser() {
        return currentPrincipal().getUser();
    }

    public static Long currentUserId() {
        return currentPrincipal().getId();
    }
}
