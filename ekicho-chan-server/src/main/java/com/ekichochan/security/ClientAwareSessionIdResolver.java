package com.ekichochan.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

public class ClientAwareSessionIdResolver implements HttpSessionIdResolver {
    private final HttpSessionIdResolver android = HeaderHttpSessionIdResolver.xAuthToken();
    private final HttpSessionIdResolver web = new CookieHttpSessionIdResolver();

    private HttpSessionIdResolver delegate(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/v1/web/") ? web : android;
    }

    @Override public List<String> resolveSessionIds(HttpServletRequest request) {
        return delegate(request).resolveSessionIds(request);
    }
    @Override public void setSessionId(HttpServletRequest request, HttpServletResponse response, String id) {
        delegate(request).setSessionId(request, response, id);
    }
    @Override public void expireSession(HttpServletRequest request, HttpServletResponse response) {
        delegate(request).expireSession(request, response);
    }
}
