package com.finance.analytics.service;

import com.finance.analytics.model.dto.CreateRecordDTO;
import com.finance.analytics.model.dto.UpdateRecordDTO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import com.finance.analytics.model.enums.RecordTypeEnum;
import java.time.LocalDateTime;

public interface FinancialRecordService {
    SuccessResponseVO<FinancialRecordResponseVO> createRecord(UUID userId, CreateRecordDTO createRecordDTO);
    SuccessResponseVO<FinancialRecordResponseVO> updateRecord(UUID recordId, UpdateRecordDTO updateRecordDTO);
    void deleteRecord(UUID recordId);
    SuccessResponseVO<FinancialRecordResponseVO> getRecordById(UUID recordId);
    SuccessResponseVO<Page<FinancialRecordResponseVO>> getFilteredRecords(
            UUID userId, String type, String category,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
