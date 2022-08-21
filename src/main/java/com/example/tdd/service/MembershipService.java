package com.example.tdd.service;

import com.example.tdd.domain.dto.MembershipResponse;
import com.example.tdd.domain.entity.Membership;
import com.example.tdd.domain.enums.MembershipType;
import com.example.tdd.exception.MembershipErrorResult;
import com.example.tdd.exception.MembershipException;
import com.example.tdd.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipResponse addMembership(final String userId, final MembershipType membershipType, final Integer point) {
        final Membership result = membershipRepository.findByUserIdAndMembershipType(userId,membershipType);
        if(result != null) {
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }

        final Membership membership = Membership.builder()
                .userId(userId)
                .point(point)
                .membershipType(membershipType)
                .build();

        final Membership savedMembership = membershipRepository.save(membership);

        return MembershipResponse.builder()
                .id(savedMembership.getId())
                .membershipType(savedMembership.getMembershipType())
                .build();
    }
}
