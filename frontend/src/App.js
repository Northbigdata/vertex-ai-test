import React from "react";

function App() {
  const [prompt, setPrompt] = useState();
  const [imageUrl, setImageUrl] = useState();

  const generate = async () = {
    const res = await fetch(apigenerate, {  Cloud Run backend가 리버스 프록시도메인으로 연결되면 api로 사용
      method POST,
      headers { Content-Type applicationjson },
      body JSON.stringify({ prompt })
    });
    const json = await res.json();
     backend는 GCS에 저장된 public URL 또는 signed URL을 반환한다고 가정
    setImageUrl(json.imageUrl);
  };

  return (
    div style={{ padding 24 }}
      h1Generate Image (Vertex AI)h1
      textarea rows={4} cols={60}
        value={prompt}
        onChange={(e)=setPrompt(e.target.value)}
        placeholder=Describe the image...
      
      br
      button onClick={generate}Generatebutton

      {imageUrl && (
        div style={{ marginTop 16 }}
          h3Resulth3
          img src={imageUrl} alt=generated style={{maxWidth'100%'}}
        div
      )}
    div
  );
}

export default App;
