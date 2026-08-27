import { useState } from "react";

interface ClaimFormProps {
    onCreated: () => void;
}

export function ClaimForm({ onCreated }: ClaimFormProps) {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [amount, setAmount] = useState("");
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        setSubmitting(true);
        setError(null);

        fetch("http://localhost:8080/api/claims", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ title, description, amount: Number(amount) }),
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error(`Server rejected the claim (${response.status})`);
                }
                return response.json();
            })
            .then(() => {
                setTitle("");
                setDescription("");
                setAmount("");
                onCreated();
            })
            .catch((err: Error) => setError(err.message))
            .finally(() => setSubmitting(false));
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>New claim</h2>
            <div>
                <input
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="Title"
                />
            </div>
            <div>
        <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Description"
        />
            </div>
            <div>
                <input
                    type="number"
                    value={amount}
                    onChange={(e) => setAmount(e.target.value)}
                    placeholder="Amount (€)"
                    step="0.01"
                />
            </div>
            <button type="submit" disabled={submitting}>
                {submitting ? "Submitting…" : "Submit claim"}
            </button>
            {error && <p>{error}</p>}
        </form>
    );
}