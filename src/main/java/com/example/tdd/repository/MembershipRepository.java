package com.example.tdd.repository;

import com.example.tdd.domain.entity.Membership;
import com.example.tdd.domain.enums.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Membership findByUserIdAndMembershipType(final String userId, final MembershipType membershipType);
}
