package com.ekichochan.controller;

import com.ekichochan.entity.AuthAccountEntity;
import com.ekichochan.request.DisplayNameUpdateRequest;
import com.ekichochan.request.PasswordChangeRequest;
import com.ekichochan.request.AccountDeletionRequest;
import com.ekichochan.response.AccountProfileResponse;
import com.ekichochan.security.AuthenticatedAccountPrincipal;
import com.ekichochan.service.AccountProfileService;
import com.ekichochan.service.AuthPasswordService;
import com.ekichochan.service.AccountDeletionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/web/account")
public class WebAccountController {
    private final AccountProfileService profiles;
    private final AuthPasswordService passwords;
    private final SecurityContextRepository securityContexts;
    private final AccountDeletionService accountDeletion;

    public WebAccountController(AccountProfileService profiles, AuthPasswordService passwords,
            SecurityContextRepository securityContexts, AccountDeletionService accountDeletion) {
        this.profiles = profiles;
        this.passwords = passwords;
        this.securityContexts = securityContexts;
        this.accountDeletion = accountDeletion;
    }

    @GetMapping
    public AccountProfileResponse profile(@AuthenticationPrincipal AuthenticatedAccountPrincipal principal) {
        return AccountProfileResponse.from(profiles.get(principal.accountId()));
    }

    @PutMapping("/display-name")
    public AccountProfileResponse changeDisplayName(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @Valid @RequestBody DisplayNameUpdateRequest request,
            HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        AuthAccountEntity account = profiles.changeDisplayName(principal.accountId(), request.displayName());
        AuthenticatedAccountPrincipal updated = new AuthenticatedAccountPrincipal(
                account.getId(), account.getEmail(), account.getDisplayName(), account.getRole());
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(updated, null, updated.authorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContexts.saveContext(context, servletRequest, servletResponse);
        return AccountProfileResponse.from(account);
    }

    @PostMapping("/password/change")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @Valid @RequestBody PasswordChangeRequest request,
            HttpServletRequest servletRequest) {
        passwords.changePassword(principal.accountId(), request.currentPassword(), request.newPassword());
        HttpSession session = servletRequest.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal AuthenticatedAccountPrincipal principal,
            @Valid @RequestBody AccountDeletionRequest request,
            HttpServletRequest servletRequest) {
        accountDeletion.delete(principal.accountId(), request.currentPassword());
        HttpSession session = servletRequest.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}
