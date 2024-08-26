package bae.springexperiment.config.jwt;

import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import bae.springexperiment.util.RedisKeyUtil;
import bae.springexperiment.util.RedisUtil;
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
    private final RedisUtil redisUtil;
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // this apis doesn't have to check accessToken
        String requestURI = httpServletRequest.getRequestURI();
        boolean isPermittedPath =
                requestURI.startsWith("/swagger-ui/") ||
                        requestURI.startsWith("/v3/api-docs/") ||
                        requestURI.equals("/api/v1/member/login") ||
                        requestURI.equals("/api/v1/member/renew") ||
                        requestURI.equals("/api/v1/member")
                ;


        if (isPermittedPath) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = resolveToken(httpServletRequest);
            // check if accessToken exist in redis
            String blackListKey = RedisKeyUtil.generateBlackListKey(token);
            if (redisUtil.isCacheExists(blackListKey)) {
                handleException(httpServletResponse, ErrorCode.INVALID_TOKEN);
                return;
            }


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
