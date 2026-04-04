package com.finance.analytics;

import com.finance.analytics.repository.RoleRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.repository.UserRoleRepository;
import com.finance.analytics.service.UserService;
import com.finance.analytics.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FinanceAnalyticsAutoConfiguration {

    @Bean
    public UserService userService(UserRepository userRepository, RoleRepository roleRepository,
                                   UserRoleRepository userRoleRepository) {
        return new UserServiceImpl(userRepository, roleRepository, userRoleRepository);
    }
}
