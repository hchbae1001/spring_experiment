package bae.springexperiment.member.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        MemberCommonResponse member
) {
}
