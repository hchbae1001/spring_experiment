package bae.springexperiment.member.dto.request;

import bae.springexperiment.entity.enumerate.DeviceType;

public record RenewTokenRequest(
        String refreshToken,
        DeviceType deviceType
) {
}
