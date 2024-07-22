package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
import bae.springexperiment.member.dto.request.LoginRequest;
import bae.springexperiment.member.dto.response.LoginResponse;
import org.hibernate.query.Page;

import java.util.List;

public interface MemberService {

    void save(Member member);
    void update(Member member);
    void deleteById(long member_id);
    void softDeleteById(long member_id);
    Member findByNickName(String nickname);
    Member findById(long member_id);
    Member findByEmail(String email);
    List<Member> findAll();
    LoginResponse login(LoginRequest request);
    LoginResponse renewToken(String refreshToken);
    void logout();
}
