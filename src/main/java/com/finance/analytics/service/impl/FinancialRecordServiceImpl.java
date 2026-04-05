package com.finance.analytics.service.impl;

import com.finance.analytics.entity.FinancialRecordEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.exception.InvalidInputException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.CreateRecordDTO;
import com.finance.analytics.model.dto.UpdateRecordDTO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.service.FinancialRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;

    @Override
    public SuccessResponseVO<FinancialRecordResponseVO> getRecordById(UUID recordId) {
        FinancialRecordEntity record = financialRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found"));
        return SuccessResponseVO.of(200, "Record fetched successfully", mapToVO(record));
    }

    @Override
    public SuccessResponseVO<Page<FinancialRecordResponseVO>> getRecordsByUserId(UUID userId, Pageable pageable) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Page<FinancialRecordResponseVO> records = financialRecordRepository.findByUserAndIsActiveTrue(user, pageable)
                .map(this::mapToVO);
        return SuccessResponseVO.of(200, "Records fetched successfully", records);
    }

    @Override
    public SuccessResponseVO<FinancialRecordResponseVO> createRecord(UUID userId, CreateRecordDTO createRecordDTO) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));
        FinancialRecordEntity financialRecordEntity = mapToEntity(userEntity, createRecordDTO);
        financialRecordEntity = financialRecordRepository.save(financialRecordEntity);
        return SuccessResponseVO.of(201, "Record created successfully", mapToVO(financialRecordEntity));
    }

    @Override
    public SuccessResponseVO<FinancialRecordResponseVO> updateRecord(UUID recordId, UpdateRecordDTO updateRecordDTO) {
        FinancialRecordEntity financialRecordEntity = financialRecordRepository.findById(recordId)
                .orElseThrow(()-> new ResourceNotFoundException("Record not found"));
        if(!Boolean.TRUE.equals(financialRecordEntity.getIsActive())){
            throw new InvalidInputException("Record is already deleted");
        }
        if(updateRecordDTO.getAmount() != null){
            if(updateRecordDTO.getAmount() <= 0){
                throw new InvalidInputException("Amount must be greater than 0");
            }
            financialRecordEntity.setAmount(updateRecordDTO.getAmount());
        }
        if(updateRecordDTO.getCategory() != null && !updateRecordDTO.getCategory().trim().isEmpty()){
            String newCategory = updateRecordDTO.getCategory().trim().toLowerCase();
            String existingCategory = null;
            if(financialRecordEntity.getCategory() != null){
                existingCategory = financialRecordEntity.getCategory().trim().toLowerCase();
            }
            if(!newCategory.equals(existingCategory)){
                financialRecordEntity.setCategory(newCategory);
            }
        }
        if(updateRecordDTO.getDescription() != null){
            financialRecordEntity.setDescription(updateRecordDTO.getDescription());
        }
        financialRecordEntity = financialRecordRepository.save(financialRecordEntity);
        return SuccessResponseVO.of(200, "Record updated successfully", mapToVO(financialRecordEntity));
    }

    @Override
    public void deleteRecord(UUID recordId) {
        FinancialRecordEntity financialRecordEntity = financialRecordRepository.findById(recordId)
                .orElseThrow(()-> new ResourceNotFoundException("Record not found"));
        if(!Boolean.TRUE.equals(financialRecordEntity.getIsActive())) {
            throw new ResourceNotFoundException("Record is not active");
        }
        financialRecordEntity.setIsActive(Boolean.FALSE);
        financialRecordRepository.save(financialRecordEntity);
    }

    private FinancialRecordResponseVO mapToVO(FinancialRecordEntity entity) {
        return new FinancialRecordResponseVO(
                entity.getId(),
                entity.getRecordType(),
                entity.getAmount(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getCreatedAt()
        );
    }

    private FinancialRecordEntity mapToEntity(UserEntity userEntity, CreateRecordDTO createRecordDTO) {
        FinancialRecordEntity financialRecordEntity = new FinancialRecordEntity();
        financialRecordEntity.setRecordType(createRecordDTO.getRecordType());
        financialRecordEntity.setAmount(createRecordDTO.getAmount());
        financialRecordEntity.setCategory(createRecordDTO.getCategory());
        financialRecordEntity.setDescription(createRecordDTO.getDescription());
        financialRecordEntity.setUser(userEntity);
        financialRecordEntity.setCreatedAt(LocalDateTime.now());
        return financialRecordEntity;
    }
}
