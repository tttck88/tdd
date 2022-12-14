package com.example.tdd.repository;

import com.example.tdd.domain.entity.Membership;
import com.example.tdd.domain.enums.MembershipType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    public void MembershipRepository가Null이아님() {
        assertThat(membershipRepository).isNotNull();
    }

    @Test
    @DisplayName("설명")
    void 멤버십등록() {
        // given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        // when
        final Membership result = membershipRepository.save(membership);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getUserId()).isEqualTo("userId");
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(result.getPoint()).isEqualTo(10000);
    }

    @Test
    @DisplayName("설명")
    void 멤버십이존재하는지테스트() {
        // given
        final Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        // when
        membershipRepository.save(membership);
        final Membership findResult = membershipRepository.findByUserIdAndMembershipType("userId", MembershipType.NAVER);

        // then
        assertThat(findResult).isNotNull();
        assertThat(findResult.getId()).isNotNull();
        assertThat(findResult.getUserId()).isEqualTo("userId");
        assertThat(findResult.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(findResult.getPoint()).isEqualTo(10000);
    }
    
    @Test
    @DisplayName("")
    public void 멤버십조회_사이즈가0() {
        // given
        
        // when
        List<Membership> result = membershipRepository.findAllByUserId("userId");
        
        // then
        assertThat(result.size()).isEqualTo(0);
    }
    
    @Test
    public void 멤버십조회_사이즈가2() {
        // given
        final Membership naverMembership = Membership.builder()
            .userId("userId")
            .membershipType(MembershipType.NAVER)
            .point(10000)
            .build();

        final Membership kakaoMembership = Membership.builder()
            .userId("userId")
            .membershipType(MembershipType.KAKAO)
            .point(10000)
            .build();

        membershipRepository.save(naverMembership);
        membershipRepository.save(kakaoMembership);

        // when
        List<Membership> result = membershipRepository.findAllByUserId("userId");

        // then
        assertThat(result.size()).isEqualTo(2);
    }
    
    @Test
    public void 멤버십추가후삭제() {
        // given
        final Membership membership = Membership.builder()
            .userId("userId")
            .membershipType(MembershipType.NAVER)
            .point(10000)
            .build();

        final Membership resultMembership = membershipRepository.save(membership);
        
        // when
        membershipRepository.deleteById(resultMembership.getId());
        
        // then
    }
}
















