package com.example.tdd.controller;

import com.example.tdd.domain.dto.MembershipDetailResponse;
import com.example.tdd.domain.dto.MembershipRequest;
import com.example.tdd.domain.dto.MembershipAddResponse;
import com.example.tdd.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.example.tdd.constants.MembershipConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipAddResponse> addMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @RequestBody @Valid final MembershipRequest membershipRequest) {

        final MembershipAddResponse membershipAddResponse = membershipService.addMembership(userId, membershipRequest.getMembershipType(), membershipRequest.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED).body(membershipAddResponse);
    }

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponse>> getMembershipList(
        @RequestHeader(USER_ID_HEADER) final String userId) {

        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    @GetMapping("/api/v1/memberships/{membershipId}")
    public ResponseEntity<MembershipDetailResponse> getMembership(
        @RequestHeader(USER_ID_HEADER) final String userId
        , @PathVariable final Long membershipId
    ) {
        return ResponseEntity.ok(membershipService.getMembership(membershipId,userId));
    }

    @DeleteMapping("/api/v1/memberships/{membershipId}")
    public ResponseEntity<MembershipDetailResponse> deleteMembership(
        @RequestHeader(USER_ID_HEADER) final String userId
        , @PathVariable final Long membershipId
    ) {
        membershipService.removeMembership(membershipId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/v1/memberships/{id}/accumulate")
    public ResponseEntity<MembershipDetailResponse> accumulateMembershipPoint(
        @RequestHeader(USER_ID_HEADER) final String userId
        , @PathVariable final Long id
        , @RequestBody @Valid final MembershipRequest membershipRequest) {

        membershipService.accumulateMembershipPoint(id, userId, membershipRequest.getPoint());
        return ResponseEntity.noContent().build();
    }
}
