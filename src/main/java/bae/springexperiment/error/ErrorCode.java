package bae.springexperiment.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    MEMBER_DUPLICATE(409, "Member is already registered."),
    MEMBER_NOT_FOUND(404, "User not found."),

    ALREADY_REGISTERED_EMAIL(409, "Email is already registered."),
    ALREADY_REGISTERED_PHONE(409, "Phone number is already registered."),
    ALREADY_REGISTERED_NICKNAME(409, "Nickname is already registered."),


    PASSWORD_DOES_NOT_MATCH(409, "Password does not match."),

    INVALID_INPUT_VALUE(400, "Invalid input value."),

    INVALID_TOKEN(401, "Invalid token."),
    EXPIRED_TOKEN(401, "Token has expired."),
    INVALID_REFRESH_TOKEN(401, "Invalid refresh token."),
    EXPIRED_REFRESH_TOKEN(401, "Refresh token has expired."),

    ALREADY_REGISTERED_DEVICE_TYPE(HttpStatus.NOT_ACCEPTABLE.value(), "Already registered deviceType"),

    INVALID_PHONE_NUMBER(400, "Invalid phone number."),
    PHONE_NOT_FOUND(404, "Phone number not found."),

    PERMISSION_DENIED(403, "Permission denied."),

    NOT_EXIST_INFO(404, "No matching information found."),
    ALREADY_REGISTER_TOKEN(409, "Token is already registered."),
    ;
    private final Integer status;
    private final String message;
}
