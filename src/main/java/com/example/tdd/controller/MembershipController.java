package com.example.tdd.controller;

import com.example.tdd.domain.dto.MembershipRequest;
import com.example.tdd.domain.dto.MembershipResponse;
import com.example.tdd.domain.entity.Membership;
import com.example.tdd.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.example.tdd.constants.MembershipConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipResponse> addMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Valid final MembershipRequest membershipRequest) {

        final MembershipResponse membershipResponse = membershipService.addMembership(userId, membershipRequest.getMembershipType(), membershipRequest.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED).body(membershipResponse);
    }
}
