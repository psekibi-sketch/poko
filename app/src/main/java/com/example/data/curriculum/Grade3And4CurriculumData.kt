package com.example.data.curriculum

import com.example.data.model.Exercise
import com.example.data.model.ExerciseType
import com.example.data.model.GradeLevel
import com.example.data.model.Lesson
import com.example.data.model.UnitTopic
import com.example.data.model.WordItem
import com.example.data.model.WordOption

/**
 * MEB 3. ve 4. SINIF İNGİLİZCE MÜFREDATI (Official Turkish Ministry of National Education Curriculum)
 */
object Grade3And4CurriculumData {

    // =========================================================================
    // GRADE 3 (3. SINIF) - 14 TAM MEB & ZENGİNLEŞTİRİLMİŞ ÜNİTE
    // =========================================================================
    val grade3Units: List<UnitTopic> by lazy {
        listOf(
            createG3Unit1Greetings(),
            createG3Unit2MyFamily(),
            createG3Unit3PeopleILove(),
            createG3Unit4Feelings(),
            createG3Unit5ToysAndGames(),
            createG3Unit6MyHouse(),
            createG3Unit7InMyCity(),
            createG3Unit8Transportation(),
            createG3Unit9Weather(),
            createG3Unit10Nature(),
            createG3Unit11SpaceAndPlanets(),
            createG3Unit12CampingAdventure(),
            createG3Unit13SupermarketShopping(),
            createG3Unit14ArtStudio()
        )
    }

    // =========================================================================
    // GRADE 4 (4. SINIF) - 14 TAM MEB & ZENGİNLEŞTİRİLMİŞ ÜNİTE
    // =========================================================================
    val grade4Units: List<UnitTopic> by lazy {
        listOf(
            createG4Unit1ClassroomRules(),
            createG4Unit2Nationality(),
            createG4Unit3FreeTime(),
            createG4Unit4CartoonCharacters(),
            createG4Unit5MyDay(),
            createG4Unit6FunWithScience(),
            createG4Unit7Jobs(),
            createG4Unit8MyClothes(),
            createG4Unit9MyFriends(),
            createG4Unit10FoodAndDrinks(),
            createG4Unit11DigitalWorld(),
            createG4Unit12OceanLife(),
            createG4Unit13SportsChampions(),
            createG4Unit14SaveOurPlanet()
        )
    }

    // --- Helper builders ---
    private fun createFlashcard(word: WordItem): Exercise =
        Exercise("ex_fc_${word.id}", ExerciseType.FLASHCARD, "Kelimeyi dinle ve sesli tekrar et!", "Listen and repeat!", word)

    private fun createPictureMatch(target: WordItem, pool: List<WordItem>): Exercise {
        val filtered = pool.filter { it.id != target.id }.take(3)
        val opts = (listOf(target) + filtered).shuffled().map {
            WordOption(it.english, it.turkish, it.emoji, it.id == target.id)
        }
        return Exercise("ex_pm_${target.id}", ExerciseType.PICTURE_MATCH, "\"${target.english}\" için doğru kartı seç!", "Pick the matching card!", target, opts)
    }

    private fun createAudioQuiz(target: WordItem, pool: List<WordItem>): Exercise {
        val filtered = pool.filter { it.id != target.id }.take(3)
        val opts = (listOf(target) + filtered).shuffled().map {
            WordOption(it.english, it.turkish, it.emoji, it.id == target.id)
        }
        return Exercise("ex_aq_${target.id}", ExerciseType.AUDIO_QUIZ, "Sesi dinle ve Türkçe karşılığına dokun!", "Listen and choose Turkish meaning!", target, opts)
    }

    private fun createBalloonPop(target: WordItem, pool: List<WordItem>): Exercise {
        val filtered = pool.filter { it.id != target.id }.take(3)
        val opts = (listOf(target) + filtered).shuffled().map {
            WordOption(it.english, it.turkish, it.emoji, it.id == target.id)
        }
        return Exercise("ex_bp_${target.id}", ExerciseType.BALLOON_POP, "\"${target.turkish}\" balonunu patlat!", "Pop the balloon!", target, opts)
    }

    private fun createWordBuilder(word: WordItem): Exercise {
        val scrambled = word.english.uppercase().filter { it.isLetter() }.map { it.toString() }.shuffled()
        return Exercise("ex_wb_${word.id}", ExerciseType.WORD_BUILDER, "\"${word.turkish}\" kelimesini harflerle yaz!", "Spell the word!", word, letterTiles = scrambled)
    }

    private fun createSentenceBuilder(sentenceEn: String, sentenceTr: String, words: List<String>, target: WordItem): Exercise =
        Exercise("ex_sb_${target.id}_${sentenceEn.hashCode()}", ExerciseType.SENTENCE_BUILDER, "\"$sentenceTr\" cümlesini oluştur!", sentenceEn, target, sentenceTokens = words.shuffled())

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 1: Greeting & Introductions
    // -------------------------------------------------------------------------
    private fun createG3Unit1Greetings(): UnitTopic {
        val wName = WordItem("w_g3_name", GradeLevel.GRADE_3, 1, "Name", "İsim", "neym", "🏷️", "Greetings", "What is your name?", "Adın nedir?")
        val wFriend = WordItem("w_g3_friend", GradeLevel.GRADE_3, 1, "Friend", "Arkadaş", "frend", "🤝", "Greetings", "This is my friend.", "Bu benim arkadaşım.")
        val wMeet = WordItem("w_g3_meet", GradeLevel.GRADE_3, 1, "Meet", "Tanışmak", "miyt", "🤝", "Greetings", "Nice to meet you!", "Tanıştığıma memnun oldum!")
        val wFine = WordItem("w_g3_fine", GradeLevel.GRADE_3, 1, "Fine", "İyi", "fayn", "😊", "Greetings", "I am fine, thank you.", "İyiyim, teşekkür ederim.")
        val words = listOf(wName, wFriend, wMeet, wFine)

        val lesson1 = Lesson(
            id = "g3_u1_l1", unitId = 1, grade = GradeLevel.GRADE_3,
            titleTr = "Tanışma & Selamlaşma", titleEn = "Greetings & Friends",
            description = "Kendini tanıtmayı ve yeni arkadaşlarla tanışmayı öğrenelim!",
            iconEmoji = "🤝", targetWords = words,
            exercises = listOf(
                createFlashcard(wName), createPictureMatch(wName, words),
                createFlashcard(wFriend), createBalloonPop(wFriend, words),
                createFlashcard(wMeet), createAudioQuiz(wMeet, words),
                createWordBuilder(wFriend),
                createSentenceBuilder("Nice to meet you", "Tanıştığıma memnun oldum", listOf("Nice", "to", "meet", "you"), wMeet)
            )
        )
        return UnitTopic(1, GradeLevel.GRADE_3, 1, "Tanışma & Arkadaşlar", "Greetings", "🤝", 0xFF3B82F6, "E3.1", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 2: My Family
    // -------------------------------------------------------------------------
    private fun createG3Unit2MyFamily(): UnitTopic {
        val wFather = WordItem("w_g3_father", GradeLevel.GRADE_3, 2, "Father", "Baba", "fa-dır", "👨", "Family", "My father is kind.", "Babam naziktir.")
        val wMother = WordItem("w_g3_mother", GradeLevel.GRADE_3, 2, "Mother", "Anne", "ma-dır", "👩", "Family", "My mother is happy.", "Annem mutludur.")
        val wBrother = WordItem("w_g3_brother", GradeLevel.GRADE_3, 2, "Brother", "Erkek Kardeş", "bra-dır", "👦", "Family", "I love my brother.", "Kardeşimi seviyorum.")
        val wSister = WordItem("w_g3_sister", GradeLevel.GRADE_3, 2, "Sister", "Kız Kardeş", "sis-tır", "👧", "Family", "This is my sister.", "Bu benim kız kardeşim.")
        val words = listOf(wFather, wMother, wBrother, wSister)

        val lesson1 = Lesson(
            id = "g3_u2_l1", unitId = 2, grade = GradeLevel.GRADE_3,
            titleTr = "Benim Ailem", titleEn = "My Family",
            description = "Aile bireylerimizi İngilizce tanıtalım!",
            iconEmoji = "👨‍👩‍👧‍👦", targetWords = words,
            exercises = listOf(
                createFlashcard(wFather), createPictureMatch(wFather, words),
                createFlashcard(wMother), createAudioQuiz(wMother, words),
                createFlashcard(wBrother), createBalloonPop(wBrother, words),
                createWordBuilder(wSister),
                createSentenceBuilder("This is my mother", "Bu benim annem", listOf("This", "is", "my", "mother"), wMother)
            )
        )
        return UnitTopic(2, GradeLevel.GRADE_3, 2, "Benim Ailem", "My Family", "👨‍👩‍👧‍👦", 0xFFEC4899, "E3.2", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 3: People I Love (Adjectives)
    // -------------------------------------------------------------------------
    private fun createG3Unit3PeopleILove(): UnitTopic {
        val wTall = WordItem("w_g3_tall", GradeLevel.GRADE_3, 3, "Tall", "Uzun Boylu", "tol", "🦒", "Adjectives", "He is tall.", "O uzun boyludur.")
        val wShort = WordItem("w_g3_short", GradeLevel.GRADE_3, 3, "Short", "Kısa Boylu", "şort", "🦔", "Adjectives", "She is short.", "O kısa boyludur.")
        val wStrong = WordItem("w_g3_strong", GradeLevel.GRADE_3, 3, "Strong", "Güçlü", "strang", "💪", "Adjectives", "The hero is strong.", "Kahraman güçlüdür.")
        val wYoung = WordItem("w_g3_young", GradeLevel.GRADE_3, 3, "Young", "Genç", "yang", "👶", "Adjectives", "He is very young.", "O çok genç.")
        val words = listOf(wTall, wShort, wStrong, wYoung)

        val lesson1 = Lesson(
            id = "g3_u3_l1", unitId = 3, grade = GradeLevel.GRADE_3,
            titleTr = "Kişisel Özellikler", titleEn = "People I Love",
            description = "İnsanların fiziksel özelliklerini tarif etmeyi öğrenelim!",
            iconEmoji = "💪", targetWords = words,
            exercises = listOf(
                createFlashcard(wTall), createPictureMatch(wTall, words),
                createFlashcard(wStrong), createAudioQuiz(wStrong, words),
                createWordBuilder(wTall), createBalloonPop(wShort, words),
                createSentenceBuilder("He is strong", "O güçlüdür", listOf("He", "is", "strong"), wStrong)
            )
        )
        return UnitTopic(3, GradeLevel.GRADE_3, 3, "Kişisel Özellikler", "People I Love", "💪", 0xFF8B5CF6, "E3.3", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 4: Feelings (Duygular)
    // -------------------------------------------------------------------------
    private fun createG3Unit4Feelings(): UnitTopic {
        val wHappy = WordItem("w_g3_happy", GradeLevel.GRADE_3, 4, "Happy", "Mutlu", "hep-pi", "😄", "Feelings", "I am happy today.", "Bugün mutluyum.")
        val wSad = WordItem("w_g3_sad", GradeLevel.GRADE_3, 4, "Sad", "Üzgün", "sed", "😢", "Feelings", "Don't be sad.", "Üzgün olma.")
        val wHungry = WordItem("w_g3_hungry", GradeLevel.GRADE_3, 4, "Hungry", "Aç / Acıkmış", "han-gri", "🍔", "Feelings", "I am hungry.", "Karnım aç.")
        val wTired = WordItem("w_g3_tired", GradeLevel.GRADE_3, 4, "Tired", "Yorgun", "tay-ırd", "🥱", "Feelings", "Poko is tired.", "Poko yorgun.")
        val words = listOf(wHappy, wSad, wHungry, wTired)

        val lesson1 = Lesson(
            id = "g3_u4_l1", unitId = 4, grade = GradeLevel.GRADE_3,
            titleTr = "Nasıl Hissediyoruz?", titleEn = "Feelings",
            description = "Duygularımızı İngilizce ifade etmeyi öğrenelim!",
            iconEmoji = "😄", targetWords = words,
            exercises = listOf(
                createFlashcard(wHappy), createPictureMatch(wHappy, words),
                createFlashcard(wHungry), createAudioQuiz(wHungry, words),
                createWordBuilder(wHappy), createBalloonPop(wTired, words),
                createSentenceBuilder("I feel happy", "Mutlu hissediyorum", listOf("I", "feel", "happy"), wHappy)
            )
        )
        return UnitTopic(4, GradeLevel.GRADE_3, 4, "Duygular", "Feelings", "😄", 0xFFF59E0B, "E3.4", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 5: Toys and Games (Oyuncaklar)
    // -------------------------------------------------------------------------
    private fun createG3Unit5ToysAndGames(): UnitTopic {
        val wRobot = WordItem("w_g3_robot", GradeLevel.GRADE_3, 5, "Robot", "Robot", "ro-bot", "🤖", "Toys", "I have a toy robot.", "Oyuncak bir robotum var.")
        val wKite = WordItem("w_g3_kite", GradeLevel.GRADE_3, 5, "Kite", "Uçurtma", "kayt", "🪁", "Toys", "Fly the kite in the sky.", "Uçurtmayı gökyüzünde uçur.")
        val wDoll = WordItem("w_g3_doll", GradeLevel.GRADE_3, 5, "Doll", "Oyuncak Bebek", "dol", "🪆", "Toys", "This is a pretty doll.", "Bu sevimli bir oyuncak bebek.")
        val wBike = WordItem("w_g3_bike", GradeLevel.GRADE_3, 5, "Bike", "Bisiklet", "bayk", "🚲", "Toys", "I can ride a bike.", "Bisiklet sürebilirim.")
        val words = listOf(wRobot, wKite, wDoll, wBike)

        val lesson1 = Lesson(
            id = "g3_u5_l1", unitId = 5, grade = GradeLevel.GRADE_3,
            titleTr = "Oyuncaklar & Oyunlar", titleEn = "Toys & Games",
            description = "En sevdiğimiz oyuncakların İngilizce isimleri!",
            iconEmoji = "🪁", targetWords = words,
            exercises = listOf(
                createFlashcard(wRobot), createPictureMatch(wRobot, words),
                createFlashcard(wKite), createAudioQuiz(wKite, words),
                createWordBuilder(wBike), createBalloonPop(wDoll, words),
                createSentenceBuilder("I have a robot", "Bir robotum var", listOf("I", "have", "a", "robot"), wRobot)
            )
        )
        return UnitTopic(5, GradeLevel.GRADE_3, 5, "Oyuncaklar ve Oyunlar", "Toys and Games", "🪁", 0xFF10B981, "E3.5", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 6: My House (Evimiz)
    // -------------------------------------------------------------------------
    private fun createG3Unit6MyHouse(): UnitTopic {
        val wLivingRoom = WordItem("w_g3_livingroom", GradeLevel.GRADE_3, 6, "Living room", "Oturma Odası", "li-ving rum", "🛋️", "House", "We sit in the living room.", "Oturma odasında otururuz.")
        val wBedroom = WordItem("w_g3_bedroom", GradeLevel.GRADE_3, 6, "Bedroom", "Yatak Odası", "bed-rum", "🛏️", "House", "I sleep in my bedroom.", "Yatak odamda uyurum.")
        val wKitchen = WordItem("w_g3_kitchen", GradeLevel.GRADE_3, 6, "Kitchen", "Mutfak", "kit-çın", "🍳", "House", "Mom cooks in the kitchen.", "Annem mutfakta yemek yapar.")
        val wGarden = WordItem("w_g3_garden", GradeLevel.GRADE_3, 6, "Garden", "Bahçe", "gar-dın", "🌳", "House", "Play in the green garden.", "Yeşil bahçede oyna.")
        val words = listOf(wLivingRoom, wBedroom, wKitchen, wGarden)

        val lesson1 = Lesson(
            id = "g3_u6_l1", unitId = 6, grade = GradeLevel.GRADE_3,
            titleTr = "Evimizin Odaları", titleEn = "My House",
            description = "Evin bölümlerini ve odalarını öğrenelim!",
            iconEmoji = "🏡", targetWords = words,
            exercises = listOf(
                createFlashcard(wKitchen), createPictureMatch(wKitchen, words),
                createFlashcard(wBedroom), createAudioQuiz(wBedroom, words),
                createWordBuilder(wGarden), createBalloonPop(wLivingRoom, words),
                createSentenceBuilder("She is in the kitchen", "O mutfaktadır", listOf("She", "is", "in", "the", "kitchen"), wKitchen)
            )
        )
        return UnitTopic(6, GradeLevel.GRADE_3, 6, "Evimiz & Odalar", "My House", "🏡", 0xFF6366F1, "E3.6", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 7: In My City (Şehrimde)
    // -------------------------------------------------------------------------
    private fun createG3Unit7InMyCity(): UnitTopic {
        val wSchool = WordItem("w_g3_school", GradeLevel.GRADE_3, 7, "School", "Okul", "skul", "🏫", "City", "We learn at school.", "Okulda öğreniriz.")
        val wHospital = WordItem("w_g3_hospital", GradeLevel.GRADE_3, 7, "Hospital", "Hastane", "hos-pi-tıl", "🏥", "City", "Doctors work at hospital.", "Doktorlar hastanede çalışır.")
        val wPark = WordItem("w_g3_park", GradeLevel.GRADE_3, 7, "Park", "Park", "park", "🛝", "City", "Let's play in the park.", "Parkta oynayalım.")
        val wLibrary = WordItem("w_g3_library", GradeLevel.GRADE_3, 7, "Library", "Kütüphane", "layb-re-ri", "📖", "City", "Read books in the library.", "Kütüphanede kitap oku.")
        val words = listOf(wSchool, wHospital, wPark, wLibrary)

        val lesson1 = Lesson(
            id = "g3_u7_l1", unitId = 7, grade = GradeLevel.GRADE_3,
            titleTr = "Şehrimizdeki Yerler", titleEn = "In My City",
            description = "Şehirdeki önemli binaları ve parkları öğrenelim!",
            iconEmoji = "🏙️", targetWords = words,
            exercises = listOf(
                createFlashcard(wSchool), createPictureMatch(wSchool, words),
                createFlashcard(wHospital), createAudioQuiz(wHospital, words),
                createWordBuilder(wPark), createBalloonPop(wLibrary, words),
                createSentenceBuilder("I am at school", "Okuldayım", listOf("I", "am", "at", "school"), wSchool)
            )
        )
        return UnitTopic(7, GradeLevel.GRADE_3, 7, "Şehrimde Neler Var?", "In My City", "🏙️", 0xFF06B6D4, "E3.7", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 8: Transportation (Ulaşım)
    // -------------------------------------------------------------------------
    private fun createG3Unit8Transportation(): UnitTopic {
        val wTrain = WordItem("w_g3_train", GradeLevel.GRADE_3, 8, "Train", "Tren", "treyn", "🚆", "Transport", "The train is fast.", "Tren hızlıdır.")
        val wPlane = WordItem("w_g3_plane", GradeLevel.GRADE_3, 8, "Plane", "Uçak", "pleyn", "✈️", "Transport", "The plane flies high.", "Uçak yüksekte uçar.")
        val wShip = WordItem("w_g3_ship", GradeLevel.GRADE_3, 8, "Ship", "Gemi", "şip", "🚢", "Transport", "Big ship on the sea.", "Denizde büyük gemi.")
        val wBus = WordItem("w_g3_bus", GradeLevel.GRADE_3, 8, "Bus", "Otobüs", "bas", "🚌", "Transport", "Take the yellow school bus.", "Sarı okul otobüsüne bin.")
        val words = listOf(wTrain, wPlane, wShip, wBus)

        val lesson1 = Lesson(
            id = "g3_u8_l1", unitId = 8, grade = GradeLevel.GRADE_3,
            titleTr = "Ulaşım Araçları", titleEn = "Transportation",
            description = "Taşıtları ve seyahat etmeyi öğrenelim!",
            iconEmoji = "✈️", targetWords = words,
            exercises = listOf(
                createFlashcard(wPlane), createPictureMatch(wPlane, words),
                createFlashcard(wTrain), createAudioQuiz(wTrain, words),
                createWordBuilder(wShip), createBalloonPop(wBus, words),
                createSentenceBuilder("The train is fast", "Tren hızlıdır", listOf("The", "train", "is", "fast"), wTrain)
            )
        )
        return UnitTopic(8, GradeLevel.GRADE_3, 8, "Ulaşım Araçları", "Transportation", "✈️", 0xFFE11D48, "E3.8", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 9: Weather (Hava Durumu)
    // -------------------------------------------------------------------------
    private fun createG3Unit9Weather(): UnitTopic {
        val wSunny = WordItem("w_g3_sunny", GradeLevel.GRADE_3, 9, "Sunny", "Güneşli", "sa-ni", "☀️", "Weather", "It is sunny today.", "Bugün hava güneşli.")
        val wRainy = WordItem("w_g3_rainy", GradeLevel.GRADE_3, 9, "Rainy", "Yağmurlu", "rey-ni", "🌧️", "Weather", "Take an umbrella on rainy days.", "Yağmurlu günlerde şemsiye al.")
        val wSnowy = WordItem("w_g3_snowy", GradeLevel.GRADE_3, 9, "Snowy", "Karlı", "sno-vi", "❄️", "Weather", "Build a snowman on snowy days.", "Karlı günlerde kardan adam yap.")
        val wWindy = WordItem("w_g3_windy", GradeLevel.GRADE_3, 9, "Windy", "Rüzgarlı", "vin-di", "💨", "Weather", "It is windy outside.", "Dışarısı rüzgarlı.")
        val words = listOf(wSunny, wRainy, wSnowy, wWindy)

        val lesson1 = Lesson(
            id = "g3_u9_l1", unitId = 9, grade = GradeLevel.GRADE_3,
            titleTr = "Hava Nasıl?", titleEn = "Weather",
            description = "Güneşli, yağmurlu ve karlı havaları anlatalım!",
            iconEmoji = "☀️", targetWords = words,
            exercises = listOf(
                createFlashcard(wSunny), createPictureMatch(wSunny, words),
                createFlashcard(wRainy), createAudioQuiz(wRainy, words),
                createWordBuilder(wSnowy), createBalloonPop(wWindy, words),
                createSentenceBuilder("It is sunny today", "Bugün hava güneşli", listOf("It", "is", "sunny", "today"), wSunny)
            )
        )
        return UnitTopic(9, GradeLevel.GRADE_3, 9, "Hava Durumu", "Weather", "☀️", 0xFFF97316, "E3.9", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 10: Nature (Doğa)
    // -------------------------------------------------------------------------
    private fun createG3Unit10Nature(): UnitTopic {
        val wTree = WordItem("w_g3_tree", GradeLevel.GRADE_3, 10, "Tree", "Ağaç", "triy", "🌳", "Nature", "Green tree in the forest.", "Ormanda yeşil ağaç.")
        val wFlower = WordItem("w_g3_flower", GradeLevel.GRADE_3, 10, "Flower", "Çiçek", "fla-vır", "🌸", "Nature", "The flower smells sweet.", "Çiçek güzel kokuyor.")
        val wMoon = WordItem("w_g3_moon", GradeLevel.GRADE_3, 10, "Moon", "Ay", "mun", "🌙", "Nature", "The moon shines at night.", "Ay gece parıldar.")
        val wStar = WordItem("w_g3_star", GradeLevel.GRADE_3, 10, "Star", "Yıldız", "star", "⭐", "Nature", "Look at the shining star.", "Parlayan yıldıza bak.")
        val words = listOf(wTree, wFlower, wMoon, wStar)

        val lesson1 = Lesson(
            id = "g3_u10_l1", unitId = 10, grade = GradeLevel.GRADE_3,
            titleTr = "Güzel Doğa", titleEn = "Nature",
            description = "Ağaçları, çiçekleri ve gökyüzünü keşfedelim!",
            iconEmoji = "🌸", targetWords = words,
            exercises = listOf(
                createFlashcard(wTree), createPictureMatch(wTree, words),
                createFlashcard(wFlower), createAudioQuiz(wFlower, words),
                createWordBuilder(wStar), createBalloonPop(wMoon, words),
                createSentenceBuilder("Look at the stars", "Yıldızlara bak", listOf("Look", "at", "the", "stars"), wStar)
            )
        )
        return UnitTopic(10, GradeLevel.GRADE_3, 10, "Doğa ve Gökyüzü", "Nature", "🌸", 0xFF14B8A6, "E3.10", listOf(lesson1))
    }

    // =========================================================================
    // GRADE 4 (4. SINIF) - 10 TAM MEB ÜNİTESİ
    // =========================================================================

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 1: Classroom Rules (Sınıf Kuralları)
    // -------------------------------------------------------------------------
    private fun createG4Unit1ClassroomRules(): UnitTopic {
        val wListen = WordItem("w_g4_listen", GradeLevel.GRADE_4, 1, "Listen", "Dinlemek", "li-sın", "👂", "Rules", "Listen to your teacher.", "Öğretmenini dinle.")
        val wOpen = WordItem("w_g4_open", GradeLevel.GRADE_4, 1, "Open", "Açmak", "o-pın", "🚪", "Rules", "Open the door, please.", "Lütfen kapıyı aç.")
        val wClose = WordItem("w_g4_close", GradeLevel.GRADE_4, 1, "Close", "Kapatmak", "kloz", "🪟", "Rules", "Close the window.", "Pencereyi kapat.")
        val wQuiet = WordItem("w_g4_quiet", GradeLevel.GRADE_4, 1, "Quiet", "Sessiz", "kway-ıt", "🤫", "Rules", "Be quiet in class.", "Sınıfta sessiz ol.")
        val words = listOf(wListen, wOpen, wClose, wQuiet)

        val lesson1 = Lesson(
            id = "g4_u1_l1", unitId = 1, grade = GradeLevel.GRADE_4,
            titleTr = "Sınıf Kuralları", titleEn = "Classroom Rules",
            description = "Sınıfta nezaket kurallarını ve yönergeleri öğrenelim!",
            iconEmoji = "🏫", targetWords = words,
            exercises = listOf(
                createFlashcard(wListen), createPictureMatch(wListen, words),
                createFlashcard(wOpen), createAudioQuiz(wOpen, words),
                createWordBuilder(wClose), createBalloonPop(wQuiet, words),
                createSentenceBuilder("Open the door please", "Lütfen kapıyı aç", listOf("Open", "the", "door", "please"), wOpen)
            )
        )
        return UnitTopic(1, GradeLevel.GRADE_4, 1, "Sınıf Kuralları", "Classroom Rules", "🏫", 0xFF3B82F6, "E4.1", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 2: Nationality & Countries (Ülkeler & Milletler)
    // -------------------------------------------------------------------------
    private fun createG4Unit2Nationality(): UnitTopic {
        val wTurkey = WordItem("w_g4_turkey", GradeLevel.GRADE_4, 2, "Turkey", "Türkiye", "tör-ki", "🇹🇷", "Country", "I live in Turkey.", "Türkiye'de yaşıyorum.")
        val wTurkish = WordItem("w_g4_turkish", GradeLevel.GRADE_4, 2, "Turkish", "Türk / Türkçe", "tör-kiş", "🇹🇷", "Country", "I am Turkish.", "Ben Türküm.")
        val wBritain = WordItem("w_g4_britain", GradeLevel.GRADE_4, 2, "Britain", "İngiltere", "bri-tın", "🇬🇧", "Country", "London is in Britain.", "Londra İngiltere'dedir.")
        val wGermany = WordItem("w_g4_germany", GradeLevel.GRADE_4, 2, "Germany", "Almanya", "cör-mı-ni", "🇩🇪", "Country", "Germany is in Europe.", "Almanya Avrupa'dadır.")
        val words = listOf(wTurkey, wTurkish, wBritain, wGermany)

        val lesson1 = Lesson(
            id = "g4_u2_l1", unitId = 2, grade = GradeLevel.GRADE_4,
            titleTr = "Ülkeler & Milletler", titleEn = "Nationality",
            description = "Ülkeleri ve nereli olduğumuzu İngilizce söyleyelim!",
            iconEmoji = "🌍", targetWords = words,
            exercises = listOf(
                createFlashcard(wTurkey), createPictureMatch(wTurkey, words),
                createFlashcard(wTurkish), createAudioQuiz(wTurkish, words),
                createWordBuilder(wBritain), createBalloonPop(wGermany, words),
                createSentenceBuilder("I am from Turkey", "Ben Türkiyeliyim", listOf("I", "am", "from", "Turkey"), wTurkey)
            )
        )
        return UnitTopic(2, GradeLevel.GRADE_4, 2, "Ülkeler ve Milletler", "Nationality", "🌍", 0xFFEF4444, "E4.2", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 3: Free Time & Hobbies (Boş Zaman Etkinlikleri)
    // -------------------------------------------------------------------------
    private fun createG4Unit3FreeTime(): UnitTopic {
        val wReading = WordItem("w_g4_reading", GradeLevel.GRADE_4, 3, "Reading", "Kitap Okuma", "riy-ding", "📚", "Hobbies", "I like reading comics.", "Çizgi roman okumayı severim.")
        val wSwimming = WordItem("w_g4_swimming", GradeLevel.GRADE_4, 3, "Swimming", "Yüzme", "svim-ming", "🏊", "Hobbies", "Swimming is fun.", "Yüzmek eğlencelidir.")
        val wDrawing = WordItem("w_g4_drawing", GradeLevel.GRADE_4, 3, "Drawing", "Resim Çizme", "dro-ing", "🎨", "Hobbies", "Drawing beautiful pictures.", "Güzel resimler çizmek.")
        val wGuitar = WordItem("w_g4_guitar", GradeLevel.GRADE_4, 3, "Guitar", "Gitar Çalma", "gi-tar", "🎸", "Hobbies", "Play the guitar nicely.", "Gitarı güzelce çal.")
        val words = listOf(wReading, wSwimming, wDrawing, wGuitar)

        val lesson1 = Lesson(
            id = "g4_u3_l1", unitId = 3, grade = GradeLevel.GRADE_4,
            titleTr = "Hobilerimiz", titleEn = "Free Time",
            description = "Boş zamanlarımızda neler yapmaktan hoşlanırız?",
            iconEmoji = "🎨", targetWords = words,
            exercises = listOf(
                createFlashcard(wSwimming), createPictureMatch(wSwimming, words),
                createFlashcard(wReading), createAudioQuiz(wReading, words),
                createWordBuilder(wGuitar), createBalloonPop(wDrawing, words),
                createSentenceBuilder("I like reading books", "Kitap okumayı severim", listOf("I", "like", "reading", "books"), wReading)
            )
        )
        return UnitTopic(3, GradeLevel.GRADE_4, 3, "Boş Zaman & Hobiler", "Free Time", "🎨", 0xFF8B5CF6, "E4.3", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 4: Cartoon Characters (Yetenekler: Can / Can't)
    // -------------------------------------------------------------------------
    private fun createG4Unit4CartoonCharacters(): UnitTopic {
        val wFly = WordItem("w_g4_fly", GradeLevel.GRADE_4, 4, "Fly", "Uçmak", "flay", "🦸", "Abilities", "Superman can fly.", "Süperman uçabilir.")
        val wSwim = WordItem("w_g4_swim", GradeLevel.GRADE_4, 4, "Swim", "Yüzmek", "svim", "🐟", "Abilities", "Fish can swim fast.", "Balıklar hızlı yüzebilir.")
        val wJump = WordItem("w_g4_jump", GradeLevel.GRADE_4, 4, "Jump", "Zıplamak", "camp", "🦘", "Abilities", "Kangaroo can jump high.", "Kanguru yükseğe zıplayabilir.")
        val wSing = WordItem("w_g4_sing", GradeLevel.GRADE_4, 4, "Sing", "Şarkı Söylemek", "sing", "🎤", "Abilities", "She can sing a song.", "O şarkı söyleyebilir.")
        val words = listOf(wFly, wSwim, wJump, wSing)

        val lesson1 = Lesson(
            id = "g4_u4_l1", unitId = 4, grade = GradeLevel.GRADE_4,
            titleTr = "Süper Yetenekler", titleEn = "Cartoon Characters",
            description = "Neler yapabildiğimizi 'Can' ile ifade edelim!",
            iconEmoji = "🦸", targetWords = words,
            exercises = listOf(
                createFlashcard(wFly), createPictureMatch(wFly, words),
                createFlashcard(wJump), createAudioQuiz(wJump, words),
                createWordBuilder(wSing), createBalloonPop(wSwim, words),
                createSentenceBuilder("He can fly high", "O yükseğe uçabilir", listOf("He", "can", "fly", "high"), wFly)
            )
        )
        return UnitTopic(4, GradeLevel.GRADE_4, 4, "Çizgi Karakterler & Yetenekler", "Cartoon Characters", "🦸", 0xFFF59E0B, "E4.4", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 5: My Day & Daily Routines (Günlük Yaşamım)
    // -------------------------------------------------------------------------
    private fun createG4Unit5MyDay(): UnitTopic {
        val wWakeUp = WordItem("w_g4_wakeup", GradeLevel.GRADE_4, 5, "Wake up", "Uyanmak", "veyk ap", "⏰", "Daily", "I wake up early.", "Erkenden uyanırım.")
        val wBreakfast = WordItem("w_g4_breakfast", GradeLevel.GRADE_4, 5, "Breakfast", "Kahvaltı Yapmak", "brek-fıst", "🍳", "Daily", "Have a healthy breakfast.", "Sağlıklı bir kahvaltı yap.")
        val wHomework = WordItem("w_g4_homework", GradeLevel.GRADE_4, 5, "Homework", "Ödev Yapmak", "hom-vörk", "📝", "Daily", "Do your English homework.", "İngilizce ödevini yap.")
        val wSleep = WordItem("w_g4_sleep", GradeLevel.GRADE_4, 5, "Sleep", "Uyumak", "sliyp", "😴", "Daily", "Sleep well at night.", "Geceleri iyi uyu.")
        val words = listOf(wWakeUp, wBreakfast, wHomework, wSleep)

        val lesson1 = Lesson(
            id = "g4_u5_l1", unitId = 5, grade = GradeLevel.GRADE_4,
            titleTr = "Bir Günüm", titleEn = "My Day",
            description = "Sabah uyanıştan gece uykuya günlük rutinlerimiz!",
            iconEmoji = "⏰", targetWords = words,
            exercises = listOf(
                createFlashcard(wWakeUp), createPictureMatch(wWakeUp, words),
                createFlashcard(wBreakfast), createAudioQuiz(wBreakfast, words),
                createWordBuilder(wSleep), createBalloonPop(wHomework, words),
                createSentenceBuilder("I wake up early", "Erken uyanırım", listOf("I", "wake", "up", "early"), wWakeUp)
            )
        )
        return UnitTopic(5, GradeLevel.GRADE_4, 5, "Günlük Rutinlerim", "My Day", "⏰", 0xFF10B981, "E4.5", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 6: Fun with Science (Eğlenceli Bilim)
    // -------------------------------------------------------------------------
    private fun createG4Unit6FunWithScience(): UnitTopic {
        val wBottle = WordItem("w_g4_bottle", GradeLevel.GRADE_4, 6, "Bottle", "Şişe", "ba-tıl", "🧴", "Science", "Glass water bottle.", "Cam su şişesi.")
        val wWater = WordItem("w_g4_water", GradeLevel.GRADE_4, 6, "Water", "Su", "va-tır", "💧", "Science", "Pure clean water.", "Saf temiz su.")
        val wMix = WordItem("w_g4_mix", GradeLevel.GRADE_4, 6, "Mix", "Karıştırmak", "miks", "🧪", "Science", "Mix the colors together.", "Renkleri birbirine karıştır.")
        val wMelt = WordItem("w_g4_melt", GradeLevel.GRADE_4, 6, "Melt", "Erimek", "melt", "🧊", "Science", "Ice will melt in heat.", "Buz sıcakta erir.")
        val words = listOf(wBottle, wWater, wMix, wMelt)

        val lesson1 = Lesson(
            id = "g4_u6_l1", unitId = 6, grade = GradeLevel.GRADE_4,
            titleTr = "Eğlenceli Deneyler", titleEn = "Fun with Science",
            description = "Bilimsel kelimeler ve eğlenceli deneyler yapalım!",
            iconEmoji = "🔬", targetWords = words,
            exercises = listOf(
                createFlashcard(wMix), createPictureMatch(wMix, words),
                createFlashcard(wWater), createAudioQuiz(wWater, words),
                createWordBuilder(wMelt), createBalloonPop(wBottle, words),
                createSentenceBuilder("Mix water and colors", "Suyu ve renkleri karıştır", listOf("Mix", "water", "and", "colors"), wMix)
            )
        )
        return UnitTopic(6, GradeLevel.GRADE_4, 6, "Eğlenceli Bilim", "Fun with Science", "🔬", 0xFF0EA5E9, "E4.6", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 7: Jobs & Professions (Meslekler)
    // -------------------------------------------------------------------------
    private fun createG4Unit7Jobs(): UnitTopic {
        val wDoctor = WordItem("w_g4_doctor", GradeLevel.GRADE_4, 7, "Doctor", "Doktor", "dak-tır", "🩺", "Jobs", "The doctor helps sick people.", "Doktor hasta insanlara yardım eder.")
        val wTeacher = WordItem("w_g4_teacher", GradeLevel.GRADE_4, 7, "Teacher", "Öğretmen", "tiy-çır", "👩‍🏫", "Jobs", "Our teacher is wonderful.", "Öğretmenimiz harikadır.")
        val wPilot = WordItem("w_g4_pilot", GradeLevel.GRADE_4, 7, "Pilot", "Pilot", "pay-lıt", "👨‍✈️", "Jobs", "The pilot flies planes.", "Pilot uçakları uçurur.")
        val wChef = WordItem("w_g4_chef", GradeLevel.GRADE_4, 7, "Chef", "Aşçı", "şef", "👨‍🍳", "Jobs", "The chef cooks delicious food.", "Aşçı lezzetli yemekler pişirir.")
        val words = listOf(wDoctor, wTeacher, wPilot, wChef)

        val lesson1 = Lesson(
            id = "g4_u7_l1", unitId = 7, grade = GradeLevel.GRADE_4,
            titleTr = "Geleceğin Meslekleri", titleEn = "Jobs",
            description = "Büyüyünce ne olmak istersin?",
            iconEmoji = "🩺", targetWords = words,
            exercises = listOf(
                createFlashcard(wDoctor), createPictureMatch(wDoctor, words),
                createFlashcard(wPilot), createAudioQuiz(wPilot, words),
                createWordBuilder(wChef), createBalloonPop(wTeacher, words),
                createSentenceBuilder("I want to be a pilot", "Pilot olmak istiyorum", listOf("I", "want", "to", "be", "a", "pilot"), wPilot)
            )
        )
        return UnitTopic(7, GradeLevel.GRADE_4, 7, "Meslekler", "Jobs", "🩺", 0xFF6366F1, "E4.7", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 8: My Clothes & Seasons (Kıyafetler & Mevsimler)
    // -------------------------------------------------------------------------
    private fun createG4Unit8MyClothes(): UnitTopic {
        val wJacket = WordItem("w_g4_jacket", GradeLevel.GRADE_4, 8, "Jacket", "Ceket / Mont", "ce-kıt", "🧥", "Clothes", "Wear your warm jacket.", "Sıcak ceketini giy.")
        val wShoes = WordItem("w_g4_shoes", GradeLevel.GRADE_4, 8, "Shoes", "Ayakkabı", "şuz", "👟", "Clothes", "Put on your shoes.", "Ayakkabılarını giy.")
        val wHat = WordItem("w_g4_hat", GradeLevel.GRADE_4, 8, "Hat", "Şapka", "het", "👒", "Clothes", "Sun hat in summer.", "Yazın güneş şapkası.")
        val wWinter = WordItem("w_g4_winter", GradeLevel.GRADE_4, 8, "Winter", "Kış Mevsimi", "vin-tır", "⛄", "Seasons", "Winter is cold and snowy.", "Kış soğuk ve karlıdır.")
        val words = listOf(wJacket, wShoes, wHat, wWinter)

        val lesson1 = Lesson(
            id = "g4_u8_l1", unitId = 8, grade = GradeLevel.GRADE_4,
            titleTr = "Kıyafetler & Mevsimler", titleEn = "Clothes & Seasons",
            description = "Mevsimlere göre giydiğimiz kıyafetler!",
            iconEmoji = "🧥", targetWords = words,
            exercises = listOf(
                createFlashcard(wJacket), createPictureMatch(wJacket, words),
                createFlashcard(wShoes), createAudioQuiz(wShoes, words),
                createWordBuilder(wHat), createBalloonPop(wWinter, words),
                createSentenceBuilder("Wear your jacket in winter", "Kışın ceketini giy", listOf("Wear", "your", "jacket", "in", "winter"), wJacket)
            )
        )
        return UnitTopic(8, GradeLevel.GRADE_4, 8, "Kıyafetler ve Mevsimler", "My Clothes", "🧥", 0xFFEC4899, "E4.8", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 9: My Friends (Fiziksel Görünüm)
    // -------------------------------------------------------------------------
    private fun createG4Unit9MyFriends(): UnitTopic {
        val wHair = WordItem("w_g4_hair", GradeLevel.GRADE_4, 9, "Hair", "Saç", "he-ır", "💇", "Appearance", "She has long hair.", "Onun uzun saçları var.")
        val wEyes = WordItem("w_g4_eyes", GradeLevel.GRADE_4, 9, "Eyes", "Gözler", "ayz", "👀", "Appearance", "He has blue eyes.", "Onun mavi gözleri var.")
        val wGlasses = WordItem("w_g4_glasses", GradeLevel.GRADE_4, 9, "Glasses", "Gözlük", "gla-sız", "👓", "Appearance", "Poko wears cool glasses.", "Poko havalı gözlük takar.")
        val wSmile = WordItem("w_g4_smile", GradeLevel.GRADE_4, 9, "Smile", "Gülümseme", "s-mayl", "😊", "Appearance", "A warm friendly smile.", "Sıcak dostça bir gülümseme.")
        val words = listOf(wHair, wEyes, wGlasses, wSmile)

        val lesson1 = Lesson(
            id = "g4_u9_l1", unitId = 9, grade = GradeLevel.GRADE_4,
            titleTr = "Arkadaşlarımı Tanıtıyorum", titleEn = "My Friends",
            description = "Arkadaşlarımızın saç, göz ve görünüşlerini anlatalım!",
            iconEmoji = "👀", targetWords = words,
            exercises = listOf(
                createFlashcard(wEyes), createPictureMatch(wEyes, words),
                createFlashcard(wGlasses), createAudioQuiz(wGlasses, words),
                createWordBuilder(wSmile), createBalloonPop(wHair, words),
                createSentenceBuilder("She has blue eyes", "Onun mavi gözleri var", listOf("She", "has", "blue", "eyes"), wEyes)
            )
        )
        return UnitTopic(9, GradeLevel.GRADE_4, 9, "Arkadaşlarım ve Görünüş", "My Friends", "👀", 0xFF14B8A6, "E4.9", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 10: Food and Drinks (Yiyecekler & İçecekler)
    // -------------------------------------------------------------------------
    private fun createG4Unit10FoodAndDrinks(): UnitTopic {
        val wBread = WordItem("w_g4_bread", GradeLevel.GRADE_4, 10, "Bread", "Ekmek", "bred", "🍞", "Food", "Fresh hot bread.", "Taze sıcak ekmek.")
        val wCheese = WordItem("w_g4_cheese", GradeLevel.GRADE_4, 10, "Cheese", "Peynir", "çiyz", "🧀", "Food", "Delicious yellow cheese.", "Lezzetli sarı peynir.")
        val wMilk = WordItem("w_g4_milk", GradeLevel.GRADE_4, 10, "Milk", "Süt", "milk", "🥛", "Food", "Drink healthy milk.", "Sağlıklı süt iç.")
        val wPizza = WordItem("w_g4_pizza", GradeLevel.GRADE_4, 10, "Pizza", "Pizza", "pit-sa", "🍕", "Food", "We love sharing pizza.", "Pizza paylaşmayı severiz.")
        val words = listOf(wBread, wCheese, wMilk, wPizza)

        val lesson1 = Lesson(
            id = "g4_u10_l1", unitId = 10, grade = GradeLevel.GRADE_4,
            titleTr = "Lezzetli Yiyecekler", titleEn = "Food & Drinks",
            description = "Sevdiğimiz yemekleri ve içecekleri İngilizce isteyelim!",
            iconEmoji = "🍕", targetWords = words,
            exercises = listOf(
                createFlashcard(wPizza), createPictureMatch(wPizza, words),
                createFlashcard(wCheese), createAudioQuiz(wCheese, words),
                createWordBuilder(wBread), createBalloonPop(wMilk, words),
                createSentenceBuilder("I like cheese and milk", "Peynir ve sütü severim", listOf("I", "like", "cheese", "and", "milk"), wCheese)
            )
        )
        return UnitTopic(10, GradeLevel.GRADE_4, 10, "Yiyecekler ve İçecekler", "Food & Drinks", "🍕", 0xFFF97316, "E4.10", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 11: Space & Solar System (Uzay ve Gezegenler)
    // -------------------------------------------------------------------------
    private fun createG3Unit11SpaceAndPlanets(): UnitTopic {
        val wSun = WordItem("w_g3_sun", GradeLevel.GRADE_3, 11, "Sun", "Güneş", "san", "☀️", "Space", "The bright shining sun.", "Parlak parlayan güneş.")
        val wMoon = WordItem("w_g3_moon", GradeLevel.GRADE_3, 11, "Moon", "Ay", "muun", "🌙", "Space", "The moon is in the night sky.", "Ay gece gökyüzünde.")
        val wStar = WordItem("w_g3_star3", GradeLevel.GRADE_3, 11, "Star", "Yıldız", "staar", "⭐", "Space", "Look at the twinkling star.", "Göz kırpan yıldıza bak.")
        val wPlanet = WordItem("w_g3_planet", GradeLevel.GRADE_3, 11, "Planet", "Gezegen", "ple-nıt", "🪐", "Space", "Saturn is a big planet.", "Satürn büyük bir gezegendir.")
        val wEarth = WordItem("w_g3_earth", GradeLevel.GRADE_3, 11, "Earth", "Dünya", "öörs", "🌍", "Space", "We live on planet Earth.", "Biz Dünya gezegeninde yaşıyoruz.")
        val wRocket = WordItem("w_g3_rocket", GradeLevel.GRADE_3, 11, "Rocket", "Roket", "ra-kıt", "🚀", "Space", "The rocket flies to space.", "Roket uzaya uçar.")
        val wAstronaut = WordItem("w_g3_astro", GradeLevel.GRADE_3, 11, "Astronaut", "Astronot", "es-tır-noot", "👨‍🚀", "Space", "The brave astronaut explores.", "Cesur astronot keşfeder.")
        val words = listOf(wSun, wMoon, wStar, wPlanet, wEarth, wRocket, wAstronaut)

        val lesson1 = Lesson(
            id = "g3_u11_l1", unitId = 11, grade = GradeLevel.GRADE_3,
            titleTr = "Uzay & Güneş Sistemi", titleEn = "Space & Planets",
            description = "Sun, moon, star, planet, Earth, rocket ve astronaut!",
            iconEmoji = "🚀", targetWords = words,
            exercises = listOf(
                createFlashcard(wRocket), createFlashcard(wPlanet), createFlashcard(wEarth),
                createPictureMatch(wRocket, listOf(wRocket, wPlanet, wEarth, wAstronaut)),
                createWordBuilder(wMoon), createWordBuilder(wSun), createWordBuilder(wStar),
                createBalloonPop(wPlanet, listOf(wPlanet, wEarth, wRocket, wMoon)),
                createAudioQuiz(wAstronaut, listOf(wAstronaut, wRocket, wStar, wPlanet)),
                createSentenceBuilder("The rocket flies to space", "Roket uzaya uçar", listOf("The", "rocket", "flies", "to", "space"), wRocket)
            )
        )
        return UnitTopic(311, GradeLevel.GRADE_3, 11, "11. Ünite: Space & Solar System (Uzay)", "Space & Planets", "🚀", 0xFF6366F1, "E3.11", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 12: Camping Adventure (Kamp Macerası)
    // -------------------------------------------------------------------------
    private fun createG3Unit12CampingAdventure(): UnitTopic {
        val wTent = WordItem("w_g3_tent", GradeLevel.GRADE_3, 12, "Tent", "Çadır", "tent", "⛺", "Camping", "Sleep in the green tent.", "Yeşil çadırda uyu.")
        val wCampfire = WordItem("w_g3_campfire", GradeLevel.GRADE_3, 12, "Campfire", "Kamp Ateşi", "kemp-fay-ır", "🔥", "Camping", "Warm campfire at night.", "Gece sıcak kamp ateşi.")
        val wFlashlight = WordItem("w_g3_flashlight", GradeLevel.GRADE_3, 12, "Flashlight", "El Feneri", "fleş-layt", "🔦", "Camping", "Turn on the flashlight.", "El fenerini aç.")
        val wBackpack = WordItem("w_g3_backpack", GradeLevel.GRADE_3, 12, "Backpack", "Sırt Çantası", "bek-pek", "🎒", "Camping", "Carry your heavy backpack.", "Ağır sırt çantanı taşı.")
        val wForest = WordItem("w_g3_forest", GradeLevel.GRADE_3, 12, "Forest", "Orman", "fo-rıst", "🌲", "Camping", "Walk in the green forest.", "Yeşil ormanda yürü.")
        val wRiver = WordItem("w_g3_river", GradeLevel.GRADE_3, 12, "River", "Nehir / Irmak", "ri-vır", "🌊", "Camping", "Swim in the cool river.", "Serin nehirde yüz.")
        val words = listOf(wTent, wCampfire, wFlashlight, wBackpack, wForest, wRiver)

        val lesson1 = Lesson(
            id = "g3_u12_l1", unitId = 12, grade = GradeLevel.GRADE_3,
            titleTr = "Doğada Kamp Zamanı", titleEn = "Camping Adventure",
            description = "Tent, campfire, flashlight, backpack, forest ve river!",
            iconEmoji = "🏕️", targetWords = words,
            exercises = listOf(
                createFlashcard(wTent), createFlashcard(wCampfire), createFlashcard(wFlashlight),
                createPictureMatch(wTent, listOf(wTent, wCampfire, wFlashlight, wBackpack)),
                createWordBuilder(wTent), createWordBuilder(wRiver), createWordBuilder(wForest),
                createBalloonPop(wFlashlight, listOf(wFlashlight, wBackpack, wCampfire, wTent)),
                createAudioQuiz(wCampfire, listOf(wCampfire, wForest, wRiver, wTent)),
                createSentenceBuilder("Sleep in the tent", "Çadırda uyu", listOf("Sleep", "in", "the", "tent"), wTent)
            )
        )
        return UnitTopic(312, GradeLevel.GRADE_3, 12, "12. Ünite: Camping Adventure (Kamp)", "Camping Adventure", "🏕️", 0xFF059669, "E3.12", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 13: Supermarket & Shopping (Market Alışverişi)
    // -------------------------------------------------------------------------
    private fun createG3Unit13SupermarketShopping(): UnitTopic {
        val wCart = WordItem("w_g3_cart", GradeLevel.GRADE_3, 13, "Shopping Cart", "Alışveriş Arabası", "şa-ping kaart", "🛒", "Shopping", "Push the shopping cart.", "Alışveriş arabasını it.")
        val wMoney = WordItem("w_g3_money", GradeLevel.GRADE_3, 13, "Money", "Para", "ma-ni", "💵", "Shopping", "Pay with money.", "Parayla öde.")
        val wPrice = WordItem("w_g3_price", GradeLevel.GRADE_3, 13, "Price", "Fiyat", "prays", "🏷️", "Shopping", "What is the price?", "Fiyatı ne kadar?")
        val wBuy = WordItem("w_g3_buy", GradeLevel.GRADE_3, 13, "Buy", "Satın Almak", "bay", "🛍️", "Shopping", "I want to buy apples.", "Elma satın almak istiyorum.")
        val wCashier = WordItem("w_g3_cashier", GradeLevel.GRADE_3, 13, "Cashier", "Kasiyer", "ke-şi-ır", "🧑‍💼", "Shopping", "Pay to the cashier.", "Kasiyere ödeme yap.")
        val wBag = WordItem("w_g3_bag", GradeLevel.GRADE_3, 13, "Bag", "Alışveriş Poşeti", "beg", "🛍️", "Shopping", "Put groceries in the bag.", "Alışverişi poşete koy.")
        val words = listOf(wCart, wMoney, wPrice, wBuy, wCashier, wBag)

        val lesson1 = Lesson(
            id = "g3_u13_l1", unitId = 13, grade = GradeLevel.GRADE_3,
            titleTr = "Markette Alışveriş", titleEn = "Supermarket Shopping",
            description = "Shopping cart, money, price, buy, cashier ve bag!",
            iconEmoji = "🛒", targetWords = words,
            exercises = listOf(
                createFlashcard(wCart), createFlashcard(wMoney), createFlashcard(wPrice),
                createPictureMatch(wCart, listOf(wCart, wMoney, wPrice, wCashier)),
                createWordBuilder(wBuy), createWordBuilder(wMoney), createWordBuilder(wBag),
                createBalloonPop(wPrice, listOf(wPrice, wCart, wMoney, wCashier)),
                createAudioQuiz(wMoney, listOf(wMoney, wBuy, wPrice, wCart)),
                createSentenceBuilder("I want to buy apples", "Elma almak istiyorum", listOf("I", "want", "to", "buy", "apples"), wBuy)
            )
        )
        return UnitTopic(313, GradeLevel.GRADE_3, 13, "13. Ünite: Supermarket (Alışveriş)", "Supermarket Shopping", "🛒", 0xFFEAB308, "E3.13", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 3 - UNIT 14: Art Studio (Sanat ve El Becerileri)
    // -------------------------------------------------------------------------
    private fun createG3Unit14ArtStudio(): UnitTopic {
        val wBrush = WordItem("w_g3_brush", GradeLevel.GRADE_3, 14, "Brush", "Boya Fırçası", "braş", "🖌️", "Art", "Paint with a soft brush.", "Yumuşak fırçayla boya.")
        val wPaint = WordItem("w_g3_paint", GradeLevel.GRADE_3, 14, "Paint", "Boya Yapmak", "peynt", "🎨", "Art", "Colorful oil paint.", "Renkli yağlı boya.")
        val wPaper = WordItem("w_g3_paper3", GradeLevel.GRADE_3, 14, "Paper", "Kağıt", "pey-pır", "📄", "Art", "Draw on clean paper.", "Temiz kağıda çiz.")
        val wScissors = WordItem("w_g3_scissors3", GradeLevel.GRADE_3, 14, "Scissors", "Makas", "si-zırz", "✂️", "Art", "Cut with sharp scissors.", "Makasla kes.")
        val wGlue = WordItem("w_g3_glue3", GradeLevel.GRADE_3, 14, "Glue", "Yapıştırıcı", "gluu", "🧴", "Art", "Glue pictures on board.", "Resimleri tahtaya yapıştır.")
        val wDraw = WordItem("w_g3_draw", GradeLevel.GRADE_3, 14, "Draw", "Resim Çizmek", "drov", "✏️", "Art", "I can draw a house.", "Bir ev çizebilirim.")
        val words = listOf(wBrush, wPaint, wPaper, wScissors, wGlue, wDraw)

        val lesson1 = Lesson(
            id = "g3_u14_l1", unitId = 14, grade = GradeLevel.GRADE_3,
            titleTr = "Sanat Atölyesi & Resim", titleEn = "Art Studio & Crafts",
            description = "Brush, paint, paper, scissors, glue ve draw!",
            iconEmoji = "🎨", targetWords = words,
            exercises = listOf(
                createFlashcard(wBrush), createFlashcard(wPaint), createFlashcard(wDraw),
                createPictureMatch(wBrush, listOf(wBrush, wPaint, wScissors, wGlue)),
                createWordBuilder(wDraw), createWordBuilder(wBrush), createWordBuilder(wGlue),
                createBalloonPop(wPaint, listOf(wPaint, wScissors, wBrush, wPaper)),
                createAudioQuiz(wDraw, listOf(wDraw, wPaint, wBrush, wScissors)),
                createSentenceBuilder("I can draw a house", "Bir ev çizebilirim", listOf("I", "can", "draw", "a", "house"), wDraw)
            )
        )
        return UnitTopic(314, GradeLevel.GRADE_3, 14, "14. Ünite: Art Studio (Sanat)", "Art Studio & Crafts", "🎨", 0xFFEC4899, "E3.14", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 11: Digital World & Technology (Teknoloji)
    // -------------------------------------------------------------------------
    private fun createG4Unit11DigitalWorld(): UnitTopic {
        val wComputer = WordItem("w_g4_computer", GradeLevel.GRADE_4, 11, "Computer", "Bilgisayar", "kım-pyu-tır", "💻", "Technology", "Learn with the computer.", "Bilgisayarla öğren.")
        val wTablet = WordItem("w_g4_tablet", GradeLevel.GRADE_4, 11, "Tablet", "Tablet", "teb-lıt", "📱", "Technology", "Touch the smart tablet.", "Akıllı tablete dokun.")
        val wMouse = WordItem("w_g4_mouse", GradeLevel.GRADE_4, 11, "Mouse", "Fare (Bilgisayar)", "maws", "🖱️", "Technology", "Click with the mouse.", "Fareyle tıkla.")
        val wScreen = WordItem("w_g4_screen", GradeLevel.GRADE_4, 11, "Screen", "Ekran", "skriin", "🖥️", "Technology", "Look at the bright screen.", "Parlak ekrana bak.")
        val wRobot = WordItem("w_g4_robot4", GradeLevel.GRADE_4, 11, "Robot", "Robot", "rov-bat", "🤖", "Technology", "A smart friendly robot.", "Akıllı sevimli bir robot.")
        val wInternet = WordItem("w_g4_internet", GradeLevel.GRADE_4, 11, "Internet", "İnternet", "in-tır-net", "🌐", "Technology", "Surf the safe internet.", "Güvenli internette gezin.")
        val wCode = WordItem("w_g4_code", GradeLevel.GRADE_4, 11, "Code", "Kod Yazmak", "kovd", "💻", "Technology", "I love learning to code.", "Kod yazmayı öğrenmeyi severim.")
        val words = listOf(wComputer, wTablet, wMouse, wScreen, wRobot, wInternet, wCode)

        val lesson1 = Lesson(
            id = "g4_u11_l1", unitId = 11, grade = GradeLevel.GRADE_4,
            titleTr = "Dijital Dünya & Bilgisayar", titleEn = "Digital World & Tech",
            description = "Computer, tablet, mouse, screen, robot, internet ve code!",
            iconEmoji = "💻", targetWords = words,
            exercises = listOf(
                createFlashcard(wComputer), createFlashcard(wTablet), createFlashcard(wRobot),
                createPictureMatch(wComputer, listOf(wComputer, wTablet, wMouse, wRobot)),
                createWordBuilder(wCode), createWordBuilder(wRobot), createWordBuilder(wMouse),
                createBalloonPop(wScreen, listOf(wScreen, wComputer, wTablet, wInternet)),
                createAudioQuiz(wInternet, listOf(wInternet, wCode, wRobot, wMouse)),
                createSentenceBuilder("I learn to code", "Kod yazmayı öğreniyorum", listOf("I", "learn", "to", "code"), wCode)
            )
        )
        return UnitTopic(411, GradeLevel.GRADE_4, 11, "11. Ünite: Digital World (Teknoloji)", "Digital World & Tech", "💻", 0xFF0284C7, "E4.11", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 12: Ocean Life & Under the Sea (Okyanus Hayatı)
    // -------------------------------------------------------------------------
    private fun createG4Unit12OceanLife(): UnitTopic {
        val wDolphin = WordItem("w_g4_dolphin", GradeLevel.GRADE_4, 12, "Dolphin", "Yunus", "dal-fin", "🐬", "Ocean", "A friendly playful dolphin.", "Sevimli oyuncu bir yunus.")
        val wWhale = WordItem("w_g4_whale", GradeLevel.GRADE_4, 12, "Whale", "Balina", "veyl", "🐋", "Ocean", "A huge blue whale.", "Dev bir mavi balina.")
        val wShark = WordItem("w_g4_shark", GradeLevel.GRADE_4, 12, "Shark", "Köpekbalığı", "şaark", "🦈", "Ocean", "The fast swimming shark.", "Hızlı yüzen köpekbalığı.")
        val wOctopus = WordItem("w_g4_octopus", GradeLevel.GRADE_4, 12, "Octopus", "Ahtapot", "ak-tı-pıs", "🐙", "Ocean", "An octopus has eight arms.", "Bir ahtapotun sekiz kolu vardır.")
        val wTurtle = WordItem("w_g4_turtle", GradeLevel.GRADE_4, 12, "Sea Turtle", "Deniz Kaplumbağası", "sii tör-tıl", "🐢", "Ocean", "Ancient sea turtle.", "Kadim deniz kaplumbağası.")
        val wCoral = WordItem("w_g4_coral", GradeLevel.GRADE_4, 12, "Coral", "Mercan", "ko-rıl", "🪸", "Ocean", "Colorful coral reef.", "Renkli mercan resifi.")
        val wSubmarine = WordItem("w_g4_submarine", GradeLevel.GRADE_4, 12, "Submarine", "Denizaltı", "sab-mı-riin", "🤿", "Ocean", "Explore in a submarine.", "Denizaltıyla keşfet.")
        val words = listOf(wDolphin, wWhale, wShark, wOctopus, wTurtle, wCoral, wSubmarine)

        val lesson1 = Lesson(
            id = "g4_u12_l1", unitId = 12, grade = GradeLevel.GRADE_4,
            titleTr = "Derin Deniz & Okyanus Canlıları", titleEn = "Ocean Life & Deep Sea",
            description = "Dolphin, whale, shark, octopus, sea turtle, coral ve submarine!",
            iconEmoji = "🌊", targetWords = words,
            exercises = listOf(
                createFlashcard(wDolphin), createFlashcard(wWhale), createFlashcard(wOctopus),
                createPictureMatch(wDolphin, listOf(wDolphin, wWhale, wShark, wOctopus)),
                createWordBuilder(wWhale), createWordBuilder(wShark), createWordBuilder(wCoral),
                createBalloonPop(wTurtle, listOf(wTurtle, wDolphin, wShark, wOctopus)),
                createAudioQuiz(wSubmarine, listOf(wSubmarine, wTurtle, wCoral, wWhale)),
                createSentenceBuilder("Dolphins are very smart", "Yunuslar çok akıllıdır", listOf("Dolphins", "are", "very", "smart"), wDolphin)
            )
        )
        return UnitTopic(412, GradeLevel.GRADE_4, 12, "12. Ünite: Ocean Life (Okyanus)", "Ocean Life & Deep Sea", "🌊", 0xFF06B6D4, "E4.12", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 13: Sports & Champions (Sporlar ve Şampiyonlar)
    // -------------------------------------------------------------------------
    private fun createG4Unit13SportsChampions(): UnitTopic {
        val wFootball = WordItem("w_g4_football", GradeLevel.GRADE_4, 13, "Football", "Futbol", "fut-bool", "⚽", "Sports", "Play football with friends.", "Arkadaşlarla futbol oyna.")
        val wBasketball = WordItem("w_g4_basketball", GradeLevel.GRADE_4, 13, "Basketball", "Basketbol", "bas-kıt-bool", "🏀", "Sports", "Shoot a basketball.", "Basketbol atışı yap.")
        val wSwimming = WordItem("w_g4_swimming", GradeLevel.GRADE_4, 13, "Swimming", "Yüzme", "svi-ming", "🏊", "Sports", "I love swimming in pool.", "Havuzda yüzmeyi severim.")
        val wTennis = WordItem("w_g4_tennis", GradeLevel.GRADE_4, 13, "Tennis", "Tenis", "te-nis", "🎾", "Sports", "Hit the tennis ball.", "Tenis topuna vur.")
        val wCycling = WordItem("w_g4_cycling", GradeLevel.GRADE_4, 13, "Cycling", "Bisiklete Binme", "sayk-ling", "🚴", "Sports", "Cycling in the green park.", "Parkta bisiklete binme.")
        val wMedal = WordItem("w_g4_medal", GradeLevel.GRADE_4, 13, "Medal", "Madalya", "me-dıl", "🥇", "Sports", "Win a gold medal.", "Altın madalya kazan.")
        val wTrophy = WordItem("w_g4_trophy", GradeLevel.GRADE_4, 13, "Trophy", "Kupa / Şampiyonluk", "trov-fi", "🏆", "Sports", "Raise the victory trophy.", "Zafer kupasını kaldır.")
        val words = listOf(wFootball, wBasketball, wSwimming, wTennis, wCycling, wMedal, wTrophy)

        val lesson1 = Lesson(
            id = "g4_u13_l1", unitId = 13, grade = GradeLevel.GRADE_4,
            titleTr = "Şampiyonlar & Spor Dalları", titleEn = "Sports & Champions",
            description = "Football, basketball, swimming, tennis, cycling, medal ve trophy!",
            iconEmoji = "🏅", targetWords = words,
            exercises = listOf(
                createFlashcard(wFootball), createFlashcard(wBasketball), createFlashcard(wMedal),
                createPictureMatch(wFootball, listOf(wFootball, wBasketball, wTennis, wMedal)),
                createWordBuilder(wTennis), createWordBuilder(wMedal), createWordBuilder(wTrophy),
                createBalloonPop(wSwimming, listOf(wSwimming, wFootball, wTennis, wCycling)),
                createAudioQuiz(wTrophy, listOf(wTrophy, wMedal, wBasketball, wFootball)),
                createSentenceBuilder("Win a gold medal", "Altın madalya kazan", listOf("Win", "a", "gold", "medal"), wMedal)
            )
        )
        return UnitTopic(413, GradeLevel.GRADE_4, 13, "13. Ünite: Sports & Champions (Spor)", "Sports & Champions", "🏅", 0xFFF59E0B, "E4.13", listOf(lesson1))
    }

    // -------------------------------------------------------------------------
    // GRADE 4 - UNIT 14: Save Our Planet (Gezegenimizi Koruyalım / Çevre)
    // -------------------------------------------------------------------------
    private fun createG4Unit14SaveOurPlanet(): UnitTopic {
        val wRecycle = WordItem("w_g4_recycle", GradeLevel.GRADE_4, 14, "Recycle", "Geri Dönüştürmek", "rii-say-kıl", "♻️", "Environment", "Recycle plastic and paper.", "Plastik ve kağıdı geri dönüştür.")
        val wPlantTree = WordItem("w_g4_planttree", GradeLevel.GRADE_4, 14, "Plant Trees", "Ağaç Dikmek", "plent triiz", "🌲", "Environment", "Plant trees for clean air.", "Temiz hava için ağaç dik.")
        val wCleanWater = WordItem("w_g4_cleanwater", GradeLevel.GRADE_4, 14, "Clean Water", "Temiz Su", "kliin voo-tır", "💧", "Environment", "Protect our clean water.", "Temiz suyumuzu koru.")
        val wSaveEnergy = WordItem("w_g4_saveenergy", GradeLevel.GRADE_4, 14, "Save Energy", "Enerji Tasarrufu Yapmak", "seyv e-nır-ci", "💡", "Environment", "Turn off lights to save energy.", "Enerji tasarrufu için lambaları kapat.")
        val wEarth = WordItem("w_g4_earth4", GradeLevel.GRADE_4, 14, "Earth", "Dünyamız", "öörs", "🌍", "Environment", "Love our beautiful Earth.", "Güzel Dünyamızı sev.")
        val words = listOf(wRecycle, wPlantTree, wCleanWater, wSaveEnergy, wEarth)

        val lesson1 = Lesson(
            id = "g4_u14_l1", unitId = 14, grade = GradeLevel.GRADE_4,
            titleTr = "Yeşil Dünya & Çevre Koruma", titleEn = "Save Our Green Planet",
            description = "Recycle, plant trees, clean water, save energy ve Earth!",
            iconEmoji = "🌿", targetWords = words,
            exercises = listOf(
                createFlashcard(wRecycle), createFlashcard(wPlantTree), createFlashcard(wEarth),
                createPictureMatch(wRecycle, listOf(wRecycle, wPlantTree, wCleanWater, wEarth)),
                createWordBuilder(wEarth), createWordBuilder(wRecycle),
                createBalloonPop(wCleanWater, listOf(wCleanWater, wSaveEnergy, wRecycle, wEarth)),
                createAudioQuiz(wSaveEnergy, listOf(wSaveEnergy, wPlantTree, wEarth, wRecycle)),
                createSentenceBuilder("Plant trees for clean air", "Temiz hava için ağaç dik", listOf("Plant", "trees", "for", "clean", "air"), wPlantTree)
            )
        )
        return UnitTopic(414, GradeLevel.GRADE_4, 14, "14. Ünite: Save Our Planet (Çevre)", "Save Our Planet", "🌿", 0xFF10B981, "E4.14", listOf(lesson1))
    }
}
