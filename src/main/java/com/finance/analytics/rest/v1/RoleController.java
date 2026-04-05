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

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<RoleResponseVO> createRole(@RequestBody RoleRequestDTO roleRequestDTO) {
        return new ResponseEntity<>(roleService.createRole(roleRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<RoleResponseVO> updateRole(@PathVariable UUID id, @RequestBody RoleRequestDTO roleRequestDTO) {
        return ResponseEntity.ok(roleService.updateRole(id, roleRequestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_WRITE')")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<RoleResponseVO> getRoleById(@PathVariable UUID id) {
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_READ')")
    public ResponseEntity<List<RoleResponseVO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
