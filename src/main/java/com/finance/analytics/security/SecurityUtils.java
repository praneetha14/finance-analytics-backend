package com.finance.analytics.security;

import com.finance.analytics.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("securityUtils")
@RequiredArgsConstructor
public class SecurityUtils {

    private final FinancialRecordRepository financialRecordRepository;

    public boolean canAccessUser(UUID userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return false;
        }

        if (isAdmin(authentication)) {
            return true;
        }

        // If user is a VIEWER, they can only access their own data
        if (isViewer(authentication)) {
            return principal.getId().equals(userId);
        }

        return true;
    }

    public boolean canAccessRecord(UUID recordId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            return false;
        }

        if (isAdmin(authentication)) {
            return true;
        }

        if (isViewer(authentication)) {
            return financialRecordRepository.findById(recordId)
                    .map(record -> record.getUser().getId().equals(principal.getId()))
                    .orElse(false);
        }

        return true;
    }

    public boolean isViewer(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_VIEWER"));
    }

    public boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
