import { useEffect, useState } from "react";
import type { Claim, ClaimStatus } from "./types";

export function ClaimList({ refreshKey }: { refreshKey: number }) {
    const [claims, setClaims] = useState<Claim[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const NEXT_STATUSES: Record<ClaimStatus, ClaimStatus[]> = {
        SUBMITTED: ["IN_REVIEW", "REJECTED"],
        IN_REVIEW: ["APPROVED", "REJECTED"],
        APPROVED: [],
        REJECTED: [],
    };
    const updateStatus = (id: number, status: ClaimStatus) => {
        fetch(`http://localhost:8080/api/claims/${id}/status`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ status }),
        })
            .then((response) => {
                if (!response.ok) throw new Error(`Update failed (${response.status})`);
                return response.json();
            })
            .then((updated: Claim) => {
                setClaims((prev) =>
                    prev.map((c) => (c.id === updated.id ? updated : c))
                );
            })
            .catch((err: Error) => setError(err.message));
    };

    useEffect(() => {
        fetch("http://localhost:8080/api/claims")
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`Server responded with ${response.status}`);
                }
                return response.json();
            })
            .then((data: Claim[]) => {
                setClaims(data);
                setLoading(false);
            })
            .catch((err: Error) => {
                setError(err.message);
                setLoading(false);
            });
    }, [refreshKey]);

    if (loading) return <p>Loading claims…</p>;
    if (error) return <p>Something went wrong: {error}</p>;

    return (
        <div>
            <h2>Claims ({claims.length})</h2>
            <ul>
                {claims.map((claim) => (
                    <li key={claim.id}>
                        <strong>{claim.title}</strong> — {claim.amount.toFixed(2)} € — {claim.status}
                        {NEXT_STATUSES[claim.status].map((next) => (
                            <button key={next} onClick={() => updateStatus(claim.id, next)}>
                                → {next}
                            </button>
                        ))}
                    </li>
                ))}
            </ul>
        </div>
    );
}