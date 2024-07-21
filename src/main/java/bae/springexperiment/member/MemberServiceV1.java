package bae.springexperiment.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberServiceV1 implements MemberService{
    private final MemberRepository memberRepository;

}
