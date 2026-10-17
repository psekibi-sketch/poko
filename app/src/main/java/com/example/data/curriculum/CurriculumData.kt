package com.example.data.curriculum

import com.example.data.model.Exercise
import com.example.data.model.ExerciseType
import com.example.data.model.GradeLevel
import com.example.data.model.Lesson
import com.example.data.model.UnitTopic
import com.example.data.model.WordItem
import com.example.data.model.WordOption

object CurriculumData {

    // ==========================================
    // GRADE 1 UNITS (MEB 1. Sınıf - 10 Tam Ünite)
    // ==========================================
    val grade1Units: List<UnitTopic> by lazy {
        listOf(
            createG1Unit1Greetings(),
            createG1Unit2Colors(),
            createG1Unit3Numbers(),
            createG1Unit4Classroom(),
            createG1Unit5Animals(),
            createG1Unit6MyFamily(),
            createG1Unit7Toys(),
            createG1Unit8YummySnacks(),
            createG1Unit9Shapes(),
            createG1Unit10MySweetHome()
        ).map { normalizeUnitTopic(it) }
    }

    // ==========================================
    // GRADE 2 UNITS (MEB 2. Sınıf - 14 Tam Ünite)
    // ==========================================
    val grade2Units: List<UnitTopic> by lazy {
        listOf(
            createG2Unit1Words(),
            createG2Unit2Friends(),
            createG2Unit3InTheClassroom(),
            createG2Unit4Numbers(),
            createG2Unit5Colors(),
            createG2Unit6AtThePlayground(),
            createG2Unit7BodyParts(),
            createG2Unit8Pets(),
            createG2Unit9Fruit(),
            createG2Unit10Animals(),
            createG2Unit11NatureGarden(),
            createG2Unit12PicnicParty(),
            createG2Unit13MusicInstruments(),
            createG2Unit14CircusFun()
        ).map { normalizeUnitTopic(it) }
    }

    val grade3Units: List<UnitTopic> by lazy {
        Grade3And4CurriculumData.grade3Units.map { normalizeUnitTopic(it) }
    }

    val grade4Units: List<UnitTopic> by lazy {
        Grade3And4CurriculumData.grade4Units.map { normalizeUnitTopic(it) }
    }

    private fun normalizeUnitTopic(unit: UnitTopic): UnitTopic {
        return unit.copy(
            lessons = unit.lessons.map { normalizeLesson(it) }
        )
    }

    private fun normalizeLesson(lesson: Lesson): Lesson {
        val targetWords = lesson.targetWords
        // 1. Mandatory Flashcards for every target word at the beginning of the lesson (Never quiz without teaching first)
        val flashcards = targetWords.map { word ->
            lesson.exercises.find { it.type == ExerciseType.FLASHCARD && it.targetWord.id == word.id }
                ?: createFlashcard(word)
        }

        // 2. Only quiz exercises testing words that belong to targetWords
        // AND ensure distractors/options only contain words that are taught in targetWords
        val validQuizzes = lesson.exercises.filter { ex ->
            ex.type != ExerciseType.FLASHCARD && targetWords.any { it.id == ex.targetWord.id }
        }.map { ex ->
            if (ex.options.isNotEmpty()) {
                val target = ex.targetWord
                val targetOption = ex.options.find { it.isCorrect }
                    ?: WordOption(target.english, target.turkish, target.emoji, true)
                val otherOptions = ex.options.filter { opt ->
                    !opt.isCorrect && targetWords.any { tw -> tw.english.equals(opt.word, ignoreCase = true) }
                }
                val otherTargetWords = targetWords.filter { it.id != target.id }
                val fallbackDistractors = otherTargetWords.map {
                    WordOption(it.english, it.turkish, it.emoji, false)
                }
                val finalOptions = (listOf(targetOption) + (otherOptions + fallbackDistractors).distinctBy { it.word }).take(4).shuffled()
                ex.copy(options = finalOptions)
            } else {
                ex
            }
        }

        return lesson.copy(
            exercises = flashcards + validQuizzes
        )
    }

    fun getAllUnits(): List<UnitTopic> = grade1Units + grade2Units + grade3Units + grade4Units

    fun getUnitsForGrade(grade: GradeLevel): List<UnitTopic> {
        return when (grade) {
            GradeLevel.GRADE_1 -> grade1Units
            GradeLevel.GRADE_2 -> grade2Units
            GradeLevel.GRADE_3 -> grade3Units
            GradeLevel.GRADE_4 -> grade4Units
        }
    }

    fun findLessonById(lessonId: String): Lesson? {
        return getAllUnits().flatMap { it.lessons }.find { it.id == lessonId }
    }

    fun getAllWords(): List<WordItem> {
        return getAllUnits().flatMap { unit -> unit.lessons.flatMap { it.targetWords } }.distinctBy { it.id }
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 1: Hello & Greetings
    // ----------------------------------------------------------------------
    private fun createG1Unit1Greetings(): UnitTopic {
        val wHello = WordItem("w_g1_hello", GradeLevel.GRADE_1, 1, "Hello", "Merhaba", "he-lo", "👋", "Greetings", "Hello, my friend!", "Merhaba arkadaşım!")
        val wHi = WordItem("w_g1_hi", GradeLevel.GRADE_1, 1, "Hi", "Selam", "hay", "😃", "Greetings", "Hi Poko!", "Selam Poko!")
        val wGoodbye = WordItem("w_g1_bye", GradeLevel.GRADE_1, 1, "Goodbye", "Hoşça kal", "gud-bay", "🙋‍♂️", "Greetings", "Goodbye teacher!", "Hoşça kal öğretmenim!")
        val wGoodMorning = WordItem("w_g1_gmorning", GradeLevel.GRADE_1, 1, "Good morning", "Günaydın", "gud mor-ning", "🌅", "Greetings", "Good morning sun!", "Günaydın güneş!")
        val wThankYou = WordItem("w_g1_thanks", GradeLevel.GRADE_1, 1, "Thank you", "Teşekkür ederim", "tenk yu", "🙏", "Polite", "Thank you very much!", "Çok teşekkür ederim!")

        val lesson1 = Lesson(
            id = "g1_u1_l1",
            unitId = 1,
            grade = GradeLevel.GRADE_1,
            titleTr = "Merhaba & Selamlaşma",
            titleEn = "Hello & Greetings",
            description = "İngilizce selam vermeyi ve vedalaşmayı öğrenelim!",
            iconEmoji = "👋",
            targetWords = listOf(wHello, wHi, wGoodbye, wGoodMorning, wThankYou),
            exercises = listOf(
                createFlashcard(wHello),
                createPictureMatch(wHello, listOf(wHello, wGoodbye, wGoodMorning, wThankYou)),
                createBalloonPop(wHi, listOf(wHi, wHello, wGoodbye, wThankYou)),
                createAudioQuiz(wGoodbye, listOf(wGoodbye, wHello, wGoodMorning, wThankYou)),
                createWordBuilder(wHi),
                createFlashcard(wGoodMorning),
                createSentenceBuilder("Good morning", "Günaydın", listOf("Good", "morning"), wGoodMorning)
            )
        )

        return UnitTopic(
            id = 1,
            grade = GradeLevel.GRADE_1,
            number = 1,
            titleTr = "Selamlaşma & Nezaket",
            titleEn = "Hello & Greetings",
            iconEmoji = "👋",
            colorHex = 0xFF4E75F5,
            mebCode = "E1.1",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 2: Colors
    // ----------------------------------------------------------------------
    private fun createG1Unit2Colors(): UnitTopic {
        val wRed = WordItem("w_g1_red", GradeLevel.GRADE_1, 2, "Red", "Kırmızı", "red", "🔴", "Colors", "Red apple", "Kırmızı elma")
        val wBlue = WordItem("w_g1_blue", GradeLevel.GRADE_1, 2, "Blue", "Mavi", "blu", "🔵", "Colors", "Blue sky", "Mavi gökyüzü")
        val wYellow = WordItem("w_g1_yellow", GradeLevel.GRADE_1, 2, "Yellow", "Sarı", "ye-lov", "🟡", "Colors", "Yellow sun", "Sarı güneş")
        val wGreen = WordItem("w_g1_green", GradeLevel.GRADE_1, 2, "Green", "Yeşil", "griin", "🟢", "Colors", "Green tree", "Yeşil ağaç")

        val lesson1 = Lesson(
            id = "g1_u2_l1",
            unitId = 2,
            grade = GradeLevel.GRADE_1,
            titleTr = "Renk Dünyası",
            titleEn = "Color World",
            description = "Kırmızı, mavi, sarı ve yeşil renkleri tanıyalım!",
            iconEmoji = "🎨",
            targetWords = listOf(wRed, wBlue, wYellow, wGreen),
            exercises = listOf(
                createFlashcard(wRed),
                createFlashcard(wBlue),
                createPictureMatch(wRed, listOf(wRed, wBlue, wYellow, wGreen)),
                createWordBuilder(wRed),
                createBalloonPop(wYellow, listOf(wYellow, wRed, wBlue, wGreen)),
                createAudioQuiz(wGreen, listOf(wGreen, wYellow, wRed, wBlue))
            )
        )

        return UnitTopic(
            id = 2,
            grade = GradeLevel.GRADE_1,
            number = 2,
            titleTr = "Rengarenk Dünyamız",
            titleEn = "Magic Colors",
            iconEmoji = "🎨",
            colorHex = 0xFFEC4899,
            mebCode = "E1.2",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 3: Numbers (1-5)
    // ----------------------------------------------------------------------
    private fun createG1Unit3Numbers(): UnitTopic {
        val wOne = WordItem("w_g1_one", GradeLevel.GRADE_1, 3, "One", "Bir (1)", "van", "1️⃣", "Numbers", "One star", "Bir yıldız")
        val wTwo = WordItem("w_g1_two", GradeLevel.GRADE_1, 3, "Two", "İki (2)", "tu", "2️⃣", "Numbers", "Two shoes", "İki ayakkabı")
        val wThree = WordItem("w_g1_three", GradeLevel.GRADE_1, 3, "Three", "Üç (3)", "tri", "3️⃣", "Numbers", "Three birds", "Üç kuş")
        val wFour = WordItem("w_g1_four", GradeLevel.GRADE_1, 3, "Four", "Dört (4)", "for", "4️⃣", "Numbers", "Four cats", "Dört kedi")
        val wFive = WordItem("w_g1_five", GradeLevel.GRADE_1, 3, "Five", "Beş (5)", "fayv", "5️⃣", "Numbers", "Five stars", "Beş yıldız")

        val lesson1 = Lesson(
            id = "g1_u3_l1",
            unitId = 3,
            grade = GradeLevel.GRADE_1,
            titleTr = "Sayıları Sayalım (1-5)",
            titleEn = "Counting Fun (1-5)",
            description = "Birden beşe kadar saymayı öğrenelim!",
            iconEmoji = "🔢",
            targetWords = listOf(wOne, wTwo, wThree, wFour, wFive),
            exercises = listOf(
                createFlashcard(wOne),
                createPictureMatch(wOne, listOf(wOne, wTwo, wThree, wFour)),
                createWordBuilder(wOne),
                createBalloonPop(wTwo, listOf(wTwo, wOne, wThree, wFive)),
                createAudioQuiz(wThree, listOf(wThree, wTwo, wFour, wFive)),
                createWordBuilder(wTwo)
            )
        )

        return UnitTopic(
            id = 3,
            grade = GradeLevel.GRADE_1,
            number = 3,
            titleTr = "Neşeli Sayılar",
            titleEn = "Playful Numbers",
            iconEmoji = "🔢",
            colorHex = 0xFF8B5CF6,
            mebCode = "E1.3",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 4: Classroom Objects
    // ----------------------------------------------------------------------
    private fun createG1Unit4Classroom(): UnitTopic {
        val wBook = WordItem("w_g1_book", GradeLevel.GRADE_1, 4, "Book", "Kitap", "buk", "📖", "Classroom", "Open your book", "Kitabını aç")
        val wPencil = WordItem("w_g1_pencil", GradeLevel.GRADE_1, 4, "Pencil", "Kurşun Kalem", "pen-sıl", "✏️", "Classroom", "A red pencil", "Kırmızı bir kalem")
        val wBag = WordItem("w_g1_bag", GradeLevel.GRADE_1, 4, "School Bag", "Okul Çantası", "skuul beg", "🎒", "Classroom", "My school bag", "Benim okul çantam")

        val lesson1 = Lesson(
            id = "g1_u4_l1",
            unitId = 4,
            grade = GradeLevel.GRADE_1,
            titleTr = "Sınıf Eşyalarımız",
            titleEn = "Classroom Objects",
            description = "Kitap, kalem ve çanta kelimeleri!",
            iconEmoji = "🎒",
            targetWords = listOf(wBook, wPencil, wBag),
            exercises = listOf(
                createFlashcard(wBook),
                createFlashcard(wPencil),
                createFlashcard(wBag),
                createPictureMatch(wBook, listOf(wBook, wPencil, wBag)),
                createWordBuilder(wBag),
                createAudioQuiz(wPencil, listOf(wPencil, wBook, wBag))
            )
        )

        return UnitTopic(
            id = 4,
            grade = GradeLevel.GRADE_1,
            number = 4,
            titleTr = "Okul & Sınıfımız",
            titleEn = "My Classroom",
            iconEmoji = "🎒",
            colorHex = 0xFF06B6D4,
            mebCode = "E1.4",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 5: Animals
    // ----------------------------------------------------------------------
    private fun createG1Unit5Animals(): UnitTopic {
        val wCat = WordItem("w_g1_cat", GradeLevel.GRADE_1, 5, "Cat", "Kedi", "ket", "🐱", "Animals", "I love my cat", "Kedimi seviyorum")
        val wDog = WordItem("w_g1_dog", GradeLevel.GRADE_1, 5, "Dog", "Köpek", "dog", "🐶", "Animals", "A cute dog", "Sevimli bir köpek")
        val wBird = WordItem("w_g1_bird", GradeLevel.GRADE_1, 5, "Bird", "Kuş", "böörd", "🐦", "Animals", "A singing bird", "Öten bir kuş")

        val lesson1 = Lesson(
            id = "g1_u5_l1",
            unitId = 5,
            grade = GradeLevel.GRADE_1,
            titleTr = "Sevimli Hayvan Dostlarımız",
            titleEn = "Cute Animal Friends",
            description = "Kedi, köpek ve kuş ile tanışalım!",
            iconEmoji = "🐱",
            targetWords = listOf(wCat, wDog, wBird),
            exercises = listOf(
                createFlashcard(wCat),
                createPictureMatch(wCat, listOf(wCat, wDog, wBird)),
                createWordBuilder(wCat),
                createWordBuilder(wDog),
                createAudioQuiz(wBird, listOf(wBird, wDog, wCat))
            )
        )

        return UnitTopic(
            id = 5,
            grade = GradeLevel.GRADE_1,
            number = 5,
            titleTr = "Hayvan Arkadaşlarım",
            titleEn = "Cute Animals",
            iconEmoji = "🐱",
            colorHex = 0xFFF59E0B,
            mebCode = "E1.5",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 6: My Family
    // ----------------------------------------------------------------------
    private fun createG1Unit6MyFamily(): UnitTopic {
        val wMom = WordItem("w_g1_mom", GradeLevel.GRADE_1, 6, "Mother", "Anne", "ma-dır", "👩", "Family", "I love my mother", "Annemi seviyorum")
        val wDad = WordItem("w_g1_dad", GradeLevel.GRADE_1, 6, "Father", "Baba", "fa-dır", "👨", "Family", "This is my father", "Bu benim babam")
        val wBaby = WordItem("w_g1_baby", GradeLevel.GRADE_1, 6, "Baby", "Bebek", "bey-bi", "👶", "Family", "A cute baby", "Sevimli bir bebek")

        val lesson1 = Lesson(
            id = "g1_u6_l1",
            unitId = 6,
            grade = GradeLevel.GRADE_1,
            titleTr = "Benim Ailem",
            titleEn = "My Family",
            description = "Anne, baba ve bebek kelimeleri!",
            iconEmoji = "👨‍👩‍👧",
            targetWords = listOf(wMom, wDad, wBaby),
            exercises = listOf(
                createFlashcard(wMom),
                createFlashcard(wDad),
                createPictureMatch(wMom, listOf(wMom, wDad, wBaby)),
                createWordBuilder(wDad),
                createAudioQuiz(wBaby, listOf(wBaby, wMom, wDad))
            )
        )

        return UnitTopic(
            id = 6,
            grade = GradeLevel.GRADE_1,
            number = 6,
            titleTr = "Sevgi Dolu Ailem",
            titleEn = "My Family",
            iconEmoji = "👨‍👩‍👧",
            colorHex = 0xFF10B981,
            mebCode = "E1.6",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 7: My Toys (Oyuncaklarım)
    // ----------------------------------------------------------------------
    private fun createG1Unit7Toys(): UnitTopic {
        val wTeddy = WordItem("w_g1_teddy", GradeLevel.GRADE_1, 7, "Teddy Bear", "Oyuncak Ayı", "te-di beer", "🧸", "Toys", "A cute teddy bear.", "Sevimli bir oyuncak ayı.")
        val wRobot = WordItem("w_g1_robot", GradeLevel.GRADE_1, 7, "Robot", "Robot", "rov-bot", "🤖", "Toys", "My shiny robot.", "Parlak robotum.")
        val wDoll = WordItem("w_g1_doll", GradeLevel.GRADE_1, 7, "Doll", "Oyuncak Bebek", "dol", "🪆", "Toys", "A pretty doll.", "Güzel bir oyuncak bebek.")
        val wBall = WordItem("w_g1_ball", GradeLevel.GRADE_1, 7, "Ball", "Top", "bool", "⚽", "Toys", "Play with the ball.", "Topla oyna.")
        val wCar = WordItem("w_g1_car", GradeLevel.GRADE_1, 7, "Car", "Oyuncak Araba", "kaar", "🚗", "Toys", "Fast red car.", "Hızlı kırmızı araba.")
        val wTrain = WordItem("w_g1_train", GradeLevel.GRADE_1, 7, "Train", "Oyuncak Tren", "treyn", "🚂", "Toys", "Choo choo train!", "Çuf çuf tren!")

        val lesson1 = Lesson(
            id = "g1_u7_l1",
            unitId = 7,
            grade = GradeLevel.GRADE_1,
            titleTr = "Renkli Oyuncaklarım",
            titleEn = "My Colorful Toys",
            description = "Teddy bear, robot, doll, ball, car ve train!",
            iconEmoji = "🧸",
            targetWords = listOf(wTeddy, wRobot, wDoll, wBall, wCar, wTrain),
            exercises = listOf(
                createFlashcard(wTeddy),
                createFlashcard(wRobot),
                createFlashcard(wDoll),
                createPictureMatch(wTeddy, listOf(wTeddy, wRobot, wDoll, wBall)),
                createWordBuilder(wDoll),
                createWordBuilder(wBall),
                createWordBuilder(wCar),
                createBalloonPop(wRobot, listOf(wRobot, wTeddy, wCar, wTrain)),
                createAudioQuiz(wTrain, listOf(wTrain, wCar, wDoll, wTeddy)),
                createSentenceBuilder("A red car", "Kırmızı bir araba", listOf("A", "red", "car"), wCar)
            )
        )

        return UnitTopic(
            id = 701,
            grade = GradeLevel.GRADE_1,
            number = 7,
            titleTr = "Oyuncaklarım & Oyunlar",
            titleEn = "My Toys & Fun",
            iconEmoji = "🧸",
            colorHex = 0xFFF59E0B,
            mebCode = "E1.7",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 8: Yummy Snacks (Sağlıklı Yiyecekler)
    // ----------------------------------------------------------------------
    private fun createG1Unit8YummySnacks(): UnitTopic {
        val wWater = WordItem("w_g1_water", GradeLevel.GRADE_1, 8, "Water", "Su", "voo-tır", "💧", "Food", "Drink cool water.", "Serin su iç.")
        val wMilk = WordItem("w_g1_milk", GradeLevel.GRADE_1, 8, "Milk", "Süt", "milk", "🥛", "Food", "White sweet milk.", "Beyaz tatlı süt.")
        val wBread = WordItem("w_g1_bread", GradeLevel.GRADE_1, 8, "Bread", "Ekmek", "bred", "🍞", "Food", "Fresh warm bread.", "Taze sıcak ekmek.")
        val wEgg = WordItem("w_g1_egg", GradeLevel.GRADE_1, 8, "Egg", "Yumurta", "eg", "🥚", "Food", "Eat an egg.", "Bir yumurta ye.")
        val wCheese = WordItem("w_g1_cheese", GradeLevel.GRADE_1, 8, "Cheese", "Peynir", "çiiz", "🧀", "Food", "Yummy yellow cheese.", "Nefis sarı peynir.")

        val lesson1 = Lesson(
            id = "g1_u8_l1",
            unitId = 8,
            grade = GradeLevel.GRADE_1,
            titleTr = "Nefis Atıştırmalıklar",
            titleEn = "Yummy Healthy Snacks",
            description = "Water, milk, bread, egg ve cheese!",
            iconEmoji = "🍎",
            targetWords = listOf(wWater, wMilk, wBread, wEgg, wCheese),
            exercises = listOf(
                createFlashcard(wWater),
                createFlashcard(wMilk),
                createFlashcard(wEgg),
                createPictureMatch(wMilk, listOf(wMilk, wWater, wBread, wCheese)),
                createWordBuilder(wEgg),
                createWordBuilder(wMilk),
                createBalloonPop(wBread, listOf(wBread, wCheese, wEgg, wMilk)),
                createAudioQuiz(wCheese, listOf(wCheese, wBread, wEgg, wWater)),
                createSentenceBuilder("I like milk", "Süt severim", listOf("I", "like", "milk"), wMilk)
            )
        )

        return UnitTopic(
            id = 702,
            grade = GradeLevel.GRADE_1,
            number = 8,
            titleTr = "Lezzetli & Sağlıklı Besinler",
            titleEn = "Yummy Snacks",
            iconEmoji = "🍎",
            colorHex = 0xFFEF4444,
            mebCode = "E1.8",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 9: Shapes & Little World (Şekiller)
    // ----------------------------------------------------------------------
    private fun createG1Unit9Shapes(): UnitTopic {
        val wCircle = WordItem("w_g1_circle", GradeLevel.GRADE_1, 9, "Circle", "Daire / Çember", "sör-kıl", "⭕", "Shapes", "A round circle.", "Yuvarlak bir daire.")
        val wSquare = WordItem("w_g1_square", GradeLevel.GRADE_1, 9, "Square", "Kare", "skve-ır", "🟩", "Shapes", "A green square.", "Yeşil bir kare.")
        val wTriangle = WordItem("w_g1_triangle", GradeLevel.GRADE_1, 9, "Triangle", "Üçgen", "tray-eng-gıl", "🔺", "Shapes", "A sharp triangle.", "Sivri bir üçgen.")
        val wStar = WordItem("w_g1_star", GradeLevel.GRADE_1, 9, "Star", "Yıldız", "staar", "⭐", "Shapes", "A shiny yellow star.", "Parlak sarı bir yıldız.")
        val wHeart = WordItem("w_g1_heart", GradeLevel.GRADE_1, 9, "Heart", "Kalp", "haart", "❤️", "Shapes", "A sweet red heart.", "Tatlı kırmızı bir kalp.")

        val lesson1 = Lesson(
            id = "g1_u9_l1",
            unitId = 9,
            grade = GradeLevel.GRADE_1,
            titleTr = "Sihirli Şekiller",
            titleEn = "Magic Shapes",
            description = "Circle, square, triangle, star ve heart!",
            iconEmoji = "🔷",
            targetWords = listOf(wCircle, wSquare, wTriangle, wStar, wHeart),
            exercises = listOf(
                createFlashcard(wCircle),
                createFlashcard(wSquare),
                createFlashcard(wStar),
                createPictureMatch(wStar, listOf(wStar, wCircle, wSquare, wTriangle)),
                createWordBuilder(wStar),
                createWordBuilder(wHeart),
                createBalloonPop(wTriangle, listOf(wTriangle, wSquare, wCircle, wStar)),
                createAudioQuiz(wSquare, listOf(wSquare, wCircle, wHeart, wStar)),
                createSentenceBuilder("A yellow star", "Sarı bir yıldız", listOf("A", "yellow", "star"), wStar)
            )
        )

        return UnitTopic(
            id = 703,
            grade = GradeLevel.GRADE_1,
            number = 9,
            titleTr = "Sihirli Şekiller Dünyası",
            titleEn = "Magic Shapes",
            iconEmoji = "🔷",
            colorHex = 0xFF8B5CF6,
            mebCode = "E1.9",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // GRADE 1 - UNIT 10: My Sweet Home (Evimdeki Eşyalar)
    // ----------------------------------------------------------------------
    private fun createG1Unit10MySweetHome(): UnitTopic {
        val wDoor = WordItem("w_g1_door", GradeLevel.GRADE_1, 10, "Door", "Kapı", "door", "🚪", "Home", "Open the door.", "Kapıyı aç.")
        val wWindow = WordItem("w_g1_window", GradeLevel.GRADE_1, 10, "Window", "Pencere", "vin-dov", "🪟", "Home", "Look at the window.", "Pencereye bak.")
        val wBed = WordItem("w_g1_bed", GradeLevel.GRADE_1, 10, "Bed", "Yatak", "bed", "🛏️", "Home", "A soft cozy bed.", "Yumuşak rahat bir yatak.")
        val wChair = WordItem("w_g1_chair", GradeLevel.GRADE_1, 10, "Chair", "Sandalye", "çe-ır", "🪑", "Home", "Sit on the chair.", "Sandalyeye otur.")
        val wTable = WordItem("w_g1_table", GradeLevel.GRADE_1, 10, "Table", "Masa", "tey-bıl", "🪵", "Home", "The book is on the table.", "Kitap masanın üstünde.")
        val wLamp = WordItem("w_g1_lamp", GradeLevel.GRADE_1, 10, "Lamp", "Lamba", "lemp", "💡", "Home", "Turn on the lamp.", "Lambayı aç.")

        val lesson1 = Lesson(
            id = "g1_u10_l1",
            unitId = 10,
            grade = GradeLevel.GRADE_1,
            titleTr = "Güzel Evim & Eşyalar",
            titleEn = "My Cozy Home",
            description = "Door, window, bed, chair, table ve lamp!",
            iconEmoji = "🏡",
            targetWords = listOf(wDoor, wWindow, wBed, wChair, wTable, wLamp),
            exercises = listOf(
                createFlashcard(wDoor),
                createFlashcard(wBed),
                createFlashcard(wLamp),
                createPictureMatch(wBed, listOf(wBed, wDoor, wWindow, wChair)),
                createWordBuilder(wDoor),
                createWordBuilder(wBed),
                createWordBuilder(wLamp),
                createBalloonPop(wWindow, listOf(wWindow, wTable, wDoor, wBed)),
                createAudioQuiz(wChair, listOf(wChair, wTable, wLamp, wDoor)),
                createSentenceBuilder("Open the door", "Kapıyı aç", listOf("Open", "the", "door"), wDoor)
            )
        )

        return UnitTopic(
            id = 704,
            grade = GradeLevel.GRADE_1,
            number = 10,
            titleTr = "Sıcak & Güzel Evim",
            titleEn = "My Sweet Home",
            iconEmoji = "🏡",
            colorHex = 0xFF10B981,
            mebCode = "E1.10",
            lessons = listOf(lesson1)
        )
    }

    // ======================================================================
    // MEB 2. SINIF MÜFREDATI - 10 TAM ÜNİTE
    // ======================================================================

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 1: WORDS (MEB E2.1 - İngilizce Sözcükler & Tanıdık Kelimeler)
    // ----------------------------------------------------------------------
    private fun createG2Unit1Words(): UnitTopic {
        val wAmbulance = WordItem("w_g2_ambulance", GradeLevel.GRADE_2, 1, "Ambulance", "Ambulans", "em-byu-lıns", "🚑", "Words", "Call the ambulance!", "Ambulansı ara!")
        val wBalloon = WordItem("w_g2_balloon", GradeLevel.GRADE_2, 1, "Balloon", "Balon", "bı-luun", "🎈", "Words", "A red balloon flies.", "Kırmızı bir balon uçuyor.")
        val wCake = WordItem("w_g2_cake", GradeLevel.GRADE_2, 1, "Cake", "Pasta / Kek", "keyk", "🎂", "Words", "A sweet birthday cake.", "Tatlı bir doğum günü pastası.")
        val wDoctor = WordItem("w_g2_doctor", GradeLevel.GRADE_2, 1, "Doctor", "Doktor", "dak-tır", "🩺", "Words", "The doctor helps us.", "Doktor bize yardım eder.")
        val wGorilla = WordItem("w_g2_gorilla", GradeLevel.GRADE_2, 1, "Gorilla", "Goril", "gı-ri-la", "🦍", "Words", "A strong gorilla.", "Güçlü bir goril.")
        val wHotel = WordItem("w_g2_hotel", GradeLevel.GRADE_2, 1, "Hotel", "Otel", "hov-tel", "🏨", "Words", "We stay in a hotel.", "Bir otelde kalıyoruz.")
        val wJudo = WordItem("w_g2_judo", GradeLevel.GRADE_2, 1, "Judo", "Judo (Spor)", "cuu-dov", "🥋", "Words", "I do judo.", "Ben judo yapıyorum.")
        val wKangaroo = WordItem("w_g2_kangaroo", GradeLevel.GRADE_2, 1, "Kangaroo", "Kanguru", "keng-gı-ruu", "🦘", "Words", "The kangaroo can jump.", "Kanguru zıplayabilir.")
        val wLemon = WordItem("w_g2_lemon", GradeLevel.GRADE_2, 1, "Lemon", "Limon", "le-mın", "🍋", "Words", "Yellow sour lemon.", "Sarı ekşi limon.")
        val wMilkshake = WordItem("w_g2_milkshake", GradeLevel.GRADE_2, 1, "Milkshake", "Muzlu/Çilekli Süt", "milk-şeyk", "🥤", "Words", "I like strawberry milkshake.", "Çilekli milkshake severim.")
        val wPanda = WordItem("w_g2_panda", GradeLevel.GRADE_2, 1, "Panda", "Panda", "pen-da", "🐼", "Words", "Cute black and white panda.", "Sevimli siyah beyaz panda.")
        val wPark = WordItem("w_g2_park", GradeLevel.GRADE_2, 1, "Park", "Park", "paark", "🌳", "Words", "Let's go to the park.", "Hadi parka gidelim.")
        val wPizza = WordItem("w_g2_pizza", GradeLevel.GRADE_2, 1, "Pizza", "Pizza", "pit-sa", "🍕", "Words", "I love cheese pizza.", "Peynirli pizzayı severim.")
        val wRadio = WordItem("w_g2_radio", GradeLevel.GRADE_2, 1, "Radio", "Radyo", "rey-di-ov", "📻", "Words", "Listen to the radio.", "Radyoyu dinle.")
        val wSalad = WordItem("w_g2_salad", GradeLevel.GRADE_2, 1, "Salad", "Salata", "se-lıd", "🥗", "Words", "Fresh green salad.", "Taze yeşil salata.")
        val wSupermarket = WordItem("w_g2_supermarket", GradeLevel.GRADE_2, 1, "Supermarket", "Süpermarket", "suu-pır-maar-kıt", "🛒", "Words", "Shopping at the supermarket.", "Süpermarkette alışveriş.")
        val wTaxi = WordItem("w_g2_taxi", GradeLevel.GRADE_2, 1, "Taxi", "Taksi", "tek-si", "🚕", "Words", "A yellow taxi arrives.", "Sarı bir taksi geliyor.")
        val wTelevision = WordItem("w_g2_tv", GradeLevel.GRADE_2, 1, "Television", "Televizyon", "te-le-vi-jın", "📺", "Words", "Watch television.", "Televizyon izle.")
        val wTrain = WordItem("w_g2_train", GradeLevel.GRADE_2, 1, "Train", "Tren", "treyn", "🚆", "Words", "The train is fast.", "Tren hızlıdır.")
        val wYoghurt = WordItem("w_g2_yoghurt", GradeLevel.GRADE_2, 1, "Yoghurt", "Yoğurt", "yov-gıt", "🥣", "Words", "Eat fresh yoghurt.", "Taze yoğurt ye.")
        val wZebra = WordItem("w_g2_zebra", GradeLevel.GRADE_2, 1, "Zebra", "Zebra", "zeb-ra", "🦓", "Words", "A striped zebra.", "Çizgili bir zebra.")

        val lesson1 = Lesson(
            id = "g2_u1_l1",
            unitId = 1,
            grade = GradeLevel.GRADE_2,
            titleTr = "Tanıdık Kelimeler (A-L)",
            titleEn = "Familiar Words (A-L)",
            description = "Ambulans, balon, pasta, doktor ve limon kelimelerini öğrenelim!",
            iconEmoji = "🔤",
            targetWords = listOf(wAmbulance, wBalloon, wCake, wDoctor, wGorilla, wKangaroo, wLemon, wMilkshake),
            exercises = listOf(
                createFlashcard(wAmbulance),
                createFlashcard(wBalloon),
                createFlashcard(wCake),
                createPictureMatch(wCake, listOf(wCake, wBalloon, wDoctor, wLemon)),
                createWordBuilder(wCake),
                createWordBuilder(wLemon),
                createBalloonPop(wBalloon, listOf(wBalloon, wAmbulance, wDoctor, wGorilla)),
                createAudioQuiz(wKangaroo, listOf(wKangaroo, wGorilla, wAmbulance, wLemon)),
                createSentenceBuilder("A red balloon", "Kırmızı bir balon", listOf("A", "red", "balloon"), wBalloon)
            )
        )

        val lesson2 = Lesson(
            id = "g2_u1_l2",
            unitId = 1,
            grade = GradeLevel.GRADE_2,
            titleTr = "Tanıdık Kelimeler (P-Z)",
            titleEn = "Familiar Words (P-Z)",
            description = "Panda, park, pizza, taksi, tren ve zebra kelimelerini keşfedelim!",
            iconEmoji = "🐼",
            targetWords = listOf(wPanda, wPark, wPizza, wRadio, wSalad, wSupermarket, wTaxi, wTelevision, wTrain, wYoghurt, wZebra),
            exercises = listOf(
                createFlashcard(wPanda),
                createFlashcard(wPizza),
                createPictureMatch(wPizza, listOf(wPizza, wPanda, wTaxi, wTrain)),
                createWordBuilder(wPark),
                createWordBuilder(wTaxi),
                createWordBuilder(wZebra),
                createBalloonPop(wTrain, listOf(wTrain, wTaxi, wPizza, wRadio)),
                createAudioQuiz(wPanda, listOf(wPanda, wZebra, wSalad, wYoghurt)),
                createSentenceBuilder("I like pizza", "Pizza severim", listOf("I", "like", "pizza"), wPizza)
            )
        )

        return UnitTopic(
            id = 7,
            grade = GradeLevel.GRADE_2,
            number = 1,
            titleTr = "1. Ünite: Words (Kelimeler)",
            titleEn = "Unit 1: Words",
            iconEmoji = "🔤",
            colorHex = 0xFF4E75F5,
            mebCode = "E2.1",
            lessons = listOf(lesson1, lesson2)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 2: FRIENDS (MEB E2.2 - Selamlaşma, Tanışma, İsim & Sınıf İletişimi)
    // ----------------------------------------------------------------------
    private fun createG2Unit2Friends(): UnitTopic {
        val wHello = WordItem("w_g2_hello", GradeLevel.GRADE_2, 2, "Hello", "Merhaba", "he-lo", "👋", "Friends", "Hello! What is your name?", "Merhaba! Senin adın ne?")
        val wHi = WordItem("w_g2_hi", GradeLevel.GRADE_2, 2, "Hi", "Selam", "hay", "🙋", "Friends", "Hi! I am Poko.", "Selam! Ben Poko.")
        val wGoodMorning = WordItem("w_g2_gmorning", GradeLevel.GRADE_2, 2, "Good morning", "Günaydın", "gud mor-ning", "🌅", "Friends", "Good morning teacher!", "Günaydın öğretmenim!")
        val wGoodAfternoon = WordItem("w_g2_gafternoon", GradeLevel.GRADE_2, 2, "Good afternoon", "Tünaydın / İyi günler", "gud aaf-tır-nuun", "☀️", "Friends", "Good afternoon my friend.", "İyi günler arkadaşım.")
        val wGoodEvening = WordItem("w_g2_gevening", GradeLevel.GRADE_2, 2, "Good evening", "İyi akşamlar", "gud iiv-ning", "🌆", "Friends", "Good evening mom and dad.", "İyi akşamlar anne ve baba.")
        val wGoodNight = WordItem("w_g2_gnight", GradeLevel.GRADE_2, 2, "Good night", "İyi geceler", "gud nayt", "🌙", "Friends", "Good night! Sweet dreams.", "İyi geceler! Tatlı rüyalar.")
        val wGoodbye = WordItem("w_g2_goodbye", GradeLevel.GRADE_2, 2, "Goodbye", "Hoşça kal", "gud-bay", "👋", "Friends", "Goodbye! See you soon.", "Hoşça kal! Yakında görüşürüz.")
        val wBye = WordItem("w_g2_bye", GradeLevel.GRADE_2, 2, "Bye", "Güle güle", "bay", "🙋", "Friends", "Bye bye!", "Güle güle!")
        val wFine = WordItem("w_g2_fine", GradeLevel.GRADE_2, 2, "I am fine", "İyiyim", "ay em fayn", "😊", "Friends", "I am fine, thank you.", "İyiyim, teşekkür ederim.")
        val wGreat = WordItem("w_g2_great", GradeLevel.GRADE_2, 2, "Great", "Harikayım / Çok iyi", "greyt", "🌟", "Friends", "I feel great today!", "Bugün harika hissediyorum!")
        val wNiceToMeetYou = WordItem("w_g2_nicetomeet", GradeLevel.GRADE_2, 2, "Nice to meet you", "Tanıştığıma memnun oldum", "nays tu miit yu", "🤝", "Friends", "Nice to meet you too.", "Ben de tanıştığıma memnun oldum.")
        val wExcuseMe = WordItem("w_g2_excuseme", GradeLevel.GRADE_2, 2, "Excuse me", "Affedersiniz / Özür dilerim", "eks-kyuuz mi", "🙋", "Polite Words", "Excuse me, may I come in?", "Affedersiniz, içeri girebilir miyim?")
        val wThankYou = WordItem("w_g2_thankyou", GradeLevel.GRADE_2, 2, "Thank you", "Teşekkür ederim", "tenk yu", "🙏", "Polite Words", "Thank you very much!", "Çok teşekkür ederim!")

        val lesson1 = Lesson(
            id = "g2_u2_l1",
            unitId = 2,
            grade = GradeLevel.GRADE_2,
            titleTr = "Selamlaşma & Günün Vakitleri",
            titleEn = "Greetings & Day Times",
            description = "Good morning, Good afternoon, Good evening ve Good night!",
            iconEmoji = "🌅",
            targetWords = listOf(wHello, wHi, wGoodMorning, wGoodAfternoon, wGoodEvening, wGoodNight, wGoodbye, wBye),
            exercises = listOf(
                createFlashcard(wGoodMorning),
                createFlashcard(wGoodNight),
                createPictureMatch(wGoodMorning, listOf(wGoodMorning, wGoodNight, wGoodbye, wHello)),
                createWordBuilder(wHello),
                createWordBuilder(wBye),
                createBalloonPop(wGoodEvening, listOf(wGoodEvening, wGoodMorning, wGoodNight, wGoodbye)),
                createAudioQuiz(wGoodNight, listOf(wGoodNight, wGoodMorning, wGoodAfternoon, wHello)),
                createSentenceBuilder("Good morning teacher", "Günaydın öğretmenim", listOf("Good", "morning", "teacher"), wGoodMorning)
            )
        )

        val lesson2 = Lesson(
            id = "g2_u2_l2",
            unitId = 2,
            grade = GradeLevel.GRADE_2,
            titleTr = "Tanışma & Hal Hatır Sorma",
            titleEn = "Meeting & Asking How Are You",
            description = "What is your name? How are you? I am fine, thank you!",
            iconEmoji = "🤝",
            targetWords = listOf(wFine, wGreat, wNiceToMeetYou, wHello, wGoodbye, wExcuseMe, wThankYou),
            exercises = listOf(
                createFlashcard(wFine),
                createFlashcard(wNiceToMeetYou),
                createPictureMatch(wFine, listOf(wFine, wGreat, wHello, wGoodbye)),
                createWordBuilder(wFine),
                createAudioQuiz(wNiceToMeetYou, listOf(wNiceToMeetYou, wFine, wGreat, wHello)),
                createSentenceBuilder("How are you", "Nasılsın", listOf("How", "are", "you"), wFine),
                createSentenceBuilder("I am fine thank you", "İyiyim teşekkür ederim", listOf("I", "am", "fine", "thank", "you"), wFine),
                createSentenceBuilder("Nice to meet you", "Tanıştığıma memnun oldum", listOf("Nice", "to", "meet", "you"), wNiceToMeetYou),
                createSentenceBuilder("Excuse me may I come in", "Affedersiniz içeri girebilir miyim", listOf("Excuse", "me", "may", "I", "come", "in"), wExcuseMe)
            )
        )

        return UnitTopic(
            id = 8,
            grade = GradeLevel.GRADE_2,
            number = 2,
            titleTr = "2. Ünite: Friends (Arkadaşlar)",
            titleEn = "Unit 2: Friends",
            iconEmoji = "🤝",
            colorHex = 0xFFEC4899,
            mebCode = "E2.2",
            lessons = listOf(lesson1, lesson2)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 3: CLASSROOM LIFE / IN THE CLASSROOM (MEB E2.3 & 2. Tema)
    // ----------------------------------------------------------------------
    private fun createG2Unit3InTheClassroom(): UnitTopic {
        // Sınıf Eşyaları & Araç Gereçler (Kitap 17-28. Sayfalar Tam Liste)
        val wBook = WordItem("w_g2_book", GradeLevel.GRADE_2, 3, "Book", "Kitap", "buk", "📖", "Classroom", "Open your book, please.", "Lütfen kitabını aç.")
        val wNotebook = WordItem("w_g2_notebook", GradeLevel.GRADE_2, 3, "Notebook", "Defter", "novt-buk", "📓", "Classroom", "Write in your notebook.", "Defterine yaz.")
        val wPencil = WordItem("w_g2_pencil", GradeLevel.GRADE_2, 3, "Pencil", "Kurşun Kalem", "pen-sıl", "✏️", "Classroom", "The pencil is yellow.", "Kalem sarıdır.")
        val wEraser = WordItem("w_g2_eraser", GradeLevel.GRADE_2, 3, "Eraser", "Silgi", "i-rey-zır", "🧼", "Classroom", "The eraser is pink.", "Silgi pembedir.")
        val wRuler = WordItem("w_g2_ruler", GradeLevel.GRADE_2, 3, "Ruler", "Cetvel", "ruu-lır", "📏", "Classroom", "The ruler is blue.", "Cetvel mavidir.")
        val wSchoolBag = WordItem("w_g2_schoolbag", GradeLevel.GRADE_2, 3, "School Bag", "Okul Çantası", "skuul beg", "🎒", "Classroom", "The bag is red.", "Çanta kırmızıdır.")
        val wPencilCase = WordItem("w_g2_pencilcase", GradeLevel.GRADE_2, 3, "Pencil Case", "Kalemlik", "pen-sıl keys", "👝", "Classroom", "It is a pencil case.", "O bir kalemliktir.")
        val wSharpener = WordItem("w_g2_sharpener", GradeLevel.GRADE_2, 3, "Sharpener", "Kalemtıraş", "şaar-pı-nır", "✏️", "Classroom", "The sharpener is green.", "Kalemtıraş yeşildir.")
        val wCrayon = WordItem("w_g2_crayon", GradeLevel.GRADE_2, 3, "Crayon", "Pastel Boya", "krey-on", "🖍️", "Classroom", "Color with a crayon.", "Pastel boya ile boya.")
        val wScissors = WordItem("w_g2_scissors", GradeLevel.GRADE_2, 3, "Scissors", "Makas", "si-zırz", "✂️", "Classroom", "Cut the paper with scissors.", "Makasla kağıdı kes.")
        val wGlue = WordItem("w_g2_glue", GradeLevel.GRADE_2, 3, "Glue", "Yapıştırıcı", "gluu", "🧴", "Classroom", "Glue the shapes.", "Şekilleri yapıştır.")
        val wPaper = WordItem("w_g2_paper", GradeLevel.GRADE_2, 3, "Paper", "Kağıt", "pey-pır", "📄", "Classroom", "Draw on white paper.", "Beyaz kağıda çiz.")

        // Sınıf Mekanı & Mobilyalar
        val wDesk = WordItem("w_g2_desk", GradeLevel.GRADE_2, 3, "Desk", "Sıra / Çalışma Masası", "desk", "🗄️", "Classroom", "Sit at your desk.", "Sırana otur.")
        val wChair = WordItem("w_g2_chair", GradeLevel.GRADE_2, 3, "Chair", "Sandalye", "çe-ır", "🪑", "Classroom", "Sit on the chair.", "Sandalyeye otur.")
        val wTable = WordItem("w_g2_table", GradeLevel.GRADE_2, 3, "Table", "Masa", "tey-bıl", "🪵", "Classroom", "The book is on the table.", "Kitap masanın üstünde.")
        val wBoard = WordItem("w_g2_board", GradeLevel.GRADE_2, 3, "Board", "Yazı Tahtası", "boord", "📋", "Classroom", "Clean the board, please.", "Lütfen tahtayı sil.")
        val wBookcase = WordItem("w_g2_bookcase", GradeLevel.GRADE_2, 3, "Bookcase", "Kitaplık", "buk-keys", "📚", "Classroom", "Books in the bookcase.", "Kitaplıktaki kitaplar.")
        val wClock = WordItem("w_g2_clock", GradeLevel.GRADE_2, 3, "Clock", "Duvar Saati", "klak", "⏰", "Classroom", "Look at the clock.", "Saate bak.")
        val wDoor = WordItem("w_g2_door", GradeLevel.GRADE_2, 3, "Door", "Kapı", "door", "🚪", "Classroom", "Close the door, please.", "Lütfen kapıyı kapat.")
        val wWindow = WordItem("w_g2_window", GradeLevel.GRADE_2, 3, "Window", "Pencere", "vin-dov", "🪟", "Classroom", "Open the window.", "Pencereyi aç.")
        val wLight = WordItem("w_g2_light", GradeLevel.GRADE_2, 3, "Light", "Lamba / Işık", "layt", "💡", "Classroom", "Turn on the light.", "Işığı aç.")
        val wTeacher = WordItem("w_g2_teacher", GradeLevel.GRADE_2, 3, "Teacher", "Öğretmen", "tii-çır", "👩‍🏫", "Classroom", "Listen to the teacher.", "Öğretmeni dinle.")
        val wPupil = WordItem("w_g2_pupil", GradeLevel.GRADE_2, 3, "Pupil", "Öğrenci", "pyuu-pıl", "🧒", "Classroom", "Good morning, pupils!", "Günaydın öğrenciler!")

        // Sınıf İçi Yönergeler ve Eylemler (Classroom Commands & Actions)
        val wStandUp = WordItem("w_g2_standup", GradeLevel.GRADE_2, 3, "Stand up", "Ayağa kalk", "stend ap", "🧍", "Instructions", "Stand up, please!", "Lütfen ayağa kalk!")
        val wSitDown = WordItem("w_g2_sitdown", GradeLevel.GRADE_2, 3, "Sit down", "Otur", "sit dawn", "🪑", "Instructions", "Sit down, please!", "Lütfen otur!")
        val wListen = WordItem("w_g2_listen", GradeLevel.GRADE_2, 3, "Listen", "Dinle", "li-sın", "👂", "Instructions", "Listen carefully.", "Dikkatlice dinle.")
        val wBeQuiet = WordItem("w_g2_bequiet", GradeLevel.GRADE_2, 3, "Be quiet", "Sessiz ol", "bii kva-yıt", "🤫", "Instructions", "Silence, please!", "Sessizlik, lütfen!")
        val wRaiseHand = WordItem("w_g2_raisehand", GradeLevel.GRADE_2, 3, "Raise your hand", "Elini kaldır", "reyz yor hend", "🙋", "Instructions", "Raise your hand to speak.", "Konuşmak için elini kaldır.")
        val wPointTo = WordItem("w_g2_pointto", GradeLevel.GRADE_2, 3, "Point to", "İşaret et / Göster", "poynt tu", "👉", "Instructions", "Point to the board.", "Tahtayı göster.")
        val wTurnOn = WordItem("w_g2_turnon", GradeLevel.GRADE_2, 3, "Turn on", "Aç (Lamba/Cihaz)", "törn on", "💡", "Instructions", "Turn on the light.", "Işığı aç.")
        val wTurnOff = WordItem("w_g2_turnoff", GradeLevel.GRADE_2, 3, "Turn off", "Kapat (Lamba/Cihaz)", "törn of", "🔌", "Instructions", "Turn off the light.", "Işığı kapat.")
        val wCleanBoard = WordItem("w_g2_cleanboard", GradeLevel.GRADE_2, 3, "Clean the board", "Tahtayı sil", "kliin dı boord", "🧹", "Instructions", "Clean the board, please.", "Lütfen tahtayı sil.")
        val wTakeOut = WordItem("w_g2_takeout", GradeLevel.GRADE_2, 3, "Take out", "Çıkar", "teyk awt", "🎒", "Instructions", "Take out your pencil.", "Kalemini çıkar.")
        val wPutAway = WordItem("w_g2_putaway", GradeLevel.GRADE_2, 3, "Put away", "Yerine koy / Kaldır", "put ı-vey", "📥", "Instructions", "Put away your book.", "Kitabını yerine koy.")
        val wTouchDesk = WordItem("w_g2_touchdesk", GradeLevel.GRADE_2, 3, "Touch your desk", "Sırana dokun", "taç yor desk", "👆", "Instructions", "Touch your desk and chair.", "Sırana ve sandalyene dokun.")
        val wGiveMe = WordItem("w_g2_giveme", GradeLevel.GRADE_2, 3, "Give me", "Bana ver", "giv mi", "🤲", "Polite Requests", "Give me the ruler, please.", "Bana cetveli ver, lütfen.")
        val wHereYouAre = WordItem("w_g2_hereyouare", GradeLevel.GRADE_2, 3, "Here you are", "Buyur / İşte burada", "hi-ır yu aar", "🎁", "Polite Requests", "Here you are! - Thank you.", "Buyur! - Teşekkürler.")

        // Ders 1: Okul & Çanta Eşyaları (School Objects)
        val lesson1 = Lesson(
            id = "g2_u3_l1",
            unitId = 3,
            grade = GradeLevel.GRADE_2,
            titleTr = "Sınıf & Çanta Eşyalarımız",
            titleEn = "Classroom & Bag Objects",
            description = "Book, pencil, eraser, ruler, sharpener, crayon, pencil case, scissors ve glue!",
            iconEmoji = "🎒",
            targetWords = listOf(wBook, wNotebook, wPencil, wEraser, wRuler, wSchoolBag, wPencilCase, wSharpener, wCrayon, wScissors, wGlue, wPaper),
            exercises = listOf(
                createFlashcard(wBook),
                createFlashcard(wPencil),
                createFlashcard(wEraser),
                createFlashcard(wSharpener),
                createFlashcard(wCrayon),
                createPictureMatch(wSharpener, listOf(wSharpener, wEraser, wPencil, wRuler)),
                createPictureMatch(wCrayon, listOf(wCrayon, wScissors, wGlue, wNotebook)),
                createWordBuilder(wBook),
                createWordBuilder(wRuler),
                createWordBuilder(wCrayon),
                createWordBuilder(wGlue),
                createBalloonPop(wEraser, listOf(wEraser, wBook, wPencil, wSharpener)),
                createAudioQuiz(wSchoolBag, listOf(wSchoolBag, wPencilCase, wBook, wPencil)),
                createAudioQuiz(wSharpener, listOf(wSharpener, wRuler, wScissors, wGlue)),
                createSentenceBuilder("Open your book", "Kitabını aç", listOf("Open", "your", "book"), wBook),
                createSentenceBuilder("The ruler is blue", "Cetvel mavidir", listOf("The", "ruler", "is", "blue"), wRuler),
                createSentenceBuilder("The eraser is pink", "Silgi pembedir", listOf("The", "eraser", "is", "pink"), wEraser),
                createSentenceBuilder("The sharpener is green", "Kalemtıraş yeşildir", listOf("The", "sharpener", "is", "green"), wSharpener)
            )
        )

        // Ders 2: Sınıf Mekanı ve Mobilyalar (In Our Classroom)
        val lesson2 = Lesson(
            id = "g2_u3_l2",
            unitId = 3,
            grade = GradeLevel.GRADE_2,
            titleTr = "Sınıfımız ve Mobilyalar",
            titleEn = "In Our Classroom & Furniture",
            description = "Desk, chair, table, board, bookcase, clock, door, window, teacher ve pupil!",
            iconEmoji = "🏫",
            targetWords = listOf(wDesk, wChair, wTable, wBoard, wBookcase, wClock, wDoor, wWindow, wLight, wTeacher, wPupil),
            exercises = listOf(
                createFlashcard(wDesk),
                createFlashcard(wChair),
                createFlashcard(wBoard),
                createFlashcard(wBookcase),
                createFlashcard(wClock),
                createPictureMatch(wBoard, listOf(wBoard, wDesk, wChair, wDoor)),
                createPictureMatch(wClock, listOf(wClock, wBookcase, wTable, wWindow)),
                createWordBuilder(wDesk),
                createWordBuilder(wChair),
                createWordBuilder(wDoor),
                createWordBuilder(wClock),
                createBalloonPop(wBoard, listOf(wBoard, wDesk, wWindow, wDoor)),
                createAudioQuiz(wWindow, listOf(wWindow, wDoor, wBoard, wTable)),
                createAudioQuiz(wTeacher, listOf(wTeacher, wPupil, wDesk, wChair)),
                createSentenceBuilder("Close the door please", "Lütfen kapıyı kapat", listOf("Close", "the", "door", "please"), wDoor),
                createSentenceBuilder("Open the window", "Pencereyi aç", listOf("Open", "the", "window"), wWindow),
                createSentenceBuilder("Good morning pupils", "Günaydın öğrenciler", listOf("Good", "morning", "pupils"), wPupil)
            )
        )

        // Ders 3: Sınıf Yönergeleri & Komutlar (Classroom Instructions & Actions)
        val lesson3 = Lesson(
            id = "g2_u3_l3",
            unitId = 3,
            grade = GradeLevel.GRADE_2,
            titleTr = "Sınıf Yönergeleri & Komutlar",
            titleEn = "Classroom Commands & Actions",
            description = "Stand up, Sit down, Listen, Be quiet, Clean the board, Turn on/off the light, Raise your hand!",
            iconEmoji = "🤫",
            targetWords = listOf(wStandUp, wSitDown, wListen, wBeQuiet, wRaiseHand, wPointTo, wTurnOn, wTurnOff, wCleanBoard, wTakeOut, wPutAway, wTouchDesk),
            exercises = listOf(
                createFlashcard(wStandUp),
                createFlashcard(wSitDown),
                createFlashcard(wBeQuiet),
                createFlashcard(wRaiseHand),
                createFlashcard(wTurnOn),
                createPictureMatch(wStandUp, listOf(wStandUp, wSitDown, wBeQuiet, wListen)),
                createPictureMatch(wRaiseHand, listOf(wRaiseHand, wPointTo, wStandUp, wSitDown)),
                createWordBuilder(wListen),
                createBalloonPop(wBeQuiet, listOf(wBeQuiet, wListen, wStandUp, wSitDown)),
                createAudioQuiz(wStandUp, listOf(wStandUp, wSitDown, wRaiseHand, wListen)),
                createSentenceBuilder("Stand up please", "Lütfen ayağa kalk", listOf("Stand", "up", "please"), wStandUp),
                createSentenceBuilder("Sit down please", "Lütfen otur", listOf("Sit", "down", "please"), wSitDown),
                createSentenceBuilder("Clean the board please", "Lütfen tahtayı sil", listOf("Clean", "the", "board", "please"), wCleanBoard),
                createSentenceBuilder("Turn on the light", "Işığı aç", listOf("Turn", "on", "the", "light"), wTurnOn),
                createSentenceBuilder("Turn off the light", "Işığı kapat", listOf("Turn", "off", "the", "light"), wTurnOff),
                createSentenceBuilder("Raise your hand", "Elini kaldır", listOf("Raise", "your", "hand"), wRaiseHand),
                createSentenceBuilder("Point to the board", "Tahtayı göster", listOf("Point", "to", "the", "board"), wPointTo),
                createSentenceBuilder("Take out your pencil", "Kalemini çıkar", listOf("Take", "out", "your", "pencil"), wTakeOut),
                createSentenceBuilder("Touch your desk", "Sırana dokun", listOf("Touch", "your", "desk"), wTouchDesk)
            )
        )

        // Ders 4: Nezaket Cümleleri & Sınıf Diyalogları (Polite Classroom Dialogues & Q&A)
        val lesson4 = Lesson(
            id = "g2_u3_l4",
            unitId = 3,
            grade = GradeLevel.GRADE_2,
            titleTr = "Sınıf İçi Nezaket & Diyaloglar",
            titleEn = "Classroom Requests & Dialogues",
            description = "Give me the pencil, please. Here you are! What color is it? Is it a book?",
            iconEmoji = "🤝",
            targetWords = listOf(wGiveMe, wHereYouAre, wPencilCase, wRuler, wEraser, wPencil),
            exercises = listOf(
                createFlashcard(wGiveMe),
                createFlashcard(wHereYouAre),
                createPictureMatch(wGiveMe, listOf(wGiveMe, wHereYouAre, wPencilCase, wRuler)),
                createAudioQuiz(wHereYouAre, listOf(wHereYouAre, wGiveMe, wPencilCase, wRuler)),
                createSentenceBuilder("Give me the pencil please", "Bana kalemi ver lütfen", listOf("Give", "me", "the", "pencil", "please"), wGiveMe),
                createSentenceBuilder("Here you are", "Buyrun / İşte burada", listOf("Here", "you", "are"), wHereYouAre),
                createSentenceBuilder("What color is the pencil", "Kalem ne renk", listOf("What", "color", "is", "the", "pencil"), wPencil),
                createSentenceBuilder("It is a pencil case", "Bu bir kalemliktir", listOf("It", "is", "a", "pencil", "case"), wPencilCase),
                createSentenceBuilder("May I sit down", "Oturabilir miyim", listOf("May", "I", "sit", "down"), wSitDown),
                createSentenceBuilder("Can you repeat that please", "Tekrar edebilir misiniz lütfen", listOf("Can", "you", "repeat", "that", "please"), wListen)
            )
        )

        return UnitTopic(
            id = 9,
            grade = GradeLevel.GRADE_2,
            number = 3,
            titleTr = "3. Ünite: In the Classroom (Sınıfta)",
            titleEn = "Unit 3: In the Classroom",
            iconEmoji = "🏫",
            colorHex = 0xFF8B5CF6,
            mebCode = "E2.3",
            lessons = listOf(lesson1, lesson2, lesson3, lesson4)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 4: NUMBERS (MEB E2.4 - Sayılar 1-10 & Yaşımız)
    // ----------------------------------------------------------------------
    private fun createG2Unit4Numbers(): UnitTopic {
        // Aile Bireyleri & Akrabalar (Family Members & Roles)
        val wMother = WordItem("w_g2_mother", GradeLevel.GRADE_2, 4, "Mother / Mum", "Anne", "ma-dır / mam", "👩", "Family", "My mother is beautiful.", "Annem güzeldir.")
        val wFather = WordItem("w_g2_father", GradeLevel.GRADE_2, 4, "Father / Dad", "Baba", "fa-dır / ded", "👨", "Family", "My father is tall and handsome.", "Babam uzun boylu ve yakışıklıdır.")
        val wBrother = WordItem("w_g2_brother", GradeLevel.GRADE_2, 4, "Brother", "Erkek Kardeş / Abi", "bra-dır", "👦", "Family", "Çınar is my brother.", "Çınar benim erkek kardeşimdir.")
        val wSister = WordItem("w_g2_sister", GradeLevel.GRADE_2, 4, "Sister", "Kız Kardeş / Abla", "sis-tır", "👧", "Family", "My sister has got big blue eyes.", "Kız kardeşimin büyük mavi gözleri var.")
        val wGrandmother = WordItem("w_g2_grandmother", GradeLevel.GRADE_2, 4, "Grandmother / Grandma", "Büyükanne / Nine", "grend-ma-dır / grend-maa", "👵", "Family", "My grandma has got a headscarf.", "Büyükannemin başörtüsü var.")
        val wGrandfather = WordItem("w_g2_grandfather", GradeLevel.GRADE_2, 4, "Grandfather / Grandpa", "Büyükbaba / Dede", "grend-fa-dır / grend-paa", "👴", "Family", "My grandpa has got glasses.", "Büyükbabamın gözlükleri var.")
        val wBaby = WordItem("w_g2_baby", GradeLevel.GRADE_2, 4, "Baby / Baby brother", "Bebek / Bebek kardeş", "bey-bi", "👶", "Family", "My baby brother is one year old.", "Bebek kardeşim bir yaşında.")
        val wSon = WordItem("w_g2_son", GradeLevel.GRADE_2, 4, "Son", "Erkek Evlat / Oğul", "san", "👦", "Family", "Kerim is Zehra's son.", "Kerim, Zehra'nın oğludur.")
        val wDaughter = WordItem("w_g2_daughter", GradeLevel.GRADE_2, 4, "Daughter", "Kız Evlat", "doo-tır", "👧", "Family", "Ceyda is Zeynep's daughter.", "Ceyda, Zeynep'in kızıdır.")
        val wFamily = WordItem("w_g2_family", GradeLevel.GRADE_2, 4, "Family / Family tree", "Aile / Aile Ağacı", "fe-mi-li", "👨‍👩‍👧‍👦", "Family", "I have got a big family.", "Geniş bir ailem var.")
        val wFriend = WordItem("w_g2_fam_friend", GradeLevel.GRADE_2, 4, "Friends", "Arkadaşlar", "frendz", "🧑‍🤝‍🧑", "Family", "They are my friends.", "Onlar benim arkadaşlarım.")

        // Fiziksel Özellikler & Nitelemeler (Appearance & Accessories)
        val wHandsome = WordItem("w_g2_handsome", GradeLevel.GRADE_2, 4, "Handsome", "Yakışıklı", "hend-sım", "✨", "Appearance", "My dad is handsome.", "Babam yakışıklıdır.")
        val wBeautiful = WordItem("w_g2_beautiful", GradeLevel.GRADE_2, 4, "Beautiful", "Güzel", "byuu-ti-ful", "🌸", "Appearance", "My mum is beautiful.", "Annem güzeldir.")
        val wBlondeHair = WordItem("w_g2_blondehair", GradeLevel.GRADE_2, 4, "Blonde hair", "Sarı saç", "blond heer", "👱‍♀️", "Appearance", "She has got long blonde hair.", "Onun uzun sarı saçları var.")
        val wHeadscarf = WordItem("w_g2_headscarf", GradeLevel.GRADE_2, 4, "Headscarf", "Başörtüsü", "hed-skaarf", "🧕", "Appearance", "She has got a headscarf.", "Başörtüsü var.")
        val wNice = WordItem("w_g2_nice", GradeLevel.GRADE_2, 4, "Nice", "Hoş / Tatlı / Nazik", "nays", "😊", "Appearance", "My sister is nice.", "Kız kardeşim çok tatlıdır.")
        val wLove = WordItem("w_g2_love", GradeLevel.GRADE_2, 4, "Love", "Sevmek", "lav", "❤️", "Feelings", "I love my family.", "Ailemi seviyorum.")

        // Sayılar & Yaş (Numbers 1-20 & Age)
        val wOne = WordItem("w_g2_one", GradeLevel.GRADE_2, 4, "One", "Bir (1)", "van", "1️⃣", "Numbers", "One year old", "Bir yaşında")
        val wTwo = WordItem("w_g2_two", GradeLevel.GRADE_2, 4, "Two", "İki (2)", "tu", "2️⃣", "Numbers", "Two sisters", "İki kız kardeş")
        val wThree = WordItem("w_g2_three", GradeLevel.GRADE_2, 4, "Three", "Üç (3)", "tri", "3️⃣", "Numbers", "Three balloons", "Üç balon")
        val wFour = WordItem("w_g2_four", GradeLevel.GRADE_2, 4, "Four", "Dört (4)", "for", "4️⃣", "Numbers", "Four people", "Dört kişi")
        val wFive = WordItem("w_g2_five", GradeLevel.GRADE_2, 4, "Five", "Beş (5)", "fayv", "5️⃣", "Numbers", "Five stars", "Beş yıldız")
        val wSix = WordItem("w_g2_six", GradeLevel.GRADE_2, 4, "Six", "Altı (6)", "siks", "6️⃣", "Numbers", "Six books", "Altı kitap")
        val wSeven = WordItem("w_g2_seven", GradeLevel.GRADE_2, 4, "Seven", "Yedi (7)", "se-vın", "7️⃣", "Numbers", "Seven years old", "Yedi yaşında")
        val wEight = WordItem("w_g2_eight", GradeLevel.GRADE_2, 4, "Eight", "Sekiz (8)", "eyt", "8️⃣", "Numbers", "I am eight years old.", "Ben sekiz yaşındayım.")
        val wNine = WordItem("w_g2_nine", GradeLevel.GRADE_2, 4, "Nine", "Dokuz (9)", "nayn", "9️⃣", "Numbers", "Nine cats", "Dokuz kedi")
        val wTen = WordItem("w_g2_ten", GradeLevel.GRADE_2, 4, "Ten", "On (10)", "ten", "🔟", "Numbers", "Ten stars", "On yıldız")
        val wEleven = WordItem("w_g2_eleven", GradeLevel.GRADE_2, 4, "Eleven", "On Bir (11)", "i-le-vın", "1️⃣1️⃣", "Numbers", "Eleven cars", "On bir araba")
        val wTwelve = WordItem("w_g2_twelve", GradeLevel.GRADE_2, 4, "Twelve", "On İki (12)", "tvelv", "1️⃣2️⃣", "Numbers", "Twelve crayons", "On iki boya kalemi")
        val wTwenty = WordItem("w_g2_twenty", GradeLevel.GRADE_2, 4, "Twenty", "Yirmi (20)", "twen-ti", "2️⃣0️⃣", "Numbers", "Twenty children", "Yirmi çocuk")

        // Ders 1: Sevgi Dolu Ailem (My Family Members)
        val lesson1 = Lesson(
            id = "g2_u4_l1",
            unitId = 4,
            grade = GradeLevel.GRADE_2,
            titleTr = "Sevgi Dolu Ailem",
            titleEn = "My Lovely Family Members",
            description = "Mother/Mum, Father/Dad, Brother, Sister, Grandmother, Grandfather, Baby ve Family Tree!",
            iconEmoji = "👨‍👩‍👧‍👦",
            targetWords = listOf(wMother, wFather, wBrother, wSister, wGrandmother, wGrandfather, wBaby, wFamily),
            exercises = listOf(
                createFlashcard(wMother),
                createFlashcard(wFather),
                createFlashcard(wBrother),
                createFlashcard(wSister),
                createFlashcard(wGrandmother),
                createFlashcard(wGrandfather),
                createFlashcard(wBaby),
                createPictureMatch(wMother, listOf(wMother, wFather, wSister, wBrother)),
                createPictureMatch(wGrandfather, listOf(wGrandfather, wGrandmother, wFather, wMother)),
                createPictureMatch(wBaby, listOf(wBaby, wBrother, wSister, wMother)),
                createWordBuilder(wMother),
                createWordBuilder(wFather),
                createWordBuilder(wBrother),
                createWordBuilder(wSister),
                createBalloonPop(wGrandmother, listOf(wGrandmother, wMother, wSister, wFather)),
                createAudioQuiz(wFather, listOf(wFather, wBrother, wGrandfather, wBaby)),
                createAudioQuiz(wGrandmother, listOf(wGrandmother, wGrandfather, wMother, wSister)),
                createSentenceBuilder("He is my father", "O benim babamdır", listOf("He", "is", "my", "father"), wFather),
                createSentenceBuilder("She is my mother", "O benim annemdir", listOf("She", "is", "my", "mother"), wMother),
                createSentenceBuilder("Esma is my grandmother", "Esma benim büyükannemdir", listOf("Esma", "is", "my", "grandmother"), wGrandmother),
                createSentenceBuilder("Kadir is my grandfather", "Kadir benim büyükbabamdır", listOf("Kadir", "is", "my", "grandfather"), wGrandfather),
                createSentenceBuilder("Who is she", "O kimdir (kız)", listOf("Who", "is", "she"), wMother),
                createSentenceBuilder("Who is he", "O kimdir (erkek)", listOf("Who", "is", "he"), wFather)
            )
        )

        // Ders 2: Akraba İlişkileri (Family Roles: Son & Daughter)
        val lesson2 = Lesson(
            id = "g2_u4_l2",
            unitId = 4,
            grade = GradeLevel.GRADE_2,
            titleTr = "Akraba İlişkileri (Son & Daughter)",
            titleEn = "Family Roles & Relationships",
            description = "Son, daughter, brother, sister, friends ve aile ağacı eşleştirmeleri!",
            iconEmoji = "🌳",
            targetWords = listOf(wSon, wDaughter, wBrother, wSister, wFriend, wFamily),
            exercises = listOf(
                createFlashcard(wSon),
                createFlashcard(wDaughter),
                createFlashcard(wFriend),
                createPictureMatch(wSon, listOf(wSon, wDaughter, wMother, wFather)),
                createPictureMatch(wDaughter, listOf(wDaughter, wSon, wSister, wBrother)),
                createWordBuilder(wSon),
                createWordBuilder(wDaughter),
                createWordBuilder(wFamily),
                createBalloonPop(wDaughter, listOf(wDaughter, wSon, wBrother, wSister)),
                createAudioQuiz(wSon, listOf(wSon, wDaughter, wFather, wBrother)),
                createSentenceBuilder("Ahmet is Kerim's father", "Ahmet, Kerim'in babasıdır", listOf("Ahmet", "is", "Kerim's", "father"), wFather),
                createSentenceBuilder("Cinar is Ceyda's brother", "Çınar, Ceyda'nın erkek kardeşidir", listOf("Cinar", "is", "Ceyda's", "brother"), wBrother),
                createSentenceBuilder("Kerim is Zehra's son", "Kerim, Zehra'nın oğludur", listOf("Kerim", "is", "Zehra's", "son"), wSon),
                createSentenceBuilder("Ceyda is Zeynep's daughter", "Ceyda, Zeynep'in kızıdır", listOf("Ceyda", "is", "Zeynep's", "daughter"), wDaughter),
                createSentenceBuilder("They are my friends", "Onlar benim arkadaşlarımdır", listOf("They", "are", "my", "friends"), wFriend),
                createSentenceBuilder("They are my daughters", "Onlar benim kızlarımdır", listOf("They", "are", "my", "daughters"), wDaughter)
            )
        )

        // Ders 3: Arda'nın Ailesi & Fiziksel Nitelemeler (Arda's Story & Descriptions)
        val lesson3 = Lesson(
            id = "g2_u4_l3",
            unitId = 4,
            grade = GradeLevel.GRADE_2,
            titleTr = "Ailemizin Özellikleri & Arda'nın Hikayesi",
            titleEn = "Family Descriptions & Arda's Story",
            description = "Handsome, beautiful, blonde hair, headscarf, glasses, nice ve I love my family!",
            iconEmoji = "✨",
            targetWords = listOf(wHandsome, wBeautiful, wBlondeHair, wHeadscarf, wNice, wLove),
            exercises = listOf(
                createFlashcard(wHandsome),
                createFlashcard(wBeautiful),
                createFlashcard(wBlondeHair),
                createFlashcard(wHeadscarf),
                createFlashcard(wLove),
                createPictureMatch(wHandsome, listOf(wHandsome, wBeautiful, wNice, wHeadscarf)),
                createPictureMatch(wHeadscarf, listOf(wHeadscarf, wBlondeHair, wHandsome, wBeautiful)),
                createWordBuilder(wHandsome),
                createWordBuilder(wBeautiful),
                createWordBuilder(wLove),
                createBalloonPop(wBeautiful, listOf(wBeautiful, wHandsome, wNice, wLove)),
                createAudioQuiz(wHandsome, listOf(wHandsome, wBeautiful, wNice, wHeadscarf)),
                createSentenceBuilder("Is he handsome", "O yakışıklı mıdır", listOf("Is", "he", "handsome"), wHandsome),
                createSentenceBuilder("Yes he is", "Evet, öyledir", listOf("Yes", "he", "is"), wHandsome),
                createSentenceBuilder("She has got blonde hair", "Onun sarı saçları var", listOf("She", "has", "got", "blonde", "hair"), wBlondeHair),
                createSentenceBuilder("My brother is tall and handsome", "Erkek kardeşim uzun ve yakışıklıdır", listOf("My", "brother", "is", "tall", "and", "handsome"), wHandsome),
                createSentenceBuilder("She has got a headscarf", "Onun bir başörtüsü var", listOf("She", "has", "got", "a", "headscarf"), wHeadscarf),
                createSentenceBuilder("He has got blue eyes", "Onun mavi gözleri var", listOf("He", "has", "got", "blue", "eyes"), wFather),
                createSentenceBuilder("My mother has got brown eyes", "Annemin kahverengi gözleri var", listOf("My", "mother", "has", "got", "brown", "eyes"), wMother),
                createSentenceBuilder("I love my family", "Ailemi seviyorum", listOf("I", "love", "my", "family"), wLove)
            )
        )

        // Ders 4: Yaşımız & Sayılar (Numbers 1-20 & Age Q&A)
        val lesson4 = Lesson(
            id = "g2_u4_l4",
            unitId = 4,
            grade = GradeLevel.GRADE_2,
            titleTr = "Yaşımız & Sayılar (1-20)",
            titleEn = "Numbers 1-20 & Age Q&A",
            description = "How old are you? I am eight years old. One, Seven, Eight, Ten, Eleven, Twenty!",
            iconEmoji = "🔢",
            targetWords = listOf(wOne, wTwo, wThree, wFour, wFive, wSix, wSeven, wEight, wNine, wTen, wEleven, wTwelve, wTwenty),
            exercises = listOf(
                createFlashcard(wSeven),
                createFlashcard(wEight),
                createFlashcard(wTen),
                createFlashcard(wEleven),
                createFlashcard(wTwenty),
                createPictureMatch(wEight, listOf(wEight, wSeven, wNine, wTen)),
                createPictureMatch(wEleven, listOf(wEleven, wTwelve, wTwenty, wTen)),
                createWordBuilder(wSeven),
                createWordBuilder(wEight),
                createWordBuilder(wEleven),
                createWordBuilder(wTwelve),
                createBalloonPop(wEight, listOf(wEight, wSeven, wSix, wFive)),
                createAudioQuiz(wEight, listOf(wEight, wSeven, wNine, wTen)),
                createAudioQuiz(wTwenty, listOf(wTwenty, wTwelve, wEleven, wTen)),
                createSentenceBuilder("How old are you", "Kaç yaşındasın", listOf("How", "old", "are", "you"), wEight),
                createSentenceBuilder("I am eight years old", "Sekiz yaşındayım", listOf("I", "am", "eight", "years", "old"), wEight),
                createSentenceBuilder("My baby brother is one year old", "Bebek kardeşim bir yaşında", listOf("My", "baby", "brother", "is", "one", "year", "old"), wBaby),
                createSentenceBuilder("I have got a big family", "Geniş bir ailem var", listOf("I", "have", "got", "a", "big", "family"), wFamily)
            )
        )

        return UnitTopic(
            id = 10,
            grade = GradeLevel.GRADE_2,
            number = 4,
            titleTr = "4. Ünite: Numbers (Sayılar 1-10 & Yaşımız)",
            titleEn = "Unit 4: Numbers",
            iconEmoji = "🔢",
            colorHex = 0xFFF59E0B,
            mebCode = "E2.4",
            lessons = listOf(lesson1, lesson2, lesson3, lesson4)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 5: COLORS (MEB E2.5 - Renkler & Nesneler)
    // ----------------------------------------------------------------------
    private fun createG2Unit5Colors(): UnitTopic {
        // Evin Odaları & Bölümleri (Rooms & Parts of House)
        val wBedroom = WordItem("w_g2_bedroom", GradeLevel.GRADE_2, 5, "Bedroom", "Yatak Odası", "bed-ruum", "🛏️", "House", "The cat is in the bedroom.", "Kedi yatak odasındadır.")
        val wSittingRoom = WordItem("w_g2_sittingroom", GradeLevel.GRADE_2, 5, "Sitting room / Living room", "Oturma Odası / Salon", "si-ting ruum", "🛋️", "House", "My dog is in the sitting room.", "Köpeğim oturma odasındadır.")
        val wKitchen = WordItem("w_g2_kitchen", GradeLevel.GRADE_2, 5, "Kitchen", "Mutfak", "kit-çın", "🍳", "House", "The goldfish is in the kitchen.", "Japon balığı mutfaktadır.")
        val wDiningRoom = WordItem("w_g2_diningroom", GradeLevel.GRADE_2, 5, "Dining room", "Yemek Odası", "day-ning ruum", "🍽️", "House", "The fish is in the dining room.", "Balık yemek odasındadır.")
        val wBathroom = WordItem("w_g2_bathroom", GradeLevel.GRADE_2, 5, "Bathroom", "Banyo", "baat-ruum", "🛁", "House", "My bathroom is very big.", "Banyom çok büyüktür.")
        val wGarden = WordItem("w_g2_garden", GradeLevel.GRADE_2, 5, "Garden", "Bahçe", "gaar-dın", "🏡", "House", "We've got rabbits in the garden.", "Bahçede tavşanlarımız var.")
        val wBalcony = WordItem("w_g2_balcony", GradeLevel.GRADE_2, 5, "Balcony", "Balkon", "bel-kı-ni", "🪴", "House", "The birds are on the balcony.", "Kuşlar balkondadır.")
        val wHouse = WordItem("w_g2_house", GradeLevel.GRADE_2, 5, "House / Home", "Ev", "haws / hovm", "🏠", "House", "We have got a lovely house.", "Çok sevimli bir evimiz var.")

        // Mobilyalar & Ev Eşyaları (Furniture & Objects)
        val wSofa = WordItem("w_g2_sofa", GradeLevel.GRADE_2, 5, "Sofa", "Koltuk / Kanepe", "sov-fa", "🛋️", "Furniture", "The sofa is in the sitting room.", "Koltuk oturma odasındadır.")
        val wBed = WordItem("w_g2_bed", GradeLevel.GRADE_2, 5, "Bed", "Yatak", "bed", "🛏️", "Furniture", "There is a pink bed in the bedroom.", "Yatak odasında pembe bir yatak var.")
        val wCoffeeTable = WordItem("w_g2_coffeetable", GradeLevel.GRADE_2, 5, "Coffee table", "Sehpa / Küçük Masa", "ka-fi tey-bıl", "🪵", "Furniture", "A wooden coffee table.", "Ahşap bir sehpa.")

        // Evcil Hayvanlar & Vücut Özellikleri (Pets & Body Features)
        val wPet = WordItem("w_g2_pet", GradeLevel.GRADE_2, 5, "Pet / Pets", "Evcil Hayvan / Hayvanlar", "pet", "🐾", "Pets", "I love my pets.", "Evcil hayvanlarımı seviyorum.")
        val wDog = WordItem("w_g2_pet_dog", GradeLevel.GRADE_2, 5, "Dog", "Köpek", "dog", "🐶", "Pets", "The dog is in the garden.", "Köpek bahçededir.")
        val wCat = WordItem("w_g2_pet_cat", GradeLevel.GRADE_2, 5, "Cat", "Kedi", "ket", "🐱", "Pets", "I have got a cute cat.", "Sevimli bir kedim var.")
        val wRabbit = WordItem("w_g2_pet_rabbit", GradeLevel.GRADE_2, 5, "Rabbit", "Tavşan", "re-bit", "🐰", "Pets", "The rabbits are white.", "Tavşanlar beyazdır.")
        val wBird = WordItem("w_g2_pet_bird", GradeLevel.GRADE_2, 5, "Bird", "Kuş", "börd", "🐦", "Pets", "There are birds in the garden.", "Bahçede kuşlar var.")
        val wGoldfish = WordItem("w_g2_pet_goldfish", GradeLevel.GRADE_2, 5, "Goldfish / Fish", "Japon Balığı / Balık", "gold-fiş", "🐠", "Pets", "I have got a goldfish.", "Bir japon balığım var.")
        val wTail = WordItem("w_g2_tail", GradeLevel.GRADE_2, 5, "Tail", "Kuyruk", "teyl", "🐕‍🦺", "Pets", "It has got a long tail.", "Onun uzun bir kuyruğu var.")
        val wPaw = WordItem("w_g2_paw", GradeLevel.GRADE_2, 5, "Paw / Paws", "Pati / Patiler", "poo", "🐾", "Pets", "I am cute. I have got paws.", "Çok sevimliyim. Patilerim var.")
        val wWhiskers = WordItem("w_g2_whiskers", GradeLevel.GRADE_2, 5, "Whiskers", "Bıyıklar (Kedi/Hayvan)", "wis-kırz", "🐱", "Pets", "It has got long whiskers.", "Onun uzun bıyıkları var.")
        val wBeak = WordItem("w_g2_beak", GradeLevel.GRADE_2, 5, "Beak", "Gaga", "biik", "🦜", "Pets", "I have got a beak. I am yellow.", "Gagam var. Ben sarıyım.")
        val wClaw = WordItem("w_g2_claw", GradeLevel.GRADE_2, 5, "Claw / Claws", "Pençe / Tırnak", "kloo", "🦅", "Pets", "Sharp claws.", "Keskin pençeler.")

        // Renkler (Colors & Descriptive Words)
        val wRed = WordItem("w_g2_red", GradeLevel.GRADE_2, 5, "Red", "Kırmızı", "red", "🔴", "Colors", "A red sofa", "Kırmızı bir koltuk")
        val wBlue = WordItem("w_g2_blue", GradeLevel.GRADE_2, 5, "Blue", "Mavi", "bluu", "🔵", "Colors", "Blue sky", "Mavi gökyüzü")
        val wYellow = WordItem("w_g2_yellow", GradeLevel.GRADE_2, 5, "Yellow", "Sarı", "ye-lov", "🟡", "Colors", "Yellow bird", "Sarı kuş")
        val wGreen = WordItem("w_g2_green", GradeLevel.GRADE_2, 5, "Green", "Yeşil", "griin", "🟢", "Colors", "Green garden", "Yeşil bahçe")
        val wOrange = WordItem("w_g2_orange", GradeLevel.GRADE_2, 5, "Orange", "Turuncu", "o-rınç", "🟠", "Colors", "I am orange.", "Ben turuncuyum.")
        val wPink = WordItem("w_g2_pink", GradeLevel.GRADE_2, 5, "Pink", "Pembe", "pink", "🌸", "Colors", "A pink bed", "Pembe bir yatak")
        val wPurple = WordItem("w_g2_purple", GradeLevel.GRADE_2, 5, "Purple", "Mor", "pör-pıl", "🟣", "Colors", "Purple flowers", "Mor çiçekler")
        val wBrown = WordItem("w_g2_brown", GradeLevel.GRADE_2, 5, "Brown", "Kahverengi", "brawn", "🟤", "Colors", "Brown puppy", "Kahverengi yavru köpek")
        val wBlack = WordItem("w_g2_black", GradeLevel.GRADE_2, 5, "Black", "Siyah", "blek", "⚫", "Colors", "Black and yellow", "Siyah ve sarı")
        val wWhite = WordItem("w_g2_white", GradeLevel.GRADE_2, 5, "White", "Beyaz", "vayt", "⚪", "Colors", "The rabbits are white.", "Tavşanlar beyazdır.")

        // Ders 1: Evimizin Odaları & Bölümleri (Rooms of the House)
        val lesson1 = Lesson(
            id = "g2_u5_l1",
            unitId = 5,
            grade = GradeLevel.GRADE_2,
            titleTr = "Evimizin Odaları (Rooms of the House)",
            titleEn = "Rooms & Parts of the House",
            description = "Bedroom, sitting room, kitchen, dining room, bathroom, balcony, garden ve house!",
            iconEmoji = "🏡",
            targetWords = listOf(wBedroom, wSittingRoom, wKitchen, wDiningRoom, wBathroom, wGarden, wBalcony, wHouse),
            exercises = listOf(
                createFlashcard(wBedroom),
                createFlashcard(wSittingRoom),
                createFlashcard(wKitchen),
                createFlashcard(wDiningRoom),
                createFlashcard(wBathroom),
                createFlashcard(wGarden),
                createFlashcard(wBalcony),
                createPictureMatch(wBedroom, listOf(wBedroom, wKitchen, wBathroom, wGarden)),
                createPictureMatch(wKitchen, listOf(wKitchen, wSittingRoom, wDiningRoom, wBalcony)),
                createPictureMatch(wGarden, listOf(wGarden, wBalcony, wBathroom, wBedroom)),
                createWordBuilder(wKitchen),
                createWordBuilder(wGarden),
                createWordBuilder(wBedroom),
                createWordBuilder(wBalcony),
                createBalloonPop(wBathroom, listOf(wBathroom, wKitchen, wBedroom, wDiningRoom)),
                createAudioQuiz(wSittingRoom, listOf(wSittingRoom, wDiningRoom, wKitchen, wBathroom)),
                createAudioQuiz(wDiningRoom, listOf(wDiningRoom, wSittingRoom, wGarden, wBalcony)),
                createSentenceBuilder("Where is the dog", "Köpek nerededir", listOf("Where", "is", "the", "dog"), wDog),
                createSentenceBuilder("The dog is in the garden", "Köpek bahçededir", listOf("The", "dog", "is", "in", "the", "garden"), wGarden),
                createSentenceBuilder("Where are the birds", "Kuşlar nerededir", listOf("Where", "are", "the", "birds"), wBird),
                createSentenceBuilder("They are on the balcony", "Onlar balkondadır", listOf("They", "are", "on", "the", "balcony"), wBalcony),
                createSentenceBuilder("The cat is in the bedroom", "Kedi yatak odasındadır", listOf("The", "cat", "is", "in", "the", "bedroom"), wBedroom),
                createSentenceBuilder("The fish is in the dining room", "Balık yemek odasındadır", listOf("The", "fish", "is", "in", "the", "dining", "room"), wDiningRoom)
            )
        )

        // Ders 2: Mobilyalar & Renkler (Furniture & Colors)
        val lesson2 = Lesson(
            id = "g2_u5_l2",
            unitId = 5,
            grade = GradeLevel.GRADE_2,
            titleTr = "Mobilyalar & Renkler (Furniture & Colors)",
            titleEn = "Furniture & Colorful Rooms",
            description = "Sofa, bed, coffee table, pink, red, blue, green, yellow, orange, white!",
            iconEmoji = "🛋️",
            targetWords = listOf(wSofa, wBed, wCoffeeTable, wPink, wRed, wBlue, wYellow, wGreen, wOrange, wWhite),
            exercises = listOf(
                createFlashcard(wSofa),
                createFlashcard(wBed),
                createFlashcard(wCoffeeTable),
                createFlashcard(wPink),
                createPictureMatch(wSofa, listOf(wSofa, wBed, wCoffeeTable, wSittingRoom)),
                createPictureMatch(wBed, listOf(wBed, wSofa, wBedroom, wDiningRoom)),
                createWordBuilder(wSofa),
                createWordBuilder(wBed),
                createWordBuilder(wPink),
                createBalloonPop(wCoffeeTable, listOf(wCoffeeTable, wSofa, wBed, wKitchen)),
                createAudioQuiz(wSofa, listOf(wSofa, wBed, wCoffeeTable, wHouse)),
                createSentenceBuilder("Where is the sofa", "Koltuk nerededir", listOf("Where", "is", "the", "sofa"), wSofa),
                createSentenceBuilder("The sofa is in the sitting room", "Koltuk oturma odasındadır", listOf("The", "sofa", "is", "in", "the", "sitting", "room"), wSofa),
                createSentenceBuilder("There is a pink bed in the bedroom", "Yatak odasında pembe bir yatak var", listOf("There", "is", "a", "pink", "bed", "in", "the", "bedroom"), wBed),
                createSentenceBuilder("We have got a lovely house", "Sevimli bir evimiz var", listOf("We", "have", "got", "a", "lovely", "house"), wHouse),
                createSentenceBuilder("What color is it", "Bu ne renktir", listOf("What", "color", "is", "it"), wPink),
                createSentenceBuilder("It is pink", "O pembedir", listOf("It", "is", "pink"), wPink)
            )
        )

        // Ders 3: Sevimli Evcil Hayvanlarımız (Our Lovely Pets)
        val lesson3 = Lesson(
            id = "g2_u5_l3",
            unitId = 5,
            grade = GradeLevel.GRADE_2,
            titleTr = "Sevimli Evcil Hayvanlarımız & Özellikleri",
            titleEn = "Our Pets & Features",
            description = "Dog, cat, rabbit, bird, goldfish, tail, paw, whiskers, beak, claw ve pet sevgisi!",
            iconEmoji = "🐾",
            targetWords = listOf(wPet, wDog, wCat, wRabbit, wBird, wGoldfish, wTail, wPaw, wWhiskers, wBeak, wClaw),
            exercises = listOf(
                createFlashcard(wPet),
                createFlashcard(wGoldfish),
                createFlashcard(wTail),
                createFlashcard(wPaw),
                createFlashcard(wWhiskers),
                createFlashcard(wBeak),
                createFlashcard(wClaw),
                createPictureMatch(wGoldfish, listOf(wGoldfish, wBird, wCat, wDog)),
                createPictureMatch(wBeak, listOf(wBeak, wPaw, wTail, wWhiskers)),
                createPictureMatch(wWhiskers, listOf(wWhiskers, wTail, wPaw, wClaw)),
                createWordBuilder(wTail),
                createWordBuilder(wBeak),
                createWordBuilder(wGoldfish),
                createBalloonPop(wPaw, listOf(wPaw, wTail, wBeak, wWhiskers)),
                createAudioQuiz(wWhiskers, listOf(wWhiskers, wBeak, wTail, wPaw)),
                createAudioQuiz(wGoldfish, listOf(wGoldfish, wRabbit, wBird, wDog)),
                createSentenceBuilder("I have got a cute cat", "Sevimli bir kedim var", listOf("I", "have", "got", "a", "cute", "cat"), wCat),
                createSentenceBuilder("It has got long whiskers", "Onun uzun bıyıkları var", listOf("It", "has", "got", "long", "whiskers"), wWhiskers),
                createSentenceBuilder("The dog has got a long tail", "Köpeğin uzun bir kuyruğu var", listOf("The", "dog", "has", "got", "a", "long", "tail"), wTail),
                createSentenceBuilder("Have you got a pet", "Evcil hayvanın var mı", listOf("Have", "you", "got", "a", "pet"), wPet),
                createSentenceBuilder("I love my pets and house", "Evcil hayvanlarımı ve evimi seviyorum", listOf("I", "love", "my", "pets", "and", "house"), wPet),
                createSentenceBuilder("I am small I have got a beak", "Ben küçüğüm bir gagam var", listOf("I", "am", "small", "I", "have", "got", "a", "beak"), wBeak)
            )
        )

        // Ders 4: Evde Kim Var? & Sayma (There is / There are & Counting Pets)
        val lesson4 = Lesson(
            id = "g2_u5_l4",
            unitId = 5,
            grade = GradeLevel.GRADE_2,
            titleTr = "Evde Kim Var? (There is / There are)",
            titleEn = "There is / There are in the House",
            description = "There is a cat in the bedroom, There are two birds in the garden, True / False alıştırmaları!",
            iconEmoji = "🔢",
            targetWords = listOf(wHouse, wBedroom, wSittingRoom, wKitchen, wGarden, wDog, wCat, wRabbit, wBird, wGoldfish),
            exercises = listOf(
                createFlashcard(wHouse),
                createFlashcard(wGarden),
                createPictureMatch(wDog, listOf(wDog, wCat, wRabbit, wBird)),
                createPictureMatch(wRabbit, listOf(wRabbit, wDog, wCat, wGoldfish)),
                createWordBuilder(wRabbit),
                createWordBuilder(wHouse),
                createBalloonPop(wGoldfish, listOf(wGoldfish, wBird, wCat, wRabbit)),
                createAudioQuiz(wRabbit, listOf(wRabbit, wCat, wDog, wBird)),
                createSentenceBuilder("There is a cat in the bedroom", "Yatak odasında bir kedi var", listOf("There", "is", "a", "cat", "in", "the", "bedroom"), wBedroom),
                createSentenceBuilder("There is a fish in the sitting room", "Oturma odasında bir balık var", listOf("There", "is", "a", "fish", "in", "the", "sitting", "room"), wSittingRoom),
                createSentenceBuilder("There are two birds in the garden", "Bahçede iki kuş var", listOf("There", "are", "two", "birds", "in", "the", "garden"), wGarden),
                createSentenceBuilder("There are four rooms", "Dört oda vardır", listOf("There", "are", "four", "rooms"), wHouse),
                createSentenceBuilder("There are four cats", "Dört kedi vardır", listOf("There", "are", "four", "cats"), wCat),
                createSentenceBuilder("The rabbits are white", "Tavşanlar beyazdır", listOf("The", "rabbits", "are", "white"), wRabbit)
            )
        )

        return UnitTopic(
            id = 11,
            grade = GradeLevel.GRADE_2,
            number = 5,
            titleTr = "5. Ünite: Colors (Renkler)",
            titleEn = "Unit 5: Colors",
            iconEmoji = "🎨",
            colorHex = 0xFF10B981,
            mebCode = "E2.5",
            lessons = listOf(lesson1, lesson2, lesson3, lesson4)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 6: AT THE PLAYGROUND (MEB E2.6 - Oyun Parkında & Eylemler)
    // ----------------------------------------------------------------------
    private fun createG2Unit6AtThePlayground(): UnitTopic {
        // Kahvaltılıklar & Yiyecekler (Breakfast & Foods)
        val wBread = WordItem("w_g2_bread", GradeLevel.GRADE_2, 6, "Bread", "Ekmek", "bred", "🍞", "Food", "Eat fresh bread.", "Taze ekmek ye.")
        val wCheese = WordItem("w_g2_cheese", GradeLevel.GRADE_2, 6, "Cheese", "Peynir", "çiiz", "🧀", "Food", "I like cheese for breakfast.", "Kahvaltıda peyniri severim.")
        val wOlive = WordItem("w_g2_olive", GradeLevel.GRADE_2, 6, "Olive", "Zeytin", "aliv", "🫒", "Food", "Black and green olives.", "Siyah ve yeşil zeytinler.")
        val wEgg = WordItem("w_g2_egg", GradeLevel.GRADE_2, 6, "Egg", "Yumurta", "eg", "🥚", "Food", "I eat an egg every morning.", "Her sabah bir yumurta yerim.")
        val wButter = WordItem("w_g2_butter", GradeLevel.GRADE_2, 6, "Butter", "Tereyağı", "batır", "🧈", "Food", "Butter on bread.", "Ekmeğin üstünde tereyağı.")
        val wHoney = WordItem("w_g2_honey", GradeLevel.GRADE_2, 6, "Honey", "Bal", "hani", "🍯", "Food", "Sweet and healthy honey.", "Tatlı ve sağlıklı bal.")
        val wJam = WordItem("w_g2_jam", GradeLevel.GRADE_2, 6, "Jam", "Reçel", "cem", "🍓", "Food", "Strawberry jam is yummy.", "Çilek reçeli nefistir.")
        
        // Yemekler & İçecekler (Meals, Dishes & Drinks)
        val wSoup = WordItem("w_g2_soup", GradeLevel.GRADE_2, 6, "Soup", "Çorba", "suup", "🥣", "Food", "Hot delicious soup.", "Sıcak lezzetli çorba.")
        val wSalad = WordItem("w_g2_salad", GradeLevel.GRADE_2, 6, "Salad", "Salata", "salıd", "🥗", "Food", "Fresh green salad.", "Taze yeşil salata.")
        val wPasta = WordItem("w_g2_pasta", GradeLevel.GRADE_2, 6, "Pasta", "Makarna", "pasta", "🍝", "Food", "I like pasta for dinner.", "Akşam yemeğinde makarnayı severim.")
        val wPizza = WordItem("w_g2_pizza", GradeLevel.GRADE_2, 6, "Pizza", "Pizza", "pitsa", "🍕", "Food", "Delicious cheese pizza.", "Lezzetli peynirli pizza.")
        val wSandwich = WordItem("w_g2_sandwich", GradeLevel.GRADE_2, 6, "Sandwich", "Sandviç", "sendviç", "🥪", "Food", "A yummy sandwich in my lunchbox.", "Beslenme çantamda nefis bir sandviç.")
        val wCake = WordItem("w_g2_cake", GradeLevel.GRADE_2, 6, "Cake", "Kek / Pasta", "keyk", "🍰", "Food", "Do you want some cake?", "Biraz kek ister misin?")
        val wIceCream = WordItem("w_g2_icecream", GradeLevel.GRADE_2, 6, "Ice cream", "Dondurma", "ays kriim", "🍦", "Food", "Cold sweet ice cream.", "Soğuk tatlı dondurma.")
        
        val wMilk = WordItem("w_g2_milk", GradeLevel.GRADE_2, 6, "Milk", "Süt", "milk", "🥛", "Drinks", "Drink milk for breakfast.", "Kahvaltıda süt iç.")
        val wTea = WordItem("w_g2_tea", GradeLevel.GRADE_2, 6, "Tea", "Çay", "tii", "🍵", "Drinks", "A warm cup of tea.", "Ilık bir fincan çay.")
        val wWater = WordItem("w_g2_water", GradeLevel.GRADE_2, 6, "Water", "Su", "wotır", "💧", "Drinks", "Drink water every day.", "Her gün su iç.")
        val wJuice = WordItem("w_g2_juice", GradeLevel.GRADE_2, 6, "Juice", "Meyve Suyu", "cuus", "🧃", "Drinks", "Fresh orange juice.", "Taze portakal suyu.")

        // Meyveler & Sağlık (Fruits & Healthy Nutrition)
        val wApple = WordItem("w_g2_apple", GradeLevel.GRADE_2, 6, "Apple", "Elma", "epıl", "🍎", "Fruit", "Can I have an apple please?", "Bir elma alabilir miyim lütfen?")
        val wBanana = WordItem("w_g2_banana", GradeLevel.GRADE_2, 6, "Banana", "Muz", "bınena", "🍌", "Fruit", "Three yellow bananas.", "Üç sarı muz.")
        val wOrange = WordItem("w_g2_orange_fruit", GradeLevel.GRADE_2, 6, "Orange", "Portakal", "orınc", "🍊", "Fruit", "Sweet orange slices.", "Tatlı portakal dilimleri.")
        val wStrawberry = WordItem("w_g2_strawberry", GradeLevel.GRADE_2, 6, "Strawberry", "Çilek", "strovberi", "🍓", "Fruit", "Red strawberries.", "Kırmızı çilekler.")
        
        val wBreakfast = WordItem("w_g2_breakfast", GradeLevel.GRADE_2, 6, "Breakfast", "Kahvaltı", "brekfıst", "🍳", "Meals", "Have a healthy breakfast.", "Sağlıklı bir kahvaltı yap.")
        val wLunch = WordItem("w_g2_lunch", GradeLevel.GRADE_2, 6, "Lunch", "Öğle Yemeği", "lanç", "🍱", "Meals", "I eat lunch at school.", "Okulda öğle yemeği yerim.")
        val wDinner = WordItem("w_g2_dinner", GradeLevel.GRADE_2, 6, "Dinner", "Akşam Yemeği", "dinır", "🍲", "Meals", "Dinner with my family.", "Ailemle akşam yemeği.")
        val wLunchbox = WordItem("w_g2_lunchbox", GradeLevel.GRADE_2, 6, "Lunchbox", "Beslenme Çantası", "lançboks", "🎒", "Food", "Two sandwiches in my lunchbox.", "Beslenme çantamda iki sandviç.")
        val wHealthy = WordItem("w_g2_healthy", GradeLevel.GRADE_2, 6, "Healthy", "Sağlıklı", "helti", "🥗", "Health", "Eat healthy food every day.", "Her gün sağlıklı yiyecekler ye.")

        // Ders 1: Kahvaltı & İçecekler (Breakfast & Morning Drinks)
        val lesson1 = Lesson(
            id = "g2_u6_l1",
            unitId = 6,
            grade = GradeLevel.GRADE_2,
            titleTr = "Kahvaltı & İçecekler (Breakfast & Drinks)",
            titleEn = "Breakfast & Morning Foods",
            description = "Bread, cheese, olive, egg, butter, honey, jam, milk, tea ve kahvaltı kalıpları!",
            iconEmoji = "🍳",
            targetWords = listOf(wBreakfast, wBread, wCheese, wOlive, wEgg, wButter, wHoney, wJam, wMilk, wTea),
            exercises = listOf(
                createFlashcard(wCheese),
                createFlashcard(wOlive),
                createFlashcard(wEgg),
                createFlashcard(wBread),
                createFlashcard(wHoney),
                createFlashcard(wJam),
                createFlashcard(wMilk),
                createPictureMatch(wCheese, listOf(wCheese, wOlive, wEgg, wBread)),
                createPictureMatch(wOlive, listOf(wOlive, wCheese, wButter, wJam)),
                createPictureMatch(wEgg, listOf(wEgg, wBread, wMilk, wTea)),
                createWordBuilder(wMilk),
                createWordBuilder(wEgg),
                createWordBuilder(wBread),
                createWordBuilder(wCheese),
                createBalloonPop(wHoney, listOf(wHoney, wJam, wButter, wOlive)),
                createBalloonPop(wTea, listOf(wTea, wMilk, wWater, wJuice)),
                createAudioQuiz(wCheese, listOf(wCheese, wEgg, wBread, wOlive)),
                createAudioQuiz(wOlive, listOf(wOlive, wButter, wJam, wHoney)),
                createAudioQuiz(wMilk, listOf(wMilk, wTea, wWater, wJuice)),
                createSentenceBuilder("I like cheese and olives", "Peynir ve zeytini severim", listOf("I", "like", "cheese", "and", "olives"), wCheese),
                createSentenceBuilder("I eat eggs for breakfast", "Kahvaltıda yumurta yerim", listOf("I", "eat", "eggs", "for", "breakfast"), wEgg),
                createSentenceBuilder("I drink milk every day", "Her gün süt içerim", listOf("I", "drink", "milk", "every", "day"), wMilk),
                createSentenceBuilder("Do you like honey", "Bal sever misin", listOf("Do", "you", "like", "honey"), wHoney),
                createSentenceBuilder("Yes I do", "Evet severim", listOf("Yes", "I", "do"), wHoney),
                createSentenceBuilder("No I don't", "Hayır sevmem", listOf("No", "I", "don't"), wHoney),
                createSentenceBuilder("Bread and butter are yummy", "Ekmek ve tereyağı nefistir", listOf("Bread", "and", "butter", "are", "yummy"), wButter)
            )
        )

        // Ders 2: Öğle, Akşam Yemekleri & Meyveler (Lunch, Dinner, Fruits & Healthy Food)
        val lesson2 = Lesson(
            id = "g2_u6_l2",
            unitId = 6,
            grade = GradeLevel.GRADE_2,
            titleTr = "Öğle & Akşam Yemekleri ve Meyveler",
            titleEn = "Meals, Dishes & Fresh Fruits",
            description = "Soup, salad, pasta, pizza, sandwich, apple, banana, orange, strawberry & healthy plate!",
            iconEmoji = "🥗",
            targetWords = listOf(wLunch, wDinner, wSoup, wSalad, wPasta, wSandwich, wPizza, wApple, wBanana, wOrange, wStrawberry, wHealthy),
            exercises = listOf(
                createFlashcard(wSoup),
                createFlashcard(wSalad),
                createFlashcard(wPasta),
                createFlashcard(wSandwich),
                createFlashcard(wApple),
                createFlashcard(wBanana),
                createFlashcard(wStrawberry),
                createPictureMatch(wSoup, listOf(wSoup, wSalad, wPasta, wPizza)),
                createPictureMatch(wSalad, listOf(wSalad, wSoup, wSandwich, wPasta)),
                createPictureMatch(wApple, listOf(wApple, wBanana, wOrange, wStrawberry)),
                createPictureMatch(wStrawberry, listOf(wStrawberry, wApple, wOrange, wBanana)),
                createWordBuilder(wSoup),
                createWordBuilder(wSalad),
                createWordBuilder(wApple),
                createWordBuilder(wBanana),
                createBalloonPop(wPasta, listOf(wPasta, wSoup, wSalad, wPizza)),
                createBalloonPop(wOrange, listOf(wOrange, wApple, wBanana, wStrawberry)),
                createAudioQuiz(wSalad, listOf(wSalad, wSoup, wPasta, wSandwich)),
                createAudioQuiz(wStrawberry, listOf(wStrawberry, wApple, wOrange, wBanana)),
                createSentenceBuilder("I like pasta and salad", "Makarna ve salatayı severim", listOf("I", "like", "pasta", "and", "salad"), wPasta),
                createSentenceBuilder("Eat healthy food", "Sağlıklı yiyecekler ye", listOf("Eat", "healthy", "food"), wHealthy),
                createSentenceBuilder("Do you like apples", "Elma sever misin", listOf("Do", "you", "like", "apples"), wApple),
                createSentenceBuilder("I like red strawberries", "Kırmızı çilekleri severim", listOf("I", "like", "red", "strawberries"), wStrawberry),
                createSentenceBuilder("Hot soup is delicious", "Sıcak çorba lezzetlidir", listOf("Hot", "soup", "is", "delicious"), wSoup),
                createSentenceBuilder("I eat a sandwich for lunch", "Öğle yemeğinde bir sandviç yerim", listOf("I", "eat", "a", "sandwich", "for", "lunch"), wSandwich)
            )
        )

        // Ders 3: Markette & Kafede Nezaketle İsteme (Polite Requests & Ordering at Market/Cafe)
        val lesson3 = Lesson(
            id = "g2_u6_l3",
            unitId = 6,
            grade = GradeLevel.GRADE_2,
            titleTr = "Kafede & Markette Sipariş (Can I have...?)",
            titleEn = "Ordering & Polite Requests",
            description = "Can I have an apple please? Here you are! Do you want cake? Yes please / No thank you!",
            iconEmoji = "🛍️",
            targetWords = listOf(wWater, wJuice, wCake, wIceCream, wPizza, wSandwich, wApple),
            exercises = listOf(
                createFlashcard(wCake),
                createFlashcard(wIceCream),
                createFlashcard(wWater),
                createFlashcard(wJuice),
                createPictureMatch(wCake, listOf(wCake, wIceCream, wPizza, wSandwich)),
                createPictureMatch(wIceCream, listOf(wIceCream, wCake, wJuice, wWater)),
                createPictureMatch(wWater, listOf(wWater, wJuice, wMilk, wTea)),
                createWordBuilder(wWater),
                createWordBuilder(wCake),
                createWordBuilder(wJuice),
                createBalloonPop(wIceCream, listOf(wIceCream, wCake, wWater, wJuice)),
                createAudioQuiz(wWater, listOf(wWater, wJuice, wMilk, wTea)),
                createAudioQuiz(wCake, listOf(wCake, wIceCream, wSandwich, wPizza)),
                createSentenceBuilder("Can I have an apple please", "Bir elma alabilir miyim lütfen", listOf("Can", "I", "have", "an", "apple", "please"), wApple),
                createSentenceBuilder("Here you are", "Buyrun işte burada", listOf("Here", "you", "are"), wApple),
                createSentenceBuilder("Thank you very much", "Çok teşekkür ederim", listOf("Thank", "you", "very", "much"), wApple),
                createSentenceBuilder("Can I have some water please", "Biraz su alabilir miyim lütfen", listOf("Can", "I", "have", "some", "water", "please"), wWater),
                createSentenceBuilder("Do you want some cake", "Biraz kek ister misin", listOf("Do", "you", "want", "some", "cake"), wCake),
                createSentenceBuilder("Yes please", "Evet lütfen", listOf("Yes", "please"), wCake),
                createSentenceBuilder("No thank you", "Hayır teşekkür ederim", listOf("No", "thank", "you"), wCake),
                createSentenceBuilder("Can I help you", "Yardımcı olabilir miyim", listOf("Can", "I", "help", "you"), wWater)
            )
        )

        // Ders 4: Beslenme Çantam & Sayma (Lunchbox, There is / There are & Counting)
        val lesson4 = Lesson(
            id = "g2_u6_l4",
            unitId = 6,
            grade = GradeLevel.GRADE_2,
            titleTr = "Beslenme Çantam & Sayma (Lunchbox & Counting)",
            titleEn = "Lunchbox Quantities & Counting",
            description = "There are two sandwiches in my lunchbox, How many oranges are there? Is there any milk?",
            iconEmoji = "🎒",
            targetWords = listOf(wLunchbox, wSandwich, wEgg, wApple, wBanana, wOrange, wWater, wMilk, wHealthy),
            exercises = listOf(
                createFlashcard(wLunchbox),
                createFlashcard(wSandwich),
                createFlashcard(wBanana),
                createPictureMatch(wLunchbox, listOf(wLunchbox, wSandwich, wApple, wEgg)),
                createPictureMatch(wBanana, listOf(wBanana, wOrange, wApple, wStrawberry)),
                createWordBuilder(wSandwich),
                createWordBuilder(wLunchbox),
                createBalloonPop(wSandwich, listOf(wSandwich, wLunchbox, wApple, wEgg)),
                createAudioQuiz(wLunchbox, listOf(wLunchbox, wSandwich, wBanana, wApple)),
                createSentenceBuilder("There are two sandwiches in my lunchbox", "Beslenme çantamda iki sandviç var", listOf("There", "are", "two", "sandwiches", "in", "my", "lunchbox"), wLunchbox),
                createSentenceBuilder("There are three bananas", "Üç muz var", listOf("There", "are", "three", "bananas"), wBanana),
                createSentenceBuilder("How many oranges are there", "Kaç tane portakal var", listOf("How", "many", "oranges", "are", "there"), wOrange),
                createSentenceBuilder("There are four oranges", "Dört tane portakal var", listOf("There", "are", "four", "oranges"), wOrange),
                createSentenceBuilder("Is there any milk", "Hiç süt var mı", listOf("Is", "there", "any", "milk"), wMilk),
                createSentenceBuilder("Yes there is", "Evet var", listOf("Yes", "there", "is"), wMilk),
                createSentenceBuilder("I love my healthy lunchbox", "Sağlıklı beslenme çantamı seviyorum", listOf("I", "love", "my", "healthy", "lunchbox"), wHealthy)
            )
        )

        return UnitTopic(
            id = 12,
            grade = GradeLevel.GRADE_2,
            number = 6,
            titleTr = "6. Ünite: At the Playground (Oyun Parkında)",
            titleEn = "Unit 6: At the Playground",
            iconEmoji = "🛝",
            colorHex = 0xFFF59E0B,
            mebCode = "E2.6",
            lessons = listOf(lesson1, lesson2, lesson3, lesson4)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 7: BODY PARTS (MEB E2.7 - Vücudumuzun Bölümleri)
    // ----------------------------------------------------------------------
    private fun createG2Unit7BodyParts(): UnitTopic {
        // Vücudun Bölümleri (Body Parts)
        val wHead = WordItem("w_g2_head", GradeLevel.GRADE_2, 7, "Head", "Baş / Kafa", "hed", "🗣️", "Body", "Touch your head!", "Başına dokun!")
        val wBody = WordItem("w_g2_body", GradeLevel.GRADE_2, 7, "Body", "Vücut / Gövde", "ba-di", "🧍", "Body", "Spots on my body.", "Vücudumdaki benekler.")
        val wEye = WordItem("w_g2_eye", GradeLevel.GRADE_2, 7, "Eye", "Göz", "ay", "👁️", "Body", "Big brown eyes.", "Büyük kahverengi gözler.")
        val wEar = WordItem("w_g2_ear", GradeLevel.GRADE_2, 7, "Ear", "Kulak", "i-ır", "👂", "Body", "This is an ear.", "Bu bir kulaktır.")
        val wNose = WordItem("w_g2_nose", GradeLevel.GRADE_2, 7, "Nose", "Burun", "novz", "👃", "Body", "Touch your nose.", "Burnuna dokun.")
        val wMouth = WordItem("w_g2_mouth", GradeLevel.GRADE_2, 7, "Mouth", "Ağız", "mavt", "👄", "Body", "Open your mouth.", "Ağzını aç.")
        val wHair = WordItem("w_g2_hair", GradeLevel.GRADE_2, 7, "Hair", "Saç", "heer", "💇", "Body", "Short brown hair.", "Kısa kahverengi saç.")
        val wFace = WordItem("w_g2_face", GradeLevel.GRADE_2, 7, "Face", "Yüz", "feys", "🧒", "Body", "A smiling face.", "Gülen bir yüz.")
        val wHand = WordItem("w_g2_hand", GradeLevel.GRADE_2, 7, "Hand", "El", "hend", "✋", "Body", "Clap your hands!", "Ellerini çırp!")
        val wArm = WordItem("w_g2_arm", GradeLevel.GRADE_2, 7, "Arm", "Kol", "aarm", "💪", "Body", "Two strong arms.", "İki güçlü kol.")
        val wLeg = WordItem("w_g2_leg", GradeLevel.GRADE_2, 7, "Leg", "Bacak", "leg", "🦵", "Body", "Four long legs.", "Dört uzun bacak.")
        val wFoot = WordItem("w_g2_foot", GradeLevel.GRADE_2, 7, "Foot / Feet", "Ayak / Ayaklar", "fut / fiit", "🦶", "Body", "Stomp your feet!", "Ayaklarını yere vur!")

        // Kıyafetler & Aksesuarlar (Clothes & Accessories)
        val wGlasses = WordItem("w_g2_glasses", GradeLevel.GRADE_2, 7, "Glasses", "Gözlük", "glaa-siz", "👓", "Clothes", "I have got blue glasses.", "Mavi gözlüğüm var.")
        val wGloves = WordItem("w_g2_gloves", GradeLevel.GRADE_2, 7, "Gloves", "Eldiven", "glavz", "🧤", "Clothes", "Warm winter gloves.", "Sıcak kış eldivenleri.")
        val wCoat = WordItem("w_g2_coat", GradeLevel.GRADE_2, 7, "Coat", "Kaban / Mont", "kovt", "🧥", "Clothes", "Wear your warm coat.", "Sıcak montunu giy.")
        val wShirt = WordItem("w_g2_shirt", GradeLevel.GRADE_2, 7, "Shirt", "Gömlek", "şöört", "👔", "Clothes", "A clean white shirt.", "Temiz beyaz bir gömlek.")
        val wScarf = WordItem("w_g2_scarf", GradeLevel.GRADE_2, 7, "Scarf", "Atkı", "skaarf", "🧣", "Clothes", "A red scarf.", "Kırmızı bir atkı.")
        val wShoes = WordItem("w_g2_shoes", GradeLevel.GRADE_2, 7, "Shoes", "Ayakkabılar", "şuuz", "👟", "Clothes", "Blue sports shoes.", "Mavi spor ayakkabılar.")
        val wTrousers = WordItem("w_g2_trousers", GradeLevel.GRADE_2, 7, "Trousers", "Pantolon", "traw-zırz", "👖", "Clothes", "Brown trousers.", "Kahverengi pantolon.")
        val wTShirt = WordItem("w_g2_tshirt", GradeLevel.GRADE_2, 7, "T-shirt", "Tişört", "tii-şöört", "👕", "Clothes", "A green T-shirt.", "Yeşil bir tişört.")
        val wUmbrella = WordItem("w_g2_umbrella", GradeLevel.GRADE_2, 7, "Umbrella", "Şemsiye", "am-bre-la", "☂️", "Clothes", "Open the purple umbrella.", "Mor şemsiyeyi aç.")
        val wDress = WordItem("w_g2_dress", GradeLevel.GRADE_2, 7, "Dress", "Elbise", "dres", "👗", "Clothes", "A yellow dress.", "Sarı bir elbise.")
        val wHat = WordItem("w_g2_hat", GradeLevel.GRADE_2, 7, "Hat", "Şapka", "het", "👒", "Clothes", "A green hat.", "Yeşil bir şapka.")

        // Fiziksel Nitelemeler & Sıfatlar (Physical Appearance)
        val wTall = WordItem("w_g2_tall", GradeLevel.GRADE_2, 7, "Tall", "Uzun boylu", "tool", "🦒", "Appearance", "Ali is tall.", "Ali uzun boyludur.")
        val wShort = WordItem("w_g2_short", GradeLevel.GRADE_2, 7, "Short", "Kısa boylu / Kısa", "şoort", "🧒", "Appearance", "Short brown hair.", "Kısa kahverengi saç.")
        val wBig = WordItem("w_g2_big", GradeLevel.GRADE_2, 7, "Big", "Büyük", "big", "🐘", "Appearance", "Big blue eyes.", "Büyük mavi gözler.")
        val wSmall = WordItem("w_g2_small", GradeLevel.GRADE_2, 7, "Small", "Küçük", "smool", "🐭", "Appearance", "Two small ears.", "İki küçük kulak.")
        val wGiraffe = WordItem("w_g2_giraffe", GradeLevel.GRADE_2, 7, "Giraffe", "Zürafa", "ci-raaf", "🦒", "Appearance", "The giraffe is very tall.", "Zürafa çok uzundur.")

        // Hava Durumu & Günler (Weather & Days)
        val wCold = WordItem("w_g2_cold", GradeLevel.GRADE_2, 7, "Cold", "Soğuk", "kovld", "❄️", "Weather", "It is cold today.", "Bugün hava soğuk.")
        val wHot = WordItem("w_g2_hot", GradeLevel.GRADE_2, 7, "Hot", "Sıcak", "hat", "🔥", "Weather", "It is very hot.", "Hava çok sıcak.")
        val wSunny = WordItem("w_g2_sunny", GradeLevel.GRADE_2, 7, "Sunny", "Güneşli", "sa-ni", "☀️", "Weather", "It's sunny outside.", "Dışarısı güneşli.")
        val wRainy = WordItem("w_g2_rainy", GradeLevel.GRADE_2, 7, "Rainy", "Yağmurlu", "rey-ni", "🌧️", "Weather", "It is rainy today.", "Bugün hava yağmurlu.")
        val wSnowy = WordItem("w_g2_snowy", GradeLevel.GRADE_2, 7, "Snowy", "Karlı", "snov-i", "🌨️", "Weather", "It's snowy in winter.", "Kışın hava karlıdır.")
        val wFriday = WordItem("w_g2_friday", GradeLevel.GRADE_2, 7, "Friday", "Cuma", "fray-dey", "📅", "Days", "It's Friday today!", "Bugün Cuma!")

        // Ders 1: Vücudumuz & Yüzümüz (My Body & Face)
        val lesson1 = Lesson(
            id = "g2_u7_l1",
            unitId = 7,
            grade = GradeLevel.GRADE_2,
            titleTr = "Vücudumuz ve Yüzümüz",
            titleEn = "My Body & Face",
            description = "Head, body, eye, ear, nose, mouth, hair, face, arm, hand, leg ve foot!",
            iconEmoji = "👀",
            targetWords = listOf(wHead, wBody, wEye, wEar, wNose, wMouth, wHair, wFace, wArm, wHand, wLeg, wFoot),
            exercises = listOf(
                createFlashcard(wHead),
                createFlashcard(wEye),
                createFlashcard(wEar),
                createFlashcard(wNose),
                createFlashcard(wHand),
                createPictureMatch(wEye, listOf(wEye, wEar, wNose, wMouth)),
                createPictureMatch(wHand, listOf(wHand, wArm, wLeg, wFoot)),
                createWordBuilder(wEye),
                createWordBuilder(wEar),
                createWordBuilder(wNose),
                createWordBuilder(wHand),
                createBalloonPop(wMouth, listOf(wMouth, wHead, wEye, wNose)),
                createAudioQuiz(wEar, listOf(wEar, wEye, wHead, wFace)),
                createAudioQuiz(wLeg, listOf(wLeg, wArm, wHand, wFoot)),
                createSentenceBuilder("Touch your nose", "Burnuna dokun", listOf("Touch", "your", "nose"), wNose),
                createSentenceBuilder("Point to your eye", "Gözünü göster", listOf("Point", "to", "your", "eye"), wEye),
                createSentenceBuilder("This is an ear", "Bu bir kulaktır", listOf("This", "is", "an", "ear"), wEar),
                createSentenceBuilder("Clap your hands", "Ellerini çırp", listOf("Clap", "your", "hands"), wHand)
            )
        )

        // Ders 2: Kıyafetlerimiz & Aksesuarlar (Clothes & Accessories)
        val lesson2 = Lesson(
            id = "g2_u7_l2",
            unitId = 7,
            grade = GradeLevel.GRADE_2,
            titleTr = "Kıyafetlerimiz & Aksesuarlar",
            titleEn = "Clothes & Accessories",
            description = "Glasses, gloves, coat, shirt, scarf, shoes, trousers, t-shirt, dress, hat ve umbrella!",
            iconEmoji = "🧣",
            targetWords = listOf(wGlasses, wGloves, wCoat, wShirt, wScarf, wShoes, wTrousers, wTShirt, wDress, wHat, wUmbrella),
            exercises = listOf(
                createFlashcard(wGlasses),
                createFlashcard(wCoat),
                createFlashcard(wScarf),
                createFlashcard(wShoes),
                createFlashcard(wTrousers),
                createPictureMatch(wGlasses, listOf(wGlasses, wGloves, wHat, wScarf)),
                createPictureMatch(wDress, listOf(wDress, wShirt, wTShirt, wCoat)),
                createWordBuilder(wCoat),
                createWordBuilder(wScarf),
                createWordBuilder(wShoes),
                createWordBuilder(wHat),
                createBalloonPop(wUmbrella, listOf(wUmbrella, wGlasses, wShoes, wShirt)),
                createAudioQuiz(wGlasses, listOf(wGlasses, wGloves, wCoat, wScarf)),
                createAudioQuiz(wTrousers, listOf(wTrousers, wShoes, wDress, wHat)),
                createSentenceBuilder("Deniz has got brown trousers", "Deniz kahverengi pantolona sahip", listOf("Deniz", "has", "got", "brown", "trousers"), wTrousers),
                createSentenceBuilder("Ceylin has got a yellow dress", "Ceylin sarı bir elbiseye sahip", listOf("Ceylin", "has", "got", "a", "yellow", "dress"), wDress),
                createSentenceBuilder("Bartu has got a red scarf", "Bartu kırmızı bir atkıya sahip", listOf("Bartu", "has", "got", "a", "red", "scarf"), wScarf),
                createSentenceBuilder("Aras has got a green hat", "Aras yeşil bir şapkaya sahip", listOf("Aras", "has", "got", "a", "green", "hat"), wHat)
            )
        )

        // Ders 3: Fiziksel Özellikler & "Have got / Has got" (Appearance & Descriptions)
        val lesson3 = Lesson(
            id = "g2_u7_l3",
            unitId = 7,
            grade = GradeLevel.GRADE_2,
            titleTr = "Fiziksel Özellikler & Tanımlamalar",
            titleEn = "Physical Appearance & Giraffe Story",
            description = "Tall, short, big, small, giraffe, blonde hair, green eyes ve have got!",
            iconEmoji = "🦒",
            targetWords = listOf(wTall, wShort, wBig, wSmall, wGiraffe, wEye, wHair),
            exercises = listOf(
                createFlashcard(wTall),
                createFlashcard(wShort),
                createFlashcard(wGiraffe),
                createPictureMatch(wGiraffe, listOf(wGiraffe, wTall, wBig, wSmall)),
                createWordBuilder(wTall),
                createWordBuilder(wBig),
                createWordBuilder(wSmall),
                createBalloonPop(wGiraffe, listOf(wGiraffe, wTall, wShort, wBig)),
                createAudioQuiz(wTall, listOf(wTall, wShort, wBig, wSmall)),
                createSentenceBuilder("Ayse has got green eyes", "Ayşe yeşil gözlere sahiptir", listOf("Ayse", "has", "got", "green", "eyes"), wEye),
                createSentenceBuilder("Ali is tall", "Ali uzun boyludur", listOf("Ali", "is", "tall"), wTall),
                createSentenceBuilder("I have got four long legs", "Dört uzun bacağım var", listOf("I", "have", "got", "four", "long", "legs"), wLeg),
                createSentenceBuilder("I am a very tall animal", "Çok uzun bir hayvanım", listOf("I", "am", "a", "very", "tall", "animal"), wGiraffe)
            )
        )

        // Ders 4: Hava Durumu & Günlük Sorular (Weather & Daily Questions)
        val lesson4 = Lesson(
            id = "g2_u7_l4",
            unitId = 7,
            grade = GradeLevel.GRADE_2,
            titleTr = "Hava Durumu & Günlük Sorular",
            titleEn = "Weather Today & Core Q&A",
            description = "How is the weather? It's cold, sunny, rainy, snowy. What day is it today? It's Friday!",
            iconEmoji = "☀️",
            targetWords = listOf(wCold, wHot, wSunny, wRainy, wSnowy, wFriday),
            exercises = listOf(
                createFlashcard(wCold),
                createFlashcard(wSunny),
                createFlashcard(wRainy),
                createFlashcard(wSnowy),
                createFlashcard(wFriday),
                createPictureMatch(wCold, listOf(wCold, wSunny, wRainy, wSnowy)),
                createPictureMatch(wRainy, listOf(wRainy, wSnowy, wSunny, wHot)),
                createWordBuilder(wCold),
                createWordBuilder(wSunny),
                createWordBuilder(wRainy),
                createWordBuilder(wSnowy),
                createBalloonPop(wSunny, listOf(wSunny, wRainy, wSnowy, wCold)),
                createAudioQuiz(wFriday, listOf(wFriday, wSunny, wRainy, wCold)),
                createSentenceBuilder("How is the weather", "Hava nasıl", listOf("How", "is", "the", "weather"), wCold),
                createSentenceBuilder("It is cold", "Hava soğuktur", listOf("It", "is", "cold"), wCold),
                createSentenceBuilder("It is rainy today", "Bugün hava yağmurludur", listOf("It", "is", "rainy", "today"), wRainy),
                createSentenceBuilder("What day is it today", "Bugün günlerden ne", listOf("What", "day", "is", "it", "today"), wFriday),
                createSentenceBuilder("It is Friday", "Bugün Cuma", listOf("It", "is", "Friday"), wFriday),
                createSentenceBuilder("What have you got", "Neye sahipsin / Neyin var", listOf("What", "have", "you", "got"), wGlasses),
                createSentenceBuilder("I have got blue glasses", "Mavi gözlüğüm var", listOf("I", "have", "got", "blue", "glasses"), wGlasses)
            )
        )

        return UnitTopic(
            id = 13,
            grade = GradeLevel.GRADE_2,
            number = 7,
            titleTr = "7. Ünite: Body Parts (Vücudumuzun Bölümleri)",
            titleEn = "Unit 7: Body Parts",
            iconEmoji = "👀",
            colorHex = 0xFF3B82F6,
            mebCode = "E2.7",
            lessons = listOf(lesson1, lesson2, lesson3, lesson4)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 8: PETS (MEB E2.8 - Evcil Hayvanlar)
    // ----------------------------------------------------------------------
    private fun createG2Unit8Pets(): UnitTopic {
        val wCat = WordItem("w_g2_cat", GradeLevel.GRADE_2, 8, "Cat", "Kedi", "ket", "🐱", "Pets", "A cute white cat.", "Sevimli beyaz bir kedi.")
        val wDog = WordItem("w_g2_dog", GradeLevel.GRADE_2, 8, "Dog", "Köpek", "dog", "🐶", "Pets", "The dog barks: Woof!", "Köpek havlar: Hav!")
        val wBird = WordItem("w_g2_bird", GradeLevel.GRADE_2, 8, "Bird", "Kuş", "böörd", "🐦", "Pets", "The bird can fly.", "Kuş uçabilir.")
        val wFish = WordItem("w_g2_fish", GradeLevel.GRADE_2, 8, "Fish", "Balık", "fiş", "🐟", "Pets", "A small blue fish swims.", "Küçük mavi bir balık yüzüyor.")
        val wTurtle = WordItem("w_g2_turtle", GradeLevel.GRADE_2, 8, "Turtle", "Kaplumbağa", "tör-tıl", "🐢", "Pets", "The turtle is slow.", "Kaplumbağa yavaştır.")
        val wRabbit = WordItem("w_g2_rabbit", GradeLevel.GRADE_2, 8, "Rabbit", "Tavşan", "re-bit", "🐰", "Pets", "A fast jumping rabbit.", "Hızlı zıplayan bir tavşan.")
        val wFrog = WordItem("w_g2_frog", GradeLevel.GRADE_2, 8, "Frog", "Kurbağa", "frog", "🐸", "Pets", "A green frog jumps.", "Yeşil bir kurbağa zıplar.")
        val wDuck = WordItem("w_g2_duck", GradeLevel.GRADE_2, 8, "Duck", "Ördek", "dak", "🦆", "Pets", "Yellow duck swims.", "Sarı ördek yüzer.")
        val wParrot = WordItem("w_g2_parrot", GradeLevel.GRADE_2, 8, "Parrot", "Papağan", "pe-rıt", "🦜", "Pets", "A colorful talking parrot.", "Renkli konuşan bir papağan.")
        val wHamster = WordItem("w_g2_hamster", GradeLevel.GRADE_2, 8, "Hamster", "Hamster", "hem-stır", "🐹", "Pets", "A tiny cute hamster.", "Minik sevimli bir hamster.")

        val lesson1 = Lesson(
            id = "g2_u8_l1",
            unitId = 8,
            grade = GradeLevel.GRADE_2,
            titleTr = "Sevimli Evcil Dostlar (Kedi & Köpek)",
            titleEn = "Beloved Pets (Cat & Dog)",
            description = "Cat, dog, bird, fish ve turtle!",
            iconEmoji = "🐶",
            targetWords = listOf(wCat, wDog, wBird, wFish, wTurtle),
            exercises = listOf(
                createFlashcard(wCat),
                createFlashcard(wDog),
                createFlashcard(wBird),
                createPictureMatch(wCat, listOf(wCat, wDog, wBird, wFish)),
                createWordBuilder(wCat),
                createWordBuilder(wDog),
                createWordBuilder(wFish),
                createBalloonPop(wBird, listOf(wBird, wCat, wDog, wTurtle)),
                createAudioQuiz(wDog, listOf(wDog, wCat, wBird, wFish)),
                createSentenceBuilder("I love my cat", "Kedimi seviyorum", listOf("I", "love", "my", "cat"), wCat),
                createSentenceBuilder("A cute brown dog", "Sevimli kahverengi bir köpek", listOf("A", "cute", "brown", "dog"), wDog)
            )
        )

        val lesson2 = Lesson(
            id = "g2_u8_l2",
            unitId = 8,
            grade = GradeLevel.GRADE_2,
            titleTr = "Bahçemizdeki Hayvanlar",
            titleEn = "Garden Friends",
            description = "Rabbit, frog, duck, parrot ve hamster!",
            iconEmoji = "🐰",
            targetWords = listOf(wRabbit, wFrog, wDuck, wParrot, wHamster),
            exercises = listOf(
                createFlashcard(wRabbit),
                createFlashcard(wFrog),
                createFlashcard(wDuck),
                createPictureMatch(wRabbit, listOf(wRabbit, wFrog, wDuck, wParrot)),
                createWordBuilder(wFrog),
                createWordBuilder(wDuck),
                createBalloonPop(wParrot, listOf(wParrot, wRabbit, wFrog, wHamster)),
                createAudioQuiz(wDuck, listOf(wDuck, wFrog, wRabbit, wParrot)),
                createSentenceBuilder("The rabbit can jump", "Tavşan zıplayabilir", listOf("The", "rabbit", "can", "jump"), wRabbit)
            )
        )

        return UnitTopic(
            id = 14,
            grade = GradeLevel.GRADE_2,
            number = 8,
            titleTr = "8. Ünite: Pets (Evcil Hayvanlar)",
            titleEn = "Unit 8: Pets",
            iconEmoji = "🐶",
            colorHex = 0xFFF59E0B,
            mebCode = "E2.8",
            lessons = listOf(lesson1, lesson2)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 9: FRUIT (MEB E2.9 - Meyveler & Beğeniler)
    // ----------------------------------------------------------------------
    private fun createG2Unit9Fruit(): UnitTopic {
        val wApple = WordItem("w_g2_apple", GradeLevel.GRADE_2, 9, "Apple", "Elma", "e-pıl", "🍎", "Fruit", "I like red apples.", "Kırmızı elmaları severim.")
        val wBanana = WordItem("w_g2_banana", GradeLevel.GRADE_2, 9, "Banana", "Muz", "bı-na-na", "🍌", "Fruit", "Monkeys like bananas.", "Maymunlar muz sever.")
        val wOrange = WordItem("w_g2_orange_fruit", GradeLevel.GRADE_2, 9, "Orange", "Portakal", "o-rınç", "🍊", "Fruit", "Sweet juicy orange.", "Tatlı sulu portakal.")
        val wLemon = WordItem("w_g2_lemon_fruit", GradeLevel.GRADE_2, 9, "Lemon", "Limon", "le-mın", "🍋", "Fruit", "Sour yellow lemon.", "Ekşi sarı limon.")
        val wPeach = WordItem("w_g2_peach", GradeLevel.GRADE_2, 9, "Peach", "Şeftali", "piiç", "🍑", "Fruit", "I love sweet peaches.", "Tatlı şeftalileri severim.")
        val wStrawberry = WordItem("w_g2_strawberry", GradeLevel.GRADE_2, 9, "Strawberry", "Çilek", "strov-be-ri", "🍓", "Fruit", "Red yummy strawberry.", "Kırmızı lezzetli çilek.")
        val wGrape = WordItem("w_g2_grape", GradeLevel.GRADE_2, 9, "Grape", "Üzüm", "greyp", "🍇", "Fruit", "Purple sweet grapes.", "Mor tatlı üzümler.")
        val wMelon = WordItem("w_g2_melon", GradeLevel.GRADE_2, 9, "Melon", "Kavun", "me-lın", "🍈", "Fruit", "Yellow sweet melon.", "Sarı tatlı kavun.")
        val wWatermelon = WordItem("w_g2_watermelon", GradeLevel.GRADE_2, 9, "Watermelon", "Karpuz", "voo-tır-me-lın", "🍉", "Fruit", "A big cold watermelon.", "Büyük soğuk bir karpuz.")
        val wCherry = WordItem("w_g2_cherry", GradeLevel.GRADE_2, 9, "Cherry", "Kiraz", "çe-ri", "🍒", "Fruit", "Two red cherries.", "İki kırmızı kiraz.")

        val lesson1 = Lesson(
            id = "g2_u9_l1",
            unitId = 9,
            grade = GradeLevel.GRADE_2,
            titleTr = "Sevilen Meyveler (I Like...)",
            titleEn = "Favorite Fruits",
            description = "Apple, banana, orange, lemon ve peach!",
            iconEmoji = "🍎",
            targetWords = listOf(wApple, wBanana, wOrange, wLemon, wPeach),
            exercises = listOf(
                createFlashcard(wApple),
                createFlashcard(wBanana),
                createFlashcard(wOrange),
                createPictureMatch(wApple, listOf(wApple, wBanana, wOrange, wLemon)),
                createWordBuilder(wApple),
                createWordBuilder(wBanana),
                createWordBuilder(wPeach),
                createBalloonPop(wLemon, listOf(wLemon, wApple, wBanana, wOrange)),
                createAudioQuiz(wOrange, listOf(wOrange, wBanana, wApple, wPeach)),
                createSentenceBuilder("I like apples", "Elma severim", listOf("I", "like", "apples"), wApple),
                createSentenceBuilder("Eat an apple", "Bir elma ye", listOf("Eat", "an", "apple"), wApple)
            )
        )

        val lesson2 = Lesson(
            id = "g2_u9_l2",
            unitId = 9,
            grade = GradeLevel.GRADE_2,
            titleTr = "Yaz Meyveleri & Lezzetler",
            titleEn = "Summer Yummy Fruits",
            description = "Strawberry, grape, melon, watermelon ve cherry!",
            iconEmoji = "🍓",
            targetWords = listOf(wStrawberry, wGrape, wMelon, wWatermelon, wCherry),
            exercises = listOf(
                createFlashcard(wStrawberry),
                createFlashcard(wWatermelon),
                createFlashcard(wCherry),
                createPictureMatch(wStrawberry, listOf(wStrawberry, wGrape, wMelon, wWatermelon)),
                createWordBuilder(wGrape),
                createWordBuilder(wMelon),
                createWordBuilder(wCherry),
                createBalloonPop(wWatermelon, listOf(wWatermelon, wStrawberry, wGrape, wMelon)),
                createAudioQuiz(wCherry, listOf(wCherry, wStrawberry, wGrape, wWatermelon)),
                createSentenceBuilder("I like strawberry", "Çilek severim", listOf("I", "like", "strawberry"), wStrawberry)
            )
        )

        return UnitTopic(
            id = 15,
            grade = GradeLevel.GRADE_2,
            number = 9,
            titleTr = "9. Ünite: Fruit (Meyveler)",
            titleEn = "Unit 9: Fruit",
            iconEmoji = "🍎",
            colorHex = 0xFFEF4444,
            mebCode = "E2.9",
            lessons = listOf(lesson1, lesson2)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 10: ANIMALS (MEB E2.10 - Hayvanlar Dünyası & Sıfatlar)
    // ----------------------------------------------------------------------
    private fun createG2Unit10Animals(): UnitTopic {
        val wLion = WordItem("w_g2_lion", GradeLevel.GRADE_2, 10, "Lion", "Aslan", "lay-ın", "🦁", "Animals", "The lion is strong.", "Aslan güçlüdür.")
        val wElephant = WordItem("w_g2_elephant", GradeLevel.GRADE_2, 10, "Elephant", "Fil", "e-lı-fınt", "🐘", "Animals", "A big grey elephant.", "Büyük gri bir fil.")
        val wMonkey = WordItem("w_g2_monkey", GradeLevel.GRADE_2, 10, "Monkey", "Maymun", "man-ki", "🐵", "Animals", "A funny monkey climbs.", "Komik bir maymun tırmanıyor.")
        val wTiger = WordItem("w_g2_tiger", GradeLevel.GRADE_2, 10, "Tiger", "Kaplan", "tay-gır", "🐯", "Animals", "The tiger runs fast.", "Kaplan hızlı koşar.")
        val wGiraffe = WordItem("w_g2_giraffe", GradeLevel.GRADE_2, 10, "Giraffe", "Zürafa", "ci-raaf", "🦒", "Animals", "A tall giraffe eats leaves.", "Uzun boylu bir zürafa yaprak yer.")
        val wBear = WordItem("w_g2_bear", GradeLevel.GRADE_2, 10, "Bear", "Ayı", "beer", "🐻", "Animals", "A big brown bear.", "Büyük kahverengi bir ayı.")
        val wSnake = WordItem("w_g2_snake", GradeLevel.GRADE_2, 10, "Snake", "Yılan", "sneyk", "🐍", "Animals", "A green snake.", "Yeşil bir yılan.")
        val wZebra = WordItem("w_g2_zebra_anim", GradeLevel.GRADE_2, 10, "Zebra", "Zebra", "zeb-ra", "🦓", "Animals", "A black and white zebra.", "Siyah beyaz bir zebra.")
        val wHorse = WordItem("w_g2_horse", GradeLevel.GRADE_2, 10, "Horse", "At", "hoors", "🐴", "Animals", "The horse can run fast.", "At hızlı koşabilir.")
        val wCow = WordItem("w_g2_cow", GradeLevel.GRADE_2, 10, "Cow", "İnek", "kaw", "🐮", "Animals", "The cow gives milk.", "İnek süt verir.")
        val wSheep = WordItem("w_g2_sheep", GradeLevel.GRADE_2, 10, "Sheep", "Koyun", "şiip", "🐑", "Animals", "A soft white sheep.", "Yumuşak beyaz bir koyun.")

        val lesson1 = Lesson(
            id = "g2_u10_l1",
            unitId = 10,
            grade = GradeLevel.GRADE_2,
            titleTr = "Safari ve Orman Hayvanları",
            titleEn = "Safari Wild Animals",
            description = "Lion, elephant, monkey, tiger, giraffe ve bear!",
            iconEmoji = "🦁",
            targetWords = listOf(wLion, wElephant, wMonkey, wTiger, wGiraffe, wBear),
            exercises = listOf(
                createFlashcard(wLion),
                createFlashcard(wElephant),
                createFlashcard(wGiraffe),
                createPictureMatch(wLion, listOf(wLion, wElephant, wMonkey, wTiger)),
                createWordBuilder(wLion),
                createWordBuilder(wBear),
                createWordBuilder(wTiger),
                createBalloonPop(wMonkey, listOf(wMonkey, wLion, wElephant, wGiraffe)),
                createAudioQuiz(wElephant, listOf(wElephant, wLion, wGiraffe, wBear)),
                createSentenceBuilder("A big elephant", "Büyük bir fil", listOf("A", "big", "elephant"), wElephant),
                createSentenceBuilder("The lion is strong", "Aslan güçlüdür", listOf("The", "lion", "is", "strong"), wLion)
            )
        )

        val lesson2 = Lesson(
            id = "g2_u10_l2",
            unitId = 10,
            grade = GradeLevel.GRADE_2,
            titleTr = "Çiftlik ve Doğa Dostları",
            titleEn = "Farm & Nature Animals",
            description = "Snake, zebra, horse, cow ve sheep!",
            iconEmoji = "🐴",
            targetWords = listOf(wSnake, wZebra, wHorse, wCow, wSheep),
            exercises = listOf(
                createFlashcard(wHorse),
                createFlashcard(wCow),
                createFlashcard(wSheep),
                createPictureMatch(wHorse, listOf(wHorse, wCow, wSheep, wSnake)),
                createWordBuilder(wHorse),
                createWordBuilder(wCow),
                createWordBuilder(wSheep),
                createBalloonPop(wSnake, listOf(wSnake, wHorse, wCow, wZebra)),
                createAudioQuiz(wCow, listOf(wCow, wHorse, wSheep, wSnake)),
                createSentenceBuilder("The horse can run", "At koşabilir", listOf("The", "horse", "can", "run"), wHorse)
            )
        )

        return UnitTopic(
            id = 16,
            grade = GradeLevel.GRADE_2,
            number = 10,
            titleTr = "10. Ünite: Animals (Hayvanlar)",
            titleEn = "Unit 10: Animals",
            iconEmoji = "🦁",
            colorHex = 0xFF10B981,
            mebCode = "E2.10",
            lessons = listOf(lesson1, lesson2)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 11: NATURE & MY GARDEN (Bahçem ve Doğa)
    // ----------------------------------------------------------------------
    private fun createG2Unit11NatureGarden(): UnitTopic {
        val wFlower = WordItem("w_g2_flower", GradeLevel.GRADE_2, 11, "Flower", "Çiçek", "fla-vır", "🌸", "Nature", "A pretty pink flower.", "Güzel pembe bir çiçek.")
        val wTree = WordItem("w_g2_tree", GradeLevel.GRADE_2, 11, "Tree", "Ağaç", "trii", "🌳", "Nature", "A tall green tree.", "Uzun yeşil bir ağaç.")
        val wGrass = WordItem("w_g2_grass", GradeLevel.GRADE_2, 11, "Grass", "Çim", "gres", "🌿", "Nature", "Green soft grass.", "Yeşil yumuşak çim.")
        val wButterfly = WordItem("w_g2_butterfly", GradeLevel.GRADE_2, 11, "Butterfly", "Kelebek", "ba-tır-flay", "🦋", "Nature", "A colorful butterfly flies.", "Renkli bir kelebek uçuyor.")
        val wBee = WordItem("w_g2_bee", GradeLevel.GRADE_2, 11, "Bee", "Arı", "bii", "🐝", "Nature", "A busy little bee.", "Çalışkan minik bir arı.")
        val wBird = WordItem("w_g2_bird", GradeLevel.GRADE_2, 11, "Bird", "Kuş", "börd", "🐦", "Nature", "The bird is singing.", "Kuş şarkı söylüyor.")

        val lesson1 = Lesson(
            id = "g2_u11_l1",
            unitId = 11,
            grade = GradeLevel.GRADE_2,
            titleTr = "Bahçem ve Renkli Doğa",
            titleEn = "My Garden & Nature",
            description = "Flower, tree, grass, butterfly, bee ve bird!",
            iconEmoji = "🌳",
            targetWords = listOf(wFlower, wTree, wGrass, wButterfly, wBee, wBird),
            exercises = listOf(
                createFlashcard(wFlower),
                createFlashcard(wTree),
                createFlashcard(wButterfly),
                createPictureMatch(wFlower, listOf(wFlower, wTree, wGrass, wBee)),
                createWordBuilder(wTree),
                createWordBuilder(wBee),
                createWordBuilder(wBird),
                createBalloonPop(wButterfly, listOf(wButterfly, wFlower, wBee, wBird)),
                createAudioQuiz(wBee, listOf(wBee, wButterfly, wBird, wTree)),
                createSentenceBuilder("A green tree", "Yeşil bir ağaç", listOf("A", "green", "tree"), wTree)
            )
        )

        return UnitTopic(
            id = 211,
            grade = GradeLevel.GRADE_2,
            number = 11,
            titleTr = "11. Ünite: Nature & Garden (Doğa)",
            titleEn = "Unit 11: Nature & Garden",
            iconEmoji = "🌳",
            colorHex = 0xFF059669,
            mebCode = "E2.11",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 12: PICNIC PARTY (Piknik Zamanı & Yiyecekler)
    // ----------------------------------------------------------------------
    private fun createG2Unit12PicnicParty(): UnitTopic {
        val wSandwich = WordItem("w_g2_sandwich", GradeLevel.GRADE_2, 12, "Sandwich", "Sandviç", "send-viç", "🥪", "Food", "A yummy cheese sandwich.", "Nefis bir peynirli sandviç.")
        val wJuice = WordItem("w_g2_juice", GradeLevel.GRADE_2, 12, "Juice", "Meyve Suyu", "cuus", "🧃", "Food", "Orange juice, please.", "Portakal suyu, lütfen.")
        val wCake = WordItem("w_g2_cake2", GradeLevel.GRADE_2, 12, "Cake", "Kek / Pasta", "keyk", "🍰", "Food", "Sweet chocolate cake.", "Tatlı çikolatalı kek.")
        val wCookie = WordItem("w_g2_cookie", GradeLevel.GRADE_2, 12, "Cookie", "Kurabiye", "ku-ki", "🍪", "Food", "Crunchy sweet cookie.", "Çıtır tatlı kurabiye.")
        val wStrawberry = WordItem("w_g2_strawberry", GradeLevel.GRADE_2, 12, "Strawberry", "Çilek", "strov-be-ri", "🍓", "Food", "Red sweet strawberry.", "Kırmızı tatlı çilek.")
        val wLemonade = WordItem("w_g2_lemonade", GradeLevel.GRADE_2, 12, "Lemonade", "Limonata", "le-mı-neyd", "🍋", "Food", "Cold fresh lemonade.", "Soğuk taze limonata.")

        val lesson1 = Lesson(
            id = "g2_u12_l1",
            unitId = 12,
            grade = GradeLevel.GRADE_2,
            titleTr = "Parkta Piknik Zamanı",
            titleEn = "Picnic in the Park",
            description = "Sandwich, juice, cake, cookie, strawberry ve lemonade!",
            iconEmoji = "🥪",
            targetWords = listOf(wSandwich, wJuice, wCake, wCookie, wStrawberry, wLemonade),
            exercises = listOf(
                createFlashcard(wSandwich),
                createFlashcard(wJuice),
                createFlashcard(wStrawberry),
                createPictureMatch(wSandwich, listOf(wSandwich, wJuice, wCookie, wLemonade)),
                createWordBuilder(wJuice),
                createWordBuilder(wCookie),
                createWordBuilder(wCake),
                createBalloonPop(wStrawberry, listOf(wStrawberry, wCake, wJuice, wSandwich)),
                createAudioQuiz(wLemonade, listOf(wLemonade, wJuice, wStrawberry, wCookie)),
                createSentenceBuilder("I like juice", "Meyve suyu severim", listOf("I", "like", "juice"), wJuice)
            )
        )

        return UnitTopic(
            id = 212,
            grade = GradeLevel.GRADE_2,
            number = 12,
            titleTr = "12. Ünite: Picnic Party (Piknik)",
            titleEn = "Unit 12: Picnic Party",
            iconEmoji = "🥪",
            colorHex = 0xFFF97316,
            mebCode = "E2.12",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 13: MUSIC & INSTRUMENTS (Müzik ve Enstrümanlar)
    // ----------------------------------------------------------------------
    private fun createG2Unit13MusicInstruments(): UnitTopic {
        val wGuitar = WordItem("w_g2_guitar", GradeLevel.GRADE_2, 13, "Guitar", "Gitar", "gi-taar", "🎸", "Music", "Play the acoustic guitar.", "Gitar çal.")
        val wPiano = WordItem("w_g2_piano", GradeLevel.GRADE_2, 13, "Piano", "Piyano", "pi-ya-nov", "🎹", "Music", "Play the grand piano.", "Piyano çal.")
        val wDrum = WordItem("w_g2_drum", GradeLevel.GRADE_2, 13, "Drum", "Davul", "dram", "🥁", "Music", "Beat the loud drum.", "Davula vur.")
        val wViolin = WordItem("w_g2_violin", GradeLevel.GRADE_2, 13, "Violin", "Keman", "vay-ı-lin", "🎻", "Music", "Sweet violin sound.", "Tatlı keman sesi.")
        val wSong = WordItem("w_g2_song", GradeLevel.GRADE_2, 13, "Song", "Şarkı", "song", "🎵", "Music", "Sing a happy song.", "Neşeli bir şarkı söyle.")
        val wDance = WordItem("w_g2_dance", GradeLevel.GRADE_2, 13, "Dance", "Dans Etmek", "dens", "💃", "Music", "Let's dance together!", "Birlikte dans edelim!")

        val lesson1 = Lesson(
            id = "g2_u13_l1",
            unitId = 13,
            grade = GradeLevel.GRADE_2,
            titleTr = "Müzik Aletleri & Ritim",
            titleEn = "Music & Rhythm",
            description = "Guitar, piano, drum, violin, song ve dance!",
            iconEmoji = "🎸",
            targetWords = listOf(wGuitar, wPiano, wDrum, wViolin, wSong, wDance),
            exercises = listOf(
                createFlashcard(wGuitar),
                createFlashcard(wPiano),
                createFlashcard(wDrum),
                createPictureMatch(wGuitar, listOf(wGuitar, wPiano, wDrum, wViolin)),
                createWordBuilder(wDrum),
                createWordBuilder(wSong),
                createWordBuilder(wPiano),
                createBalloonPop(wViolin, listOf(wViolin, wGuitar, wDrum, wSong)),
                createAudioQuiz(wDance, listOf(wDance, wSong, wGuitar, wPiano)),
                createSentenceBuilder("Sing a song", "Bir şarkı söyle", listOf("Sing", "a", "song"), wSong)
            )
        )

        return UnitTopic(
            id = 213,
            grade = GradeLevel.GRADE_2,
            number = 13,
            titleTr = "13. Ünite: Music & Songs (Müzik)",
            titleEn = "Unit 13: Music & Songs",
            iconEmoji = "🎸",
            colorHex = 0xFF8B5CF6,
            mebCode = "E2.13",
            lessons = listOf(lesson1)
        )
    }

    // ----------------------------------------------------------------------
    // 2. SINIF - UNIT 14: CIRCUS FUN (Sirk ve Gösteri Dünyası)
    // ----------------------------------------------------------------------
    private fun createG2Unit14CircusFun(): UnitTopic {
        val wClown = WordItem("w_g2_clown", GradeLevel.GRADE_2, 14, "Clown", "Palyaço", "klawn", "🤡", "Circus", "A funny happy clown.", "Komik mutlu bir palyaço.")
        val wMagic = WordItem("w_g2_magic", GradeLevel.GRADE_2, 14, "Magic", "Sihir / Büyü", "me-cik", "🪄", "Circus", "Do a magic trick.", "Sihir numarası yap.")
        val wJuggler = WordItem("w_g2_juggler", GradeLevel.GRADE_2, 14, "Juggler", "Jonglör / Top Çevirici", "cag-lır", "🤹", "Circus", "The juggler catches balls.", "Jonglör topları yakalar.")
        val wAcrobat = WordItem("w_g2_acrobat", GradeLevel.GRADE_2, 14, "Acrobat", "Akrobat", "ek-rı-bet", "🤸", "Circus", "The acrobat can jump high.", "Akrobat yükseğe zıplayabilir.")
        val wPopcorn = WordItem("w_g2_popcorn", GradeLevel.GRADE_2, 14, "Popcorn", "Patlamış Mısır", "pap-korn", "🍿", "Circus", "Warm salty popcorn.", "Sıcak tuzlu mısır.")

        val lesson1 = Lesson(
            id = "g2_u14_l1",
            unitId = 14,
            grade = GradeLevel.GRADE_2,
            titleTr = "Eğlenceli Sirk Gösterisi",
            titleEn = "Circus Show Time",
            description = "Clown, magic, juggler, acrobat ve popcorn!",
            iconEmoji = "🎪",
            targetWords = listOf(wClown, wMagic, wJuggler, wAcrobat, wPopcorn),
            exercises = listOf(
                createFlashcard(wClown),
                createFlashcard(wMagic),
                createFlashcard(wPopcorn),
                createPictureMatch(wClown, listOf(wClown, wMagic, wJuggler, wPopcorn)),
                createWordBuilder(wClown),
                createWordBuilder(wMagic),
                createBalloonPop(wJuggler, listOf(wJuggler, wAcrobat, wClown, wPopcorn)),
                createAudioQuiz(wPopcorn, listOf(wPopcorn, wMagic, wClown, wAcrobat)),
                createSentenceBuilder("A funny clown", "Komik bir palyaço", listOf("A", "funny", "clown"), wClown)
            )
        )

        return UnitTopic(
            id = 214,
            grade = GradeLevel.GRADE_2,
            number = 14,
            titleTr = "14. Ünite: Circus Fun (Sirk)",
            titleEn = "Unit 14: Circus Fun",
            iconEmoji = "🎪",
            colorHex = 0xFFEC4899,
            mebCode = "E2.14",
            lessons = listOf(lesson1)
        )
    }

    // ==========================================
    // INITIAL COSTUMES & BADGES
    // ==========================================
    val initialCostumes: List<com.example.data.model.PokoCostume> by lazy {
        listOf(
            // 1. KIYAFET & ZIRH (Costumes / Outfits)
            com.example.data.model.PokoCostume("costume_default", "Lord Vader Zırhı", "🎽", "Obsidyen çelik zırh ve pelerin", 0, category = com.example.data.model.CostumeCategory.COSTUME, isUnlocked = true, isEquipped = true),
            com.example.data.model.PokoCostume("costume_kylo", "Kylo Ren Cübbesi", "⚡", "Karanlık şövalye dokuma zırhı", 50, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_jedi", "Jedi Usta Tunik", "🥋", "Işık tarafının bilgelik kıyafeti", 60, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_yoda", "Yoda Bilge Pelerini", "🟢", "Kadim Güç ustası kumaşı", 70, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_trooper", "Stormtrooper Zırhı", "⚪", "Beyaz kompozit galaktik zırh", 80, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_pilot", "X-Wing Uçuş Kostümü", "🚀", "Yıldız filosu as pilot tulumu", 90, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_cyber", "Siber Şövalye Zırhı", "🤖", "Neon LED parlamalı siber zırh", 100, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_superhero", "Süper Kahraman Kostümü", "🦸", "Kırmızı pelerinli kahraman tulumu", 120, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_wizard", "Büyücü Cübbesi", "🧙‍♂️", "Yıldızlı mistik gece kumaşı", 130, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_ninja", "Gölge Ninja Giysisi", "🥷", "Sessiz hareket zırhı", 140, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_astronaut", "Uzay Kaşifi Giysisi", "🧑‍🚀", "Gezegenler arası koruyucu tulum", 150, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_king", "Kraliyet Pelerini & Taç", "👑", "Altın işlemeli kral kostümü", 180, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_mario", "Süper Mario Tulumu", "👨‍🔧", "Kırmızı gömlek ve mavi kot tulum", 70, category = com.example.data.model.CostumeCategory.COSTUME),
            com.example.data.model.PokoCostume("costume_luigi", "Yeşil Luigi Tulumu", "👨‍🌾", "Yeşil gömlek ve maceracı tulum", 70, category = com.example.data.model.CostumeCategory.COSTUME),

            // 2. IŞIN KILICI & EL EŞYASI (Weapons & Hand Items)
            com.example.data.model.PokoCostume("weapon_red_saber", "Kırmızı Plazma Kılıcı", "⚔️", "Klasik Sith kırmızı lazer ışını", 0, category = com.example.data.model.CostumeCategory.LIGHTSABER, isUnlocked = true, isEquipped = true),
            com.example.data.model.PokoCostume("weapon_blue_saber", "Mavi Jedi Kılıcı", "🗡️", "Saf kristal mavi lazer enerjisi", 40, category = com.example.data.model.CostumeCategory.LIGHTSABER),
            com.example.data.model.PokoCostume("weapon_green_saber", "Yeşil Güç Kılıcı", "🟢", "Doğa ve bilgelik yeşil lazeri", 50, category = com.example.data.model.CostumeCategory.LIGHTSABER),
            com.example.data.model.PokoCostume("weapon_purple_saber", "Mor Galaktik Kılıç", "💜", "Nadir ametist plazma ışını", 60, category = com.example.data.model.CostumeCategory.LIGHTSABER),
            com.example.data.model.PokoCostume("weapon_yellow_saber", "Altın Tapınak Kılıcı", "🟡", "Tapınak muhafızı sarı lazeri", 70, category = com.example.data.model.CostumeCategory.LIGHTSABER),
            com.example.data.model.PokoCostume("weapon_dual_saber", "Çift Taraflı Lazer Kılıcı", "🔥", "İki uçlu alevli kırmızı ışın", 90, category = com.example.data.model.CostumeCategory.LIGHTSABER),
            com.example.data.model.PokoCostume("weapon_magic_wand", "Yıldızlı Sihirli Asa", "🪄", "Işık saçan yıldız ucu", 100, category = com.example.data.model.CostumeCategory.LIGHTSABER),
            com.example.data.model.PokoCostume("weapon_blaster", "Lazer Işın Tabancası", "🔫", "Cyan enerji atışları", 110, category = com.example.data.model.CostumeCategory.LIGHTSABER),
            com.example.data.model.PokoCostume("weapon_lightning_staff", "Şimşek Asası", "⚡", "Elektrik kıvılcımlı asa", 120, category = com.example.data.model.CostumeCategory.LIGHTSABER),
            com.example.data.model.PokoCostume("weapon_mario_star", "Süper Mantar & Yıldız Asası", "🍄", "Mantar krallığı yenilmezlik gücü", 80, category = com.example.data.model.CostumeCategory.LIGHTSABER),

            // 3. KASK & ŞAPKA & GÖZLÜK (Helmets & Headgear)
            com.example.data.model.PokoCostume("helmet_vader", "Lord Vader Miğferi", "🪖", "Solunum filtreli efsanevi siyah kask", 0, category = com.example.data.model.CostumeCategory.HELMET, isUnlocked = true, isEquipped = true),
            com.example.data.model.PokoCostume("helmet_kylo", "Kylo Ren Çelik Maskesi", "🎭", "Gümüş işlemeli savaş kaskı", 40, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_trooper", "Stormtrooper Kaskı", "⚪", "Galaktik asker beyaz kaskı", 50, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_mando", "Beskar Mando Kaskı", "🛡️", "Kırılmaz T vizörlü miğfer", 70, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_pilot", "As Pilot Kaskı", "🥽", "Vizörlü yıldız savaşçısı kaskı", 80, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_crown", "Altın Elmas Kral Tacı", "👑", "Pırlantalarla süslü görkemli taç", 100, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_vr", "Siber Neon Hologram Gözlüğü", "🕶️", "Gelecekten gelen dijital vizör", 90, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_wizard", "Yıldızlı Büyücü Şapkası", "🧙", "Sihirli sivri gece şapkası", 95, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_ninja", "Siyah Ninja Bandanası", "🥷", "Gözleri açıkta bırakan bandana", 85, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_astronaut", "Uzay Kaskı & Altın Vizör", "👨‍🚀", "Oksijen korumalı parlayan kask", 110, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_party", "Renkli Parti Şapkası", "🥳", "Eğlenceli kutlama şapkası", 30, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_mario_cap", "Kırmızı Mario Şapkası & Bıyık", "🧢", "İkonik M harfli şapka ve bıyık", 60, category = com.example.data.model.CostumeCategory.HELMET),
            com.example.data.model.PokoCostume("helmet_luigi_cap", "Yeşil Luigi Şapkası & Bıyık", "🎩", "L harfli yeşil şapka ve bıyık", 60, category = com.example.data.model.CostumeCategory.HELMET),

            // 4. ODA EŞYASI & DROID (Room Furniture & Droids)
            com.example.data.model.PokoCostume("decor_holotable", "Hologram Yıldız Masası", "🛸", "3D gezegen yansıtıcı holotable", 0, category = com.example.data.model.CostumeCategory.ROOM_DECOR, isUnlocked = true, isEquipped = true),
            com.example.data.model.PokoCostume("decor_droid_bb8", "Sadık Robot BB-8 Droid", "🤖", "Odada neşeyle dönen sevimli droid", 60, category = com.example.data.model.CostumeCategory.ROOM_DECOR),
            com.example.data.model.PokoCostume("decor_gaming_chair", "RGB Işıklı Oyuncu Koltuğu", "💺", "Dönen konforlu neon koltuk", 70, category = com.example.data.model.CostumeCategory.ROOM_DECOR),
            com.example.data.model.PokoCostume("decor_arcade", "Retro Star Wars Arcade", "🕹️", "Oyun jetonlu nostaljik makine", 90, category = com.example.data.model.CostumeCategory.ROOM_DECOR),
            com.example.data.model.PokoCostume("decor_space_fighter", "Mini X-Wing Maketi", "🚀", "Tavandan sarkan yıldız savaşçısı", 80, category = com.example.data.model.CostumeCategory.ROOM_DECOR),
            com.example.data.model.PokoCostume("decor_telescope", "Galaksi Gözlem Teleskobu", "🔭", "Yıldızları ve nebulaları izle", 100, category = com.example.data.model.CostumeCategory.ROOM_DECOR),
            com.example.data.model.PokoCostume("decor_pet_dragon", "Yavru Galaksi Ejderhası", "🐉", "Odanın köşesinde uyuyan ejderha", 130, category = com.example.data.model.CostumeCategory.ROOM_DECOR),
            com.example.data.model.PokoCostume("decor_trophy_case", "Kristal Kupa & Başarı Dolabı", "🏆", "Tüm yıldızların sergilendiği vitrin", 110, category = com.example.data.model.CostumeCategory.ROOM_DECOR),
            com.example.data.model.PokoCostume("decor_alien_plant", "Işıldayan Uzay Bitkisi", "🪴", "Geceleri parıldayan kristal yapraklar", 50, category = com.example.data.model.CostumeCategory.ROOM_DECOR),

            // 5. ODA TEMASI & ARKA PLAN (Room Themes & Backgrounds)
            com.example.data.model.PokoCostume("theme_starship", "Yıldız Gemisi Köprüsü", "🌌", "Hiperuzay yıldız çizgileri ve çelik güverte", 0, category = com.example.data.model.CostumeCategory.ROOM_THEME, isUnlocked = true, isEquipped = true),
            com.example.data.model.PokoCostume("theme_cyberpunk", "Neon Siber Laboratuvar", "🏙️", "Siyan-mor neon şehir manzarası", 80, category = com.example.data.model.CostumeCategory.ROOM_THEME),
            com.example.data.model.PokoCostume("theme_jedi_temple", "Antik Jedi Tapınağı", "🏛️", "Gün batımı ve taş sütunlar", 100, category = com.example.data.model.CostumeCategory.ROOM_THEME),
            com.example.data.model.PokoCostume("theme_space_station", "Derin Uzay İstasyonu", "🛰️", "Dünya manzaralı yörünge penceresi", 120, category = com.example.data.model.CostumeCategory.ROOM_THEME),
            com.example.data.model.PokoCostume("theme_cozy_cabin", "Sıcak Ahşap Orman Evi", "🏡", "Şömineli ve sıcak renkli ahşap oda", 70, category = com.example.data.model.CostumeCategory.ROOM_THEME)
        )
    }

    val availableBadges: List<com.example.data.model.BadgeItem> by lazy {
        listOf(
            com.example.data.model.BadgeItem("badge_first_step", "İlk Adım", "İlk dersini tamamla ve yıldız kazan!", "🌟", requiredValue = 1),
            com.example.data.model.BadgeItem("badge_star_collector", "Yıldız Avcısı", "Toplam 10 yıldız topla!", "⭐", requiredValue = 10),
            com.example.data.model.BadgeItem("badge_word_master", "Kelime Ustası", "En az 20 İngilizce kelime öğren!", "📚", requiredValue = 20),
            com.example.data.model.BadgeItem("badge_streak_3", "Süper Seri", "3 gün üst üste uygulamaya girip çalış!", "🔥", requiredValue = 3),
            com.example.data.model.BadgeItem("badge_balloon_pop", "Balon Avcısı", "15 soruyu doğru yanıtla!", "🎈", requiredValue = 15),
            com.example.data.model.BadgeItem("badge_fashion_poko", "Stil Sahibi", "Mağazadan Poko için yeni bir kostüm aç!", "👑", requiredValue = 1)
        )
    }

    // ==========================================
    // EXERCISE HELPER FACTORIES
    // ==========================================
    private fun createFlashcard(word: WordItem): Exercise {
        return Exercise(
            id = "ex_fc_${word.id}",
            type = ExerciseType.FLASHCARD,
            promptTr = "Kelimeyi dinle ve sesli tekrar et!",
            promptEn = "Listen and repeat after Poko!",
            targetWord = word
        )
    }

    private fun createPictureMatch(target: WordItem, pool: List<WordItem>): Exercise {
        val filtered = pool.filter { 
            it.id != target.id && 
            !it.english.equals(target.english, ignoreCase = true) && 
            !it.turkish.equals(target.turkish, ignoreCase = true) && 
            it.emoji != target.emoji 
        }
        val distinctPool = (listOf(target) + filtered).distinctBy { it.english }.take(4).shuffled()
        val options = distinctPool.map {
            WordOption(
                word = it.english,
                translation = it.turkish,
                emoji = it.emoji,
                isCorrect = (it.id == target.id)
            )
        }
        return Exercise(
            id = "ex_pm_${target.id}",
            type = ExerciseType.PICTURE_MATCH,
            promptTr = "\"${target.english}\" kelimesini anlatan kartı seç!",
            promptEn = "Which card matches \"${target.english}\"?",
            targetWord = target,
            options = options
        )
    }

    private fun createAudioQuiz(target: WordItem, pool: List<WordItem>): Exercise {
        val filtered = pool.filter { 
            it.id != target.id && 
            !it.english.equals(target.english, ignoreCase = true) && 
            !it.turkish.equals(target.turkish, ignoreCase = true) && 
            it.emoji != target.emoji 
        }
        val distinctPool = (listOf(target) + filtered).distinctBy { it.turkish }.take(4).shuffled()
        val options = distinctPool.map {
            WordOption(
                word = it.english,
                translation = it.turkish,
                emoji = it.emoji,
                isCorrect = (it.id == target.id)
            )
        }
        return Exercise(
            id = "ex_aq_${target.id}",
            type = ExerciseType.AUDIO_QUIZ,
            promptTr = "Sesi dinle ve Türkçe karşılığına dokun!",
            promptEn = "Listen to the audio and tap the correct Turkish meaning!",
            targetWord = target,
            options = options
        )
    }

    private fun createBalloonPop(target: WordItem, pool: List<WordItem>): Exercise {
        val distinctPool = (listOf(target) + pool.filter { it.id != target.id }).distinctBy { it.id }.take(4).shuffled()
        val options = distinctPool.map {
            WordOption(
                word = it.english,
                translation = it.turkish,
                emoji = it.emoji,
                isCorrect = (it.id == target.id)
            )
        }
        return Exercise(
            id = "ex_bp_${target.id}",
            type = ExerciseType.BALLOON_POP,
            promptTr = "\"${target.turkish}\" balonunu patlat!",
            promptEn = "Pop the balloon for \"${target.turkish}\"!",
            targetWord = target,
            options = options
        )
    }

    private fun createWordBuilder(word: WordItem): Exercise {
        val scrambled = word.english.uppercase().filter { it.isLetter() }.map { it.toString() }.shuffled()
        return Exercise(
            id = "ex_wb_${word.id}",
            type = ExerciseType.WORD_BUILDER,
            promptTr = "\"${word.turkish}\" kelimesinin harflerini sırayla diz!",
            promptEn = "Arrange letters to spell \"${word.english}\"!",
            targetWord = word,
            letterTiles = scrambled
        )
    }

    private fun createSentenceBuilder(sentenceEn: String, sentenceTr: String, words: List<String>, target: WordItem): Exercise {
        return Exercise(
            id = "ex_sb_${target.id}_${sentenceEn.hashCode()}",
            type = ExerciseType.SENTENCE_BUILDER,
            promptTr = "\"$sentenceTr\" cümlesini doğru sırayla oluştur!",
            promptEn = sentenceEn,
            targetWord = target,
            sentenceTokens = words.shuffled()
        )
    }
}
