package com.finance.analytics.rest.v1;

import com.finance.analytics.model.dto.CreateRecordDTO;
import com.finance.analytics.model.dto.UpdateRecordDTO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.service.FinancialRecordService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    @PostMapping("/create/{userId}")
    @PreAuthorize("hasAuthority('FINANCIAL_RECORD_WRITE')")
    public ResponseEntity<SuccessResponseVO<FinancialRecordResponseVO>> createRecord(@PathVariable UUID userId, @Valid @RequestBody CreateRecordDTO createRecordDTO){
        return new ResponseEntity<>(financialRecordService.createRecord(userId, createRecordDTO), HttpStatus.CREATED);
    }

    @PutMapping("/update/{recordId}")
    @PreAuthorize("hasAuthority('FINANCIAL_RECORD_WRITE')")
    public ResponseEntity<SuccessResponseVO<FinancialRecordResponseVO>> updateRecord(@PathVariable UUID recordId,
                                                                                     @Valid @RequestBody UpdateRecordDTO updateRecordDTO){
        return ResponseEntity.ok(financialRecordService.updateRecord(recordId, updateRecordDTO));
    }

    @DeleteMapping("/delete/{recordId}")
    @PreAuthorize("hasAuthority('FINANCIAL_RECORD_DELETE')")
    public ResponseEntity<SuccessResponseVO<Void>> deleteRecord(@PathVariable UUID recordId){
        financialRecordService.deleteRecord(recordId);
        return ResponseEntity.ok(SuccessResponseVO.of(200, "Record deleted successfully", null));
    }

    @GetMapping("/get/{recordId}")
    @PreAuthorize("hasAuthority('FINANCIAL_RECORD_READ') and @securityUtils.canAccessRecord(#recordId)")
    public ResponseEntity<SuccessResponseVO<FinancialRecordResponseVO>> getRecordById(@PathVariable UUID recordId) {
        return ResponseEntity.ok(financialRecordService.getRecordById(recordId));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('FINANCIAL_RECORD_READ') and (#userId == null or @securityUtils.canAccessUser(#userId))")
    public ResponseEntity<SuccessResponseVO<List<FinancialRecordResponseVO>>> getFilteredRecords(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Page number", schema = @Schema(defaultValue = "0"))
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @Parameter(description = "Page size", schema = @Schema(defaultValue = "10"))
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @Parameter(description = "Sort direction", schema = @Schema(allowableValues = {"ASC", "DESC"}, defaultValue = "DESC"))
            @RequestParam(required = false, defaultValue = "DESC") String sort) {
        return ResponseEntity.ok(financialRecordService.getFilteredRecords(userId, type, category, startDate, endDate, page, size, sort));
    }
}
