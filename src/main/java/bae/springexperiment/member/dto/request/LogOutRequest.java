package bae.springexperiment.member.dto.request;

import bae.springexperiment.entity.enumerate.DeviceType;

public record LogOutRequest(
        DeviceType deviceType
) {
}
