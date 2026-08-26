export type ClaimStatus = "SUBMITTED" | "IN_REVIEW" | "APPROVED" | "REJECTED";

export interface Claim {
    id: number;
    title: string;
    description: string;
    status: ClaimStatus;
    amount: number;
    createdAt: string;
}