package bae.springexperiment.member.dto.request;

import bae.springexperiment.entity.enumerate.DeviceType;

public record LoginRequest(
        String email,
        String password,
        DeviceType deviceType
) {
}
