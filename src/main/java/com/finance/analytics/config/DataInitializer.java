package com.finance.analytics.config;

import com.finance.analytics.entity.PermissionsEntity;
import com.finance.analytics.entity.RoleEntity;
import com.finance.analytics.entity.RolePermissionsEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.entity.UserRolesEntity;
import com.finance.analytics.model.enums.RoleEnum;
import com.finance.analytics.repository.PermissionRepository;
import com.finance.analytics.repository.RolePermissionRepository;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Checking if roles need initialization...");
        initializeRoles();
        
        log.info("Checking if permissions need initialization...");
        initializePermissions();

        log.info("Checking if role-permission mappings need initialization...");
        initializeRolePermissions();
        
        if (userRepository.count() == 0) {
            log.info("No users found. Creating default admin user...");
            createDefaultAdmin();
        }
    }

    private void initializeRoles() {
        for (RoleEnum roleEnum : RoleEnum.values()) {
            if (!roleRepository.existsByRoleName(roleEnum)) {
                log.info("Initializing role: {}", roleEnum);
                RoleEntity role = new RoleEntity();
                role.setRoleName(roleEnum);
                role.setCreatedAt(LocalDateTime.now());
                role.setUpdatedAt(LocalDateTime.now());
                roleRepository.save(role);
            }
        }
    }

    private void initializePermissions() {
        List<String> permissions = Arrays.asList(
                "FINANCIAL_RECORD_READ",
                "FINANCIAL_RECORD_WRITE",
                "FINANCIAL_RECORD_DELETE",
                "FINANCIAL_SUMMARY_READ",
                "USER_READ",
                "USER_WRITE",
                "USER_DELETE",
                "ROLE_READ",
                "ROLE_WRITE",
                "PERMISSION_READ",
                "PERMISSION_WRITE"
        );

        for (String permName : permissions) {
            if (!permissionRepository.existsByName(permName)) {
                log.info("Initializing permission: {}", permName);
                PermissionsEntity permission = new PermissionsEntity();
                permission.setName(permName);
                permission.setCreatedAt(LocalDateTime.now());
                permissionRepository.save(permission);
            }
        }
    }

    private void initializeRolePermissions() {
        if (rolePermissionRepository.count() > 0) return;

        Map<RoleEnum, List<String>> rolePermissionsMap = Map.of(
            RoleEnum.ADMIN, Arrays.asList(
                "FINANCIAL_RECORD_READ", "FINANCIAL_RECORD_WRITE", "FINANCIAL_RECORD_DELETE",
                "FINANCIAL_SUMMARY_READ", "USER_READ", "USER_WRITE", "USER_DELETE",
                "ROLE_READ", "ROLE_WRITE", "PERMISSION_READ", "PERMISSION_WRITE"
            ),
            RoleEnum.ANALYST, Arrays.asList(
                "FINANCIAL_RECORD_READ", "FINANCIAL_SUMMARY_READ", "USER_READ"
            ),
            RoleEnum.VIEWER, Arrays.asList(
                "FINANCIAL_RECORD_READ"
            )
        );

        for (Map.Entry<RoleEnum, List<String>> entry : rolePermissionsMap.entrySet()) {
            RoleEntity role = roleRepository.findByRoleName(entry.getKey())
                    .orElseThrow(() -> new RuntimeException("Role not found: " + entry.getKey()));
            
            for (String permName : entry.getValue()) {
                PermissionsEntity permission = permissionRepository.findByName(permName)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + permName));
                
                RolePermissionsEntity rolePermission = new RolePermissionsEntity();
                rolePermission.setRole(role);
                rolePermission.setPermission(permission);
                rolePermissionRepository.save(rolePermission);
            }
        }
    }

    private void createDefaultAdmin() {
        UserEntity admin = new UserEntity();
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setEmail("admin@finance.com");
        admin.setMobile("9876543210");
        admin.setPassword(passwordEncoder.encode("Admin@123"));
        admin.setIsActive(true);
        
        UserEntity savedAdmin = userRepository.save(admin);
        
        RoleEntity adminRole = roleRepository.findAll().stream()
                .filter(r -> r.getRoleName() == RoleEnum.ADMIN)
                .findFirst()
                .orElseThrow();
                
        UserRolesEntity userRole = new UserRolesEntity();
        userRole.setUser(savedAdmin);
        userRole.setRole(adminRole);
        userRoleRepository.save(userRole);
        
        log.info("Default admin user created: admin@finance.com / Admin@123");
    }
}
