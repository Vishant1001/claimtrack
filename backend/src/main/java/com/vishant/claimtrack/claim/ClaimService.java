package com.vishant.claimtrack.claim;

import org.springframework.stereotype.Service;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    public ClaimService(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public Claim updateStatus(Long id, String rawStatus) {
        ClaimStatus newStatus;
        try {
            newStatus = ClaimStatus.valueOf(rawStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidStatusException("unknown status: " + rawStatus);
        }
        Claim claim = claimRepository.findById(id)
                .orElseThrow(() -> new ClaimNotFoundException(id));
        if (!claim.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidTransitionException("invalid transition");
        }
        claim.setStatus(newStatus);
        return claimRepository.save(claim);
    }
}