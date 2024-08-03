package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
import bae.springexperiment.error.CustomException;
import bae.springexperiment.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceV1 implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public Member findByNickName(String nickname) {
        log.info("Finding member by nickname: {}", nickname);
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> {
                    log.warn("Member not found with nickname: {}", nickname);
                    return new CustomException(ErrorCode.NOT_EXIST_INFO);
                });
    }

    @Override
    public Member findById(long member_id) {
        log.info("Finding member by ID: {}", member_id);
        return memberRepository.findById(member_id)
                .orElseThrow(() -> {
                    log.warn("Member not found with ID: {}", member_id);
                    return new CustomException(ErrorCode.NOT_EXIST_INFO);
                });
    }

    @Override
    public Member findByEmail(String email) {
        log.info("Finding member by email: {}", email);
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Member not found with email: {}", email);
                    return new CustomException(ErrorCode.NOT_EXIST_INFO);
                });
    }

    @Override
    public void existsById(Long member_id) {
        log.info("Checking existence of member by ID: {}", member_id);
        if (!memberRepository.existsById(member_id)) {
            log.warn("Member not found with ID: {}", member_id);
            throw new CustomException(ErrorCode.NOT_EXIST_INFO);
        }
    }

    @Override
    public void existsByPhone(String phone) {
        log.info("Checking existence of member by phone: {}", phone);
        if (memberRepository.existsByPhone(phone)) {
            log.warn("Phone number already registered: {}", phone);
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_PHONE);
        }
    }

    @Override
    public void existsByNickname(String nickname) {
        log.info("Checking existence of member by nickname: {}", nickname);
        if (memberRepository.existsByNickname(nickname)) {
            log.warn("Nickname already registered: {}", nickname);
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }
    }

    @Override
    public void existsByEmail(String email) {
        log.info("Checking existence of member by email: {}", email);
        if (memberRepository.existsByEmail(email)) {
            log.warn("Email already registered: {}", email);
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
    }

    @Override
    public void existsByPhoneAndIdNot(String phone, Long id) {
        log.info("Checking existence of phone number for member ID: {}", id);
        if (memberRepository.existsByPhoneAndIdNot(phone, id)) {
            log.warn("Phone number already registered for a different member ID: {}", id);
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_PHONE);
        }
    }

    @Override
    public void existsByNicknameAndIdNot(String nickname, Long id) {
        log.info("Checking existence of nickname for member ID: {}", id);
        if (memberRepository.existsByNicknameAndIdNot(nickname, id)) {
            log.warn("Nickname already registered for a different member ID: {}", id);
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_NICKNAME);
        }
    }

    @Override
    public void existsByEmailAndIdNot(String email, Long id) {
        log.info("Checking existence of email for member ID: {}", id);
        if (memberRepository.existsByEmailAndIdNot(email, id)) {
            log.warn("Email already registered for a different member ID: {}", id);
            throw new CustomException(ErrorCode.ALREADY_REGISTERED_EMAIL);
        }
    }

    @Override
    @Transactional
    public Member save(Member member) {
        log.info("Saving member with email: {}", member.getEmail());
        Member savedMember = memberRepository.save(member);
        log.info("Successfully saved member with email: {}", member.getEmail());
        return savedMember;
    }

    @Override
    @Transactional
    public void deleteById(long member_id) {
        log.info("Deleting member by ID: {}", member_id);
        memberRepository.deleteById(member_id);
        log.info("Successfully deleted member by ID: {}", member_id);
    }

    @Override
    public List<Member> findAll() {
        log.info("Finding all members");
        return memberRepository.findAll();
    }
}
