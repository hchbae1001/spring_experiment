package bae.springexperiment.member;

import bae.springexperiment.authtoken.AuthTokenService;
import bae.springexperiment.config.jwt.Auth;
import bae.springexperiment.config.jwt.AuthInformation;
import bae.springexperiment.config.jwt.JwtTokenProvider;
import bae.springexperiment.entity.Member;
import bae.springexperiment.entity.enumerate.Role;
import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import bae.springexperiment.member.dto.request.*;
import bae.springexperiment.member.dto.response.LoginResponse;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
import bae.springexperiment.util.BcryptUtil;
import bae.springexperiment.util.RedisKeyUtil;
import bae.springexperiment.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberFacadeV1 implements MemberFacade{

    private final MemberService memberService;
    private final AuthTokenService authTokenService;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private static final long accessExpiration = 1000 * 60 * 60 * 24 * 7; // 1 week
    private static final long memberCacheExpiration = 1000L * 60 * 60 * 24 * 7 * 5; // one month

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Attempting login for email: {}", request.email());
        Member member = memberService.findByEmail(request.email());
        BcryptUtil.matchPassword(request.password(), member.getPassword());
        AuthInformation authInformation = new AuthInformation(member);
        String accessToken = jwtTokenProvider.generateAccessToken(authInformation);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authInformation);
        String redisKey = RedisKeyUtil.generateAccessTokenKey(member.getId(), request.deviceType().toString());
        redisUtil.setRedisString(redisKey, accessToken, accessExpiration);
        log.info("Login successful for email: {}", request.email());
        return new LoginResponse(accessToken, refreshToken, new MemberCommonResponse(member));
    }

    @Override
    public LoginResponse renewToken(RenewTokenRequest request) {
        log.info("Attempting to renew token for device type: {}", request.deviceType());
        String refreshToken = request.refreshToken();
        String deviceType = request.deviceType();
        AuthInformation authInformation = jwtTokenProvider.verifyRefreshToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateAccessToken(authInformation);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authInformation);

        String redisKey = RedisKeyUtil.generateAccessTokenKey(authInformation.member_id(), deviceType);
        String accessTokenForBlackList = RedisKeyUtil.generateBlackListKey(redisKey);
        String blackListKey = RedisKeyUtil.generateBlackListKey(accessTokenForBlackList);
        redisUtil.setRedisString(blackListKey, "1", accessExpiration);
        redisUtil.setRedisString(redisKey, String.valueOf(authInformation.member_id()), accessExpiration);

        log.info("Token renewed for member ID: {}", authInformation.member_id());
        return new LoginResponse(newAccessToken, newRefreshToken, new MemberCommonResponse(findById(authInformation.member_id())));
    }

    @Override
    public void logout(LogOutRequest request) {
        Long member_id = Auth.getMemberId();
        log.info("Attempting logout for member ID: {}", member_id);
        String redisKey = RedisKeyUtil.generateAccessTokenKey(member_id, request.deviceType().toString());
        String blackListKey = RedisKeyUtil.generateBlackListKey(redisUtil.getRedisString(redisKey));
        redisUtil.setRedisString(blackListKey, String.valueOf(member_id), accessExpiration);
        redisUtil.deleteRedisCache(redisKey);
        authTokenService.deleteByMemberIdAndDeviceType(member_id, request.deviceType());
        log.info("Logout successful for member ID: {}", member_id);
    }

    @Override
    public void softDeleteById(Long member_id) {
        memberService.existsById(member_id);
        if (!Auth.getMemberId().equals(member_id)) {
            log.warn("Permission denied for soft delete. Member ID: {}", member_id);
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
        Member member = findById(member_id);
        member.setIsRemoved(true);
        memberService.save(member);
        redisUtil.deleteRedisCache(RedisKeyUtil.generateMemberKey(member_id));
        log.info("Soft delete successful for member ID: {}", member_id);
    }

    @Override
    public Member findById(Long member_id) {
        String key = RedisKeyUtil.generateMemberKey(member_id);
        if (redisUtil.isCacheExists(key)) {
            log.info("Member found in cache. Member ID: {}", member_id);
            return redisUtil.getRedisStringToObject(key, Member.class);
        }
        Member member = memberService.findById(member_id);
        redisUtil.setRedisObjectToString(key, member, memberCacheExpiration);
        log.info("Member found in database. Member ID: {}", member_id);
        return member;
    }

    @Override
    public void deleteById(Long member_id) {
        memberService.existsById(member_id);
        if (!Auth.getMemberId().equals(member_id)) {
            log.warn("Permission denied for hard delete. Member ID: {}", member_id);
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
        memberService.deleteById(member_id);
        redisUtil.deleteRedisCache(RedisKeyUtil.generateMemberKey(member_id));
        log.info("Hard delete successful for member ID: {}", member_id);
    }

    @Override
    public void save(SaveMemberRequest request) {
        log.info("Attempting to save member with email: {}", request.email());
        memberService.existsByPhone(request.phone());
        memberService.existsByNickname(request.nickname());
        memberService.existsByEmail(request.email());
        Member member = Member.builder()
                .phone(request.phone())
                .name(request.name())
                .nickname(request.nickname())
                .email(request.email())
                .role(Role.normal)
                .password(BcryptUtil.encodedPassword(request.password()))
                .isRemoved(false)
                .build();
        Member savedMember = memberService.save(member);
        String redisKey = RedisKeyUtil.generateMemberKey(savedMember.getId());
        redisUtil.setRedisObjectToString(redisKey, savedMember,memberCacheExpiration);
        log.info("Member saved. Uid: {},  Email: {}", savedMember.getId(), request.email());
    }

    @Override
    public void update(Long member_id, UpdateMemberRequest request) {
        log.info("Attempting to update member by ID: {}", member_id);
        Member existingMember = findById(member_id);
        if (!Auth.getMemberId().equals(existingMember.getId())) {
            log.warn("Permission denied for update. Member ID: {}", member_id);
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
        Optional.ofNullable(request.phone())
                .filter(phone -> !phone.isEmpty())
                .ifPresent(phone -> {
            memberService.existsByPhoneAndIdNot(phone, member_id);
            existingMember.setPhone(phone);
        });
        Optional.ofNullable(request.email())
                .filter(email -> !email.isEmpty())
                .ifPresent(email -> {
            memberService.existsByEmailAndIdNot(email, member_id);
            existingMember.setEmail(email);
        });
        Optional.ofNullable(request.nickname())
                .filter(nickname -> !nickname.isEmpty())
                .ifPresent(nickname -> {
            memberService.existsByNicknameAndIdNot(nickname, member_id);
            existingMember.setNickname(nickname);
        });
        Optional.ofNullable(request.password())
                .filter(password -> !password.isEmpty())
                .ifPresent(password -> existingMember.setPassword(BcryptUtil.encodedPassword(password)));

        String redisKey = RedisKeyUtil.generateMemberKey(member_id);
        Member updatedMember = memberService.save(existingMember);
        redisUtil.setRedisObjectToString(redisKey,updatedMember,memberCacheExpiration);
        log.info("Member updated. Member ID: {}", member_id);
    }
}
