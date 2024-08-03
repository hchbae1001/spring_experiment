package bae.springexperiment.member.dto.request;

public record UpdateMemberRequest(
    String email,
    String phone,
    String password,
    String nickname
) {
}
