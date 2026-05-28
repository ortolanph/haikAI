# The poet and the AI

Will the AI bubble burst? No one knows, but while the bubble is still there, let's use it.


## Simple

```
http GET localhost:9010/haikais/simple
```

```
HTTP/1.1 200
Connection: keep-alive
Content-Length: 198
Content-Type: text/plain;charset=UTF-8
Date: Thu, 28 May 2026 08:50:18 GMT
Keep-Alive: timeout=60
```

```json
{
  "content": "Mountains grin wide\nAI codes with me—cheerful\npeaks applaud each commit",
  "genre": "Playful",
  "language": "en-GB",
  "theme": "Mountains and programming with AI",
  "title": "Peaks and Commits"
}
```

![Simple Hakai Picture](simple_kaikai_picture.png)

## Complex

```
> http POST localhost:9010/haikais/complex genre="Sci-Fi" theme="Artificial Intelligence" language="en-GB"
```

```
HTTP/1.1 200
Connection: keep-alive
Content-Length: 198
Content-Type: application/json
Date: Thu, 28 May 2026 08:43:36 GMT
Keep-Alive: timeout=60
```

```json
{
    "content": "AI learns in silence\nfrom drifting data constellations—\nholds our futures steady",
    "genre": "Sci-Fi",
    "language": "en-GB",
    "theme": "Artificial intelligence",
    "title": "Constellation Quiet"
}
```

![Complex Hakai Picture](complex_hakai_picture.png)

## Actors

```
```



## Ollama 

```yaml

services:
  ollama:
    image: ollama/ollama
    container_name: ollama
    ports:
      - "11434:11434"
    volumes:
      - ollama:/root/.ollama
    mem_limit: 6g
    restart: unless-stopped
 
volumes:
  ollama:
```