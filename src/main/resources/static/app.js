const API_BASE = '/api/albums';

// ── Fetch and render all albums ──────────────────────────────────────────────

async function loadAlbums() {
  const container = document.getElementById('album-container');
  container.innerHTML = '';

  try {
    const response = await fetch(API_BASE);
    const albums = await response.json();

    if (albums.length === 0) {
      container.innerHTML = '<p class="no-albums">No albums yet. Create one above!</p>';
      return;
    }

    albums.forEach(function (album) {
      const card = createAlbumCard(album);
      container.appendChild(card);
    });

  } catch (error) {
    container.innerHTML = '<p class="no-albums">Failed to load albums.</p>';
    console.error('Error loading albums:', error);
  }
}

// ── Build a single album card element ────────────────────────────────────────

function createAlbumCard(album) {
  const card = document.createElement('div');
  card.className = 'album-card';

  const img = document.createElement('img');
  img.src = album.coverImageUrl || 'https://placehold.co/400x160?text=No+Image';
  img.alt = album.title;

  const body = document.createElement('div');
  body.className = 'album-card-body';

  const title = document.createElement('h3');
  title.textContent = album.title;

  const date = document.createElement('p');
  date.textContent = album.eventDate ? formatDate(album.eventDate) : 'No date';

  body.appendChild(title);
  body.appendChild(date);
  card.appendChild(img);
  card.appendChild(body);

  card.addEventListener('click', function () {
    window.location.href = 'Album.html?id=' + album.id;
  });

  return card;
}

// ── Handle album form submission ──────────────────────────────────────────────

document.getElementById('album-form').addEventListener('submit', async function (event) {
  event.preventDefault();

  const title = document.getElementById('title').value.trim();
  const coverImageUrl = document.getElementById('coverImageUrl').value.trim();
  const eventDate = document.getElementById('eventDate').value;

  const payload = {
    title: title,
    coverImageUrl: coverImageUrl || null,
    eventDate: eventDate || null
  };

  try {
    const response = await fetch(API_BASE, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      document.getElementById('album-form').reset();
      await loadAlbums();
    } else {
      console.error('Failed to create album. Status:', response.status);
    }

  } catch (error) {
    console.error('Error creating album:', error);
  }
});

// ── Utility ───────────────────────────────────────────────────────────────────

function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}

// ── Init ──────────────────────────────────────────────────────────────────────

loadAlbums();
