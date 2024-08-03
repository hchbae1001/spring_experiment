package bae.springexperiment.member.dto.request;

public record RenewTokenRequest(
        String refreshToken,
        String deviceType
) {
}
