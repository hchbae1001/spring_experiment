package bae.springexperiment.config.jwt;

import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String token = resolveToken(httpServletRequest);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                AuthInformation authInformation = jwtTokenProvider.verifyAccessToken(token);
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + authInformation.role().name()));
                Authentication auth = new UsernamePasswordAuthenticationToken(authInformation, "", authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (CustomException e) {
            handleException((HttpServletResponse) response, e.getErrorCode());
            return;
        } catch (Exception e) {
            handleException((HttpServletResponse) response, ErrorCode.INVALID_TOKEN);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void handleException(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        SecurityContextHolder.clearContext();
        response.setStatus(errorCode.getStatus());
        response.setContentType("application/json");
        response.getWriter().write("{\"message\": \"" + errorCode.getMessage() + "\"}");
    }
}
