package com.finance.analytics;

import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.repository.UserRoleRepository;
import com.finance.analytics.service.FinancialRecordService;
import com.finance.analytics.service.UserService;
import com.finance.analytics.service.impl.FinancialRecordServiceImpl;
import com.finance.analytics.service.impl.UserServiceImpl;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FinanceAnalyticsAutoConfiguration {

    @Bean
    public UserService userService(UserRepository userRepository, RoleRepository roleRepository,
                                   UserRoleRepository userRoleRepository) {
        return new UserServiceImpl(userRepository, roleRepository, userRoleRepository);
    }

    @Bean
    public FinancialRecordService financialRecordService(FinancialRecordRepository financialRecordRepository,
                                                         UserRepository userRepository){
        return new FinancialRecordServiceImpl(financialRecordRepository, userRepository);
    }

    @Bean
    public OpenAPI openAPI() {
        OpenAPI openAPI = new OpenAPI();
        openAPI.servers(List.of(new Server().url("http://localhost:8080")));
        return openAPI;
    }
}
