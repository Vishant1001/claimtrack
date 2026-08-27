import { useEffect, useState } from "react";
import type { Claim } from "./types";

export function ClaimList({ refreshKey }: { refreshKey: number }) {
    const [claims, setClaims] = useState<Claim[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

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
                    </li>
                ))}
            </ul>
        </div>
    );
}