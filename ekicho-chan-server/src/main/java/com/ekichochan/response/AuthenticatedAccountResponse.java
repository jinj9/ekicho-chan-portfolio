package com.ekichochan.response;

import com.ekichochan.security.AuthenticatedAccountPrincipal;
import java.util.UUID;
import com.ekichochan.entity.AuthAccountRole;

public record AuthenticatedAccountResponse(UUID accountId, String email, String displayName, AuthAccountRole role) {
    public static AuthenticatedAccountResponse from(AuthenticatedAccountPrincipal principal) {
        return new AuthenticatedAccountResponse(
                principal.accountId(), principal.email(), principal.displayName(),
                principal.role() == null ? AuthAccountRole.USER : principal.role());
    }
}
