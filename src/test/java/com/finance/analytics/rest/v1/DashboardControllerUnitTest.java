package com.finance.analytics.rest.v1;

import com.finance.analytics.BaseControllerTest;
import com.finance.analytics.model.vo.DashboardSummaryVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardController.class)
@ExtendWith(SpringExtension.class)
public class DashboardControllerUnitTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/dashboard";

    @MockBean
    private DashboardService dashboardService;

    @Test
    @WithMockUser(authorities = "FINANCIAL_SUMMARY_READ")
    void getSummaryByUserIdSuccessTest() throws Exception {
        UUID userId = UUID.randomUUID();
        DashboardSummaryVO summaryVO = new DashboardSummaryVO(1000.0, 200.0, 800.0, null, null, null, null);
        when(dashboardService.getSummaryByUserId(userId)).thenReturn(SuccessResponseVO.of(200, "Summary fetched successfully", summaryVO));

        mockMvc.perform(get(BASE_URL + "/summary/" + userId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Summary fetched successfully"))
                .andExpect(jsonPath("$.data.balance").value(800.0));
    }

    @Test
    @WithMockUser(authorities = "FINANCIAL_RECORD_READ")
    void getAllRecordsSuccessTest() throws Exception {
        when(securityUtils.isViewer(any())).thenReturn(false);
        when(dashboardService.getAllRecords(anyInt(), anyInt())).thenReturn(SuccessResponseVO.of(200, "All Records fetched successfully", null));

        mockMvc.perform(get(BASE_URL + "/records/all")
                .param("page", "0")
                .param("size", "10")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All Records fetched successfully"));
    }
}
