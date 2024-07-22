package bae.springexperiment.config.jwt;

import bae.springexperiment.entity.Member;
import bae.springexperiment.entity.enumerate.Role;
import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import bae.springexperiment.member.MemberRepository;
import bae.springexperiment.member.dto.response.LoginResponse;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key accesskey;
    private final Key refreshkey;
    private final MemberRepository memberRepository;

    private static final long accessExpiration = 1000 * 60 * 60 * 24 * 7; // 1 week
    private static final long refreshExpiration = 1000L * 60 * 60 * 24 * 30; // 30 days

    public JwtTokenProvider(@Value("${jwt.accessSecret}") String accessSecret, @Value("${jwt.refreshSecret}") String refreshSecret, MemberRepository memberRepository) {
        this.accesskey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshkey = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
        this.memberRepository = memberRepository;
    }

    public String generateAccessToken(AuthInformation authInformation) {
        return generateToken(authInformation, accessExpiration, accesskey);
    }

    public String generateRefreshToken(AuthInformation authInformation) {
        return generateToken(authInformation, refreshExpiration, refreshkey);
    }

    private String generateToken(AuthInformation authInformation, long expiration, Key key) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("member_id", authInformation.member_id())
                .claim("email", authInformation.email())
                .claim("role", authInformation.role().name().toLowerCase())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public LoginResponse renewTokens(String refreshToken) {
        Member member = memberRepository.findById(verifyRefreshToken(refreshToken).member_id()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        MemberCommonResponse memberCommonResponse = new MemberCommonResponse(member);
        AuthInformation authInformation = new AuthInformation(member);
        String newAccessToken = generateAccessToken(authInformation);
        String newRefreshToken = generateRefreshToken(authInformation);
        return new LoginResponse(newAccessToken, newRefreshToken, memberCommonResponse);
    }

    public AuthInformation verifyAccessToken(String accessToken) {
        return getAuthInformation(accessToken, accesskey);
    }

    public AuthInformation verifyRefreshToken(String refreshToken) {
        return getAuthInformation(refreshToken, refreshkey);
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(accesskey).build().parseClaimsJws(token);
            boolean isTokenValid = !claimsJws.getBody().getExpiration().before(new Date());
            if (!isTokenValid) {
                log.error("Token expired");
            }
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private AuthInformation getAuthInformation(String token, Key key) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            Claims claims = claimsJws.getBody();

            Long member_id = claims.get("member_id", Long.class);
            if (member_id == null) {
                log.error("member_id claim is missing in the token");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            String roleString = claims.get("role", String.class);
            if (roleString == null) {
                log.error("role claim is missing in the token");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            Role role;
            try {
                role = Role.valueOf(roleString.toLowerCase());
            } catch (IllegalArgumentException e) {
                log.error("Invalid role value in the token: {}", roleString);
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            String email = claims.get("email", String.class);
            if (email == null) {
                log.error("email claim is missing in the token");
                throw new CustomException(ErrorCode.INVALID_TOKEN);
            }

            return new AuthInformation(member_id, email, role);

        } catch (ExpiredJwtException e) {
            log.error("Token expired: {}", e.getMessage());
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
}
