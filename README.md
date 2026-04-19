# MyMemo — Personalized Album

A lightweight Spring Boot application for creating and managing personal memory albums. Upload photos to Cloudinary, group them into albums with dates and descriptions, and browse your memories through a simple web UI backed by a REST API and PostgreSQL.

---

## Tech stack

- Java 21
- Spring Boot 4.0.5 (WebMVC, Data JPA, Validation)
- PostgreSQL (production)
- H2 (tests)
- Cloudinary (image hosting)
- Maven (with mvnw wrapper)

---

## Quick start — Run locally

Prerequisites:
- Java 21+
- PostgreSQL 14+
- Cloudinary account (for image uploads)

1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/myMemo_Personalized_Album.git
   cd myMemo_Personalized_Album
   ```

2. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE mymemo;
   ```

3. Configure environment variables (see below). Create a `.env` at project root or set system env vars.

4. Build & run (Linux / macOS):
   ```bash
   ./mvnw spring-boot:run
   ```
   Windows:
   ```bat
   mvnw.cmd spring-boot:run
   ```

5. Open http://localhost:8080 in your browser.

To run tests (H2 in-memory DB will be used):
```bash
./mvnw test
```

---

## API (HTTP JSON)

Base URL: http://localhost:8080

Albums
- POST /api/albums — Create an album. Body: JSON album
- GET /api/albums — List albums
- GET /api/albums/{albumId} — Get album by id
- DELETE /api/albums/{albumId} — Delete album (removes Cloudinary cover image)

Memories (per album)
- POST /api/albums/{albumId}/memories — Add memory to album
- GET /api/albums/{albumId}/memories — List memories for album
- DELETE /api/albums/{albumId}/memories/{memoryId} — Delete a memory (removes Cloudinary image)

Image upload
- POST /api/cloudinary/upload — multipart form `file` to upload image; returns JSON with `url` and `publicId`.

Example album JSON:
```json
{
  "title": "Summer 2024",
  "coverImageUrl": "https://res.cloudinary.com/.../cover.jpg",
  "coverImagePublicId": "mymemo/cover_xyz",
  "eventDate": "2024-07-15"
}
```

Example memory JSON:
```json
{
  "imageUrl": "https://res.cloudinary.com/.../photo.jpg",
  "imagePublicId": "mymemo/photo_abc",
  "description": "First day at the beach!",
  "memoryDate": "2024-07-15"
}
```

---

## Environment variables

Recommended to store in a `.env` file (do not commit):

- DB_HOST (default: localhost)
- DB_PORT (default: 5432)
- DB_NAME (default: mymemo)
- DB_USERNAME (default: postgres)
- DB_PASSWORD (required)
- SERVER_PORT (default: 8080)
- CLOUDINARY_CLOUD_NAME (required)
- CLOUDINARY_API_KEY (required)
- CLOUDINARY_API_SECRET (required)

Example `.env`:
```
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mymemo
DB_USERNAME=postgres
DB_PASSWORD=your_secure_password
SERVER_PORT=8080
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

---

## Project structure (important files)

```
myMemo_Personalized_Album/
├─ src/
│  ├─ main/
│  │  ├─ java/com/venkata/mymemo/    # application code
│  │  │  ├─ controller/              # REST controllers
│  │  │  ├─ entity/                  # JPA entities: Album, Memory
│  │  │  ├─ repository/              # Spring Data JPA repositories
│  │  │  └─ service/                 # business logic + Cloudinary integration
│  │  └─ resources/
│  │     ├─ application.yaml         # app + datasource config
│  │     └─ static/                  # frontend: index.html, app.js, album.js, style.css
│  └─ test/                          # unit & integration tests (H2)
├─ pom.xml
├─ mvnw, mvnw.cmd
└─ README.md
```

---

## Notes

- The app uses Cloudinary for image hosting; ensure credentials are set to allow uploads and deletions. Images are deleted from Cloudinary when associated albums or memories are removed.
- The project uses Spring Boot auto-schema (Hibernate DDL) by default. For production, prefer managing migrations with Flyway or Liquibase.

---

## License

See the LICENSE file in the repository.
