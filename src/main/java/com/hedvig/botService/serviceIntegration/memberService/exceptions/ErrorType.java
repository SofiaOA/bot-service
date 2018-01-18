package com.hedvig.botService.serviceIntegration.memberService.exceptions;

public enum ErrorType {
    INVALID_PARAMETERS,
    ACCESS_DENIED_RP,
    CLIENT_ERR,
    CERTIFICATE_ERR,
    RETRY,
    INTERNAL_ERROR,
    ALREADY_COLLECTED,
    EXPIRED_TRANSACTION,
    ALREADY_IN_PROGRESS,
    USER_CANCEL,
    CANCELLED,
    REQ_PRECOND,
    REQ_ERROR,
    REQ_BLOCKED,
    START_FAILED;
}
