const params = new URLSearchParams(window.location.search);
const albumId = params.get('id');

if (!albumId) {
  window.location.href = 'index.html';
}

const API_ALBUM = '/api/albums/' + albumId;
const API_MEMORIES = '/api/albums/' + albumId + '/memories';

// ── Load album details ────────────────────────────────────────────────────────

async function loadAlbumDetails() {
  try {
    const response = await fetch(API_ALBUM);
    const album = await response.json();

    const cover = document.getElementById('album-cover');
    cover.src = album.coverImageUrl || 'https://placehold.co/800x300?text=No+Image';
    cover.alt = album.title;

    document.getElementById('album-title').textContent = album.title;
    document.getElementById('album-date').textContent = album.eventDate
      ? formatDate(album.eventDate)
      : 'No date';

  } catch (error) {
    console.error('Error loading album details:', error);
  }
}

// ── Load and render memories ──────────────────────────────────────────────────

async function loadMemories() {
  const container = document.getElementById('memory-container');
  container.innerHTML = '';

  try {
    const response = await fetch(API_MEMORIES);
    const memories = await response.json();

    if (memories.length === 0) {
      container.innerHTML = '<p class="no-albums">No memories yet. Add one above!</p>';
      return;
    }

    memories.forEach(function (memory) {
      const card = createMemoryCard(memory);
      container.appendChild(card);
    });

  } catch (error) {
    container.innerHTML = '<p class="no-albums">Failed to load memories.</p>';
    console.error('Error loading memories:', error);
  }
}

// ── Build a single memory card element ───────────────────────────────────────

function createMemoryCard(memory) {
  const card = document.createElement('div');
  card.className = 'album-card';

  const img = document.createElement('img');
  img.src = memory.imageUrl || 'https://placehold.co/400x160?text=No+Image';
  img.alt = memory.description;

  const body = document.createElement('div');
  body.className = 'album-card-body';

  const desc = document.createElement('h3');
  desc.textContent = memory.description;

  const date = document.createElement('p');
  date.textContent = memory.memoryDate ? formatDate(memory.memoryDate) : 'No date';

  body.appendChild(desc);
  body.appendChild(date);
  card.appendChild(img);
  card.appendChild(body);

  return card;
}

// ── Handle memory form submission ─────────────────────────────────────────────

document.getElementById('memory-form').addEventListener('submit', async function (event) {
  event.preventDefault();

  const imageUrl = document.getElementById('imageUrl').value.trim();
  const description = document.getElementById('description').value.trim();
  const memoryDate = document.getElementById('memoryDate').value;

  const payload = {
    imageUrl: imageUrl || null,
    description: description,
    memoryDate: memoryDate || null
  };

  try {
    const response = await fetch(API_MEMORIES, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (response.ok) {
      document.getElementById('memory-form').reset();
      await loadMemories();
    } else {
      console.error('Failed to add memory. Status:', response.status);
    }

  } catch (error) {
    console.error('Error adding memory:', error);
  }
});

// ── Utility ───────────────────────────────────────────────────────────────────

function formatDate(dateStr) {
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' });
}

// ── Init ──────────────────────────────────────────────────────────────────────

loadAlbumDetails();
loadMemories();
