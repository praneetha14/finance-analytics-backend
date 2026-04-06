package com.finance.analytics.service;

import com.finance.analytics.model.dto.CreateRecordDTO;
import com.finance.analytics.model.dto.UpdateRecordDTO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FinancialRecordService {
    SuccessResponseVO<FinancialRecordResponseVO> createRecord(UUID userId, CreateRecordDTO createRecordDTO);
    SuccessResponseVO<FinancialRecordResponseVO> updateRecord(UUID recordId, UpdateRecordDTO updateRecordDTO);
    void deleteRecord(UUID recordId);
    SuccessResponseVO<FinancialRecordResponseVO> getRecordById(UUID recordId);
}
