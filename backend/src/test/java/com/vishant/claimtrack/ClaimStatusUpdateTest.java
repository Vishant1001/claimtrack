package com.vishant.claimtrack;
import com.vishant.claimtrack.claim.Claim;
import com.vishant.claimtrack.claim.ClaimRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ClaimStatusUpdateTest {

    @Autowired MockMvc mockMvc;
    @Autowired
    ClaimRepository claimRepository;

    Long claimId;

    @BeforeEach
    void setUp() {
        claimRepository.deleteAll();
        Claim claim = claimRepository.save(
                new Claim("Test claim", "for testing", new BigDecimal("100.00")));
        claimId = claim.getId();
    }

    @Test
    void submittedClaimCanMoveToInReview() throws Exception {
        mockMvc.perform(put("/api/claims/{id}/status", claimId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_REVIEW\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_REVIEW"));
    }

    @Test
    void submittedClaimCannotJumpStraightToApproved() throws Exception {
        mockMvc.perform(put("/api/claims/{id}/status", claimId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APPROVED\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("invalid transition"));
    }

    @Test
    void unknownClaimReturns404() throws Exception {
        mockMvc.perform(put("/api/claims/{id}/status", 99999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_REVIEW\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void missingStatusReturns400() throws Exception {
        mockMvc.perform(put("/api/claims/{id}/status", claimId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void invalidStatusStringBlowsUpInTheController() {
        ServletException thrown = assertThrows(ServletException.class, () ->
                mockMvc.perform(put("/api/claims/{id}/status", claimId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"NONSENSE\"}")));

        assertInstanceOf(IllegalArgumentException.class, thrown.getRootCause());
    }
}
