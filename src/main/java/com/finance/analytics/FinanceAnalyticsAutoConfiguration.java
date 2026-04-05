package com.finance.analytics;

import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.PermissionRepository;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.repository.UserRoleRepository;
import com.finance.analytics.service.DashboardService;
import com.finance.analytics.service.FinancialRecordService;
import com.finance.analytics.service.PermissionService;
import com.finance.analytics.service.RoleService;
import com.finance.analytics.service.UserService;
import com.finance.analytics.service.impl.DashboardServiceImpl;
import com.finance.analytics.service.impl.FinancialRecordServiceImpl;
import com.finance.analytics.service.impl.PermissionServiceImpl;
import com.finance.analytics.service.impl.RoleServiceImpl;
import com.finance.analytics.service.impl.UserServiceImpl;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class FinanceAnalyticsAutoConfiguration {

    @Bean
    public UserService userService(UserRepository userRepository, RoleRepository roleRepository,
                                   UserRoleRepository userRoleRepository, PasswordEncoder passwordEncoder,
                                   org.springframework.security.authentication.AuthenticationManager authenticationManager,
                                   com.finance.analytics.config.JwtUtils jwtUtils,
                                   org.springframework.security.core.userdetails.UserDetailsService userDetailsService) {
        return new UserServiceImpl(userRepository, roleRepository, userRoleRepository, passwordEncoder, authenticationManager, jwtUtils, userDetailsService);
    }

    @Bean
    public FinancialRecordService financialRecordService(FinancialRecordRepository financialRecordRepository,
                                                         UserRepository userRepository){
        return new FinancialRecordServiceImpl(financialRecordRepository, userRepository);
    }

    @Bean
    public DashboardService dashboardService(FinancialRecordRepository financialRecordRepository,
                                             UserRepository userRepository,
                                             UserRoleRepository userRoleRepository){
        return new DashboardServiceImpl(financialRecordRepository, userRepository, userRoleRepository);
    }

    @Bean
    public RoleService roleService(RoleRepository roleRepository) {
        return new RoleServiceImpl(roleRepository);
    }

    @Bean
    public PermissionService permissionService(PermissionRepository permissionRepository) {
        return new PermissionServiceImpl(permissionRepository);
    }

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.servers(List.of(new Server().url("http://localhost:8080")));
        openAPI.info(new Info().title("Finance Analytics API").version("1.0"));
        return openAPI;
    }
}
