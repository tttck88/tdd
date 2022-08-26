package com.example.tdd.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MembershipErrorResult {

    DUPLICATED_MEMBERSHIP_REGISTER(HttpStatus.BAD_REQUEST, "Duplicated Membership Register Request"),
    MEMBERSHIP_NOT_FOUND(HttpStatus.NOT_FOUND, "Membership Not found"),
    NOT_MEMBERSHIP_OWNER(HttpStatus.BAD_REQUEST, "Not Membership Owner"),
    UNKNOWN_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Exception"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
