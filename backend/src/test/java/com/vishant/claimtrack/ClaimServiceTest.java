package com.vishant.claimtrack;

import com.vishant.claimtrack.claim.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClaimServiceTest {

    ClaimRepository claimRepository;
    ClaimService claimService;

    @BeforeEach
    void setUp() {
        claimRepository = mock(ClaimRepository.class);
        claimService = new ClaimService(claimRepository);
    }

    private Claim claimWithStatus(ClaimStatus status) {
        Claim claim = new Claim("Test", "desc", new BigDecimal("100.00"));
        claim.setStatus(status);
        return claim;
    }

    @Test
    void movesSubmittedClaimToInReview() {
        Claim claim = claimWithStatus(ClaimStatus.SUBMITTED);
        when(claimRepository.findById(1L)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(inv -> inv.getArgument(0));

        Claim result = claimService.updateStatus(1L, "IN_REVIEW");

        assertThat(result.getStatus()).isEqualTo(ClaimStatus.IN_REVIEW);
        verify(claimRepository).save(claim);
    }

    @Test
    void rejectsJumpFromSubmittedToApproved() {
        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claimWithStatus(ClaimStatus.SUBMITTED)));

        assertThatThrownBy(() -> claimService.updateStatus(1L, "APPROVED"))
                .isInstanceOf(InvalidTransitionException.class);

        verify(claimRepository, never()).save(any());
    }

    @Test
    void rejectsAnyChangeToFinalClaim() {
        when(claimRepository.findById(1L))
                .thenReturn(Optional.of(claimWithStatus(ClaimStatus.APPROVED)));

        assertThatThrownBy(() -> claimService.updateStatus(1L, "IN_REVIEW"))
                .isInstanceOf(InvalidTransitionException.class);
    }

    @Test
    void throwsForUnknownClaim() {
        when(claimRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> claimService.updateStatus(99L, "IN_REVIEW"))
                .isInstanceOf(ClaimNotFoundException.class);
    }

    @Test
    void throwsForGarbageStatusString() {
        assertThatThrownBy(() -> claimService.updateStatus(1L, "NONSENSE"))
                .isInstanceOf(InvalidStatusException.class);
    }
}
