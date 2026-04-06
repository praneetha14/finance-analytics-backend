package com.finance.analytics.rest.v1;

import com.finance.analytics.entity.FinancialRecordEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.model.enums.RecordTypeEnum;
import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    private UUID userId;

    @BeforeEach
    void setUp() {
        UserEntity user = new UserEntity();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setMobile("1234567890");
        user.setIsActive(true);
        user = userRepository.save(user);
        userId = user.getId();

        FinancialRecordEntity record = new FinancialRecordEntity();
        record.setRecordType(RecordTypeEnum.INCOME);
        record.setAmount(1000.0);
        record.setCategory("Salary");
        record.setUser(user);
        record.setCreatedAt(LocalDateTime.now());
        record.setIsActive(true);
        financialRecordRepository.save(record);
    }

    @Test
    void getAllRecordsShouldIncludeUserId() throws Exception {
        setupSecurityContext(UUID.randomUUID(), "admin@test.com", List.of("ROLE_ADMIN", "FINANCIAL_RECORD_READ"));

        mockMvc.perform(get("/api/v1/dashboard/records/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].userId").value(userId.toString()));
    }

    private void setupSecurityContext(UUID userId, String email, List<String> authorities) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setEmail(email);
        user.setPassword("password");
        user.setIsActive(true);
        
        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
                
        UserPrincipal principal = new UserPrincipal(user, grantedAuthorities);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, grantedAuthorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }
}
