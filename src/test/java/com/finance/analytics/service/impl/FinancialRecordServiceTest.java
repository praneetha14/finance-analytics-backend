package com.finance.analytics.service.impl;

import com.finance.analytics.AbstractTest;
import com.finance.analytics.entity.FinancialRecordEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.exception.DuplicateResourceException;
import com.finance.analytics.exception.ResourceNotFoundException;
import com.finance.analytics.model.dto.CreateRecordDTO;
import com.finance.analytics.model.dto.UpdateRecordDTO;
import com.finance.analytics.model.enums.RecordTypeEnum;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.repository.FinancialRecordRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.service.FinancialRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class FinancialRecordServiceTest extends AbstractTest {

    @Autowired
    private FinancialRecordService financialRecordService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FinancialRecordRepository financialRecordRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test" + UUID.randomUUID() + "@example.com");
        testUser.setMobile("1234567890");
        testUser.setPassword("password");
        testUser.setIsActive(true);
        testUser = userRepository.save(testUser);
    }

    @Test
    void createRecordSuccessTest() {
        CreateRecordDTO createDTO = new CreateRecordDTO();
        createDTO.setRecordType("INCOME");
        createDTO.setAmount(100.0);
        createDTO.setCategory("Salary");
        createDTO.setDescription("Monthly Salary");

        SuccessResponseVO<FinancialRecordResponseVO> response = financialRecordService.createRecord(testUser.getId(), createDTO);

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(201, response.getCode(), ASSERTION_ERROR_MESSAGE);
        assertEquals("Record created successfully", response.getMessage(), ASSERTION_ERROR_MESSAGE);
        assertNotNull(response.getData(), ASSERTION_ERROR_MESSAGE);
        assertEquals(100.0, response.getData().amount(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void createRecordDuplicateFailureTest() {
        CreateRecordDTO createDTO = new CreateRecordDTO();
        createDTO.setRecordType("INCOME");
        createDTO.setAmount(100.0);
        createDTO.setCategory("Salary");
        createDTO.setDescription("Monthly Salary");

        financialRecordService.createRecord(testUser.getId(), createDTO);

        assertThrows(DuplicateResourceException.class, () -> 
            financialRecordService.createRecord(testUser.getId(), createDTO)
        );
    }

    @Test
    void updateRecordSuccessTest() {
        CreateRecordDTO createDTO = new CreateRecordDTO();
        createDTO.setRecordType("EXPENSE");
        createDTO.setAmount(50.0);
        createDTO.setCategory("Food");
        SuccessResponseVO<FinancialRecordResponseVO> created = financialRecordService.createRecord(testUser.getId(), createDTO);

        UpdateRecordDTO updateDTO = new UpdateRecordDTO();
        updateDTO.setAmount(60.0);
        updateDTO.setCategory("Groceries");

        SuccessResponseVO<FinancialRecordResponseVO> response = financialRecordService.updateRecord(created.getData().id(), updateDTO);

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(200, response.getCode(), ASSERTION_ERROR_MESSAGE);
        assertEquals(60.0, response.getData().amount(), ASSERTION_ERROR_MESSAGE);
        assertEquals("groceries", response.getData().category(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void getRecordByIdSuccessTest() {
        CreateRecordDTO createDTO = new CreateRecordDTO();
        createDTO.setRecordType("INCOME");
        createDTO.setAmount(200.0);
        createDTO.setCategory("Bonus");
        SuccessResponseVO<FinancialRecordResponseVO> created = financialRecordService.createRecord(testUser.getId(), createDTO);

        SuccessResponseVO<FinancialRecordResponseVO> response = financialRecordService.getRecordById(created.getData().id());

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(200, response.getCode(), ASSERTION_ERROR_MESSAGE);
        assertEquals(200.0, response.getData().amount(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void getRecordByIdNotFoundFailureTest() {
        assertThrows(ResourceNotFoundException.class, () -> 
            financialRecordService.getRecordById(UUID.randomUUID())
        );
    }

    @Test
    void getFilteredRecordsSuccessTest() {
        CreateRecordDTO createDTO1 = new CreateRecordDTO();
        createDTO1.setRecordType("INCOME");
        createDTO1.setAmount(100.0);
        createDTO1.setCategory("Salary");
        financialRecordService.createRecord(testUser.getId(), createDTO1);

        CreateRecordDTO createDTO2 = new CreateRecordDTO();
        createDTO2.setRecordType("EXPENSE");
        createDTO2.setAmount(50.0);
        createDTO2.setCategory("Food");
        financialRecordService.createRecord(testUser.getId(), createDTO2);

        SuccessResponseVO<List<FinancialRecordResponseVO>> response = financialRecordService.getFilteredRecords(
                testUser.getId(), "INCOME", null, null, null, 0, 10, "DESC");

        assertNotNull(response, ASSERTION_ERROR_MESSAGE);
        assertEquals(1, response.getPagination().getTotalCount(), ASSERTION_ERROR_MESSAGE);
        assertEquals("income", response.getData().get(0).recordTypeEnum().name().toLowerCase(), ASSERTION_ERROR_MESSAGE);
    }

    @Test
    void deleteRecordSuccessTest() {
        CreateRecordDTO createDTO = new CreateRecordDTO();
        createDTO.setRecordType("INCOME");
        createDTO.setAmount(100.0);
        createDTO.setCategory("Salary");
        SuccessResponseVO<FinancialRecordResponseVO> created = financialRecordService.createRecord(testUser.getId(), createDTO);

        financialRecordService.deleteRecord(created.getData().id());

        FinancialRecordEntity record = financialRecordRepository.findById(created.getData().id()).orElseThrow();
        assertFalse(record.getIsActive(), ASSERTION_ERROR_MESSAGE);
    }
}
