package com.finance.analytics.rest.v1;

import com.finance.analytics.model.dto.PermissionRequestDTO;
import com.finance.analytics.model.vo.PermissionResponseVO;
import com.finance.analytics.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.finance.analytics.model.vo.SuccessResponseVO;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<SuccessResponseVO<PermissionResponseVO>> createPermission(@RequestBody PermissionRequestDTO requestDTO) {
        return new ResponseEntity<>(SuccessResponseVO.of(HttpStatus.CREATED.value(), "Permission created successfully", permissionService.createPermission(requestDTO)), HttpStatus.CREATED);
    }

    @PutMapping("/getPermission/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<SuccessResponseVO<PermissionResponseVO>> updatePermission(@PathVariable UUID id, @RequestBody PermissionRequestDTO requestDTO) {
        return ResponseEntity.ok(SuccessResponseVO.of(200, "Permission updated successfully", permissionService.updatePermission(id, requestDTO)));
    }

    @DeleteMapping("/deletePermission/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<SuccessResponseVO<Void>> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(SuccessResponseVO.of(200, "Permission deleted successfully", null));
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<SuccessResponseVO<PermissionResponseVO>> getPermissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(SuccessResponseVO.of(200, "Permission fetched successfully", permissionService.getPermissionById(id)));
    }

    @GetMapping("/getAllPermissionRead")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<SuccessResponseVO<List<PermissionResponseVO>>> getAllPermissions() {
        return ResponseEntity.ok(SuccessResponseVO.of(200, "All permissions fetched successfully", permissionService.getAllPermissions()));
    }
}
