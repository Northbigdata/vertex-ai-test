import React, { useState } from "react";

function App() {
  const [prompt, setPrompt] = useState("");
  const [imageUrl, setImageUrl] = useState("");

  // 이미지 생성 요청 함수
  const generate = async () => {
    try {
      const res = await fetch("/api/generate", {
        // ✅ Cloud Run backend가 /api/generate 로 연결된다고 가정
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ prompt }),
      });

      const json = await res.json();
      // ✅ backend는 GCS public URL 또는 Signed URL 반환한다고 가정
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
