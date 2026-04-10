# MyMemo — Personalized Album

![Java](https://img.shields.io/badge/Java-21-blue?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-336791?logo=postgresql&logoColor=white)
![Cloudinary](https://img.shields.io/badge/Cloudinary-Image%20CDN-3448C5?logo=cloudinary&logoColor=white)
![License](https://img.shields.io/github/license/venkata/myMemo_Personalized_Album)

A full-stack web application for creating and managing personal memory albums. Organize your life's moments into themed albums, attach photos hosted on Cloudinary, add descriptions and dates, and browse your memories through a clean browser-based UI — all backed by a RESTful Spring Boot API and PostgreSQL.

---

## Features

- 📁 Create, view, and delete **Albums** with a cover image and event date
- 🖼️ Add, view, and delete **Memories** within an album — each with an image, description, and date
- ☁️ Image uploads handled via **Cloudinary** (secure CDN; images are also deleted from Cloudinary when an album or memory is removed)
- 🌐 Vanilla HTML/CSS/JS frontend served directly by Spring Boot — no separate frontend build required
- 🗄️ PostgreSQL persistence with automatic schema management via Hibernate DDL

---

## Tech Stack

| Layer        | Technology                                        |
|--------------|---------------------------------------------------|
| Language     | Java 21                                           |
| Framework    | Spring Boot 4.0.5 (WebMVC, Data JPA, Validation)  |
| Database     | PostgreSQL (production) / H2 in-memory (tests)    |
| Image CDN    | Cloudinary (`cloudinary-http45` 1.39.0)           |
| Build Tool   | Maven 3 (Spring Boot Maven Plugin + `mvnw` wrapper) |
| Frontend     | Vanilla HTML, CSS, JavaScript (served as static assets) |

---

## Project Structure

```
myMemo_Personalized_Album/
├── src/
│   ├── main/
│   │   ├── java/com/venkata/mymemo/
│   │   │   ├── MyMemoApplication.java          # Application entry point
│   │   │   ├── controller/
│   │   │   │   ├── AlbumController.java         # Album CRUD REST endpoints
│   │   │   │   ├── MemoryController.java        # Memory CRUD REST endpoints
│   │   │   │   └── CloudinaryController.java    # Image upload endpoint
│   │   │   ├── entity/
│   │   │   │   ├── Album.java                   # Album JPA entity (albums table)
│   │   │   │   └── Memory.java                  # Memory JPA entity (memories table)
│   │   │   ├── repository/
│   │   │   │   ├── AlbumRepository.java         # Spring Data JPA repository
│   │   │   │   └── MemoryRepository.java
│   │   │   └── service/
│   │   │       ├── AlbumService.java            # Business logic + Cloudinary cleanup
│   │   │       └── MemoryService.java
│   │   └── resources/
│   │       ├── application.yaml                 # App & datasource configuration
│   │       └── static/
│   │           ├── index.html                   # Albums listing page
│   │           ├── Album.html                   # Album detail / memories page
│   │           ├── app.js                       # Albums page JS
│   │           ├── album.js                     # Album detail page JS
│   │           └── style.css                    # Global styles
│   └── test/
│       ├── java/com/venkata/mymemo/
│       │   ├── MyMemoApplicationTests.java
│       │   ├── controller/                      # MockMvc controller tests
│       │   ├── entity/                          # Entity unit tests
│       │   ├── repository/                      # Spring Data JPA repository tests
│       │   └── service/                         # Service layer unit tests
│       └── resources/
│           └── application-test.yaml            # H2 in-memory database config
├── pom.xml
├── mvnw / mvnw.cmd                              # Maven wrapper scripts
└── .env                                         # (not committed) local environment vars
```

---

## Data Model

```
albums
├── id               BIGSERIAL PRIMARY KEY
├── title            VARCHAR NOT NULL
├── cover_image_url  VARCHAR
├── cover_image_public_id  VARCHAR          ← Cloudinary public_id for deletion
├── event_date       DATE
└── created_at       TIMESTAMP NOT NULL

memories
├── id               BIGSERIAL PRIMARY KEY
├── album_id         BIGINT NOT NULL → albums(id)
├── image_url        VARCHAR
├── image_public_id  VARCHAR              ← Cloudinary public_id for deletion
├── description      VARCHAR NOT NULL
├── memory_date      DATE
└── created_at       TIMESTAMP NOT NULL
```

---

## API Endpoints

All endpoints return JSON. Base URL: `http://localhost:8080`

### Albums — `/api/albums`

| Method   | Endpoint                | Description                                     | Request Body / Notes           |
|----------|-------------------------|-------------------------------------------------|-------------------------------|
| `POST`   | `/api/albums`           | Create a new album                              | JSON `Album` object           |
| `GET`    | `/api/albums`           | List all albums                                 | —                             |
| `GET`    | `/api/albums/{albumId}` | Get a single album by ID                        | —                             |
| `DELETE` | `/api/albums/{albumId}` | Delete album (and its Cloudinary cover image)   | —                             |

**Example album JSON:**
```json
{
  "title": "Summer Vacation 2024",
  "coverImageUrl": "https://res.cloudinary.com/.../cover.jpg",
  "coverImagePublicId": "mymemo/cover_xyz",
  "eventDate": "2024-07-15"
}
```

### Memories — `/api/albums/{albumId}/memories`

| Method   | Endpoint                                        | Description                                       | Request Body / Notes            |
|----------|-------------------------------------------------|---------------------------------------------------|---------------------------------|
| `POST`   | `/api/albums/{albumId}/memories`                | Add a memory to an album                          | JSON `Memory` object            |
| `GET`    | `/api/albums/{albumId}/memories`                | List all memories in an album                     | —                               |
| `DELETE` | `/api/albums/{albumId}/memories/{memoryId}`     | Delete a memory (and its Cloudinary image)        | —                               |

**Example memory JSON:**
```json
{
  "imageUrl": "https://res.cloudinary.com/.../photo.jpg",
  "imagePublicId": "mymemo/photo_abc",
  "description": "First day at the beach!",
  "memoryDate": "2024-07-15"
}
```

### Image Upload — `/api/cloudinary`

| Method | Endpoint                 | Description                                 | Form Data            |
|--------|--------------------------|---------------------------------------------|----------------------|
| `POST` | `/api/cloudinary/upload` | Upload an image to Cloudinary               | `file` (multipart)   |

**Response:**
```json
{
  "url": "https://res.cloudinary.com/your-cloud/image/upload/v.../mymemo/filename.jpg",
  "publicId": "mymemo/filename"
}
```

---

## Environment Variables

Configure these via a `.env` file in the project root (auto-loaded by Spring) or as system/container environment variables.

| Variable                | Description                         | Default       | Required |
|-------------------------|-------------------------------------|---------------|----------|
| `DB_HOST`               | PostgreSQL host                     | `localhost`   | No       |
| `DB_PORT`               | PostgreSQL port                     | `5432`        | No       |
| `DB_NAME`               | Database name                       | `mymemo`      | No       |
| `DB_USERNAME`           | Database username                   | `postgres`    | No       |
| `DB_PASSWORD`           | Database password                   | —             | **Yes**  |
| `SERVER_PORT`           | HTTP port for the application       | `8080`        | No       |
| `CLOUDINARY_CLOUD_NAME` | Your Cloudinary cloud name          | —             | **Yes**  |
| `CLOUDINARY_API_KEY`    | Your Cloudinary API key             | —             | **Yes**  |
| `CLOUDINARY_API_SECRET` | Your Cloudinary API secret          | —             | **Yes**  |

### Example `.env` file

```properties
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

> **Note:** Never commit your `.env` file. It is already listed in `.gitignore`.

---

## Running Locally

### Prerequisites

- **Java 21+** — [Download](https://adoptium.net/)
- **Maven 3.9+** (or use the included `./mvnw` wrapper — no separate install needed)
- **PostgreSQL 14+** — [Download](https://www.postgresql.org/download/)
- **Cloudinary account** — [Sign up free](https://cloudinary.com/users/register_free)

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/myMemo_Personalized_Album.git
   cd myMemo_Personalized_Album
   ```

2. **Create the PostgreSQL database**
   ```sql
   CREATE DATABASE mymemo;
   ```

3. **Configure environment variables**

   Copy the example above into a `.env` file at the project root and fill in your values.

4. **Build and run**

   macOS / Linux:
   ```bash
   ./mvnw spring-boot:run
   ```
   Windows:
   ```bat
   mvnw.cmd spring-boot:run
   ```

5. **Open the app**

   Navigate to [http://localhost:8080](http://localhost:8080) in your browser.

---

## Running Tests

Tests run against an in-memory **H2** database — no PostgreSQL or Cloudinary credentials required.

```bash
./mvnw test
```

---

## License

This project is licensed under the terms of the [LICENSE](LICENSE) file included in this repository.
