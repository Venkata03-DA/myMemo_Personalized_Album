"""
Reads key project files, builds a lean prompt, calls OpenRouter API,
and writes the result to README.md.
"""

import os
import re
import json
import sys
import time
import urllib.request
import urllib.error

API_URL = "https://openrouter.ai/api/v1/chat/completions"
MODEL   = "mistralai/mistral-7b-instruct:free"


def read_file_safe(path: str) -> str:
    try:
        with open(path, encoding="utf-8") as fh:
            return fh.read()
    except FileNotFoundError:
        return ""


# ── extractors: pull only what the README needs ──────────────────────────────

def extract_pom_info(src: str) -> str:
    artifact = re.search(r"<artifactId>([^<]+)</artifactId>", src)
    java_ver = re.search(r"<java.version>([^<]+)</java.version>", src)
    deps = re.findall(r"<artifactId>([^<]+)</artifactId>", src)
    unique_deps = [d for d in dict.fromkeys(deps)
                   if "spring" in d.lower() or d in ("postgresql", "h2")]
    lines = []
    if artifact:
        lines.append(f"artifactId: {artifact.group(1)}")
    if java_ver:
        lines.append(f"java.version: {java_ver.group(1)}")
    lines.append("key dependencies: " + ", ".join(unique_deps))
    return "\n".join(lines)


def extract_endpoints(src: str, filename: str) -> str:
    lines = []
    for line in src.splitlines():
        s = line.strip()
        if any(s.startswith(a) for a in (
            "@RequestMapping", "@GetMapping", "@PostMapping",
            "@PutMapping", "@PatchMapping", "@DeleteMapping",
            "public ResponseEntity",
        )):
            lines.append(s)
    return f"# {filename}\n" + "\n".join(lines)


def extract_entity_fields(src: str, filename: str) -> str:
    lines = []
    for line in src.splitlines():
        s = line.strip()
        if re.match(r"(private|protected|public)\s+\S+\s+\w+", s):
            lines.append(s)
    return f"# {filename}\n" + "\n".join(lines)


# ── build a compact context string ───────────────────────────────────────────

def build_context() -> str:
    sections = []

    pom = read_file_safe("pom.xml")
    if pom:
        sections.append("## pom.xml (summary)\n" + extract_pom_info(pom))

    yaml = read_file_safe("src/main/resources/application.yaml")
    if yaml:
        sections.append("## application.yaml\n" + yaml.strip())

    for name in ("AlbumController.java", "MemoryController.java"):
        src = read_file_safe(f"src/main/java/com/venkata/mymemo/controller/{name}")
        if src:
            sections.append(extract_endpoints(src, name))

    for name in ("Album.java", "Memory.java"):
        src = read_file_safe(f"src/main/java/com/venkata/mymemo/entity/{name}")
        if src:
            sections.append(extract_entity_fields(src, name))

    return "\n\n".join(sections)


def build_prompt(context: str) -> str:
    return (
        "Generate a professional README.md for a Spring Boot project using ONLY the "
        "information below. Include these sections: Project Title & Description, "
        "Tech Stack, Project Structure, Getting Started (clone/env vars/build/run), "
        "API Endpoints (Markdown table per resource), Frontend, Running Tests, License. "
        "Use fenced code blocks for commands. Output raw Markdown only — no ```markdown wrapper.\n\n"
        + context
    )


def call_openrouter(token: str, prompt: str) -> str:
    payload = json.dumps({
        "model": MODEL,
        "messages": [
            {"role": "system", "content": "You are a professional technical writer."},
            {"role": "user",   "content": prompt},
        ],
        "temperature": 0.3,
        "max_tokens": 2048,
    }).encode("utf-8")

    req = urllib.request.Request(
        API_URL,
        data=payload,
        headers={
            "Content-Type": "application/json",
            "Authorization": f"Bearer {token}",
            "HTTP-Referer": "https://github.com",
        },
        method="POST",
    )

    max_retries = 3
    for attempt in range(1, max_retries + 1):
        try:
            with urllib.request.urlopen(req, timeout=90) as resp:
                body = json.loads(resp.read().decode("utf-8"))
            return body["choices"][0]["message"]["content"]
        except urllib.error.HTTPError as exc:
            error_body = exc.read().decode("utf-8", errors="replace")
            if exc.code == 429 and attempt < max_retries:
                wait = 65
                print(f"Rate limited (attempt {attempt}/{max_retries}). Waiting {wait}s...", file=sys.stderr)
                time.sleep(wait)
            else:
                print(f"HTTP {exc.code} from OpenRouter API: {error_body}", file=sys.stderr)
                sys.exit(1)


def main() -> None:
    token = os.environ.get("OPENROUTER_API_KEY")
    if not token:
        print("OPENROUTER_API_KEY environment variable is not set.", file=sys.stderr)
        sys.exit(1)

    print("Reading and compressing project files...")
    context = build_context()
    print(f"Context size: {len(context)} chars")

    print("Calling OpenRouter API...")
    readme_content = call_openrouter(token, build_prompt(context))

    with open("README.md", "w", encoding="utf-8") as fh:
        fh.write(readme_content.strip() + "\n")

    print("README.md written successfully.")


if __name__ == "__main__":
    main()
