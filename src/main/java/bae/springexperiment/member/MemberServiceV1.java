package bae.springexperiment.member;

import bae.springexperiment.authtoken.AuthTokenService;
import bae.springexperiment.config.jwt.Auth;
import bae.springexperiment.entity.Member;
import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import bae.springexperiment.member.dto.request.UpdateMemberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceV1 implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public Member findByNickName(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_INFO));
    }

    @Override
    public Member findById(long member_id) {
        return memberRepository.findById(member_id).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_INFO));
    }

    @Override
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_INFO));
    }

    @Override
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional
    public void save(Member member) {
        if (memberRepository.existsByPhone(member.getPhone())) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_PHONE);
        }
        if (memberRepository.existsByNickname(member.getNickname())){
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }
        if (memberRepository.existsByEmail(member.getEmail())){
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }

        memberRepository.save(member);
    }

    @Override
    @Transactional
    public void update(Long member_id, UpdateMemberRequest request) {
        Member existingMember = findById(member_id);
        if(!Auth.getMemberId().equals(existingMember.getId())){
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
        if (memberRepository.existsByPhoneAndIdNot(request.phone(), member_id)){
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_PHONE);
        }
        if (memberRepository.existsByNicknameAndIdNot(request.nickname(), member_id)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }
        if (memberRepository.existsByEmailAndIdNot(request.email(), member_id)){
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
        MemberMapper.updateMemberFromRequest(request,existingMember);
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
        Member member = findById(member_id);
        member.setIsRemoved(!member.getIsRemoved());
        memberRepository.save(member);
    }
}
