package com.ekichochan.service;

import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionRevocationService {
    private final JdbcTemplate jdbc;

    public SessionRevocationService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int revokeAll(UUID accountId) {
        return jdbc.update("DELETE FROM spring_session WHERE principal_name = ?", accountId.toString());
    }
}
