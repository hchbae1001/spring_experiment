package bae.springexperiment.member.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
