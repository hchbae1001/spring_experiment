package bae.springexperiment.member;

import bae.springexperiment.authtoken.AuthTokenService;
import bae.springexperiment.config.jwt.Auth;
import bae.springexperiment.config.jwt.AuthInformation;
import bae.springexperiment.config.jwt.JwtTokenProvider;
import bae.springexperiment.entity.Member;
import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import bae.springexperiment.member.dto.request.LogOutRequest;
import bae.springexperiment.member.dto.request.LoginRequest;
import bae.springexperiment.member.dto.response.LoginResponse;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
import bae.springexperiment.util.BcryptUtil;
import bae.springexperiment.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberFacade {

    private final MemberService memberService;
    private final AuthTokenService authTokenService;
    private final RedisUtil redisUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private static final long accessExpiration = 1000 * 60 * 60 * 24 * 7; // 1 week

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Member member = memberService.findByEmail(request.email());
        if(!BcryptUtil.matchPassword(request.password(), member.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_DOES_NOT_MATCH);
        }
        AuthInformation authInformation = new AuthInformation(member);
        String accessToken = jwtTokenProvider.generateAccessToken(authInformation);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authInformation);
        String redisKey = "accessToken:" + member.getId() + "deviceType:" + request.deviceType().toString();
        redisUtil.setRedisString(redisKey,accessToken, accessExpiration);
        return new LoginResponse(accessToken,refreshToken, new MemberCommonResponse(member));
    }

    public LoginResponse renewToken(String refreshToken) {
        AuthInformation authInformation = jwtTokenProvider.verifyRefreshToken(refreshToken);
        Member member = memberService.findById(authInformation.member_id());
        String newAccessToken = jwtTokenProvider.generateAccessToken(authInformation);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authInformation);
        MemberCommonResponse memberCommonResponse = new MemberCommonResponse(member);
        return new LoginResponse(newAccessToken, newRefreshToken, memberCommonResponse);
    }

    public void logout(LogOutRequest request) {
        Long member_id = Auth.getMemberId();
        String redisKey = "accessToken:" + member_id + "deviceType:" + request.deviceType().toString();
        String blacklist_token = redisUtil.getRedisString(redisKey);
        redisUtil.setRedisString("blacklist:"+blacklist_token,String.valueOf(member_id),accessExpiration);
        redisUtil.deleteRedisCache(redisKey);
        authTokenService.deleteByMemberIdAndDeviceType(member_id,request.deviceType());
    }
}
