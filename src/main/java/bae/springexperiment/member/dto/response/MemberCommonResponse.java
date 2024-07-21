package bae.springexperiment.member.dto.response;

import bae.springexperiment.entity.Member;

import java.time.LocalDateTime;

public record MemberCommonResponse(
        long member_id,
        String email,
        String name,
        String nickname,
        String phone,
        LocalDateTime created_at,
        LocalDateTime updated_at

) {
    public MemberCommonResponse(Member member) {
        this(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getPhone(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
