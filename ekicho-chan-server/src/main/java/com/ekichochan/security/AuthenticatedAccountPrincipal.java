package com.ekichochan.security;

import java.io.Serializable;
import java.util.UUID;
import java.security.Principal;
import com.ekichochan.entity.AuthAccountRole;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record AuthenticatedAccountPrincipal(
        UUID accountId, String email, String displayName, AuthAccountRole role) implements Principal, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public String getName() {
        return accountId.toString();
    }

    public List<GrantedAuthority> authorities() {
        AuthAccountRole effectiveRole = role == null ? AuthAccountRole.USER : role;
        return List.of(new SimpleGrantedAuthority("ROLE_" + effectiveRole.name()));
    }
}
