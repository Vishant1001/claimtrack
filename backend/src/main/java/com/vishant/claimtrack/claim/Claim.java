package com.vishant.claimtrack.claim;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private ClaimStatus status;

    private BigDecimal amount;
    private LocalDateTime createdAt;

    protected Claim() {
        // JPA needs a no-args constructor
    }

    public Claim(String title, String description, BigDecimal amount) {
        this.title = title;
        this.description = description;
        this.amount = amount;
        this.status = ClaimStatus.SUBMITTED;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public ClaimStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(ClaimStatus status) { this.status = status; }
}
