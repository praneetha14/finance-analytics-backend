package com.finance.analytics.rest.v1;

import com.finance.analytics.BaseControllerTest;
import com.finance.analytics.model.dto.CreateRecordDTO;
import com.finance.analytics.model.dto.UpdateRecordDTO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.service.FinancialRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FinancialRecordController.class)
@ExtendWith(SpringExtension.class)
public class FinancialRecordControllerTest extends BaseControllerTest {

    private static final String BASE_URL = "/api/v1/records";

    @MockBean
    private FinancialRecordService financialRecordService;

    @Test
    @WithMockUser(authorities = "FINANCIAL_RECORD_WRITE")
    void createRecordSuccessTest() throws Exception {
        UUID userId = UUID.randomUUID();
        CreateRecordDTO createDTO = new CreateRecordDTO();
        createDTO.setRecordType("INCOME");
        createDTO.setAmount(100.0);
        createDTO.setCategory("Salary");

        FinancialRecordResponseVO responseVO = new FinancialRecordResponseVO(UUID.randomUUID(), com.finance.analytics.model.enums.RecordTypeEnum.INCOME, 100.0, "salary", "Salary", null, userId);
        SuccessResponseVO<FinancialRecordResponseVO> response = SuccessResponseVO.of(201, "Record created successfully", responseVO);

        when(financialRecordService.createRecord(eq(userId), any(CreateRecordDTO.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/create/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Record created successfully"));
    }

    @Test
    @WithMockUser(authorities = "FINANCIAL_RECORD_READ")
    void getRecordByIdSuccessTest() throws Exception {
        UUID recordId = UUID.randomUUID();
        FinancialRecordResponseVO responseVO = new FinancialRecordResponseVO(recordId, com.finance.analytics.model.enums.RecordTypeEnum.INCOME, 100.0, "salary", "Salary", null, UUID.randomUUID());
        SuccessResponseVO<FinancialRecordResponseVO> response = SuccessResponseVO.of(200, "Record fetched successfully", responseVO);

        when(securityUtils.canAccessRecord(recordId)).thenReturn(true);
        when(financialRecordService.getRecordById(recordId)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/get/" + recordId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Record fetched successfully"));
    }

    @Test
    @WithMockUser(authorities = "FINANCIAL_RECORD_READ")
    void getRecordByIdForbiddenTest() throws Exception {
        UUID recordId = UUID.randomUUID();
        when(securityUtils.canAccessRecord(recordId)).thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/get/" + recordId)
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "FINANCIAL_RECORD_WRITE")
    void updateRecordSuccessTest() throws Exception {
        UUID recordId = UUID.randomUUID();
        UpdateRecordDTO updateDTO = new UpdateRecordDTO();
        updateDTO.setAmount(120.0);

        FinancialRecordResponseVO responseVO = new FinancialRecordResponseVO(recordId, com.finance.analytics.model.enums.RecordTypeEnum.INCOME, 120.0, "salary", "Salary", null, UUID.randomUUID());
        SuccessResponseVO<FinancialRecordResponseVO> response = SuccessResponseVO.of(200, "Record updated successfully", responseVO);

        when(financialRecordService.updateRecord(eq(recordId), any(UpdateRecordDTO.class))).thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/update/" + recordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Record updated successfully"));
    }

    @Test
    @WithMockUser(authorities = "FINANCIAL_RECORD_DELETE")
    void deleteRecordSuccessTest() throws Exception {
        UUID recordId = UUID.randomUUID();

        mockMvc.perform(delete(BASE_URL + "/delete/" + recordId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Record deleted successfully"));
    }

    @Test
    @WithMockUser(authorities = "FINANCIAL_RECORD_READ")
    void searchRecordsSuccessTest() throws Exception {
        when(financialRecordService.getFilteredRecords(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(SuccessResponseVO.of(200, "Filtered records fetched successfully", null));

        mockMvc.perform(get(BASE_URL + "/search")
                .param("type", "INCOME")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Filtered records fetched successfully"));
    }
}
