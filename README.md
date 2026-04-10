# MyMemo — Personalized Album

A full-stack web application for creating and managing personal memory albums. Organize your life's moments into themed albums, attach photos (stored on Cloudinary), add descriptions, and browse your memories through a clean browser-based UI.

---

## Features

- Create, view, and delete **Albums** with a cover image and event date
- Add, view, and delete **Memories** within an album — each with an image, description, and date
- Image uploads handled via **Cloudinary** (secure CDN storage)
- Vanilla HTML/CSS/JS frontend served directly by Spring Boot
- PostgreSQL persistence with automatic schema management

---

## Tech Stack

| Layer       | Technology                                      |
|-------------|-------------------------------------------------|
| Language    | Java 21                                         |
| Framework   | Spring Boot 4.0.5 (WebMVC, Data JPA, Validation)|
| Database    | PostgreSQL (production) / H2 (tests)            |
| Image CDN   | Cloudinary (`cloudinary-http45` 1.39.0)         |
| Build Tool  | Maven (Spring Boot Maven Plugin)                |
| Frontend    | Vanilla HTML, CSS, JavaScript                   |

---

## Project Structure

```
myMemo/
├── src/
│   ├── main/
│   │   ├── java/com/venkata/mymemo/
│   │   │   ├── MyMemoApplication.java          # Application entry point
│   │   │   ├── controller/
│   │   │   │   ├── AlbumController.java         # Album REST endpoints
│   │   │   │   ├── MemoryController.java        # Memory REST endpoints
│   │   │   │   └── CloudinaryController.java    # Image upload endpoint
│   │   │   ├── entity/
│   │   │   │   ├── Album.java                   # Album JPA entity
│   │   │   │   └── Memory.java                  # Memory JPA entity
│   │   │   ├── repository/
│   │   │   │   ├── AlbumRepository.java
│   │   │   │   └── MemoryRepository.java
│   │   │   └── service/
│   │   │       ├── AlbumService.java
│   │   │       └── MemoryService.java
│   │   └── resources/
│   │       ├── application.yaml                 # App configuration
│   │       └── static/
│   │           ├── index.html                   # Albums listing page
│   │           ├── Album.html                   # Album detail / memories page
│   │           ├── app.js                       # Albums page JS
│   │           ├── album.js                     # Album detail page JS
│   │           └── style.css                    # Global styles
│   └── test/
│       ├── java/com/venkata/mymemo/
│       │   ├── controller/                      # Controller layer tests
│       │   ├── entity/                          # Entity tests
│       │   ├── repository/                      # Repository tests
│       │   └── service/                         # Service layer tests
│       └── resources/
│           └── application-test.yaml            # H2 in-memory test config
├── pom.xml
└── mvnw / mvnw.cmd
```

---

## API Endpoints

### Albums — `/api/albums`

| Method   | Endpoint               | Description          | Body / Params         |
|----------|------------------------|----------------------|-----------------------|
| `POST`   | `/api/albums`          | Create a new album   | JSON `Album` body     |
| `GET`    | `/api/albums`          | List all albums      | —                     |
| `GET`    | `/api/albums/{albumId}`| Get album by ID      | `albumId` path param  |
| `DELETE` | `/api/albums/{albumId}`| Delete an album      | `albumId` path param  |

### Memories — `/api/albums/{albumId}/memories`

| Method   | Endpoint                                         | Description               | Body / Params                  |
|----------|--------------------------------------------------|---------------------------|--------------------------------|
| `POST`   | `/api/albums/{albumId}/memories`                 | Add a memory to an album  | JSON `Memory` body             |
| `GET`    | `/api/albums/{albumId}/memories`                 | List memories in an album | `albumId` path param           |
| `DELETE` | `/api/albums/{albumId}/memories/{memoryId}`      | Delete a memory           | `albumId`, `memoryId` params   |

### Image Upload — `/api/cloudinary`

| Method | Endpoint                  | Description                          | Form Data              |
|--------|---------------------------|--------------------------------------|------------------------|
| `POST` | `/api/cloudinary/upload`  | Upload an image file to Cloudinary   | `file` (multipart)     |

**Response:**
```json
{
  "url": "https://res.cloudinary.com/...",
  "publicId": "mymemo/..."
}
```

---

## Environment Variables

Configure these via a `.env` file in the project root (auto-loaded) or as system environment variables.

| Variable                  | Description                        | Default       |
|---------------------------|------------------------------------|---------------|
| `DB_HOST`                 | PostgreSQL host                    | `localhost`   |
| `DB_PORT`                 | PostgreSQL port                    | `5432`        |
| `DB_NAME`                 | Database name                      | `mymemo`      |
| `DB_USERNAME`             | Database username                  | `postgres`    |
| `DB_PASSWORD`             | Database password                  | *(required)*  |
| `SERVER_PORT`             | HTTP port for the application      | `8080`        |
| `CLOUDINARY_CLOUD_NAME`   | Your Cloudinary cloud name         | *(required)*  |
| `CLOUDINARY_API_KEY`      | Your Cloudinary API key            | *(required)*  |
| `CLOUDINARY_API_SECRET`   | Your Cloudinary API secret         | *(required)*  |

### Example `.env` file

```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=mymemo
DB_USERNAME=postgres
DB_PASSWORD=your_db_password

CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

---

## Running Locally

### Prerequisites

- Java 21+
- Maven 3.9+ (or use the included `./mvnw` wrapper)
- PostgreSQL 14+
- A [Cloudinary](https://cloudinary.com/) account (free tier works)

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

   Create a `.env` file in the project root (see example above).

4. **Build and run**
   ```bash
   ./mvnw spring-boot:run
   ```
   On Windows:
   ```bat
   mvnw.cmd spring-boot:run
   ```

5. **Open the app**

   Navigate to [http://localhost:8080](http://localhost:8080) in your browser.

---

## Running Tests

Tests use an in-memory H2 database — no PostgreSQL or Cloudinary credentials required.

```bash
./mvnw test
```

---

## License

This project is licensed under the terms of the [LICENSE](LICENSE) file included in this repository.
