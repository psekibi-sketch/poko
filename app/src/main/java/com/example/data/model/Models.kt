package com.example.data.model

enum class GradeLevel(val title: String, val badgeText: String) {
    GRADE_1("1. Sınıf", "İlk Adım"),
    GRADE_2("2. Sınıf", "Süper Kaşif"),
    GRADE_3("3. Sınıf", "Yıldız Kaşif"),
    GRADE_4("4. Sınıf", "Galaktik Usta")
}

enum class ExerciseType {
    FLASHCARD,       // Dinle, Oku, Telaffuz et
    PICTURE_MATCH,   // Görseli doğru kelimeyle eşleştir
    AUDIO_QUIZ,      // Sesi dinle ve doğru kartı seç
    BALLOON_POP,     // Uçan doğru balonu patlat
    WORD_BUILDER,    // Harfleri sırasıyla dizerek kelimeyi oluştur
    SENTENCE_BUILDER // Kelime bloklarından cümle kur
}

enum class LessonType(val title: String, val badge: String, val iconEmoji: String) {
    STANDARD("Normal Ders", "MEB Dersi", "⭐"),
    TREASURE_CHEST("Hazine Sandığı", "Bonus Kelimeler", "🎁"),
    BOSS_CHALLENGE("Kelime Kalesi", "Şampiyon Boss", "👑"),
    MEMORY_LIGHTNING("Hafıza Şimşeği", "Süper Tekrar", "⚡")
}

data class WordItem(
    val id: String,
    val grade: GradeLevel,
    val unitId: Int,
    val english: String,
    val turkish: String,
    val phonetic: String,
    val emoji: String,
    val category: String,
    val exampleSentenceEn: String = "",
    val exampleSentenceTr: String = "",
    val mnemonicHint: String = ""
) {
    val displayMnemonic: String
        get() = if (mnemonicHint.isNotBlank()) {
            mnemonicHint
        } else {
            getSmartMnemonic(english, turkish, emoji)
        }
}

private fun getSmartMnemonic(en: String, tr: String, emoji: String): String {
    val lower = en.lowercase().trim()
    return when {
        lower.contains("hello") || lower.contains("hi") -> "Helva yerken arkadaşına Hello de! $emoji"
        lower.contains("cat") -> "Kedi ete Kat kat atlar! $emoji"
        lower.contains("dog") -> "Köpek Doğan güneşe havlar, Dog der! $emoji"
        lower.contains("bird") -> "Böğürtlen seven minik Bird! $emoji"
        lower.contains("red") -> "Reddettiğim elma kıpkırmızı Red! $emoji"
        lower.contains("blue") -> "Bulutlar masmavi Blue! $emoji"
        lower.contains("green") -> "Giren bahçıvan yemyeşil Green görür! $emoji"
        lower.contains("yellow") -> "Yeleli aslan sapsarı Yellow! $emoji"
        lower.contains("book") -> "Buket yapıp Book oku! $emoji"
        lower.contains("door") -> "Kapıda Dur, Door'u tıkla! $emoji"
        lower.contains("pencil") -> "Pense değil Kalem Pencil! $emoji"
        lower.contains("family") -> "Familfam hep birlikte tatlı Family! $emoji"
        lower.contains("father") || lower.contains("dad") -> "Faydalı baba Father! $emoji"
        lower.contains("mother") || lower.contains("mom") -> "Masa kuran canım Mother! $emoji"
        lower.contains("apple") -> "Eee pırasa değil tatlı Apple! $emoji"
        lower.contains("banana") -> "Bana nane değil sarı Banana ver! $emoji"
        lower.contains("water") -> "Vay terledin, bir bardak Water iç! $emoji"
        lower.contains("milk") -> "Milli içeceğimiz taze Milk! $emoji"
        lower.contains("run") -> "Koşup hiç yo-Run-ma! $emoji"
        lower.contains("jump") -> "Cama doğru Jump yapıp zıpla! $emoji"
        lower.contains("sleep") -> "Gözlerini silip mışıl mışıl Sleep yap! $emoji"
        lower.contains("eat") -> "Eti afiyetle Eat yapıp ye! $emoji"
        lower.contains("drink") -> "Derin bir nefes alıp Drink yap! $emoji"
        lower.contains("sun") -> "Sabah olunca uyan-San-a, Sun doğdu! $emoji"
        lower.contains("star") -> "Sahnede parlayan Star yıldız! $emoji"
        lower.contains("car") -> "Karlı yolda giden hızlı Car! $emoji"
        lower.contains("ball") -> "Balkondan atılan yuvarlak Ball! $emoji"
        lower.contains("friend") -> "Fırındaki simidi paylaşan Friend! $emoji"
        lower.contains("school") -> "Sabah erkenden School yoluna! $emoji"
        lower.contains("teacher") -> "Tiçır öğretmenimiz bilgi saçar! $emoji"
        lower.contains("doctor") -> "Doktor bizi iyileştirir Doctor! $emoji"
        lower.contains("happy") -> "Hep iyi olunca Happy oluruz! $emoji"
        lower.contains("sad") -> "Sade kalma gülümse, Sad olma! $emoji"
        lower.contains("big") -> "Büyük bir dev Big! $emoji"
        lower.contains("small") -> "Simit kadar minik Small! $emoji"
        else -> "\"$en\" kelimesi Türkçede \"$tr\" demektir! $emoji"
    }
}

data class Exercise(
    val id: String,
    val type: ExerciseType,
    val promptTr: String,
    val promptEn: String,
    val targetWord: WordItem,
    val options: List<WordOption> = emptyList(),
    val letterTiles: List<String> = emptyList(),
    val sentenceTokens: List<String> = emptyList()
)

data class WordOption(
    val word: String,
    val translation: String,
    val emoji: String,
    val isCorrect: Boolean
)

data class Lesson(
    val id: String,
    val unitId: Int,
    val grade: GradeLevel,
    val titleTr: String,
    val titleEn: String,
    val description: String,
    val iconEmoji: String,
    val targetWords: List<WordItem>,
    val exercises: List<Exercise>,
    val lessonType: LessonType = LessonType.STANDARD
)

data class UnitTopic(
    val id: Int,
    val grade: GradeLevel,
    val number: Int,
    val titleTr: String,
    val titleEn: String,
    val iconEmoji: String,
    val colorHex: Long,
    val mebCode: String,
    val lessons: List<Lesson>
)

enum class CostumeCategory(val title: String, val emoji: String) {
    COSTUME("Kıyafet & Zırh", "🎽"),
    LIGHTSABER("Işın Kılıcı & El Eşyası", "⚔️"),
    HELMET("Kask & Şapka", "🪖"),
    ROOM_DECOR("Oda Eşyası & Droid", "🛸"),
    ROOM_THEME("Oda Teması & Arka Plan", "🌌")
}

data class PokoCostume(
    val id: String,
    val name: String,
    val emoji: String,
    val description: String,
    val price: Int,
    val category: CostumeCategory = CostumeCategory.COSTUME,
    val isUnlocked: Boolean = false,
    val isEquipped: Boolean = false
)

data class BadgeItem(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val requiredValue: Int = 1,
    val progress: Int = 0
)

data class DailyQuest(
    val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Int,
    val targetCount: Int,
    val currentCount: Int,
    val isClaimed: Boolean
)

enum class CardRarity(val titleTr: String, val colorHex: Long, val borderGlowHex: Long) {
    COMMON("Sıradan", 0xFF94A3B8, 0xFFCBD5E1),
    RARE("Nadir 🔵", 0xFF3B82F6, 0xFF93C5FD),
    EPIC("Epik 🟣", 0xFF8B5CF6, 0xFFC084FC),
    LEGENDARY("Efsanevi 🌟", 0xFFF59E0B, 0xFFFDE047)
}

data class HologramCardItem(
    val id: String,
    val nameEn: String,
    val nameTr: String,
    val descriptionEn: String,
    val descriptionTr: String,
    val emoji: String,
    val rarity: CardRarity,
    val powerLevel: Int,
    val element: String,
    val isUnlocked: Boolean = false
)

enum class LeagueTier(val titleTr: String, val emoji: String, val minPoints: Int, val badgeColorHex: Long) {
    BRONZE("Bronz Kaşif", "🥉", 0, 0xFFB45309),
    SILVER("Gümüş Savaşçı", "🥈", 150, 0xFF94A3B8),
    GOLD("Altın Jedi", "🥇", 300, 0xFFF59E0B),
    DIAMOND("Elmas Şampiyon", "💎", 500, 0xFF06B6D4),
    LEGEND("Galaktik Efsane", "👑", 800, 0xFF8B5CF6)
}

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val avatarEmoji: String,
    val points: Int,
    val isCurrentUser: Boolean = false,
    val league: LeagueTier = LeagueTier.BRONZE
)

enum class PetType(val titleTr: String, val babyEmoji: String, val adultEmoji: String, val soundGuide: String) {
    DRAGON("Galaksi Ejderhası", "🥚", "🐉", "Roar! Dragon power!"),
    DROID("Yardımcı BB Droid", "🔩", "🤖", "Beep boop! Hello friend!"),
    COSMO_CAT("Kozmik Yıldız Kedisi", "🐱", "🦁", "Meow! Let's learn English!")
}

