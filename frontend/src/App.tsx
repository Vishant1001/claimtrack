import { useState } from "react";
import { ClaimForm } from "./ClaimForm";
import { ClaimList } from "./ClaimList";

function App() {
    const [refreshKey, setRefreshKey] = useState(0);

    return (
        <div>
            <h1>ClaimTrack</h1>
            <ClaimForm onCreated={() => setRefreshKey((k) => k + 1)} />
            <ClaimList refreshKey={refreshKey} />
        </div>
    );
}

export default App;