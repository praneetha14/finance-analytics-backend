package com.finance.analytics.rest.v1;

import com.finance.analytics.model.dto.RoleRequestDTO;
import com.finance.analytics.model.vo.RoleResponseVO;
import com.finance.analytics.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import com.finance.analytics.model.vo.SuccessResponseVO;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<SuccessResponseVO<RoleResponseVO>> createRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        return new ResponseEntity<>(SuccessResponseVO.of(HttpStatus.CREATED.value(), "Role created successfully", roleService.createRole(roleRequestDTO)), HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<SuccessResponseVO<RoleResponseVO>> updateRole(@PathVariable UUID id, @RequestBody RoleRequestDTO roleRequestDTO) {
        return ResponseEntity.ok(SuccessResponseVO.of(200, "Role updated successfully", roleService.updateRole(id, roleRequestDTO)));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<SuccessResponseVO<Void>> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(SuccessResponseVO.of(200, "Role deleted successfully", null));
    }

    @GetMapping("/getRole/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<SuccessResponseVO<RoleResponseVO>> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(SuccessResponseVO.of(200, "Role fetched successfully", roleService.getRoleById(id)));
    }

    @GetMapping("/getAllRoles")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<SuccessResponseVO<List<RoleResponseVO>>> getAllRoles() {
        return ResponseEntity.ok(SuccessResponseVO.of(200, "All roles fetched successfully", roleService.getAllRoles()));
    }
}
