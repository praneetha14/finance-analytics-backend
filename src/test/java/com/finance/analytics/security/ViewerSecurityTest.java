package com.finance.analytics.security;

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
public class ViewerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    private UUID user1Id;
    private UUID user2Id;
    private UUID record1Id;

    @BeforeEach
    void setUp() {
        UserEntity user1 = new UserEntity();
        user1.setFirstName("User");
        user1.setLastName("One");
        user1.setEmail("user1@test.com");
        user1.setMobile("1234567890");
        user1.setIsActive(true);
        user1 = userRepository.save(user1);
        user1Id = user1.getId();

        UserEntity user2 = new UserEntity();
        user2.setFirstName("User");
        user2.setLastName("Two");
        user2.setEmail("user2@test.com");
        user2.setMobile("0987654321");
        user2.setIsActive(true);
        user2 = userRepository.save(user2);
        user2Id = user2.getId();

        FinancialRecordEntity record1 = new FinancialRecordEntity();
        record1.setRecordType(RecordTypeEnum.EXPENSE);
        record1.setAmount(100.0);
        record1.setCategory("food");
        record1.setUser(user1);
        record1.setCreatedAt(LocalDateTime.now());
        record1.setIsActive(true);
        record1 = financialRecordRepository.save(record1);
        record1Id = record1.getId();
    }

    @Test
    void viewerShouldNotBeAbleToSeeOthersRecords() throws Exception {
        setupSecurityContext(user2Id, "user2@test.com", List.of("ROLE_VIEWER", "FINANCIAL_RECORD_READ"));
        
        mockMvc.perform(get("/api/v1/records/get/" + record1Id))
                .andExpect(status().isForbidden()); 
    }

    @Test
    void viewerShouldBeAbleToSeeOwnRecord() throws Exception {
        setupSecurityContext(user1Id, "user1@test.com", List.of("ROLE_VIEWER", "FINANCIAL_RECORD_READ"));
        
        mockMvc.perform(get("/api/v1/records/get/" + record1Id))
                .andExpect(status().isOk());
    }

    @Test
    void viewerShouldNotBeAbleToSeeOthersRecordsList() throws Exception {
        setupSecurityContext(user2Id, "user2@test.com", List.of("ROLE_VIEWER", "FINANCIAL_RECORD_READ"));
        
        mockMvc.perform(get("/api/v1/dashboard/records/" + user1Id))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerShouldNotBeAbleToSeeAllRecords() throws Exception {
        setupSecurityContext(user2Id, "user2@test.com", List.of("ROLE_VIEWER", "FINANCIAL_RECORD_READ"));
        
        mockMvc.perform(get("/api/v1/dashboard/records/all?page=0&size=10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerShouldNotBeAbleToSeeSelfSummary() throws Exception {
        setupSecurityContext(user1Id, "user1@test.com", List.of("ROLE_VIEWER", "FINANCIAL_RECORD_READ"));
        
        mockMvc.perform(get("/api/v1/dashboard/summary/" + user1Id))
                .andExpect(status().isForbidden());
    }

    @Test
    void viewerSearchShouldBeFilteredToSelf() throws Exception {
        setupSecurityContext(user2Id, "user2@test.com", List.of("ROLE_VIEWER", "FINANCIAL_RECORD_READ"));

        mockMvc.perform(get("/api/v1/records/search?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString();
                    assert !content.contains(record1Id.toString());
                });
    }

    @Test
    void adminSearchWithDefaultPagination() throws Exception {
        setupSecurityContext(UUID.randomUUID(), "admin@test.com", List.of("ROLE_ADMIN", "FINANCIAL_RECORD_READ"));
        
        mockMvc.perform(get("/api/v1/dashboard/records/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("All Records fetched successfully"));
    }

    @Test
    void shouldReturnStandardizedFailureResponse() throws Exception {
        setupSecurityContext(user1Id, "user1@test.com", List.of("ROLE_VIEWER", "FINANCIAL_RECORD_READ"));
        
        mockMvc.perform(get("/api/v1/records/get/" + UUID.randomUUID()))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("Access Denied: You do not have the required permissions to perform this action."))
                .andExpect(jsonPath("$.body.status").value(403))
                .andExpect(jsonPath("$.body.error").value("Access Denied: You do not have the required permissions to perform this action."));
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
