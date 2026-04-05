package com.finance.analytics.rest.v1;

import com.finance.analytics.model.vo.DashboardSummaryVO;
import com.finance.analytics.model.vo.FinancialRecordResponseVO;
import com.finance.analytics.model.vo.SuccessResponseVO;
import com.finance.analytics.model.vo.UserResponseVO;
import com.finance.analytics.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/records/{userId}")
    @PreAuthorize("hasAuthority('FINANCIAL_RECORD_READ')")
    public ResponseEntity<SuccessResponseVO<List<FinancialRecordResponseVO>>> getRecordsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(dashboardService.getRecordsByUserId(userId));
    }

    @GetMapping("/records/all")
    @PreAuthorize("hasAuthority('FINANCIAL_RECORD_READ')")
    public ResponseEntity<SuccessResponseVO<Page<FinancialRecordResponseVO>>> getAllRecords(
            @RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(dashboardService.getAllRecords(page, size));
    }

    @GetMapping("/summary/{userId}")
    @PreAuthorize("hasAuthority('FINANCIAL_SUMMARY_READ')")
    public ResponseEntity<SuccessResponseVO<DashboardSummaryVO>> getSummaryByUserId(@PathVariable UUID userId){
        return ResponseEntity.ok(dashboardService.getSummaryByUserId(userId));
    }

    @GetMapping("/getAllUsers")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<SuccessResponseVO<Page<UserResponseVO>>> getAllUsers(
            @RequestParam int page, @RequestParam int size){
        return ResponseEntity.ok(dashboardService.getAllUsers(page, size));
    }
}
