package com.ekichochan.service;

import com.ekichochan.entity.AuthAccountEntity;
import com.ekichochan.entity.AuthAccountStatus;
import com.ekichochan.exception.AuthLoginException;
import com.ekichochan.repositories.AuthAccountRepository;
import com.ekichochan.repositories.ObtainedImageDataRepository;
import java.util.UUID;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountDeletionService {
    private final AuthAccountRepository accounts;
    private final ObtainedImageDataRepository obtainedImages;
    private final PasswordEncoder passwordEncoder;
    private final SessionRevocationService sessions;

    public AccountDeletionService(AuthAccountRepository accounts,
            ObtainedImageDataRepository obtainedImages,
            PasswordEncoder passwordEncoder,
            SessionRevocationService sessions) {
        this.accounts = accounts;
        this.obtainedImages = obtainedImages;
        this.passwordEncoder = passwordEncoder;
        this.sessions = sessions;
    }

    @Transactional
    public void delete(UUID accountId, String currentPassword) {
        AuthAccountEntity account = accounts.findByIdForUpdate(accountId)
                .orElseThrow(AuthLoginException::new);
        if (account.getStatus() != AuthAccountStatus.ACTIVE
                || !passwordEncoder.matches(currentPassword, account.getPasswordHash())) {
            throw new AuthLoginException();
        }

        // Web版の旧形式取得履歴には外部キーがないため、アカウントより先に明示削除する。
        obtainedImages.deleteByUserId(accountId.toString());
        sessions.revokeAll(accountId);
        // auth_action_token と account_station_collection は DB の ON DELETE CASCADE で削除される。
        accounts.delete(account);
        accounts.flush();
    }
}
