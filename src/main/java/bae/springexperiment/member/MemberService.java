package bae.springexperiment.member;

import bae.springexperiment.entity.Member;

import java.util.List;

public interface MemberService {

    Member save(Member member);
    void deleteById(long member_id);
    Member findByNickName(String nickname);
    Member findById(long member_id);
    Member findByEmail(String email);
    List<Member> findAll();
    void existsById(Long member_id);
    void existsByPhone(String phone);
    void existsByNickname(String nickname);
    void existsByEmail(String email);
    void existsByPhoneAndIdNot(String phone, Long id);
    void existsByNicknameAndIdNot(String nickname, Long id);
    void existsByEmailAndIdNot(String email, Long id);
}
