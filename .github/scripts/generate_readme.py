"""
Reads key project files, builds a prompt, calls Google Gemini API,
and writes the result to README.md.
"""

import os
import json
import sys
import urllib.request
import urllib.error

# ── files to include as context ──────────────────────────────────────────────
FILES_TO_READ = [
    "pom.xml",
    "src/main/resources/application.yaml",
    "src/main/java/com/venkata/mymemo/MyMemoApplication.java",
    "src/main/java/com/venkata/mymemo/entity/Album.java",
    "src/main/java/com/venkata/mymemo/entity/Memory.java",
    "src/main/java/com/venkata/mymemo/controller/AlbumController.java",
    "src/main/java/com/venkata/mymemo/controller/MemoryController.java",
    "src/main/java/com/venkata/mymemo/service/AlbumService.java",
    "src/main/java/com/venkata/mymemo/service/MemoryService.java",
    "src/main/java/com/venkata/mymemo/repository/AlbumRepository.java",
    "src/main/java/com/venkata/mymemo/repository/MemoryRepository.java",
    "src/main/resources/static/index.html",
]

MODEL   = "gemini-2.0-flash"


def read_file_safe(path: str) -> str | None:
    """Return file contents or None if the file doesn't exist."""
    try:
        with open(path, encoding="utf-8") as fh:
            return fh.read()
    except FileNotFoundError:
        return None


def build_context() -> str:
    lines = []
    for rel_path in FILES_TO_READ:
        content = read_file_safe(rel_path)
        if content:
            lines.append(f"### {rel_path}\n```\n{content}\n```")
    return "\n\n".join(lines)


def build_prompt(context: str) -> str:
    return f"""You are a technical writer. Analyze the following Spring Boot project files and generate a
professional, well-structured README.md file in Markdown format.

The README must include the following sections (use these exact headings):
1. **Project Title & Description** — what the app does in 2-3 sentences
2. **Tech Stack** — list frameworks, language version, database, build tool
3. **Project Structure** — a brief overview of the main packages/layers
4. **Getting Started** — prerequisites, how to clone, configure env vars, build, and run
5. **API Endpoints** — a Markdown table for each resource (Albums, Memories) with Method, Path, Description
6. **Frontend** — brief note on the static UI
7. **Running Tests** — the Maven command to run tests
8. **License** — based on what is found in the project

Rules:
- Use only the information present in the files below; do not invent details.
- Use fenced code blocks for commands.
- Keep the tone professional and concise.
- Do NOT wrap the output in ```markdown fences — output raw Markdown only.

--- PROJECT FILES ---

{context}
"""


def call_gemini(token: str, prompt: str) -> str:
    url = f"https://generativelanguage.googleapis.com/v1beta/models/{MODEL}:generateContent?key={token}"
    payload = json.dumps({
        "contents": [{"parts": [{"text": prompt}]}],
        "generationConfig": {"temperature": 0.3, "maxOutputTokens": 4096},
    }).encode("utf-8")

    req = urllib.request.Request(
        url,
        data=payload,
        headers={"Content-Type": "application/json"},
        method="POST",
    )

    try:
        with urllib.request.urlopen(req, timeout=60) as resp:
            body = json.loads(resp.read().decode("utf-8"))
    except urllib.error.HTTPError as exc:
        error_body = exc.read().decode("utf-8", errors="replace")
        print(f"HTTP {exc.code} from Gemini API: {error_body}", file=sys.stderr)
        sys.exit(1)

    return body["candidates"][0]["content"]["parts"][0]["text"]


def main() -> None:
    token = os.environ.get("GEMINI_API_KEY")
    if not token:
        print("GEMINI_API_KEY environment variable is not set.", file=sys.stderr)
        sys.exit(1)

    print("Reading project files...")
    context = build_context()

    print("Calling Gemini API...")
    readme_content = call_gemini(token, build_prompt(context))

    with open("README.md", "w", encoding="utf-8") as fh:
        fh.write(readme_content.strip() + "\n")

    print("README.md written successfully.")


if __name__ == "__main__":
    main()
