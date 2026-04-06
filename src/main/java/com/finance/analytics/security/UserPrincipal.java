package com.finance.analytics.security;

import com.finance.analytics.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserPrincipal extends User {

    private final UUID id;

    public UserPrincipal(UserEntity user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), user.getIsActive(), true, true, true, authorities);
        this.id = user.getId();
    }
}
