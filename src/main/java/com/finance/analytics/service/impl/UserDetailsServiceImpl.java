package com.finance.analytics.service.impl;

import com.finance.analytics.entity.RolePermissionsEntity;
import com.finance.analytics.entity.UserEntity;
import com.finance.analytics.entity.UserRolesEntity;
import com.finance.analytics.repository.RolePermissionRepository;
import com.finance.analytics.repository.UserRepository;
import com.finance.analytics.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        if (!Boolean.TRUE.equals(userEntity.getIsActive())) {
            throw new UsernameNotFoundException("User is inactive");
        }

        List<UserRolesEntity> userRoles = userRoleRepository.findByUser(userEntity);
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        for (UserRolesEntity ur : userRoles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + ur.getRole().getRoleName().name()));
            
            List<RolePermissionsEntity> rolePermissions = rolePermissionRepository.findByRole(ur.getRole());
            for (RolePermissionsEntity rp : rolePermissions) {
                authorities.add(new SimpleGrantedAuthority(rp.getPermission().getName()));
            }
        }

        return new User(userEntity.getEmail(), userEntity.getPassword(), authorities);
    }
}
