package com.example.baitap.security;

import com.example.baitap.domain.RoleName;
import com.example.baitap.exception.CustomException;
import com.example.baitap.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class SecurityUtils {
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "Unauthenticated");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof UserPrincipal p) {
            return p.getUserId();
        }
        if (principal instanceof UserDetails ud) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "Invalid principal");
        }
        throw new CustomException(ErrorCode.UNAUTHORIZED, "Unauthenticated");
    }

    public static boolean hasRole(String roleName) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }
        String expectedWithPrefix = "ROLE_" + roleName;
        String expectedRaw = roleName;
        return auth.getAuthorities().stream().anyMatch(a -> {
            String value = a.getAuthority();
            if (value == null) {
                return false;
            }
            return expectedWithPrefix.equals(value)
                    || expectedRaw.equals(value)
                    || value.endsWith(roleName);
        });
    }

    public static void requireRole(RoleName roleName) {
        if (!hasRole(roleName.name())) {
            throw new CustomException(ErrorCode.FORBIDDEN, roleName.name() + " required");
        }
    }
}

