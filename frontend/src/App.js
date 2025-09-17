import React, { useState } from "react";

const BACKEND_URL = "https://backend-service-268824299811.asia-northeast3.run.app";

function App() {
  const [prompt, setPrompt] = useState("");
  const [imageUrl, setImageUrl] = useState("");

  const generate = async () => {
    try {
      const res = await fetch(`${BACKEND_URL}/generate`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ prompt }),  // ✅ prompt 값을 JSON으로 전송
      });

      if (!res.ok) {
        console.error("API error:", res.status, await res.text());
        return;
      }

      const json = await res.json();
      setImageUrl(json.imageUrl);
    } catch (err) {
      console.error("Error generating image:", err);
    }
  };

  return (
    <div style={{ padding: 24 }}>
      <h1>Generate Image (Vertex AI)</h1>

      <textarea
        rows={4}
        cols={60}
        value={prompt}
        onChange={(e) => setPrompt(e.target.value)}
        placeholder="Describe the image..."
      />

      <br />
      <button onClick={generate}>Generate</button>

      {imageUrl && (
        <div style={{ marginTop: 16 }}>
          <h3>Result</h3>
          <img src={imageUrl} alt="generated" style={{ maxWidth: "100%" }} />
        </div>
      )}
    </div>
  );
}

export default App;
