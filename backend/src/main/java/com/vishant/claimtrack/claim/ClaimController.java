package com.vishant.claimtrack.claim;

import jakarta.validation.Valid;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimRepository claimRepository;

    public ClaimController(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @GetMapping
    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }
    @Bean
    CommandLineRunner seedData(ClaimRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Claim("Water damage - kitchen",
                        "Dishwasher leak damaged kitchen flooring", new BigDecimal("2400.00")));
                repository.save(new Claim("Car accident - rear bumper",
                        "Rear-ended at traffic light on B85", new BigDecimal("1850.50")));
                repository.save(new Claim("Bike theft",
                        "E-bike stolen from train station", new BigDecimal("3200.00")));
            }
        };
    }
    @PostMapping
    public ResponseEntity<Claim> createClaim(@Valid @RequestBody CreateClaimRequest request) {
        Claim claim = new Claim(request.title(), request.description(), request.amount());
        Claim saved = claimRepository.save(claim);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        if (body.get("status") == null || body.get("status").isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "status is required"));
        }
        ClaimStatus newStatus = ClaimStatus.valueOf(body.get("status"));
        var claimOpt = claimRepository.findById(id);
        if (claimOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Claim claim = claimOpt.get();
        ClaimStatus current = claim.getStatus();
        if (current == ClaimStatus.APPROVED || current == ClaimStatus.REJECTED) {
            return ResponseEntity.badRequest().body(Map.of("message", "claim is final"));
        }
        if (current == ClaimStatus.SUBMITTED) {
            if (newStatus != ClaimStatus.IN_REVIEW && newStatus != ClaimStatus.REJECTED) {
                return ResponseEntity.badRequest().body(Map.of("message", "invalid transition"));
            }
        }
        if (current == ClaimStatus.IN_REVIEW) {
            if (newStatus != ClaimStatus.APPROVED && newStatus != ClaimStatus.REJECTED) {
                return ResponseEntity.badRequest().body(Map.of("message", "invalid transition"));
            }
        }
        claim.setStatus(newStatus);
        claimRepository.save(claim);
        return ResponseEntity.ok(claim);
    }
}
