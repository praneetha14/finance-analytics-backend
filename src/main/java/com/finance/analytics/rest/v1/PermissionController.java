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

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<PermissionResponseVO> createPermission(@RequestBody PermissionRequestDTO requestDTO) {
        return new ResponseEntity<>(permissionService.createPermission(requestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<PermissionResponseVO> updatePermission(@PathVariable UUID id, @RequestBody PermissionRequestDTO requestDTO) {
        return ResponseEntity.ok(permissionService.updatePermission(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_WRITE')")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<PermissionResponseVO> getPermissionById(@PathVariable UUID id) {
        return ResponseEntity.ok(permissionService.getPermissionById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_READ')")
    public ResponseEntity<List<PermissionResponseVO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }
}
