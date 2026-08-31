package com.vishant.claimtrack.claim;

import java.util.Set;

public enum ClaimStatus {
    SUBMITTED, IN_REVIEW, APPROVED, REJECTED;

    public boolean canTransitionTo(ClaimStatus target) {
        return switch (this) {
            case SUBMITTED -> Set.of(IN_REVIEW, REJECTED).contains(target);
            case IN_REVIEW -> Set.of(APPROVED, REJECTED).contains(target);
            case APPROVED, REJECTED -> false;
        };
    }
}