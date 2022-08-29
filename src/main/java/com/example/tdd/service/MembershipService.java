package com.example.tdd.service;

import com.example.tdd.domain.dto.MembershipAddResponse;
import com.example.tdd.domain.dto.MembershipDetailResponse;
import com.example.tdd.domain.entity.Membership;
import com.example.tdd.domain.enums.MembershipType;
import com.example.tdd.exception.MembershipErrorResult;
import com.example.tdd.exception.MembershipException;
import com.example.tdd.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {

    private final PointService ratePointService;
    private final MembershipRepository membershipRepository;

    @Transactional
    public MembershipAddResponse addMembership(final String userId, final MembershipType membershipType, final Integer point) {
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

        return MembershipAddResponse.builder()
                .id(savedMembership.getId())
                .membershipType(savedMembership.getMembershipType())
                .build();
    }

    public List<MembershipDetailResponse> getMembershipList(final String userId) {
        final List<Membership> membershipList = membershipRepository.findAllByUserId(userId);

        return membershipList.stream()
            .map(membership -> MembershipDetailResponse.builder()
                .id(membership.getId())
                .membershipType(membership.getMembershipType())
                .point(membership.getPoint())
                .createdAt(membership.getCreatedAt())
                .build())
            .collect(Collectors.toList());
    }

    public MembershipDetailResponse getMembership(final Long membershipId, final String userId) {
        final Optional<Membership> membershipOpt = membershipRepository.findById(membershipId);
        final Membership membership = membershipOpt.orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if (membership.getUserId() != userId) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        return MembershipDetailResponse.builder()
            .id(membership.getId())
            .membershipType(membership.getMembershipType())
            .point(membership.getPoint())
            .createdAt(membership.getCreatedAt())
            .build();
    }

    @Transactional
    public void removeMembership(final Long membershipId, final String userId) {
        final Optional<Membership> membershipOpt = membershipRepository.findById(membershipId);
        final Membership membership = membershipOpt.orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if (membership.getUserId() != userId) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        membershipRepository.deleteById(membershipId);
    }

    @Transactional
    public void accumulateMembershipPoint(final Long membershipId, final String userId, final int amount) {
        final Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
        final Membership membership = optionalMembership.orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if (!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        final int additionalAmount = ratePointService.calculateAmount(amount);

        membership.setPoint(additionalAmount + membership.getPoint());
    }
}
