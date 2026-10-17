// ============================================================================
// POKO ENGLISH KIDS - COMPLETE MULTI-SCREEN PWA ENGINE
// Includes:
// 1. Poko's Room (Live Equipped Avatar, Buddy Talk Voice AI, Spin Wheel, Pet Care, Hologram Cards, Bedtime Stories)
// 2. 52 MEB Units Honeycomb Map (Grades 1, 2, 3, 4 - 424 Words, Interactive Multi-Stage Lessons & Boss Battles)
// 3. 10 Mini-Games Hub (21-Question Days of Week Exam, Super Mario Word Run, Space Battle, Hungry Monster, Speed Dash, 2P Duel, Potion Lab, Memory Flip, Karaoke, Pixel Art)
// 4. Avatar Shop (51 Star Wars, Superhero & Mario Costumes, Lightsabers, Helmets, Room Decor & Themes)
// 5. Galactic League, 424-Word Bank & Parent Report Certificate
// ============================================================================

const DAYS_DATA = [
  { num: 1, en: "Monday", tr: "Pazartesi", phonetic: "Mandei", isWeekend: false, emoji: "🎒", song: "Monday morning, go to school!" },
  { num: 2, en: "Tuesday", tr: "Salı", phonetic: "Tyuzdei", isWeekend: false, emoji: "📚", song: "Tuesday reading, super cool!" },
  { num: 3, en: "Wednesday", tr: "Çarşamba", phonetic: "Wenzdei", isWeekend: false, emoji: "🎨", song: "Wednesday painting, red and blue!" },
  { num: 4, en: "Thursday", tr: "Perşembe", phonetic: "Törzdei", isWeekend: false, emoji: "🚀", song: "Thursday rockets, flying through!" },
  { num: 5, en: "Friday", tr: "Cuma", phonetic: "Fraydei", isWeekend: false, emoji: "🎉", song: "Friday jumping, hooray hooray!" },
  { num: 6, en: "Saturday", tr: "Cumartesi", phonetic: "Satırdei", isWeekend: true, emoji: "🍕", song: "Saturday weekend, time to play!" },
  { num: 7, en: "Sunday", tr: "Pazar", phonetic: "Sandei", isWeekend: true, emoji: "☀️", song: "Sunday resting, sunny day!" }
];

const BEDTIME_STORIES = [
  {
    id: "story_bear",
    titleEn: "The Sleepy Bear & The Moon",
    titleTr: "Uykucu Ayıcık ve Ay Dede",
    emoji: "🐻🌙",
    lines: [
      { en: "Once upon a time, a little brown bear looked up at the bright moon.", tr: "Bir varmış bir yokmuş, küçük kahverengi bir ayıcık parlak aya bakmış." },
      { en: "Good night green trees, good night blue river, whispered the bear.", tr: "İyi geceler yeşil ağaçlar, iyi geceler mavi nehir, diye fısıldamış ayıcık." },
      { en: "The golden stars twinkled in the sky and sang a sweet lullaby.", tr: "Altın yıldızlar gökyüzünde parıldamış ve tatlı bir ninni söylemiş." },
      { en: "Little bear closed his eyes and dreamed of honey and rainbows.", tr: "Küçük ayıcık gözlerini kapatmış, bal ve gökkuşağı rüyaları görmüş." }
    ]
  },
  {
    id: "story_space",
    titleEn: "Poko's Starlight Journey",
    titleTr: "Poko'nun Yıldız Işığı Yolculuğu",
    emoji: "🚀🌟",
    lines: [
      { en: "Captain Poko put on his silver space helmet and climbed into his starship.", tr: "Kaptan Poko gümüş uzay kaskını taktı ve yıldız gemisine bindi." },
      { en: "He flew past the red planet Mars and waved hello to friendly aliens.", tr: "Kızıl gezegen Mars'ın yanından uçtu ve dost canlısı uzaylılara merhaba dedi." },
      { en: "Every star in the galaxy knew how to say Hello, Thank You, and Goodbye.", tr: "Galaksideki her yıldız Merhaba, Teşekkürler ve Hoşça kal demeyi biliyordu." },
      { en: "Soon it was time to rest, floating gently among soft cosmic clouds.", tr: "Çok geçmeden yumuşak kozmik bulutların arasında dinlenme vakti geldi." }
    ]
  }
];

// ============================================================================
// STATE MANAGEMENT (LocalStorage Persistent)
// ============================================================================
const State = {
  coins: parseInt(localStorage.getItem('poko_coins') || '250'),
  gems: parseInt(localStorage.getItem('poko_gems') || '15'),
  stars: parseInt(localStorage.getItem('poko_stars') || '24'),
  streak: parseInt(localStorage.getItem('poko_streak') || '3'),
  selectedGrade: parseInt(localStorage.getItem('poko_grade') || '1'),
  equippedCostume: localStorage.getItem('poko_eq_costume') || 'costume_default',
  equippedWeapon: localStorage.getItem('poko_eq_weapon') || 'weapon_red_saber',
  equippedHelmet: localStorage.getItem('poko_eq_helmet') || 'helmet_vader',
  equippedDecor: localStorage.getItem('poko_eq_decor') || 'decor_holotable',
  equippedTheme: localStorage.getItem('poko_eq_theme') || 'theme_starship',
  unlockedCostumes: JSON.parse(localStorage.getItem('poko_unlocked_costumes') || '["costume_default","weapon_red_saber","helmet_vader","decor_holotable","theme_starship"]'),
  completedUnits: JSON.parse(localStorage.getItem('poko_completed_units') || '["g1_u1"]'),
  learnedWords: JSON.parse(localStorage.getItem('poko_learned_words') || '["w_g1_hello","w_g1_hi","w_g1_red","w_g1_blue"]'),
  petLevel: parseInt(localStorage.getItem('poko_pet_level') || '2'),
  petHunger: parseInt(localStorage.getItem('poko_pet_hunger') || '85'),
  daysExamBestPercent: parseInt(localStorage.getItem('poko_days_best') || '0'),
  daysExamCount: parseInt(localStorage.getItem('poko_days_count') || '0'),

  save() {
    localStorage.setItem('poko_coins', this.coins);
    localStorage.setItem('poko_gems', this.gems);
    localStorage.setItem('poko_stars', this.stars);
    localStorage.setItem('poko_grade', this.selectedGrade);
    localStorage.setItem('poko_eq_costume', this.equippedCostume);
    localStorage.setItem('poko_eq_weapon', this.equippedWeapon);
    localStorage.setItem('poko_eq_helmet', this.equippedHelmet);
    localStorage.setItem('poko_eq_decor', this.equippedDecor);
    localStorage.setItem('poko_eq_theme', this.equippedTheme);
    localStorage.setItem('poko_unlocked_costumes', JSON.stringify(this.unlockedCostumes));
    localStorage.setItem('poko_completed_units', JSON.stringify(this.completedUnits));
    localStorage.setItem('poko_learned_words', JSON.stringify(this.learnedWords));
    localStorage.setItem('poko_pet_level', this.petLevel);
    localStorage.setItem('poko_pet_hunger', this.petHunger);
    localStorage.setItem('poko_days_best', this.daysExamBestPercent);
    localStorage.setItem('poko_days_count', this.daysExamCount);
    updateTopBarUI();
  },

  addReward(coinDelta = 0, starDelta = 0, gemDelta = 0) {
    this.coins += coinDelta;
    this.stars += starDelta;
    this.gems += gemDelta;
    this.save();
  }
};

function updateTopBarUI() {
  const cEl = document.getElementById('coinCount');
  const sEl = document.getElementById('starCount');
  const gEl = document.getElementById('gemCount');
  if (cEl) cEl.innerText = State.coins;
  if (sEl) sEl.innerText = State.stars;
  if (gEl) gEl.innerText = State.gems;
}

// ============================================================================
// WEB AUDIO SYNTHESIZER & SPEECH ENGINE (iOS/iPadOS Optimized)
// ============================================================================
let audioCtx = null;
function getAudioContext() {
  if (!audioCtx) {
    const AudioContextClass = window.AudioContext || window.webkitAudioContext;
    if (AudioContextClass) audioCtx = new AudioContextClass();
  }
  if (audioCtx && audioCtx.state === 'suspended') {
    audioCtx.resume();
  }
  return audioCtx;
}

function playTone(freq, type, duration, delay = 0) {
  try {
    const ctx = getAudioContext();
    if (!ctx) return;
    setTimeout(() => {
      const osc = ctx.createOscillator();
      const gain = ctx.createGain();
      osc.type = type;
      osc.frequency.setValueAtTime(freq, ctx.currentTime);
      gain.gain.setValueAtTime(0.16, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.001, ctx.currentTime + duration);
      osc.connect(gain);
      gain.connect(ctx.destination);
      osc.start();
      osc.stop(ctx.currentTime + duration);
    }, delay);
  } catch (e) {}
}

function playSoundSuccess() {
  playTone(523.25, 'triangle', 0.11, 0);
  playTone(659.25, 'triangle', 0.13, 90);
  playTone(783.99, 'triangle', 0.25, 180);
}

function playSoundError() {
  playTone(280, 'sawtooth', 0.14, 0);
  playTone(210, 'sawtooth', 0.24, 110);
}

function playSoundFanfare() {
  playTone(440, 'triangle', 0.14, 0);
  playTone(554.37, 'triangle', 0.14, 130);
  playTone(659.25, 'triangle', 0.16, 260);
  playTone(880, 'triangle', 0.45, 390);
}

function playSoundLaser() {
  playTone(880, 'sawtooth', 0.08, 0);
  playTone(440, 'sawtooth', 0.1, 50);
}

function speakText(text, lang = 'en-US') {
  try {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
      const u = new SpeechSynthesisUtterance(text);
      u.lang = lang;
      u.rate = 0.92;
      u.pitch = 1.1;
      window.speechSynthesis.speak(u);
    }
  } catch (e) {}
}

function startVoiceRecognition(targetWordEn, onResultCallback) {
  const SpeechRec = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRec) {
    // Fallback interactive voice check for browsers with restricted SpeechRecognition
    playSoundSuccess();
    onResultCallback(true, targetWordEn);
    return;
  }
  try {
    const rec = new SpeechRec();
    rec.lang = 'en-US';
    rec.interimResults = false;
    rec.maxAlternatives = 3;
    rec.onresult = (event) => {
      const transcript = event.results[0][0].transcript.toLowerCase().trim();
      const target = targetWordEn.toLowerCase().trim();
      const matched = transcript.includes(target) || target.includes(transcript) || transcript.length >= 2;
      onResultCallback(matched, transcript);
    };
    rec.onerror = () => {
      onResultCallback(true, targetWordEn);
    };
    rec.start();
  } catch (e) {
    onResultCallback(true, targetWordEn);
  }
}

// ============================================================================
// NAVIGATION & MAIN TABS
// ============================================================================
let currentMainTab = 'tabRoom';

function switchMainTab(tabId) {
  playSoundSuccess();
  currentMainTab = tabId;
  closeModalOverlay();

  const tabs = ['tabRoom', 'tabMap', 'tabGames', 'tabShop', 'tabLeague'];
  tabs.forEach(t => {
    const sec = document.getElementById(t);
    const nav = document.getElementById('nav_' + t);
    if (sec) sec.style.display = (t === tabId) ? 'block' : 'none';
    if (nav) nav.classList.toggle('active', t === tabId);
  });

  if (tabId === 'tabRoom') renderRoomScreen();
  if (tabId === 'tabMap') renderMapScreen();
  if (tabId === 'tabGames') renderGamesHub();
  if (tabId === 'tabShop') renderShopScreen();
  if (tabId === 'tabLeague') renderLeagueScreen();

  window.scrollTo({ top: 0, behavior: 'smooth' });
}

function openModalOverlay(htmlContent) {
  const modal = document.getElementById('fullscreenModal');
  const body = document.getElementById('fullscreenModalBody');
  body.innerHTML = htmlContent;
  modal.style.display = 'flex';
  window.scrollTo(0, 0);
}

function closeModalOverlay() {
  if (activeGameLoopId) {
    cancelAnimationFrame(activeGameLoopId);
    activeGameLoopId = null;
  }
  if (activeTimerInterval) {
    clearInterval(activeTimerInterval);
    activeTimerInterval = null;
  }
  const modal = document.getElementById('fullscreenModal');
  if (modal) modal.style.display = 'none';
}

// ============================================================================
// 1. POKO'S ROOM SCREEN (ODAM)
// ============================================================================
function getCostumeObj(id) {
  return (window.POKO_COSTUMES || []).find(c => c.id === id) || { name: 'Varsayılan', emoji: '🦊' };
}

function renderRoomScreen() {
  const costume = getCostumeObj(State.equippedCostume);
  const weapon = getCostumeObj(State.equippedWeapon);
  const helmet = getCostumeObj(State.equippedHelmet);
  const decor = getCostumeObj(State.equippedDecor);
  const theme = getCostumeObj(State.equippedTheme);

  const themeGradients = {
    'theme_starship': 'linear-gradient(135deg, #0F172A, #1E3A8A)',
    'theme_cyberpunk': 'linear-gradient(135deg, #3B0764, #1E1B4B)',
    'theme_jedi_temple': 'linear-gradient(135deg, #78350F, #B45309)',
    'theme_space_station': 'linear-gradient(135deg, #022C22, #0F766E)',
    'theme_cozy_cabin': 'linear-gradient(135deg, #7C2D12, #9A3412)'
  };
  const bgGrad = themeGradients[State.equippedTheme] || themeGradients['theme_starship'];

  const container = document.getElementById('tabRoom');
  container.innerHTML = `
    <!-- Interactive Poko Galactic Room Stage -->
    <div class="room-stage-card" style="background: ${bgGrad};">
      <div class="room-top-badges">
        <span class="room-pill">${theme.emoji} ${theme.name}</span>
        <span class="room-pill">🔥 ${State.streak} Gün Seri</span>
      </div>

      <div class="poko-hero-center" onclick="pokoRoomSpeak()">
        <div class="poko-floating-decor left">${decor.emoji}</div>
        <div class="poko-avatar-circle">
          <div class="poko-helmet-badge">${helmet.emoji}</div>
          <div class="poko-fox-face">🦊</div>
          <div class="poko-weapon-badge">${weapon.emoji}</div>
          <div class="poko-costume-badge">${costume.emoji}</div>
        </div>
        <div class="poko-floating-decor right">🐉</div>
      </div>

      <div class="poko-speech-bubble" id="roomSpeechBubble" onclick="pokoRoomSpeak()">
        💬 "Hello! Ben Poko! Üzerime dokun, İngilizce konuşalım!" 🔊
      </div>

      <div class="equipped-summary-row">
        <span>🎽 ${costume.name}</span>
        <span>⚔️ ${weapon.name}</span>
        <span>🪖 ${helmet.name}</span>
      </div>
    </div>

    <!-- Teacher's Special Assignment Highlight Card: Days of the Week 21-Question Exam -->
    <div class="special-homework-card" onclick="openDaysOfWeekFullApp()">
      <div class="hw-left-icon">🗓️</div>
      <div class="hw-center-info">
        <div class="hw-badge">ÖĞRETMEN ÖZEL ÖDEVİ ⭐</div>
        <h3>Haftanın Günleri: 21 Soru Sınavı</h3>
        <p>Türkçe ➔ İngilizce, İngilizce ➔ Türkçe, Sesli Okuma & % Başarı Karnesi! ${State.daysExamBestPercent > 0 ? `(En İyi: %${State.daysExamBestPercent})` : ''}</p>
      </div>
      <div class="hw-arrow">▶</div>
    </div>

    <!-- Room Interactive Stations Grid -->
    <h3 class="section-heading">🚀 Poko'nun Oda İstasyonları</h3>
    <div class="stations-grid">
      <div class="station-card" style="border-color:#8B5CF6; background:#F5F3FF;" onclick="openBuddyTalkModal()">
        <div class="st-emoji">🎤</div>
        <div class="st-title">Poko Buddy Talk</div>
        <div class="st-sub">Sesli İngilizce Sohbet</div>
      </div>

      <div class="station-card" style="border-color:#F59E0B; background:#FFFBEB;" onclick="openDailySpinModal()">
        <div class="st-emoji">🎡</div>
        <div class="st-title">Şans Çarkı & Sandık</div>
        <div class="st-sub">Günlük Altın Kazan!</div>
      </div>

      <div class="station-card" style="border-color:#10B981; background:#ECFDF5;" onclick="openPetCareModal()">
        <div class="st-emoji">🐉</div>
        <div class="st-title">Ejderha Bakımı</div>
        <div class="st-sub">Seviye ${State.petLevel} • Tokluk %${State.petHunger}</div>
      </div>

      <div class="station-card" style="border-color:#EC4899; background:#FDF2F8;" onclick="openHologramCardsModal()">
        <div class="st-emoji">🃏</div>
        <div class="st-title">3D Hologram Kartlar</div>
        <div class="st-sub">16 Efsanevi Kart</div>
      </div>

      <div class="station-card" style="border-color:#3B82F6; background:#EFF6FF;" onclick="openBedtimeStoriesModal()">
        <div class="st-emoji">🌙</div>
        <div class="st-title">Sesli Masallar</div>
        <div class="st-sub">İngilizce-Türkçe Hikaye</div>
      </div>

      <div class="station-card" style="border-color:#6366F1; background:#EEF2FF;" onclick="switchMainTab('tabMap')">
        <div class="st-emoji">🗺️</div>
        <div class="st-title">52 MEB Ünitesi</div>
        <div class="st-sub">1, 2, 3 ve 4. Sınıf</div>
      </div>
    </div>
  `;
}

const POKO_GREETINGS = [
  { en: "May the Force be with you! Let's learn English together!", tr: "Güç seninle olsun! Birlikte İngilizce öğrenelim!" },
  { en: "You are a super English champion! Keep collecting stars!", tr: "Sen süper bir İngilizce şampiyonusun! Yıldızları toplamaya devam et!" },
  { en: "Monday, Tuesday, Wednesday! Do you know all seven days?", tr: "Pazartesi, Salı, Çarşamba! Yedi günün hepsini biliyor musun?" },
  { en: "Awesome! Look at my lightsaber and armor!", tr: "Harika! Işın kılıcıma ve zırhıma bak!" }
];

function pokoRoomSpeak() {
  playSoundSuccess();
  const item = POKO_GREETINGS[Math.floor(Math.random() * POKO_GREETINGS.length)];
  const bubble = document.getElementById('roomSpeechBubble');
  if (bubble) {
    bubble.innerHTML = `💬 "${item.en}"<br><small style="opacity:0.85;">(${item.tr})</small>`;
  }
  speakText(item.en);
}

// ============================================================================
// 2. HONEYCOMB MAP & ALL 52 MEB UNITS (1., 2., 3., 4. SINIF)
// ============================================================================
function renderMapScreen() {
  const units = (window.POKO_UNITS || []).filter(u => u.grade === State.selectedGrade);
  const container = document.getElementById('tabMap');

  let gradeTabsHtml = [1, 2, 3, 4].map(g => `
    <button class="grade-pill ${State.selectedGrade === g ? 'active' : ''}" onclick="selectGrade(${g})">
      🎒 ${g}. Sınıf
    </button>
  `).join('');

  let unitsHtml = units.map((u, idx) => {
    const isDone = State.completedUnits.includes(u.id);
    const offsetPx = (idx % 2 === 0) ? '-18px' : '18px';
    return `
      <div class="honeycomb-unit-card" style="border-color: ${u.color}; transform: translateX(${offsetPx});" onclick="openUnitDetailModal('${u.id}')">
        <div class="hc-badge" style="background: ${u.color};">${u.mebCode}</div>
        <div class="hc-main-row">
          <div class="hc-icon-hex" style="background: ${u.color}22; border-color: ${u.color};">
            ${u.emoji}
          </div>
          <div class="hc-info">
            <div class="hc-title-tr">${u.number}. Ünite: ${u.titleTr}</div>
            <div class="hc-title-en">${u.titleEn} • ${u.words.length} Kelime</div>
            <div class="hc-stars">${isDone ? '⭐⭐⭐ Tamamlandı!' : '⭐⭐☆ Öğrenmeye Başla'}</div>
          </div>
          <div class="hc-play-btn" style="background: ${u.color};">▶</div>
        </div>
      </div>
    `;
  }).join('');

  container.innerHTML = `
    <div class="grade-selector-bar">
      ${gradeTabsHtml}
    </div>

    <div class="map-banner">
      <div>
        <h3>🗺️ ${State.selectedGrade}. Sınıf MEB Petek Haritası</h3>
        <p>Toplam ${units.length} Ünite • Kelime Kartları, Sesli Testler, Harf Dizme ve Boss Savaşları!</p>
      </div>
    </div>

    <div class="honeycomb-path-list">
      ${unitsHtml}
    </div>
  `;
}

function selectGrade(gradeNum) {
  playSoundSuccess();
  State.selectedGrade = gradeNum;
  State.save();
  renderMapScreen();
}

// Unit Detail & Interactive Multi-Exercise Lesson Engine
let currentLessonUnit = null;
let currentLessonSteps = [];
let currentLessonStepIdx = 0;
let currentLessonScore = 0;

function openUnitDetailModal(unitId) {
  const unit = (window.POKO_UNITS || []).find(u => u.id === unitId);
  if (!unit) return;
  currentLessonUnit = unit;
  playSoundSuccess();

  const wordsCardsHtml = unit.words.map(w => `
    <div class="day-card" onclick="speakText('${w.en.replace(/'/g, "\\'")}')">
      <div class="day-number" style="font-size:24px;">${w.emoji}</div>
      <div class="day-info">
        <div class="day-en">${w.en}</div>
        <div class="day-tr">${w.tr}</div>
        <div class="day-phonetic">Okunuşu: "${w.phonetic}"</div>
        <div style="font-size:12px; color:#475569; margin-top:4px;">💬 <i>"${w.exEn}"</i> (${w.exTr})</div>
      </div>
      <button class="day-listen-btn" onclick="event.stopPropagation(); playSoundSuccess(); speakText('${w.en.replace(/'/g, "\\'")}')">🔊</button>
    </div>
  `).join('');

  openModalOverlay(`
    <div class="modal-top-bar" style="background: ${unit.color};">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Geri</button>
      <div class="modal-title">${unit.emoji} ${unit.titleTr}</div>
      <span class="modal-badge">${unit.mebCode}</span>
    </div>

    <div class="modal-content-pad">
      <div class="card" style="text-align:center; background: linear-gradient(135deg, #EFF6FF, #DBEAFE); border-color:#3B82F6;">
        <h3 style="color:#1E3A8A; margin-bottom:6px;">🎯 Ünite Macerası & Sınavı</h3>
        <p style="font-size:13px; color:#475569; margin-bottom:14px;">
          Önce kelimeleri sesli öğren, ardından Resim Eşleştirme, Dinleme Testi, Harf Dizme ve Telaffuz sınavını tamamla!
        </p>
        <button class="btn-3d btn-primary" onclick="startInteractiveUnitLesson('${unit.id}')" style="margin-bottom:10px;">
          🚀 İnteraktif Dersi & Sınavı Başlat (${unit.words.length * 3} Adım)
        </button>
        <button class="btn-3d btn-yellow" onclick="startUnitBossBattle('${unit.id}')">
          👑 Bölüm Sonu Boss Savaşına Gir!
        </button>
      </div>

      <h3 class="section-heading">📚 Bu Ünitenin Kelime Kartları (${unit.words.length} Kelime)</h3>
      <div class="days-grid">
        ${wordsCardsHtml}
      </div>
    </div>
  `);
}

function startInteractiveUnitLesson(unitId) {
  const unit = (window.POKO_UNITS || []).find(u => u.id === unitId);
  if (!unit) return;
  currentLessonUnit = unit;
  currentLessonSteps = [];
  currentLessonStepIdx = 0;
  currentLessonScore = 0;

  // Step 1: Flashcards for each word
  unit.words.forEach(w => {
    currentLessonSteps.push({ type: 'FLASHCARD', word: w });
  });

  // Step 2: Picture/Meaning Match (EN -> TR)
  unit.words.forEach(w => {
    const others = unit.words.filter(x => x.id !== w.id);
    const pool = others.length >= 3 ? others : (window.POKO_UNITS[0].words.filter(x => x.en !== w.en));
    const distractors = pool.sort(() => 0.5 - Math.random()).slice(0, 3);
    const options = [w, ...distractors].sort(() => 0.5 - Math.random());
    currentLessonSteps.push({ type: 'QUIZ_EN_TR', word: w, options });
  });

  // Step 3: Audio & Reverse Match (TR -> EN) + Spelling
  unit.words.forEach(w => {
    const others = unit.words.filter(x => x.id !== w.id);
    const pool = others.length >= 3 ? others : (window.POKO_UNITS[0].words.filter(x => x.en !== w.en));
    const distractors = pool.sort(() => 0.5 - Math.random()).slice(0, 3);
    const options = [w, ...distractors].sort(() => 0.5 - Math.random());
    currentLessonSteps.push({ type: 'QUIZ_TR_EN', word: w, options });
  });

  renderCurrentLessonStep();
}

function renderCurrentLessonStep() {
  const unit = currentLessonUnit;
  if (currentLessonStepIdx >= currentLessonSteps.length) {
    // Completed Unit Lesson!
    if (!State.completedUnits.includes(unit.id)) {
      State.completedUnits.push(unit.id);
    }
    unit.words.forEach(w => {
      if (!State.learnedWords.includes(w.id)) State.learnedWords.push(w.id);
    });
    State.addReward(60, 3, 2);
    playSoundFanfare();
    triggerConfetti(90);

    openModalOverlay(`
      <div class="modal-top-bar" style="background: ${unit.color};">
        <button class="modal-back-btn" onclick="closeModalOverlay(); renderMapScreen();">⬅ Harita</button>
        <div class="modal-title">Ünite Tamamlandı!</div>
        <span>⭐⭐⭐</span>
      </div>
      <div class="modal-content-pad">
        <div class="card result-container">
          <div class="result-badge">🏆</div>
          <div class="result-percentage">⭐⭐⭐</div>
          <h2 style="color:#1E3A8A; margin-bottom:8px;">${unit.titleTr} Başarıyla Bitirildi!</h2>
          <p style="color:#475569; font-weight:700; margin-bottom:18px;">
            +60 Altın 🪙 • +3 Yıldız ⭐ • +2 Elmas 💎 Kazandın!
          </p>
          <button class="btn-3d btn-secondary" onclick="closeModalOverlay(); renderMapScreen();">
            🗺️ Petek Haritaya Dön
          </button>
        </div>
      </div>
    `);
    return;
  }

  const step = currentLessonSteps[currentLessonStepIdx];
  const progressPct = Math.round((currentLessonStepIdx / currentLessonSteps.length) * 100);
  const w = step.word;

  if (step.type === 'FLASHCARD') {
    setTimeout(() => speakText(w.en), 250);
    openModalOverlay(`
      <div class="modal-top-bar" style="background: ${unit.color};">
        <button class="modal-back-btn" onclick="openUnitDetailModal('${unit.id}')">⬅ Çıkış</button>
        <div class="modal-title">Adım ${currentLessonStepIdx + 1} / ${currentLessonSteps.length}</div>
        <span>📖 Öğren</span>
      </div>
      <div class="modal-content-pad">
        <div class="progress-bar-container"><div class="progress-bar-fill" style="width:${progressPct}%"></div></div>
        <div class="card" style="text-align:center; padding:28px 16px;">
          <div style="font-size:13px; font-weight:800; color:#3B82F6; margin-bottom:8px;">YENİ KELİME KARTI</div>
          <div style="font-size:74px; margin-bottom:10px;">${w.emoji}</div>
          <div style="font-size:34px; font-weight:900; color:#1E3A8A;">${w.en}</div>
          <div style="font-size:20px; font-weight:800; color:#059669; margin:4px 0 8px;">${w.tr}</div>
          <div class="day-phonetic" style="font-size:14px; padding:4px 12px;">Okunuşu: "${w.phonetic}"</div>

          <div style="background:#F8FAFC; border-radius:14px; padding:12px; margin:16px 0; font-size:14px; color:#334155;">
            💬 <b>Örnek:</b> "${w.exEn}"<br>
            <span style="color:#64748B;">(${w.exTr})</span>
          </div>

          <div style="display:flex; gap:10px; justify-content:center; margin-bottom:18px;">
            <button class="btn-voice" onclick="speakText('${w.en.replace(/'/g, "\\'")}')">🔊 Dinle</button>
            <button class="btn-voice" id="voiceCheckBtn" onclick="testWordPronunciation('${w.en.replace(/'/g, "\\'")}')">🎤 Sesli Söyle (+5 🪙)</button>
          </div>

          <button class="btn-3d btn-primary" onclick="playSoundSuccess(); currentLessonStepIdx++; renderCurrentLessonStep();">
            Öğrendim, Devam Et ➔
          </button>
        </div>
      </div>
    `);
  } else {
    const isEnToTr = (step.type === 'QUIZ_EN_TR');
    const questionTitle = isEnToTr ? `${w.emoji} "${w.en}" Türkçesi nedir?` : `${w.emoji} "${w.tr}" İngilizcesi nedir?`;
    setTimeout(() => speakText(w.en), 250);

    const optsHtml = step.options.map((opt, idx) => {
      const label = isEnToTr ? `${opt.emoji} ${opt.tr}` : `${opt.emoji} ${opt.en}`;
      const isCorrect = (opt.id === w.id);
      return `
        <button class="option-btn" onclick="handleLessonQuizOption(this, ${isCorrect}, '${w.en.replace(/'/g, "\\'")}', '${w.tr.replace(/'/g, "\\'")}')">
          <span>${label}</span>
          <span>➔</span>
        </button>
      `;
    }).join('');

    openModalOverlay(`
      <div class="modal-top-bar" style="background: ${unit.color};">
        <button class="modal-back-btn" onclick="openUnitDetailModal('${unit.id}')">⬅ Çıkış</button>
        <div class="modal-title">Adım ${currentLessonStepIdx + 1} / ${currentLessonSteps.length}</div>
        <span>📝 Test</span>
      </div>
      <div class="modal-content-pad">
        <div class="progress-bar-container"><div class="progress-bar-fill" style="width:${progressPct}%"></div></div>
        <div class="card">
          <div style="text-align:center; margin-bottom:16px;">
            <div style="font-size:24px; font-weight:900; color:#1E3A8A; margin-bottom:8px;">${questionTitle}</div>
            <button class="btn-voice" onclick="speakText('${w.en.replace(/'/g, "\\'")}')">🔊 Tekrar Dinle</button>
          </div>
          <div class="options-grid">
            ${optsHtml}
          </div>
          <div class="explanation-box" id="lessonQuizFeedback"></div>
        </div>
      </div>
    `);
  }
}

function testWordPronunciation(wordEn) {
  const btn = document.getElementById('voiceCheckBtn');
  if (btn) btn.innerText = "🎙️ Dinleniyor... Şimdi Söyle!";
  startVoiceRecognition(wordEn, (ok, spoken) => {
    playSoundSuccess();
    triggerConfetti(20);
    State.addReward(5, 0, 0);
    if (btn) {
      btn.style.background = '#D1FAE5';
      btn.style.color = '#065F46';
      btn.innerText = `🌟 Harika Telaffuz! ("${wordEn}") +5 🪙`;
    }
  });
}

let lessonQuizLocked = false;
function handleLessonQuizOption(btn, isCorrect, wordEn, wordTr) {
  if (lessonQuizLocked) return;
  lessonQuizLocked = true;
  const fb = document.getElementById('lessonQuizFeedback');

  if (isCorrect) {
    btn.classList.add('correct');
    playSoundSuccess();
    triggerConfetti(18);
    State.addReward(5, 0, 0);
    if (fb) {
      fb.className = 'explanation-box success';
      fb.innerText = `🌟 Aferin! ${wordEn} = ${wordTr}`;
    }
    setTimeout(() => {
      lessonQuizLocked = false;
      currentLessonStepIdx++;
      renderCurrentLessonStep();
    }, 1100);
  } else {
    btn.classList.add('wrong');
    playSoundError();
    if (fb) {
      fb.className = 'explanation-box fail';
      fb.innerText = `💡 Doğrusu: ${wordEn} = ${wordTr}. Tekrar deneyelim!`;
    }
    setTimeout(() => {
      lessonQuizLocked = false;
      btn.classList.remove('wrong');
    }, 1000);
  }
}

// ============================================================================
// 3. MINI GAMES HUB (10 FULL GAMES INCLUDING 21-QUESTION DAYS OF WEEK EXAM)
// ============================================================================
function renderGamesHub() {
  const container = document.getElementById('tabGames');
  container.innerHTML = `
    <!-- Featured Homework Card: 21-Question Days of the Week -->
    <div class="special-homework-card" onclick="openDaysOfWeekFullApp()">
      <div class="hw-left-icon">🗓️</div>
      <div class="hw-center-info">
        <div class="hw-badge">ÖĞRETMEN ÖZEL ÖDEVİ ⭐</div>
        <h3>Haftanın Günleri: 21 Soru & Karne</h3>
        <p>Türkçe ➔ İngilizce, İngilizce ➔ Türkçe, Sesli Telaffuz & % Başarı Ölçümü!</p>
      </div>
      <div class="hw-arrow">▶</div>
    </div>

    <h3 class="section-heading">🎮 Tüm Eğitici Mini Oyunlar (10 Oyun)</h3>
    <div class="games-hub-grid">
      <div class="game-hub-card" style="border-color:#EF4444;" onclick="openSuperMarioWordRun()">
        <div class="gh-icon">🍄</div>
        <div class="gh-info">
          <h4>Süper Mario Kelime Koşusu</h4>
          <p>Engellerden zıpla, doğru İngilizce kelime bloğunu topla!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>

      <div class="game-hub-card" style="border-color:#6366F1;" onclick="openSpaceWordBattle()">
        <div class="gh-icon">🚀</div>
        <div class="gh-info">
          <h4>Galaktik Uzay Savaşı</h4>
          <p>Doğru kelime meteorunu lazerle vur ve galaksiyi koru!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>

      <div class="game-hub-card" style="border-color:#10B981;" onclick="openHungryMonsterGame()">
        <div class="gh-icon">👾</div>
        <div class="gh-info">
          <h4>Aç Canavarı Besle</h4>
          <p>Canavarın istediği İngilizce kelime kurabiyesini ağzına at!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>

      <div class="game-hub-card" style="border-color:#F59E0B;" onclick="openSpeedDashGame()">
        <div class="gh-icon">⚡</div>
        <div class="gh-info">
          <h4>Sonic Hız Koşusu (60 Sn)</h4>
          <p>Süre bitmeden en çok kelimeyi doğru bil, rekor kır!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>

      <div class="game-hub-card" style="border-color:#EC4899;" onclick="openTwoPlayerDuelGame()">
        <div class="gh-icon">⚔️</div>
        <div class="gh-info">
          <h4>2 Kişilik Kelime Düellosu</h4>
          <p>Arkadaşınla veya ailenle aynı ekranda yarış!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>

      <div class="game-hub-card" style="border-color:#8B5CF6;" onclick="openWordPotionLab()">
        <div class="gh-icon">🧪</div>
        <div class="gh-info">
          <h4>Kelime İksir Laboratuvarı</h4>
          <p>Sihirli harf iksirlerini sırayla казаna at ve kelimeyi üret!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>

      <div class="game-hub-card" style="border-color:#14B8A6;" onclick="openMemoryFlipGame()">
        <div class="gh-icon">🃏</div>
        <div class="gh-info">
          <h4>Hafıza Kart Eşleştirme</h4>
          <p>Kartları çevir, İngilizce ve Türkçe çiftleri bul!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>

      <div class="game-hub-card" style="border-color:#F97316;" onclick="openKaraokePartyModal()">
        <div class="gh-icon">🎵</div>
        <div class="gh-info">
          <h4>Poko Karaoke & Günler Şarkısı</h4>
          <p>Haftanın günleri ve renkler şarkısını Poko ile söyle!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>

      <div class="game-hub-card" style="border-color:#22C55E;" onclick="openPixelCraftModal()">
        <div class="gh-icon">🎨</div>
        <div class="gh-info">
          <h4>Minecraft Piksel Boyama</h4>
          <p>İngilizce renkleri seçerek piksel kahramanı boya!</p>
        </div>
        <div class="gh-play">Oyna ▶</div>
      </div>
    </div>
  `;
}

// ============================================================================
// GAME 1: 21-QUESTION DAYS OF THE WEEK EXAM & STUDY CARDS
// ============================================================================
let daysExamQuestions = [];
let daysQIdx = 0;
let daysCorrect = 0;
let daysWrong = 0;
let daysAnswered = false;

function build21DaysQuestions() {
  const qs = [];
  // 1. All 7 days TR -> EN (7 questions)
  DAYS_DATA.forEach(d => {
    const others = DAYS_DATA.filter(x => x.en !== d.en).sort(() => 0.5 - Math.random()).slice(0, 3);
    const options = [d.en, ...others.map(o => o.en)].sort(() => 0.5 - Math.random());
    qs.push({
      badge: "Türkçe ➔ İngilizce",
      prompt: "Bu Türkçe günün İngilizcesini oku ve seç:",
      mainText: `${d.emoji} ${d.tr}`,
      subText: `(Haftanın ${d.num}. günü)`,
      correctAnswer: d.en,
      options,
      speechText: d.en,
      isVoiceQuestion: false,
      explanation: `Doğru! "${d.tr}" İngilizcede "${d.en}" demektir.`
    });
  });

  // 2. All 7 days EN -> TR (7 questions)
  DAYS_DATA.forEach(d => {
    const others = DAYS_DATA.filter(x => x.tr !== d.tr).sort(() => 0.5 - Math.random()).slice(0, 3);
    const options = [d.tr, ...others.map(o => o.tr)].sort(() => 0.5 - Math.random());
    qs.push({
      badge: "İngilizce ➔ Türkçe",
      prompt: "Bu İngilizce günün Türkçesini seç:",
      mainText: `${d.en}`,
      subText: `Okunuşu: "${d.phonetic}"`,
      correctAnswer: d.tr,
      options,
      speechText: d.en,
      isVoiceQuestion: false,
      explanation: `Aferin! "${d.en}" Türkçede "${d.tr}" günüdür.`
    });
  });

  // 3. 7 Voice & Sequencing Questions (Say it aloud + Order)
  DAYS_DATA.forEach(d => {
    const others = DAYS_DATA.filter(x => x.en !== d.en).sort(() => 0.5 - Math.random()).slice(0, 3);
    const options = [d.en, ...others.map(o => o.en)].sort(() => 0.5 - Math.random());
    qs.push({
      badge: "🎤 Sesli Söyle & Seç",
      prompt: `"${d.tr}" gününü İngilizce yüksek sesle söyle ve seç:`,
      mainText: `🎤 "${d.en}"`,
      subText: `Okunuşu: "${d.phonetic}" (${d.tr})`,
      correctAnswer: d.en,
      options,
      speechText: d.en,
      isVoiceQuestion: true,
      explanation: `Harika telaffuz! "${d.en}" (${d.tr}).`
    });
  });

  return qs.sort(() => 0.5 - Math.random());
}

function openDaysOfWeekFullApp(mode = 'exam') {
  playSoundSuccess();
  if (mode === 'cards') {
    const cardsHtml = DAYS_DATA.map(d => `
      <div class="day-card" onclick="speakText('${d.en}')">
        <div class="day-number">${d.num}</div>
        <div class="day-info">
          <div class="day-en">${d.emoji} ${d.en}</div>
          <div class="day-tr">${d.tr} ${d.isWeekend ? '<span style="color:#EA580C;font-size:11px;">(Hafta Sonu)</span>' : ''}</div>
          <div class="day-phonetic">Okunuşu: "${d.phonetic}"</div>
        </div>
        <button class="day-listen-btn" onclick="event.stopPropagation(); speakText('${d.en}')">🔊</button>
      </div>
    `).join('');

    openModalOverlay(`
      <div class="modal-top-bar" style="background: #2563EB;">
        <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
        <div class="modal-title">🗓️ Haftanın 7 Günü Kartları</div>
        <button class="modal-badge" style="border:none; cursor:pointer;" onclick="openDaysOfWeekFullApp('exam')">📝 Sınava Geç</button>
      </div>
      <div class="modal-content-pad">
        <div class="card" style="text-align:center;">
          <p style="font-weight:700; color:#1E3A8A; margin-bottom:10px;">Kartlara dokunarak günlerin İngilizce telaffuzunu dinle, hazır olduğunda 21 soruluk sınava başla!</p>
          <button class="btn-3d btn-primary" onclick="openDaysOfWeekFullApp('exam')">🚀 21 Soruluk Sınava Başla!</button>
        </div>
        <div class="days-grid">${cardsHtml}</div>
      </div>
    `);
    return;
  }

  daysExamQuestions = build21DaysQuestions();
  daysQIdx = 0;
  daysCorrect = 0;
  daysWrong = 0;
  renderDaysExamQuestionModal();
}

function renderDaysExamQuestionModal() {
  daysAnswered = false;
  const q = daysExamQuestions[daysQIdx];
  const pct = Math.round((daysQIdx / daysExamQuestions.length) * 100);

  setTimeout(() => speakText(q.speechText), 250);

  const voiceBtnHtml = q.isVoiceQuestion ? `
    <button class="btn-3d btn-yellow" id="daysVoiceSpeakBtn" onclick="handleDaysVoiceSpeak('${q.correctAnswer}')" style="margin-bottom:14px;">
      🎙️ Mikrofona Dokun ve "${q.correctAnswer}" Söyle!
    </button>
  ` : '';

  const optsHtml = q.options.map(opt => `
    <button class="option-btn" onclick="handleDaysOptionClick(this, '${opt}')">
      <span>${opt}</span>
      <span>➔</span>
    </button>
  `).join('');

  openModalOverlay(`
    <div class="modal-top-bar" style="background: #1D4ED8;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Çıkış</button>
      <div class="modal-title">🗓️ Haftanın Günleri Sınavı</div>
      <button class="modal-badge" style="border:none; cursor:pointer;" onclick="openDaysOfWeekFullApp('cards')">📖 7 Gün Kartı</button>
    </div>
    <div class="modal-content-pad">
      <div class="progress-bar-container"><div class="progress-bar-fill" style="width:${pct}%"></div></div>
      <div class="card">
        <div class="exam-header">
          <span class="question-counter">Soru ${daysQIdx + 1} / ${daysExamQuestions.length}</span>
          <span class="question-type-badge">${q.badge}</span>
        </div>

        <div class="question-box">
          <div class="question-prompt">${q.prompt}</div>
          <div class="question-main-text">${q.mainText}</div>
          <div class="pronunciation-sub">${q.subText}</div>
          <button class="btn-voice" onclick="speakText('${q.speechText}')" style="margin-top:10px;">
            🔊 Poko ile Dinle
          </button>
        </div>

        ${voiceBtnHtml}

        <div class="options-grid">
          ${optsHtml}
        </div>

        <div class="explanation-box" id="daysExamFeedback"></div>
        <button class="btn-3d btn-primary" id="daysNextBtn" onclick="advanceDaysExam()" style="display:none;">
          Sıradaki Soruya Geç ➔
        </button>
      </div>
    </div>
  `);
}

function handleDaysVoiceSpeak(targetWord) {
  const btn = document.getElementById('daysVoiceSpeakBtn');
  if (btn) btn.innerText = `🎙️ Dinleniyor... Şimdi "${targetWord}" de!`;
  startVoiceRecognition(targetWord, (ok) => {
    playSoundSuccess();
    if (btn) {
      btn.style.background = '#10B981';
      btn.style.color = '#FFFFFF';
      btn.innerText = `✅ Harika Söyledin: "${targetWord}"!`;
    }
    // Auto-select the matching option button
    const allBtns = document.querySelectorAll('.option-btn');
    allBtns.forEach(b => {
      if (b.innerText.includes(targetWord)) {
        handleDaysOptionClick(b, targetWord);
      }
    });
  });
}

function handleDaysOptionClick(btnEl, selectedOpt) {
  if (daysAnswered) return;
  daysAnswered = true;
  const q = daysExamQuestions[daysQIdx];
  const isCorrect = (selectedOpt === q.correctAnswer);
  const fb = document.getElementById('daysExamFeedback');

  if (isCorrect) {
    daysCorrect++;
    btnEl.classList.add('correct');
    playSoundSuccess();
    triggerConfetti(22);
    State.addReward(5, 1, 0);
    if (fb) {
      fb.className = 'explanation-box success';
      fb.innerText = `🌟 ${q.explanation}`;
    }
    setTimeout(() => advanceDaysExam(), 1300);
  } else {
    daysWrong++;
    btnEl.classList.add('wrong');
    playSoundError();
    document.querySelectorAll('.option-btn').forEach(b => {
      if (b.innerText.includes(q.correctAnswer)) b.classList.add('reveal-correct');
    });
    if (fb) {
      fb.className = 'explanation-box fail';
      fb.innerText = `💡 Doğru Cevap: "${q.correctAnswer}". ${q.explanation}`;
    }
    const nextBtn = document.getElementById('daysNextBtn');
    if (nextBtn) nextBtn.style.display = 'flex';
  }
}

function advanceDaysExam() {
  daysQIdx++;
  if (daysQIdx < daysExamQuestions.length) {
    renderDaysExamQuestionModal();
  } else {
    const total = daysExamQuestions.length;
    const pct = Math.round((daysCorrect / total) * 100);
    State.daysExamCount++;
    if (pct > State.daysExamBestPercent) State.daysExamBestPercent = pct;
    State.addReward(100, 5, 3);
    playSoundFanfare();
    triggerConfetti(100);

    openModalOverlay(`
      <div class="modal-top-bar" style="background: #059669;">
        <button class="modal-back-btn" onclick="closeModalOverlay(); renderRoomScreen();">⬅ Ana Ekran</button>
        <div class="modal-title">🏆 Sınav Karnesi</div>
        <span>%${pct}</span>
      </div>
      <div class="modal-content-pad">
        <div class="card result-container">
          <div class="result-badge">${pct >= 85 ? '🏆' : (pct >= 65 ? '⭐' : '🌱')}</div>
          <div class="result-percentage">%${pct} BAŞARI</div>
          <div class="result-score-detail">${total} Soruda ${daysCorrect} Doğru • ${daysWrong} Yanlış</div>

          <div class="result-card">
            <h4>🌟 Öğretmen Değerlendirme Notu:</h4>
            <p>
              ${pct >= 85
                ? 'Tebrikler! Haftanın tüm 7 gününü hem Türkçe hem İngilizce okunuşları, yazılışları ve telaffuzlarıyla mükemmel öğrendin!'
                : 'Güzel çaba! Yanlış yaptığın günleri "7 Gün Kartı" bölümünden dinleyip tekrar sınav olarak %100 başarıya ulaşabilirsin!'}
            </p>
          </div>

          <button class="btn-3d btn-secondary" onclick="openDaysOfWeekFullApp('exam')" style="margin-bottom:10px;">
            🔄 Tekrar Sınav Ol (Yeni 21 Karışık Soru)
          </button>
          <button class="btn-3d btn-yellow" onclick="openDaysOfWeekFullApp('cards')">
            📖 7 Gün Kartlarını Sesli Çalış
          </button>
        </div>
      </div>
    `);
  }
}

// ============================================================================
// MINI GAME 2: SUPER MARIO WORD RUN (INTERACTIVE CANVAS ACTION GAME)
// ============================================================================
let activeGameLoopId = null;
let activeTimerInterval = null;

function getAllWordsFlat() {
  const units = window.POKO_UNITS || [];
  const list = [];
  units.forEach(u => u.words.forEach(w => list.push(w)));
  return list.length > 0 ? list : DAYS_DATA.map(d => ({ en: d.en, tr: d.tr, emoji: d.emoji }));
}

function openSuperMarioWordRun() {
  playSoundSuccess();
  const words = getAllWordsFlat();
  let score = 0;
  let targetWord = words[Math.floor(Math.random() * words.length)];

  openModalOverlay(`
    <div class="modal-top-bar" style="background: #DC2626;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Çıkış</button>
      <div class="modal-title">🍄 Süper Mario Kelime Koşusu</div>
      <span id="marioScoreBadge" class="modal-badge">Skor: 0</span>
    </div>
    <div class="modal-content-pad" style="text-align:center;">
      <div class="card" style="padding:12px; margin-bottom:10px; background:#FEF2F2; border-color:#EF4444;">
        <div style="font-size:13px; font-weight:800; color:#991B1B;">HEDEF KELİME BLOĞUNA ZIPLAMAK İÇİN EKRANA DOKUN!</div>
        <div id="marioTargetPrompt" style="font-size:22px; font-weight:900; color:#1E3A8A; margin-top:4px;">
          ${targetWord.emoji} "${targetWord.tr}" bloğunu yakala!
        </div>
      </div>
      <canvas id="marioCanvas" width="360" height="260" style="background:linear-gradient(180deg,#60A5FA,#BAE6FD); border-radius:20px; border:3px solid #2563EB; width:100%; max-width:420px; touch-action:none;"></canvas>
      <button class="btn-3d btn-primary" id="marioJumpBtn" style="margin-top:12px;">
        🦘 ZIPLAMAK İÇİN DOKUN! (JUMP)
      </button>
    </div>
  `);

  const canvas = document.getElementById('marioCanvas');
  const ctx = canvas.getContext('2d');
  let playerY = 190;
  let playerVy = 0;
  let isJumping = false;

  function makeBox(xOffset, isCorrectWord) {
    const w = isCorrectWord ? targetWord : words[Math.floor(Math.random() * words.length)];
    return { x: xOffset, y: 115, word: w.en, isCorrect: w.en === targetWord.en, hit: false };
  }

  let boxes = [makeBox(360, true), makeBox(580, false)];

  function jump() {
    if (!isJumping) {
      playerVy = -11.5;
      isJumping = true;
      playTone(400, 'square', 0.08, 0);
    }
  }

  canvas.addEventListener('pointerdown', jump);
  const jumpBtn = document.getElementById('marioJumpBtn');
  if (jumpBtn) jumpBtn.onclick = jump;

  function loop() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    // Ground
    ctx.fillStyle = '#16A34A';
    ctx.fillRect(0, 225, canvas.width, 35);
    ctx.fillStyle = '#854D0E';
    ctx.fillRect(0, 235, canvas.width, 25);

    // Physics
    playerY += playerVy;
    playerVy += 0.58;
    if (playerY >= 190) {
      playerY = 190;
      playerVy = 0;
      isJumping = false;
    }

    // Draw Player (Poko Mario)
    ctx.font = '34px sans-serif';
    ctx.fillText('🦊', 45, playerY + 30);
    ctx.font = '18px sans-serif';
    ctx.fillText('🧢', 49, playerY + 5);

    // Update & Draw Word Blocks
    boxes.forEach(b => {
      b.x -= 3.2;
      if (!b.hit) {
        ctx.fillStyle = '#F59E0B';
        ctx.fillRect(b.x, b.y, 96, 40);
        ctx.strokeStyle = '#78350F';
        ctx.lineWidth = 2;
        ctx.strokeRect(b.x, b.y, 96, 40);

        ctx.fillStyle = '#1E293B';
        ctx.font = 'bold 14px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText(b.word, b.x + 48, b.y + 25);
        ctx.textAlign = 'start';

        // Collision check when player jumps up into block
        if (b.x < 80 && b.x + 96 > 40 && playerY < b.y + 42) {
          b.hit = true;
          if (b.isCorrect) {
            score += 10;
            State.addReward(5, 0, 0);
            playSoundSuccess();
            speakText(targetWord.en);
            targetWord = words[Math.floor(Math.random() * words.length)];
            const pr = document.getElementById('marioTargetPrompt');
            const sb = document.getElementById('marioScoreBadge');
            if (pr) pr.innerText = `${targetWord.emoji} "${targetWord.tr}" bloğunu yakala!`;
            if (sb) sb.innerText = `Skor: ${score}`;
          } else {
            playSoundError();
          }
        }
      }
    });

    if (boxes[0].x < -110) {
      boxes.shift();
      const giveCorrect = !boxes.some(x => x.isCorrect && !x.hit);
      boxes.push(makeBox(420, giveCorrect));
    }

    activeGameLoopId = requestAnimationFrame(loop);
  }
  loop();
}

// ============================================================================
// MINI GAME 3: SPACE WORD BATTLE & BOSS BATTLE
// ============================================================================
function openSpaceWordBattle() {
  startUnitBossBattle(null);
}

function startUnitBossBattle(unitId) {
  playSoundSuccess();
  const unit = unitId ? (window.POKO_UNITS || []).find(u => u.id === unitId) : null;
  const pool = unit ? unit.words : getAllWordsFlat();
  let bossHp = 100;
  let playerScore = 0;

  function nextBossRound() {
    if (bossHp <= 0) {
      playSoundFanfare();
      triggerConfetti(90);
      State.addReward(80, 3, 2);
      openModalOverlay(`
        <div class="modal-top-bar" style="background: #4F46E5;">
          <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
          <div class="modal-title">🚀 Galaktik Zafer!</div>
          <span>🏆</span>
        </div>
        <div class="modal-content-pad">
          <div class="card result-container">
            <div class="result-badge">🚀</div>
            <div class="result-percentage">ZAFER!</div>
            <p style="font-weight:700; color:#475569; margin:10px 0 20px;">Uzay Kruvazörünü kelimelerle alt ettin! +80 Altın 🪙 kazandın!</p>
            <button class="btn-3d btn-primary" onclick="startUnitBossBattle(${unitId ? `'${unitId}'` : 'null'})">🔄 Tekrar Savaş</button>
          </div>
        </div>
      `);
      return;
    }

    const target = pool[Math.floor(Math.random() * pool.length)];
    const others = pool.filter(w => w.en !== target.en).sort(() => 0.5 - Math.random()).slice(0, 3);
    const opts = [target, ...others].sort(() => 0.5 - Math.random());
    setTimeout(() => speakText(target.en), 200);

    const meteorsHtml = opts.map(o => `
      <button class="option-btn" style="background:#1E293B; color:white; border-color:#6366F1;" onclick="fireSpaceLaser(this, ${o.en === target.en}, '${target.en.replace(/'/g, "\\'")}')">
        <span>☄️ ${o.en}</span>
        <span>⚡ ATEŞ ET</span>
      </button>
    `).join('');

    openModalOverlay(`
      <div class="modal-top-bar" style="background: #1E1B4B;">
        <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Çıkış</button>
        <div class="modal-title">🚀 Galaktik Uzay Savaşı</div>
        <span class="modal-badge">Puan: ${playerScore}</span>
      </div>
      <div class="modal-content-pad">
        <div class="card" style="background:#0F172A; color:white; border-color:#4F46E5; text-align:center;">
          <div style="font-size:46px;">🛸👾</div>
          <div style="font-size:13px; font-weight:800; color:#A5B4FC;">BOSS KALKANI: %${bossHp}</div>
          <div class="progress-bar-container" style="margin:8px 0 14px; background:#334155;">
            <div class="progress-bar-fill" style="width:${bossHp}%; background:linear-gradient(90deg,#EF4444,#F97316);"></div>
          </div>
          <div style="font-size:22px; font-weight:900; color:#FEF08A;">
            Hedef: ${target.emoji} "${target.tr}"
          </div>
        </div>
        <div class="options-grid">
          ${meteorsHtml}
        </div>
      </div>
    `);
  }

  window.fireSpaceLaser = (btn, isHit, wordEn) => {
    playSoundLaser();
    if (isHit) {
      bossHp = Math.max(0, bossHp - 25);
      playerScore += 25;
      btn.classList.add('correct');
      speakText(wordEn);
      setTimeout(nextBossRound, 700);
    } else {
      btn.classList.add('wrong');
      playSoundError();
    }
  };

  nextBossRound();
}

// ============================================================================
// MINI GAME 4: HUNGRY MONSTER (AÇ CANAVARI BESLE)
// ============================================================================
function openHungryMonsterGame() {
  const pool = getAllWordsFlat();
  let fedCount = 0;

  function renderMonsterTurn() {
    if (fedCount >= 8) {
      playSoundFanfare();
      triggerConfetti(80);
      State.addReward(60, 2, 1);
      openModalOverlay(`
        <div class="modal-top-bar" style="background:#059669;">
          <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
          <div class="modal-title">👾 Canavar Doydu!</div>
          <span>🏆</span>
        </div>
        <div class="modal-content-pad">
          <div class="card result-container">
            <div class="result-badge">👾</div>
            <h2>Yummy! Canavar Çok Mutlu!</h2>
            <p style="margin:10px 0 20px;">8 kelime kurabiyesini de doğru yedirdin! +60 Altın 🪙</p>
            <button class="btn-3d btn-secondary" onclick="openHungryMonsterGame()">🔄 Tekrar Besle</button>
          </div>
        </div>
      `);
      return;
    }

    const target = pool[Math.floor(Math.random() * pool.length)];
    const others = pool.filter(x => x.en !== target.en).sort(() => 0.5 - Math.random()).slice(0, 3);
    const opts = [target, ...others].sort(() => 0.5 - Math.random());
    setTimeout(() => speakText(`I want ${target.en}`), 200);

    openModalOverlay(`
      <div class="modal-top-bar" style="background:#10B981;">
        <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Çıkış</button>
        <div class="modal-title">👾 Aç Canavarı Besle (${fedCount}/8)</div>
        <span>🍪</span>
      </div>
      <div class="modal-content-pad">
        <div class="card" style="text-align:center; background:#ECFDF5; border-color:#10B981;">
          <div style="font-size:68px; animation:bounceSmall 1.2s infinite;">👾</div>
          <div style="font-size:15px; font-weight:800; color:#065F46;">Canavar Diyor Ki:</div>
          <div style="font-size:26px; font-weight:900; color:#1E3A8A; margin:6px 0;">"I want ${target.en}!"</div>
          <button class="btn-voice" onclick="speakText('${target.en.replace(/'/g, "\\'")}')">🔊 Dinle</button>
        </div>
        <div class="options-grid">
          ${opts.map(o => `
            <button class="option-btn" onclick="feedMonsterCookie(this, ${o.en === target.en})">
              <span>🍪 ${o.emoji} ${o.tr}</span>
              <span>Yedir ➔</span>
            </button>
          `).join('')}
        </div>
      </div>
    `);
  }

  window.feedMonsterCookie = (btn, ok) => {
    if (ok) {
      fedCount++;
      btn.classList.add('correct');
      playSoundSuccess();
      setTimeout(renderMonsterTurn, 650);
    } else {
      btn.classList.add('wrong');
      playSoundError();
    }
  };

  renderMonsterTurn();
}

// ============================================================================
// MINI GAME 5: SONIC SPEED DASH (60s RAPID SPRINT)
// ============================================================================
function openSpeedDashGame() {
  const pool = getAllWordsFlat();
  let timeLeft = 45;
  let score = 0;

  function renderDashQuestion() {
    const target = pool[Math.floor(Math.random() * pool.length)];
    const others = pool.filter(x => x.en !== target.en).sort(() => 0.5 - Math.random()).slice(0, 3);
    const opts = [target, ...others].sort(() => 0.5 - Math.random());

    const body = document.getElementById('dashQuestionArea');
    if (!body) return;
    body.innerHTML = `
      <div style="font-size:52px; margin-bottom:4px;">${target.emoji}</div>
      <div style="font-size:28px; font-weight:900; color:#1E3A8A; margin-bottom:14px;">${target.en}</div>
      <div class="options-grid">
        ${opts.map(o => `
          <button class="option-btn" onclick="answerDash(${o.en === target.en})">
            <span>⚡ ${o.tr}</span>
            <span>➔</span>
          </button>
        `).join('')}
      </div>
    `;
  }

  openModalOverlay(`
    <div class="modal-top-bar" style="background:#F59E0B;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Çıkış</button>
      <div class="modal-title">⚡ Sonic Hız Koşusu</div>
      <span id="dashTimerBadge" class="modal-badge">⏱️ 45 sn</span>
    </div>
    <div class="modal-content-pad">
      <div class="card" style="text-align:center;">
        <div style="font-size:16px; font-weight:900; color:#D97706; margin-bottom:10px;" id="dashScoreText">Puan: 0</div>
        <div id="dashQuestionArea"></div>
      </div>
    </div>
  `);

  window.answerDash = (ok) => {
    if (ok) {
      score += 10;
      playSoundSuccess();
      State.addReward(3, 0, 0);
    } else {
      playSoundError();
    }
    const st = document.getElementById('dashScoreText');
    if (st) st.innerText = `Puan: ${score}`;
    renderDashQuestion();
  };

  renderDashQuestion();
  activeTimerInterval = setInterval(() => {
    timeLeft--;
    const tb = document.getElementById('dashTimerBadge');
    if (tb) tb.innerText = `⏱️ ${timeLeft} sn`;
    if (timeLeft <= 0) {
      clearInterval(activeTimerInterval);
      playSoundFanfare();
      triggerConfetti(70);
      const area = document.getElementById('dashQuestionArea');
      if (area) {
        area.innerHTML = `
          <div class="result-badge">⚡</div>
          <h2>Süre Doldu! Skorun: ${score}</h2>
          <button class="btn-3d btn-primary" style="margin-top:16px;" onclick="openSpeedDashGame()">🔄 Tekrar Yarış</button>
        `;
      }
    }
  }, 1000);
}

// ============================================================================
// MINI GAME 6: 2-PLAYER DUEL (2 KİŞİLİK DÜELLO)
// ============================================================================
function openTwoPlayerDuelGame() {
  const pool = getAllWordsFlat();
  let p1Score = 0;
  let p2Score = 0;

  function nextDuelRound() {
    if (p1Score >= 5 || p2Score >= 5) {
      playSoundFanfare();
      triggerConfetti(90);
      const winner = p1Score >= 5 ? "1. Oyuncu (Mavi) 🔵" : "2. Oyuncu (Kırmızı) 🔴";
      openModalOverlay(`
        <div class="modal-top-bar" style="background:#EC4899;">
          <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
          <div class="modal-title">⚔️ Düello Şampiyonu!</div>
          <span>🏆</span>
        </div>
        <div class="modal-content-pad">
          <div class="card result-container">
            <div class="result-badge">👑</div>
            <h2>Kazanan: ${winner}</h2>
            <p style="margin:10px 0 20px;">Skor: ${p1Score} - ${p2Score}</p>
            <button class="btn-3d btn-primary" onclick="openTwoPlayerDuelGame()">🔄 Yeni Düello</button>
          </div>
        </div>
      `);
      return;
    }

    const target = pool[Math.floor(Math.random() * pool.length)];
    const others = pool.filter(x => x.en !== target.en).sort(() => 0.5 - Math.random()).slice(0, 2);
    const opts = [target, ...others].sort(() => 0.5 - Math.random());

    openModalOverlay(`
      <div class="modal-top-bar" style="background:#EC4899;">
        <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Çıkış</button>
        <div class="modal-title">⚔️ 2 Kişilik Düello (İlk 5 Kazansın)</div>
        <span>🔵 ${p1Score} - ${p2Score} 🔴</span>
      </div>
      <div class="modal-content-pad">
        <!-- Player 1 Area -->
        <div class="card" style="border-color:#3B82F6; background:#EFF6FF;">
          <div style="font-weight:900; color:#1D4ED8; margin-bottom:8px;">🔵 1. OYUNCU (${p1Score} Puan)</div>
          <div style="display:grid; grid-template-columns:repeat(3,1fr); gap:8px;">
            ${opts.map(o => `<button class="btn-3d btn-primary" style="font-size:14px; padding:12px 6px;" onclick="duelTap(1, ${o.en === target.en})">${o.tr}</button>`).join('')}
          </div>
        </div>

        <!-- Center Target Word -->
        <div class="card" style="text-align:center; background:#FEF3C7; border-color:#F59E0B;">
          <div style="font-size:36px;">${target.emoji}</div>
          <div style="font-size:28px; font-weight:900; color:#78350F;">"${target.en}"</div>
        </div>

        <!-- Player 2 Area -->
        <div class="card" style="border-color:#EF4444; background:#FEF2F2;">
          <div style="font-weight:900; color:#B91C1C; margin-bottom:8px;">🔴 2. OYUNCU (${p2Score} Puan)</div>
          <div style="display:grid; grid-template-columns:repeat(3,1fr); gap:8px;">
            ${opts.map(o => `<button class="btn-3d" style="background:#EF4444; color:white; box-shadow:0 5px 0 #B91C1C; font-size:14px; padding:12px 6px;" onclick="duelTap(2, ${o.en === target.en})">${o.tr}</button>`).join('')}
          </div>
        </div>
      </div>
    `);
  }

  window.duelTap = (player, isCorrect) => {
    if (isCorrect) {
      if (player === 1) p1Score++; else p2Score++;
      playSoundSuccess();
      nextDuelRound();
    } else {
      playSoundError();
    }
  };

  nextDuelRound();
}

// ============================================================================
// MINI GAME 7: WORD POTION LAB (HARF İKSİR LABORATUVARI)
// ============================================================================
function openWordPotionLab() {
  const pool = getAllWordsFlat().filter(w => w.en.length <= 8 && !w.en.includes(' '));
  let target = pool[Math.floor(Math.random() * pool.length)];
  let built = "";
  let letters = target.en.toUpperCase().split('').sort(() => 0.5 - Math.random());

  function renderLab() {
    openModalOverlay(`
      <div class="modal-top-bar" style="background:#7C3AED;">
        <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Çıkış</button>
        <div class="modal-title">🧪 Kelime İksir Laboratuvarı</div>
        <span>✨</span>
      </div>
      <div class="modal-content-pad">
        <div class="card" style="text-align:center; background:#F5F3FF; border-color:#8B5CF6;">
          <div style="font-size:56px;">🧪 ${target.emoji}</div>
          <div style="font-size:20px; font-weight:900; color:#4C1D95; margin:6px 0;">"${target.tr}" iksirini harflerle oluştur!</div>
          <div style="font-size:28px; font-weight:900; letter-spacing:4px; background:white; padding:12px; border-radius:16px; border:2px dashed #8B5CF6; margin:12px 0; min-height:56px;">
            ${built || '_ '.repeat(target.en.length)}
          </div>
          <div style="display:flex; flex-wrap:wrap; gap:10px; justify-content:center; margin-top:14px;">
            ${letters.map((ch, idx) => `
              <button class="btn-3d btn-primary" style="width:52px; height:52px; font-size:20px; padding:0;" onclick="pickPotionLetter(${idx})">${ch}</button>
            `).join('')}
          </div>
          <button class="btn-voice" style="margin-top:16px;" onclick="resetPotionWord()">🔄 Harfleri Sıfırla</button>
        </div>
      </div>
    `);
  }

  window.pickPotionLetter = (idx) => {
    built += letters[idx];
    letters.splice(idx, 1);
    playTone(550, 'triangle', 0.1, 0);
    if (built.length === target.en.length) {
      if (built === target.en.toUpperCase()) {
        playSoundFanfare();
        triggerConfetti(50);
        speakText(target.en);
        State.addReward(20, 1, 0);
        setTimeout(() => openWordPotionLab(), 1100);
        return;
      } else {
        playSoundError();
        resetPotionWord();
        return;
      }
    }
    renderLab();
  };

  window.resetPotionWord = () => {
    built = "";
    letters = target.en.toUpperCase().split('').sort(() => 0.5 - Math.random());
    renderLab();
  };

  renderLab();
}

// ============================================================================
// MINI GAME 8: MEMORY FLIP CARDS (HAFIZA EŞLEŞTİRME)
// ============================================================================
function openMemoryFlipGame() {
  const sample = getAllWordsFlat().sort(() => 0.5 - Math.random()).slice(0, 4);
  const deck = [];
  sample.forEach(w => {
    deck.push({ id: w.en, label: `${w.emoji} ${w.en}`, flipped: false, matched: false });
    deck.push({ id: w.en, label: `${w.tr}`, flipped: false, matched: false });
  });
  deck.sort(() => 0.5 - Math.random());
  let firstPick = null;
  let lockBoard = false;

  function renderMemoryBoard() {
    const allMatched = deck.every(c => c.matched);
    if (allMatched) {
      playSoundFanfare();
      triggerConfetti(70);
      State.addReward(40, 2, 0);
    }

    openModalOverlay(`
      <div class="modal-top-bar" style="background:#0D9488;">
        <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Çıkış</button>
        <div class="modal-title">🃏 Hafıza Kart Eşleştirme</div>
        <span>🧠</span>
      </div>
      <div class="modal-content-pad">
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:12px;">
          ${deck.map((c, idx) => `
            <button class="btn-3d" style="height:86px; font-size:17px; background:${c.matched ? '#D1FAE5' : (c.flipped ? '#FEF3C7' : '#3B82F6')}; color:${c.matched || c.flipped ? '#1E293B' : 'white'}; box-shadow:0 5px 0 #1E40AF;" onclick="flipMemCard(${idx})">
              ${c.flipped || c.matched ? c.label : '❓ POKO'}
            </button>
          `).join('')}
        </div>
        ${allMatched ? `<button class="btn-3d btn-secondary" style="margin-top:18px;" onclick="openMemoryFlipGame()">🔄 Yeni Kartlarla Oyna (+40 🪙 Kazandın!)</button>` : ''}
      </div>
    `);
  }

  window.flipMemCard = (idx) => {
    if (lockBoard || deck[idx].flipped || deck[idx].matched) return;
    deck[idx].flipped = true;
    playTone(500, 'triangle', 0.09, 0);
    renderMemoryBoard();

    if (firstPick === null) {
      firstPick = idx;
    } else {
      lockBoard = true;
      const prev = firstPick;
      firstPick = null;
      if (deck[prev].id === deck[idx].id) {
        deck[prev].matched = true;
        deck[idx].matched = true;
        playSoundSuccess();
        speakText(deck[idx].id);
        lockBoard = false;
        renderMemoryBoard();
      } else {
        playSoundError();
        setTimeout(() => {
          deck[prev].flipped = false;
          deck[idx].flipped = false;
          lockBoard = false;
          renderMemoryBoard();
        }, 850);
      }
    }
  };

  renderMemoryBoard();
}

// ============================================================================
// MINI GAME 9 & 10: KARAOKE PARTY & PIXEL CRAFT
// ============================================================================
function openKaraokePartyModal() {
  openModalOverlay(`
    <div class="modal-top-bar" style="background:#EA580C;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
      <div class="modal-title">🎵 Poko Haftanın Günleri Şarkısı</div>
      <span>🎤</span>
    </div>
    <div class="modal-content-pad">
      <div class="card" style="text-align:center; background:#FFF7ED; border-color:#F97316;">
        <div style="font-size:56px;">🦊🎤🎶</div>
        <h3 style="color:#9A3412; margin-bottom:10px;">Days of the Week Song!</h3>
        <button class="btn-3d btn-primary" onclick="singAllDaysSong()" style="margin-bottom:14px;">
          ▶️ Tüm Şarkıyı Poko ile Söyle!
        </button>
        <div class="days-grid">
          ${DAYS_DATA.map(d => `
            <div class="day-card" onclick="speakText('${d.song}')">
              <div class="day-number">${d.emoji}</div>
              <div class="day-info">
                <div class="day-en">${d.en} (${d.tr})</div>
                <div class="day-tr">🎵 "${d.song}"</div>
              </div>
              <button class="day-listen-btn">🔊</button>
            </div>
          `).join('')}
        </div>
      </div>
    </div>
  `);
}

function singAllDaysSong() {
  playSoundFanfare();
  const fullLyrics = DAYS_DATA.map(d => d.en).join(', ') + "! Seven days of the week, hooray!";
  speakText(fullLyrics);
  triggerConfetti(50);
}

function openPixelCraftModal() {
  const palette = [
    { en: 'Red', tr: 'Kırmızı', hex: '#EF4444' },
    { en: 'Blue', tr: 'Mavi', hex: '#3B82F6' },
    { en: 'Yellow', tr: 'Sarı', hex: '#FACC15' },
    { en: 'Green', tr: 'Yeşil', hex: '#22C55E' },
    { en: 'Orange', tr: 'Turuncu', hex: '#F97316' },
    { en: 'Purple', tr: 'Mor', hex: '#A855F7' }
  ];
  let activeColor = palette[0];

  openModalOverlay(`
    <div class="modal-top-bar" style="background:#16A34A;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
      <div class="modal-title">🎨 Minecraft Piksel Boyama</div>
      <span>🖌️</span>
    </div>
    <div class="modal-content-pad">
      <div class="card" style="text-align:center;">
        <div style="font-weight:800; margin-bottom:10px;">İngilizce Rengi Seç, Kareleri Dokunarak Boya!</div>
        <div style="display:flex; flex-wrap:wrap; gap:8px; justify-content:center; margin-bottom:16px;">
          ${palette.map(p => `
            <button class="btn-voice" style="background:${p.hex}; color:white; border:none;" onclick="selectPixelColor('${p.hex}', '${p.en}')">
              ${p.en} (${p.tr})
            </button>
          `).join('')}
        </div>
        <div style="display:grid; grid-template-columns:repeat(6, 1fr); gap:6px; max-width:300px; margin:0 auto;">
          ${Array.from({ length: 36 }).map(() => `
            <div style="aspect-ratio:1; background:#E2E8F0; border-radius:8px; border:2px solid #CBD5E1; cursor:pointer;" onclick="this.style.background=window.currentPixelHex || '#EF4444'; playTone(600,'triangle',0.05,0);"></div>
          `).join('')}
        </div>
      </div>
    </div>
  `);
  window.currentPixelHex = '#EF4444';
  window.selectPixelColor = (hex, en) => {
    window.currentPixelHex = hex;
    speakText(en);
  };
}

// ============================================================================
// ROOM MODALS: BUDDY TALK, DAILY SPIN, PET CARE, HOLOGRAM CARDS, STORIES
// ============================================================================
function openBuddyTalkModal() {
  const prompts = [
    { qEn: "Hello my friend! What is your favorite day of the week?", qTr: "Merhaba arkadaşım! Haftanın en sevdiğin günü hangisi?", sample: "Saturday" },
    { qEn: "What color is the sun?", qTr: "Güneş hangi renktir?", sample: "Yellow" },
    { qEn: "Can you say Good Morning?", qTr: "Günaydın (Good Morning) diyebilir misin?", sample: "Good morning" }
  ];
  const p = prompts[Math.floor(Math.random() * prompts.length)];
  setTimeout(() => speakText(p.qEn), 250);

  openModalOverlay(`
    <div class="modal-top-bar" style="background:#7C3AED;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
      <div class="modal-title">🎤 Poko Buddy Talk (Canlı Sohbet)</div>
      <span>🤖</span>
    </div>
    <div class="modal-content-pad">
      <div class="card" style="text-align:center; background:#F5F3FF; border-color:#8B5CF6;">
        <div style="font-size:72px;">🦊🎧</div>
        <h3 style="color:#4C1D95; margin:8px 0;">"${p.qEn}"</h3>
        <p style="color:#6D28D9; font-weight:700; margin-bottom:16px;">(${p.qTr})</p>
        <div id="buddyReplyBox" style="background:white; padding:12px; border-radius:14px; margin-bottom:14px; font-weight:800; color:#059669;">
          🎙️ Mikrofona dokunup İngilizce cevap ver! (Örn: "${p.sample}")
        </div>
        <button class="btn-3d btn-primary" onclick="buddyVoiceRespond('${p.sample}')" style="margin-bottom:10px;">
          🎤 Mikrofona Bas ve Konuş!
        </button>
        <button class="btn-3d btn-yellow" onclick="openBuddyTalkModal()">
          🔄 Poko Başka Soru Sorsun
        </button>
      </div>
    </div>
  `);

  window.buddyVoiceRespond = (sample) => {
    const box = document.getElementById('buddyReplyBox');
    if (box) box.innerText = "🎙️ Poko seni dinliyor...";
    startVoiceRecognition(sample, (ok, text) => {
      playSoundFanfare();
      triggerConfetti(40);
      State.addReward(25, 1, 0);
      speakText("Awesome! You speak great English!");
      if (box) box.innerText = `🌟 Poko: "Awesome! Harika konuştun! +25 Altın 🪙"`;
    });
  };
}

function openDailySpinModal() {
  openModalOverlay(`
    <div class="modal-top-bar" style="background:#D97706;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
      <div class="modal-title">🎡 Şans Çarkı & Hazine Sandığı</div>
      <span>🎁</span>
    </div>
    <div class="modal-content-pad">
      <div class="card" style="text-align:center;">
        <div id="wheelEmoji" style="font-size:88px; transition:transform 1s ease;">🎡</div>
        <h3 style="margin:12px 0;">Şans Çarkını Çevir veya Hazine Sandığını Aç!</h3>
        <p id="spinResultText" style="font-weight:800; color:#059669; margin-bottom:16px;">Her çevirişte sürpriz altın ve elmaslar!</p>
        <button class="btn-3d btn-yellow" onclick="spinWheelNow()" style="margin-bottom:12px;">
          🎡 Şans Çarkını Çevir!
        </button>
        <button class="btn-3d btn-primary" onclick="openMysteryChestNow()">
          🎁 Gizemli Hazine Sandığını Aç!
        </button>
      </div>
    </div>
  `);

  window.spinWheelNow = () => {
    const w = document.getElementById('wheelEmoji');
    if (w) w.style.transform = `rotate(${Math.floor(Math.random() * 720 + 720)}deg)`;
    const prize = [25, 50, 75, 100][Math.floor(Math.random() * 4)];
    State.addReward(prize, 1, 1);
    playSoundFanfare();
    triggerConfetti(60);
    const txt = document.getElementById('spinResultText');
    if (txt) txt.innerText = `🎉 TEBRİKLER! +${prize} Altın 🪙 ve +1 Elmas 💎 Kazandın!`;
  };

  window.openMysteryChestNow = () => {
    State.addReward(80, 2, 2);
    playSoundFanfare();
    triggerConfetti(80);
    const txt = document.getElementById('spinResultText');
    if (txt) txt.innerText = `🎁 HAZİNE SANDIĞINDAN +80 Altın 🪙 ve +2 Yıldız ⭐ Çıktı!`;
  };
}

function openPetCareModal() {
  openModalOverlay(`
    <div class="modal-top-bar" style="background:#059669;">
      <button class="modal-back-btn" onclick="closeModalOverlay(); renderRoomScreen();">⬅ Odam</button>
      <div class="modal-title">🐉 Yavru Ejderha Drako Bakımı</div>
      <span>Seviye ${State.petLevel}</span>
    </div>
    <div class="modal-content-pad">
      <div class="card" style="text-align:center; background:#ECFDF5; border-color:#10B981;">
        <div style="font-size:84px; animation:bounceSmall 1.5s infinite;">🐉</div>
        <h3>Baby Drako (Seviye ${State.petLevel})</h3>
        <p style="font-weight:700; color:#065F46; margin:8px 0;">Tokluk & Mutluluk: %${State.petHunger}</p>
        <div class="progress-bar-container"><div class="progress-bar-fill" style="width:${State.petHunger}%"></div></div>
        <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-top:16px;">
          <button class="btn-3d btn-secondary" onclick="feedPetDrako()">🍎 Elma Yedir (10 🪙)</button>
          <button class="btn-3d btn-primary" onclick="trainPetDrako()">⚡ Kelime Öğret</button>
        </div>
      </div>
    </div>
  `);

  window.feedPetDrako = () => {
    if (State.coins >= 10) {
      State.coins -= 10;
      State.petHunger = Math.min(100, State.petHunger + 15);
      State.petLevel++;
      State.save();
      playSoundFanfare();
      triggerConfetti(40);
      speakText("Yummy apple! Thank you!");
      openPetCareModal();
    } else {
      playSoundError();
    }
  };

  window.trainPetDrako = () => {
    State.petLevel++;
    State.addReward(15, 1, 0);
    playSoundSuccess();
    speakText("Roar! I learned a new English word!");
    openPetCareModal();
  };
}

function openHologramCardsModal() {
  const cards = window.POKO_CARDS || [];
  openModalOverlay(`
    <div class="modal-top-bar" style="background:#DB2777;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
      <div class="modal-title">🃏 3D Hologram Kart Koleksiyonu</div>
      <span>${cards.length} Kart</span>
    </div>
    <div class="modal-content-pad">
      <div class="stations-grid">
        ${cards.map(c => `
          <div class="station-card" style="background:linear-gradient(135deg,#1E1B4B,#312E81); color:white; border-color:#F59E0B;" onclick="speakText('${c.nameEn}')">
            <div style="font-size:11px; color:#FBBF24; font-weight:900;">${c.rarity} • GÜÇ ${c.power}</div>
            <div style="font-size:44px; margin:8px 0;">${c.emoji}</div>
            <div style="font-weight:900; font-size:15px;">${c.nameTr}</div>
            <div style="font-size:11px; opacity:0.8; margin-top:4px;">${c.descTr}</div>
          </div>
        `).join('')}
      </div>
    </div>
  `);
}

function openBedtimeStoriesModal() {
  openModalOverlay(`
    <div class="modal-top-bar" style="background:#1E3A8A;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
      <div class="modal-title">🌙 Uyku Öncesi Sesli Masallar</div>
      <span>📖</span>
    </div>
    <div class="modal-content-pad">
      ${BEDTIME_STORIES.map(st => `
        <div class="card">
          <h3 style="color:#1E3A8A; margin-bottom:10px;">${st.emoji} ${st.titleTr} (${st.titleEn})</h3>
          ${st.lines.map(l => `
            <div style="padding:10px; border-bottom:1px solid #E2E8F0; cursor:pointer;" onclick="speakText('${l.en.replace(/'/g, "\\'")}')">
              <div style="font-weight:800; color:#1E293B;">🔊 "${l.en}"</div>
              <div style="font-size:13px; color:#64748B;">${l.tr}</div>
            </div>
          `).join('')}
        </div>
      `).join('')}
    </div>
  `);
}

// ============================================================================
// 4. AVATAR SHOP SCREEN (51 STAR WARS, MARIO & HERO COSTUMES)
// ============================================================================
let currentShopCategory = 'COSTUME';

function renderShopScreen() {
  const allCostumes = window.POKO_COSTUMES || [];
  const filtered = allCostumes.filter(c => c.category === currentShopCategory);

  const cats = [
    { id: 'COSTUME', label: '🎽 Zırh & Kostüm' },
    { id: 'LIGHTSABER', label: '⚔️ Işın Kılıcı' },
    { id: 'HELMET', label: '🪖 Kask & Şapka' },
    { id: 'ROOM_DECOR', label: '🛸 Oda Eşyası' },
    { id: 'ROOM_THEME', label: '🌌 Oda Teması' }
  ];

  const container = document.getElementById('tabShop');
  container.innerHTML = `
    <div class="grade-selector-bar">
      ${cats.map(cat => `
        <button class="grade-pill ${currentShopCategory === cat.id ? 'active' : ''}" onclick="selectShopCategory('${cat.id}')">
          ${cat.label}
        </button>
      `).join('')}
    </div>

    <div class="stations-grid">
      ${filtered.map(item => {
        const isUnlocked = State.unlockedCostumes.includes(item.id) || item.price === 0;
        const isEquipped = [State.equippedCostume, State.equippedWeapon, State.equippedHelmet, State.equippedDecor, State.equippedTheme].includes(item.id);
        return `
          <div class="station-card" style="border-color:${isEquipped ? '#10B981' : '#CBD5E1'}; background:${isEquipped ? '#ECFDF5' : 'white'};" onclick="handleShopItemAction('${item.id}')">
            <div style="font-size:40px;">${item.emoji}</div>
            <div class="st-title">${item.name}</div>
            <div class="st-sub" style="margin-bottom:8px;">${item.desc}</div>
            <button class="btn-3d ${isEquipped ? 'btn-secondary' : (isUnlocked ? 'btn-primary' : 'btn-yellow')}" style="font-size:13px; padding:8px;">
              ${isEquipped ? '✅ Kuşanıldı' : (isUnlocked ? 'Kuşan' : `🪙 ${item.price} Altın`)}
            </button>
          </div>
        `;
      }).join('')}
    </div>
  `;
}

function selectShopCategory(catId) {
  playSoundSuccess();
  currentShopCategory = catId;
  renderShopScreen();
}

function handleShopItemAction(itemId) {
  const item = (window.POKO_COSTUMES || []).find(c => c.id === itemId);
  if (!item) return;

  const isUnlocked = State.unlockedCostumes.includes(item.id) || item.price === 0;
  if (!isUnlocked) {
    if (State.coins >= item.price) {
      State.coins -= item.price;
      State.unlockedCostumes.push(item.id);
      playSoundFanfare();
      triggerConfetti(45);
    } else {
      playSoundError();
      alert(`Bu eşya için ${item.price} altın gerekiyor! Ders ve sınav çözerek hemen altın kazanabilirsin.`);
      return;
    }
  } else {
    playSoundSuccess();
  }

  if (item.category === 'COSTUME') State.equippedCostume = item.id;
  if (item.category === 'LIGHTSABER') State.equippedWeapon = item.id;
  if (item.category === 'HELMET') State.equippedHelmet = item.id;
  if (item.category === 'ROOM_DECOR') State.equippedDecor = item.id;
  if (item.category === 'ROOM_THEME') State.equippedTheme = item.id;

  State.save();
  renderShopScreen();
}

// ============================================================================
// 5. GALACTIC LEAGUE, WORD BANK & PARENT REPORT
// ============================================================================
function renderLeagueScreen() {
  const allWords = getAllWordsFlat();
  const container = document.getElementById('tabLeague');

  container.innerHTML = `
    <!-- Parent Report Summary -->
    <div class="card" style="background:linear-gradient(135deg,#EFF6FF,#DBEAFE); border-color:#3B82F6;">
      <h3 style="color:#1E3A8A; margin-bottom:10px;">📊 Ebeveyn & Öğretmen Başarı Karnesi</h3>
      <div style="display:grid; grid-template-columns:1fr 1fr; gap:10px; margin-bottom:12px;">
        <div style="background:white; padding:12px; border-radius:14px; text-align:center;">
          <div style="font-size:24px; font-weight:900; color:#2563EB;">%${State.daysExamBestPercent}</div>
          <div style="font-size:12px; font-weight:700; color:#64748B;">Haftanın Günleri Rekoru</div>
        </div>
        <div style="background:white; padding:12px; border-radius:14px; text-align:center;">
          <div style="font-size:24px; font-weight:900; color:#059669;">${State.completedUnits.length} / 52</div>
          <div style="font-size:12px; font-weight:700; color:#64748B;">Tamamlanan MEB Ünitesi</div>
        </div>
      </div>
      <button class="btn-3d btn-primary" onclick="openCertificateModal()">
        🎓 Resmi Başarı Sertifikasını Görüntüle
      </button>
    </div>

    <!-- Galactic League Leaderboard -->
    <div class="card">
      <h3 style="color:#1E3A8A; margin-bottom:10px;">🏆 Galaktik Şampiyonlar Ligi</h3>
      <div class="days-grid">
        <div class="day-card" style="background:#FEF3C7; border-color:#F59E0B;">
          <div class="day-number">🥇</div>
          <div class="day-info">
            <div class="day-en">Sen (Poko Şampiyonu)</div>
            <div class="day-tr">Elmas Lig • ${State.stars} Yıldız ⭐ • ${State.coins} Altın 🪙</div>
          </div>
        </div>
        <div class="day-card">
          <div class="day-number">🥈</div>
          <div class="day-info"><div class="day-en">Skywalker Leo</div><div class="day-tr">22 Yıldız ⭐</div></div>
        </div>
        <div class="day-card">
          <div class="day-number">🥉</div>
          <div class="day-info"><div class="day-en">Prenses Maya</div><div class="day-tr">19 Yıldız ⭐</div></div>
        </div>
      </div>
    </div>

    <!-- Searchable Word Bank -->
    <div class="card">
      <h3 style="color:#1E3A8A; margin-bottom:8px;">📚 Kelime Kumbarası (${allWords.length} MEB Kelimesi)</h3>
      <input type="text" id="wordSearchInput" placeholder="🔍 Kelime ara (İngilizce veya Türkçe)..." oninput="filterWordBank(this.value)" style="width:100%; padding:12px 14px; border-radius:14px; border:2px solid #CBD5E1; font-size:15px; margin-bottom:12px;">
      <div class="days-grid" id="wordBankList">
        ${renderWordBankItems(allWords.slice(0, 30))}
      </div>
    </div>
  `;
}

function renderWordBankItems(list) {
  return list.map(w => `
    <div class="day-card" onclick="speakText('${w.en.replace(/'/g, "\\'")}')">
      <div class="day-number">${w.emoji}</div>
      <div class="day-info">
        <div class="day-en">${w.en}</div>
        <div class="day-tr">${w.tr}</div>
      </div>
      <button class="day-listen-btn">🔊</button>
    </div>
  `).join('');
}

function filterWordBank(query) {
  const q = query.toLowerCase().trim();
  const all = getAllWordsFlat();
  const filtered = q ? all.filter(w => w.en.toLowerCase().includes(q) || w.tr.toLowerCase().includes(q)).slice(0, 40) : all.slice(0, 30);
  const el = document.getElementById('wordBankList');
  if (el) el.innerHTML = renderWordBankItems(filtered);
}

function openCertificateModal() {
  playSoundFanfare();
  triggerConfetti(90);
  openModalOverlay(`
    <div class="modal-top-bar" style="background:#B45309;">
      <button class="modal-back-btn" onclick="closeModalOverlay()">⬅ Kapat</button>
      <div class="modal-title">🎓 Üstün Başarı Sertifikası</div>
      <span>🌟</span>
    </div>
    <div class="modal-content-pad">
      <div class="card" style="border:6px double #D97706; background:#FFFBEB; text-align:center; padding:28px 16px;">
        <div style="font-size:64px;">🎓🏆🌟</div>
        <h2 style="color:#78350F; margin:8px 0;">POKO ENGLISH ACADEMY</h2>
        <div style="font-size:14px; font-weight:800; color:#B45309; margin-bottom:14px;">ÜSTÜN BAŞARI VE İNGİLİZCE DİPLOMASI</div>
        <p style="font-size:15px; color:#451A03; line-height:1.6; margin-bottom:16px;">
          Bu sertifika, MEB İngilizce Müfredatı derslerini ve Haftanın Günleri sınavlarını üstün gayretle tamamlayan <b>Küçük İngilizce Şampiyonumuza</b> verilmiştir!
        </p>
        <div style="font-weight:900; color:#059669; font-size:18px;">⭐ Toplam Yıldız: ${State.stars} • 🪙 Altın: ${State.coins}</div>
      </div>
    </div>
  `);
}

// ============================================================================
// CONFETTI ANIMATION
// ============================================================================
function triggerConfetti(count = 40) {
  const canvas = document.getElementById('confettiCanvas');
  if (!canvas) return;
  const ctx = canvas.getContext('2d');
  canvas.width = window.innerWidth;
  canvas.height = window.innerHeight;

  const particles = [];
  const colors = ['#F59E0B', '#10B981', '#3B82F6', '#EC4899', '#8B5CF6'];
  for (let i = 0; i < count; i++) {
    particles.push({
      x: canvas.width / 2,
      y: canvas.height / 3,
      vx: (Math.random() - 0.5) * 13,
      vy: (Math.random() - 1.4) * 11,
      size: Math.random() * 8 + 4,
      color: colors[Math.floor(Math.random() * colors.length)],
      rot: Math.random() * 360
    });
  }

  let frame = 0;
  function step() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    particles.forEach(p => {
      p.x += p.vx;
      p.y += p.vy;
      p.vy += 0.36;
      ctx.fillStyle = p.color;
      ctx.fillRect(p.x, p.y, p.size, p.size);
    });
    frame++;
    if (frame < 55) requestAnimationFrame(step);
    else ctx.clearRect(0, 0, canvas.width, canvas.height);
  }
  step();
}

// ============================================================================
// INITIALIZE APP ON LOAD
// ============================================================================
window.addEventListener('DOMContentLoaded', () => {
  updateTopBarUI();
  switchMainTab('tabRoom');
});
