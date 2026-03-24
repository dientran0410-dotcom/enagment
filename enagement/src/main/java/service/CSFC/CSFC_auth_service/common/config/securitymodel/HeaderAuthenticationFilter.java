package service.CSFC.CSFC_auth_service.common.config.securitymodel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

@Component
public class HeaderAuthenticationFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String HEADER_USER_NAME = "X-User-Name";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Try to extract from JWT token first
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            try {
                String token = authHeader.substring(BEARER_PREFIX.length());
                parseAndSetAuthenticationFromJwt(token);
            } catch (Exception e) {
                // If JWT parsing fails, fallback to headers
                parseAndSetAuthenticationFromHeaders(request);
            }
        } else {
            // Fallback to X-User-* headers
            parseAndSetAuthenticationFromHeaders(request);
        }

        filterChain.doFilter(request, response);
    }

    private void parseAndSetAuthenticationFromJwt(String token) throws Exception {
        // JWT format: header.payload.signature
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT format");
        }

        // Decode payload (parts[1])
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        JsonNode claims = objectMapper.readTree(payload);

        String userId = claims.has("userId") ? claims.get("userId").asText() : null;
        String role = claims.has("role") ? claims.get("role").asText() : null;
        String name = claims.has("name") ? claims.get("name").asText() : null;

        if (userId != null && role != null) {
            String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

            UserPrincipal principal = new UserPrincipal(userId, name);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            token, // Store token in credentials
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(authority)
                            )
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }
    }

    private void parseAndSetAuthenticationFromHeaders(HttpServletRequest request) {
        String userId = request.getHeader(HEADER_USER_ID);
        String role = request.getHeader(HEADER_USER_ROLE);
        String name = request.getHeader(HEADER_USER_NAME);

        if (userId != null && role != null) {
            String authority =
                    role.startsWith("ROLE_") ? role : "ROLE_" + role;

            UserPrincipal principal =
                    new UserPrincipal(userId, name);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            Collections.singletonList(
                                    new SimpleGrantedAuthority(authority)
                            )
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }
    }
}