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
    showConfirm('Are you sure you want to delete "' + album.title + '"?', async function () {
      try {
        const res = await fetch(API_BASE + '/' + album.id, { method: 'DELETE' });
        if (res.ok) {
          await loadAlbums();
        } else {
          alert('Failed to delete album.');
        }
      } catch (err) {
        console.error('Error deleting album:', err);
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

// ── Handle album form submission ──────────────────────────────────────────────

document.getElementById('album-form').addEventListener('submit', async function (event) {
  event.preventDefault();

  const title     = document.getElementById('title').value.trim();
  const eventDate = document.getElementById('eventDate').value;
  const fileInput = document.getElementById('coverImage');
  const file      = fileInput.files[0];

  let coverImageUrl      = null;
  let coverImagePublicId = null;

  if (file) {
    try {
      const uploaded     = await uploadToCloudinary(file);
      coverImageUrl      = uploaded.url;
      coverImagePublicId = uploaded.publicId;
    } catch (err) {
      console.error('Image upload failed:', err);
      alert('Image upload failed. Please try again.');
      return;
    }
  }

  const payload = {
    title:              title,
    coverImageUrl:      coverImageUrl,
    coverImagePublicId: coverImagePublicId,
    eventDate:          eventDate || null
  };

  try {
    const response = await fetch(API_BASE, {
      method:  'POST',
      headers: { 'Content-Type': 'application/json' },
      body:    JSON.stringify(payload)
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

loadAlbums();
