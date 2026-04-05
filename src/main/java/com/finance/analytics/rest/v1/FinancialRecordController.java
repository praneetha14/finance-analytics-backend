package com.finance.analytics.rest.v1;

import com.finance.analytics.model.dto.CreateRecordDTO;
import com.finance.analytics.model.dto.UpdateRecordDTO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<SuccessResponseVO<FinancialRecordResponseVO>> createRecord(@PathVariable UUID userId, @Valid @RequestBody CreateRecordDTO createRecordDTO){
        return new ResponseEntity<>(financialRecordService.createRecord(userId, createRecordDTO), HttpStatus.CREATED);
    }

    @PutMapping("/update/{recordId}")
    public ResponseEntity<SuccessResponseVO<FinancialRecordResponseVO>> updateRecord(@PathVariable UUID recordId,
                                                                                     @Valid @RequestBody UpdateRecordDTO updateRecordDTO){
        return ResponseEntity.ok(financialRecordService.updateRecord(recordId, updateRecordDTO));
    }

    @DeleteMapping("/delete/{recordId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecord(@PathVariable UUID recordId){
        financialRecordService.deleteRecord(recordId);
    }
}
