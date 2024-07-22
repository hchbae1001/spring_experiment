package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import bae.springexperiment.member.dto.request.LoginRequest;
import bae.springexperiment.member.dto.response.LoginResponse;
import bae.springexperiment.member.dto.response.MemberCommonResponse;
import bae.springexperiment.util.BcryptUtil;
import bae.springexperiment.config.jwt.AuthInformation;
import bae.springexperiment.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceV1 implements MemberService{
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    @Transactional
    public void save(Member member) {
        if (memberRepository.existsByPhone(member.getPhone())) {
            throw new RuntimeException("phone number already exists");
        }
        if (memberRepository.existsByNickname(member.getNickname())){
            throw new RuntimeException("nickname already exists");
        }
        if (memberRepository.existsByEmail(member.getEmail())){
            throw new RuntimeException("email already exists");
        }

        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void update(Member member) {
        Member existingMember = memberRepository.findById(member.getId()).orElseThrow(() -> new RuntimeException("Member not found"));

        if (memberRepository.existsByPhoneAndIdNot(member.getPhone(), member.getId())) {
            throw new RuntimeException("phone number already exists");
        }

        if (memberRepository.existsByNicknameAndIdNot(member.getNickname(), member.getId())) {
            throw new RuntimeException("nickname already exists");
        }

        if (memberRepository.existsByEmailAndIdNot(member.getEmail(), member.getId())){
            throw new RuntimeException("Email already exists");
        }

        existingMember.setName(member.getName());
        existingMember.setPhone(member.getPhone());
        existingMember.setNickname(member.getNickname());
        existingMember.setEmail(member.getEmail());
        memberRepository.save(existingMember);
    }

    @Override
    @Transactional
    public void deleteById(long member_id) {
        memberRepository.deleteById(member_id);
    }

    @Override
    @Transactional
    public void softDeleteById(long member_id) {
        Member member = memberRepository.findById(member_id).orElseThrow(() -> new RuntimeException("Member not found"));
        member.setIsRemoved(!member.getIsRemoved());
        memberRepository.save(member);
    }

    @Override
    public Member findByNickName(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(() -> new RuntimeException("Member not found"));
    }

    @Override
    public Member findById(long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new RuntimeException("Member not found"));
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Member not found"));
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        Member member = memberRepository.findByEmail(request.email()).orElseThrow(
                () -> new RuntimeException("member not found")
        );
        if(!BcryptUtil.matchPassword(request.password(), member.getPassword())){
            throw new CustomException(ErrorCode.PASSWORD_DOES_NOT_MATCH);
        }
        AuthInformation authInformation = new AuthInformation(member);
        String accessToken = jwtTokenProvider.generateAccessToken(authInformation);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authInformation);
        MemberCommonResponse memberCommonResponse = new MemberCommonResponse(member);
        return new LoginResponse(accessToken,refreshToken, memberCommonResponse);
    }

    @Override
    public LoginResponse renewToken(String refreshToken) {
        AuthInformation authInformation = jwtTokenProvider.verifyRefreshToken(refreshToken);
        Member member = memberRepository.findById(authInformation.member_id()).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_FOUND)
        );
        String newAccessToken = jwtTokenProvider.generateAccessToken(authInformation);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authInformation);
        MemberCommonResponse memberCommonResponse = new MemberCommonResponse(member);
        return new LoginResponse(newAccessToken, newRefreshToken, memberCommonResponse);
    }

    @Override
    public void logout() {

    }
}
