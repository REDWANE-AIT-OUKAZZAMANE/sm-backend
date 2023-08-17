package io.xhub.smwall.constants;

import io.xhub.smwall.exceptions.ErrorMessage;

import java.util.Map;

public enum ApiClientErrorCodes {
    WEBHOOKS_SUBSCRIPTION_FAILED(1, "webhooks.subscription.failed"),
    ANNOUNCEMENT_NOT_FOUND(2, "announcement.not.found"),
    PROFILE_PICTURE_EXTRACTION_FAILED(3, "profile.picture.extraction.failed"),
    WALL_HEADER_NOT_FOUND(4, "wall.header.not.found"),
    USER_NOT_ACTIVATED(5, "user.not.activated"),
    JWT_NOT_VALID(6, "jwt.not.valid"),
    USER_NOT_FOUND(7, "user.not.found"),
    INVALID_COMMAND_ARGS(8, "command.invalid.args"),
    WALL_FOOTER_NOT_FOUND(9, "wall.Footer.not.found"),
    WRONG_CREDENTIALS(10, "command.login.wrong.credentials"),
    MEDIA_NOT_FOUND(11, "media.not.found"),
    WALL_SETTING_NOT_FOUND(12, "wall.setting.not.found"),
    ANNOUNCEMENT_ALREADY_EXISTS(13, "announcement.already.exists"),
    MEDIA_HIDDEN(14, "media.hidden"),
    USER_ALREADY_EXISTS(15,"user.already.exists");


    private final Integer code;
    private final String messageKey;

    ApiClientErrorCodes(Integer code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public ErrorMessage getErrorMessage(Map<String, Object> payload, Object... params) {
        return ErrorMessage.builder()
                .code(code)
                .key(messageKey)
                .messageKeyParameters(params)
                .payload(payload)
                .build();
    }

    public ErrorMessage getErrorMessage(Object... params) {
        return ErrorMessage.builder()
                .code(code)
                .key(messageKey)
                .messageKeyParameters(params)
                .build();
    }
}