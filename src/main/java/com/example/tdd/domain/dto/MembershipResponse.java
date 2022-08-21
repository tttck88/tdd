package com.example.tdd.domain.dto;

import com.example.tdd.domain.enums.MembershipType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class MembershipResponse {

    private final Long id;
    private final MembershipType membershipType;
}
