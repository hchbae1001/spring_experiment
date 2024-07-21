package bae.springexperiment.member.dto.request;

public record SaveMemberRequest(
        String email,
        String name,
        String nickname,
        String phone
) {
}
