//package service.CSFC.CSFC_auth_service.common.config.securitymodel;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//
//@Component
//public class HeaderAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final String HEADER_USER_ID = "X-User-Id";
//    private static final String HEADER_USER_ROLE = "X-User-Role";
//    private static final String HEADER_USER_NAME = "X-User-Name";
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String userId = request.getHeader(HEADER_USER_ID);
//        String role = request.getHeader(HEADER_USER_ROLE);
//        String name = request.getHeader(HEADER_USER_NAME);
//
//        if (userId != null && role != null) {
//
//            String authority =
//                    role.startsWith("ROLE_") ? role : "ROLE_" + role;
//
//            UserPrincipal principal =
//                    new UserPrincipal(userId, name);
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(
//                            principal,
//                            null,
//                            Collections.singletonList(
//                                    new SimpleGrantedAuthority(authority)
//                            )
//                    );
//
//            SecurityContextHolder
//                    .getContext()
//                    .setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}