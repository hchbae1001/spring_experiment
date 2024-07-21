package bae.springexperiment.member;

import bae.springexperiment.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceV1 implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    public void save(Member member) {

    }

    @Override
    public void update(Member member) {

    }

    @Override
    public void deleteById(long member_id) {

    }

    @Override
    public void softDeleteById(long member_id) {

    }

    @Override
    public Member findByNickName(String nickname) {
        return null;
    }

    @Override
    public Member findById(long member_id) {
        return null;
    }

    @Override
    public Member findByEmail(String email) {
        return null;
    }

    @Override
    public List<Member> findAll() {
        return List.of();
    }
}
