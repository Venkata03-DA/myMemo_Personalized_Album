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

  // 3-dot menu
  const menu = document.createElement('div');
  menu.className = 'card-menu';

  const trigger = document.createElement('button');
  trigger.className = 'menu-trigger';
  trigger.textContent = '⋮';
  trigger.addEventListener('click', function (e) {
    e.stopPropagation();
    dropdown.classList.toggle('open');
  });

  const dropdown = document.createElement('div');
  dropdown.className = 'menu-dropdown';

  const deleteItem = document.createElement('button');
  deleteItem.textContent = 'Delete';
  deleteItem.addEventListener('click', function (e) {
    e.stopPropagation();
    dropdown.classList.remove('open');
    showConfirm('Are you sure you want to delete this memory?', async function () {
      try {
        const res = await fetch(API_MEMORIES + '/' + memory.id, { method: 'DELETE' });
        if (res.ok) {
          await loadMemories();
        } else {
          alert('Failed to delete memory.');
        }
      } catch (err) {
        console.error('Error deleting memory:', err);
      }
    });
  });

  dropdown.appendChild(deleteItem);
  menu.appendChild(trigger);
  menu.appendChild(dropdown);
  card.appendChild(menu);

  return card;
}

// ── Cloudinary upload helper ─────────────────────────────────────────────────

async function uploadToCloudinary(file) {
  const formData = new FormData();
  formData.append('file', file);

  const res = await fetch('/api/cloudinary/upload', {
    method: 'POST',
    body:   formData
  });

  if (!res.ok) throw new Error('Image upload failed');
  return await res.json(); // { url, publicId }
}

// ── Handle memory form submission ─────────────────────────────────────────────

document.getElementById('memory-form').addEventListener('submit', async function (event) {
  event.preventDefault();

  const fileInput   = document.getElementById('image');
  const file        = fileInput.files[0];
  const description = document.getElementById('description').value.trim();
  const memoryDate  = document.getElementById('memoryDate').value;

  let imageUrl      = null;
  let imagePublicId = null;

  if (file) {
    try {
      const uploaded = await uploadToCloudinary(file);
      imageUrl       = uploaded.url;
      imagePublicId  = uploaded.publicId;
    } catch (err) {
      console.error('Image upload failed:', err);
      alert('Image upload failed. Please try again.');
      return;
    }
  }

  const payload = {
    imageUrl:     imageUrl,
    imagePublicId: imagePublicId,
    description:  description,
    memoryDate:   memoryDate || null
  };

  try {
    const response = await fetch(API_MEMORIES, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(payload)
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

function showConfirm(message, onYes) {
  const overlay = document.createElement('div');
  overlay.className = 'confirm-overlay';

  const box = document.createElement('div');
  box.className = 'confirm-box';

  const msg = document.createElement('p');
  msg.textContent = message;

  const actions = document.createElement('div');
  actions.className = 'confirm-actions';

  const yesBtn = document.createElement('button');
  yesBtn.textContent = 'Yes';
  yesBtn.className = 'confirm-yes';
  yesBtn.addEventListener('click', function () {
    document.body.removeChild(overlay);
    onYes();
  });

  const noBtn = document.createElement('button');
  noBtn.textContent = 'No';
  noBtn.className = 'confirm-no';
  noBtn.addEventListener('click', function () {
    document.body.removeChild(overlay);
  });

  actions.appendChild(yesBtn);
  actions.appendChild(noBtn);
  box.appendChild(msg);
  box.appendChild(actions);
  overlay.appendChild(box);
  document.body.appendChild(overlay);
}

document.addEventListener('click', function () {
  document.querySelectorAll('.menu-dropdown.open').forEach(function (d) {
    d.classList.remove('open');
  });
});

// ── Init ──────────────────────────────────────────────────────────────────────

loadAlbumDetails();
loadMemories();
