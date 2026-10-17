package com.example.data.curriculum

import com.example.data.model.GradeLevel

// -------------------------------------------------------------
// 1. COMIC STORY BRIDGE DATA MODEL
// -------------------------------------------------------------
data class ComicPanel(
    val speaker: String,
    val textEn: String,
    val phonetic: String,
    val textTr: String,
    val characterEmoji: String,
    val backgroundEmoji: String
)

data class ComicStoryStage(
    val id: String,
    val grade: GradeLevel,
    val unitId: Int,
    val titleTr: String,
    val titleEn: String,
    val panels: List<ComicPanel>
)

// -------------------------------------------------------------
// 2. SENTENCE LAB (WORD COMBINER) DATA MODEL
// -------------------------------------------------------------
data class SentenceLabStage(
    val id: String,
    val grade: GradeLevel,
    val unitId: Int,
    val titleTr: String,
    val targetSentenceEn: String,
    val targetSentenceTr: String,
    val partA: String, // e.g., "Red"
    val partBEmoji: String, // e.g., "🍎"
    val partB: String, // e.g., "Apple"
    val fullEmoji: String // e.g., "🔴🍎"
)

// -------------------------------------------------------------
// 3. ERROR DETECTIVE / SMART REVIEW DATA MODEL
// -------------------------------------------------------------
data class ReviewWordItem(
    val english: String,
    val phonetic: String,
    val turkish: String,
    val emoji: String,
    val hintTr: String
)

data class SmartReviewStage(
    val id: String,
    val grade: GradeLevel,
    val unitId: Int,
    val titleTr: String,
    val targetWords: List<ReviewWordItem>
)

// -------------------------------------------------------------
// 4. PASSPORT STAMP & VİZE DATA MODEL
// -------------------------------------------------------------
data class PassportQuestion(
    val questionEn: String,
    val questionTr: String,
    val options: List<String>,
    val correctIndex: Int
)

data class PassportStampStage(
    val id: String,
    val grade: GradeLevel,
    val unitId: Int,
    val stampTitleTr: String,
    val stampEmoji: String,
    val questions: List<PassportQuestion>
)

// -------------------------------------------------------------
// 5. REAL-LIFE ROLEPLAY SNAPSHOT DATA MODEL
// -------------------------------------------------------------
data class RoleplaySnapshotStage(
    val id: String,
    val grade: GradeLevel,
    val unitId: Int,
    val scenarioTitleTr: String,
    val scenarioSituationTr: String,
    val phraseToSayEn: String,
    val phonetic: String,
    val phraseToSayTr: String,
    val acceptedKeywords: List<String>,
    val npcEmoji: String
)

// -------------------------------------------------------------
// 6. TRACE & DRAW KINESTHETIC WRITING DATA MODEL
// -------------------------------------------------------------
data class TraceLetterStage(
    val id: String,
    val grade: GradeLevel,
    val unitId: Int,
    val targetWordOrLetter: String,
    val meaningTr: String,
    val emoji: String,
    val strokeGuideTipTr: String
)

// -------------------------------------------------------------
// 7. CULTURE & FUN FACT CAPSULE DATA MODEL
// -------------------------------------------------------------
data class CultureFactStage(
    val id: String,
    val grade: GradeLevel,
    val unitId: Int,
    val titleTr: String,
    val factBodyTr: String,
    val factEnglishPhrase: String,
    val phonetic: String,
    val emojiIllustration: String
)

// -------------------------------------------------------------
// CENTRAL REPOSITORY FOR ALL GRADE-SPECIFIC INTERMEDIATE STAGES
// -------------------------------------------------------------
object IntermediateStagesData {

    fun getComicStoriesForGrade(grade: GradeLevel, unitId: Int): ComicStoryStage {
        return when (grade) {
            GradeLevel.GRADE_1 -> ComicStoryStage(
                id = "comic_g1_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Poko'nun Piknik Macerası 🧺",
                titleEn = "Poko's Picnic Adventure",
                panels = listOf(
                    ComicPanel("Poko", "Look! A red apple on the table!", "luk e red epıl on dı teybıl", "Bak! Masada kırmızı bir elma var!", "🦊", "🧺🍎"),
                    ComicPanel("Puppy", "Yummy! I like sweet apples!", "yami ay layk sviit epılz", "Nefis! Tatlı elmaları çok severim!", "🐶", "🌳☀️"),
                    ComicPanel("Poko", "Here you are, my cute puppy!", "hiir yu ar may kyut papi", "Buyur arkadaşım, afiyet olsun!", "🦊", "🎁✨"),
                    ComicPanel("Puppy", "Thank you, Poko! You are the best!", "tenk yu poko yu ar dı best", "Teşekkür ederim Poko! Harikasın!", "🐶", "🎉💖")
                )
            )
            GradeLevel.GRADE_2 -> ComicStoryStage(
                id = "comic_g2_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Sınıfta Resim Dersi 🎨",
                titleEn = "Drawing in Classroom",
                panels = listOf(
                    ComicPanel("Teacher", "Good morning class! Open your books!", "gud morniñ klaas opın yor buks", "Günaydın çocuklar! Kitaplarınızı açın!", "👩‍🏫", "🏫📚"),
                    ComicPanel("Poko", "I have two blue pencils and a ruler!", "ay hev tuu blu pensılz end e rulır", "Benim iki mavi kalemim ve cetvelim var!", "🦊", "✏️📐"),
                    ComicPanel("Friend", "Let's draw a happy cat and a big sun!", "lets dro e hepi ket end e big san", "Hadi mutlu bir kedi ve büyük bir güneş çizelim!", "🐻", "🎨🐱"),
                    ComicPanel("Teacher", "Great job! Your painting is colorful!", "greyt cab yor peyntiñ iz kalırful", "Harika iş! Resminiz rengarenk oldu!", "👩‍🏫", "⭐🏆")
                )
            )
            GradeLevel.GRADE_3 -> ComicStoryStage(
                id = "comic_g3_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Şehirde Hava Durumu Keşfi 🏙️",
                titleEn = "City & Weather Tour",
                panels = listOf(
                    ComicPanel("Poko", "Look out the window! It is rainy today!", "luk aut dı vindo it iz reyni tudey", "Pencereden dışarı bak! Bugün hava yağmurlu!", "🦊", "🌧️🪟"),
                    ComicPanel("Friend", "Take your yellow umbrella and warm coat!", "teyk yor yelo ambrela end vorm kot", "Sarı şemsiyeni ve sıcak montunu al!", "🐰", "🌂🧥"),
                    ComicPanel("Poko", "Let's go to the toy museum by train!", "lets go tu dı toy myuuziyım bay treyn", "Oyuncak müzesine trenle gidelim!", "🦊", "🚂🏙️"),
                    ComicPanel("Friend", "Yay! The sun is shining now! Look at the rainbow!", "yey dı san iz şayniñ nau luk et dı reynbo", "Güneş açtı! Bak gökkuşağı çıktı!", "🐰", "🌈✨")
                )
            )
            GradeLevel.GRADE_4 -> ComicStoryStage(
                id = "comic_g4_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Geleceğin Bilim Fuarı 🚀",
                titleEn = "Future Science Fair",
                panels = listOf(
                    ComicPanel("Poko", "I want to be an astronaut and explore space!", "ay vont tu bi en estronot end eksplor speys", "Ben astronot olup uzayı keşfetmek istiyorum!", "🦊", "🚀🌌"),
                    ComicPanel("Scientist", "This model shows eight planets orbiting the sun.", "dis madıl şovz eyt plenıts orbitiñ dı san", "Bu model güneşin etrafında dönen sekiz gezegeni gösteriyor.", "👨‍🔬", "🪐☀️"),
                    ComicPanel("Doctor Friend", "I want to be a doctor and help people!", "ay vont tu bi e daktır end help piipıl", "Ben de doktor olup insanlara yardım etmek istiyorum!", "👩‍⚕️", "🩺🏥"),
                    ComicPanel("Poko", "Science and jobs make our world better!", "sayıns end cabz meyk aur vorld betır", "Bilim ve meslekler dünyamızı güzelleştirir!", "🦊", "🌍🌟")
                )
            )
        }
    }

    fun getSentenceLabForGrade(grade: GradeLevel, unitId: Int): List<SentenceLabStage> {
        return when (grade) {
            GradeLevel.GRADE_1 -> listOf(
                SentenceLabStage("slab_g1_1", grade, unitId, "Renk + Meyve Birleştir", "Red Apple", "Kırmızı Elma", "Red", "🍎", "Apple", "🔴🍎"),
                SentenceLabStage("slab_g1_2", grade, unitId, "Boyut + Hayvan Birleştir", "Big Cat", "Büyük Kedi", "Big", "🐱", "Cat", "🐾🐱"),
                SentenceLabStage("slab_g1_3", grade, unitId, "Nezaket Kalıbı Birleştir", "Good morning!", "Günaydın!", "Good", "☀️", "Morning", "🌅☀️"),
                SentenceLabStage("slab_g1_4", grade, unitId, "Renk + Eşya Birleştir", "Yellow Ball", "Sarı Top", "Yellow", "⚽", "Ball", "🟡⚽")
            )
            GradeLevel.GRADE_2 -> listOf(
                SentenceLabStage("slab_g2_1", grade, unitId, "Sahiplik + Aile Birleştir", "My Family", "Benim Ailem", "My", "👨‍👩‍👧", "Family", "❤️👨‍👩‍👧"),
                SentenceLabStage("slab_g2_2", grade, unitId, "Beğeni + İçecek Birleştir", "I like milk", "Süt severim", "I like", "🥛", "Milk", "🥛✨"),
                SentenceLabStage("slab_g2_3", grade, unitId, "Eylem + Beden Birleştir", "Wash hands", "Ellerini yıka", "Wash", "🧼", "Hands", "🙌🧼"),
                SentenceLabStage("slab_g2_4", grade, unitId, "Sayı + Eşya Birleştir", "Two pencils", "İki kurşun kalem", "Two", "✏️", "Pencils", "✌️✏️")
            )
            GradeLevel.GRADE_3 -> listOf(
                SentenceLabStage("slab_g3_1", grade, unitId, "Duygu Cümlesi Birleştir", "He is happy", "O mutlu", "He is", "😄", "Happy", "✨😄"),
                SentenceLabStage("slab_g3_2", grade, unitId, "Ev Bölümü Birleştir", "In the bedroom", "Yatak odasında", "In the", "🛏️", "Bedroom", "🏠🛏️"),
                SentenceLabStage("slab_g3_3", grade, unitId, "Hava Durumu Birleştir", "It is sunny", "Hava güneşli", "It is", "☀️", "Sunny", "🌤️☀️"),
                SentenceLabStage("slab_g3_4", grade, unitId, "Ulaşım Kalıbı Birleştir", "Go by train", "Trenle git", "Go by", "🚂", "Train", "🎫🚂")
            )
            GradeLevel.GRADE_4 -> listOf(
                SentenceLabStage("slab_g4_1", grade, unitId, "Günlük Rutin Birleştir", "I wake up at 7", "Saat 7'de uyanırım", "I wake up", "⏰", "At 7", "🌅⏰"),
                SentenceLabStage("slab_g4_2", grade, unitId, "Meslek Kalıbı Birleştir", "She is a doctor", "O bir doktordur", "She is", "🩺", "A doctor", "👩‍⚕️🩺"),
                SentenceLabStage("slab_g4_3", grade, unitId, "Ülke & Millet Birleştir", "I am from Turkey", "Ben Türkiye'denim", "I am from", "🇹🇷", "Turkey", "🌏🇹🇷"),
                SentenceLabStage("slab_g4_4", grade, unitId, "Mevsim & Kıyafet Birleştir", "Wear a warm jacket", "Sıcak bir ceket giy", "Wear a", "🧥", "Warm jacket", "⛄🧥")
            )
        }
    }

    fun getSmartReviewForGrade(grade: GradeLevel, unitId: Int): SmartReviewStage {
        return when (grade) {
            GradeLevel.GRADE_1 -> SmartReviewStage(
                id = "review_g1_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Poko ile Akıllı 1. Sınıf Tekrarı 🔍",
                targetWords = listOf(
                    ReviewWordItem("Hello", "helo", "Merhaba", "👋", "İpucu: Biriyle karşılaşınca ilk söylediğin söz!"),
                    ReviewWordItem("Cat", "ket", "Kedi", "🐱", "İpucu: Miyav diyen sevimli dostumuz!"),
                    ReviewWordItem("Red", "red", "Kırmızı", "🔴", "İpucu: Elmanın ve çileğin rengi!"),
                    ReviewWordItem("Book", "buk", "Kitap", "📖", "İpucu: Okumayı çok sevdiğimiz sayfalar!")
                )
            )
            GradeLevel.GRADE_2 -> SmartReviewStage(
                id = "review_g2_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Poko ile Akıllı 2. Sınıf Tekrarı 🔍",
                targetWords = listOf(
                    ReviewWordItem("Mother", "madır", "Anne", "👩", "İpucu: Bizi sıcacık kucaklayan ailemizin kalbi!"),
                    ReviewWordItem("Pencil", "pensıl", "Kurşun Kalem", "✏️", "İpucu: Defterimize yazı yazdığımız araç!"),
                    ReviewWordItem("Eyes", "ayz", "Gözler", "👀", "İpucu: Dünyanın güzelliklerini gördüğümüz organımız!"),
                    ReviewWordItem("Milk", "milk", "Süt", "🥛", "İpucu: Kemiklerimizi güçlendiren beyaz içecek!")
                )
            )
            GradeLevel.GRADE_3 -> SmartReviewStage(
                id = "review_g3_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Poko ile Akıllı 3. Sınıf Tekrarı 🔍",
                targetWords = listOf(
                    ReviewWordItem("Kitchen", "kiçın", "Mutfak", "🍳", "İpucu: Lezzetli yemeklerin piştiği oda!"),
                    ReviewWordItem("Rainy", "reyni", "Yağmurlu", "🌧️", "İpucu: Şemsiyemizi açtığımız hava!"),
                    ReviewWordItem("Bus", "bas", "Otobüs", "🚌", "İpucu: Şehirde yolcuları taşıyan büyük araç!"),
                    ReviewWordItem("Happy", "hepi", "Mutlu", "😄", "İpucu: Yüzümüzde gülümseme açtıran güzel duygu!")
                )
            )
            GradeLevel.GRADE_4 -> SmartReviewStage(
                id = "review_g4_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Poko ile Akıllı 4. Sınıf Tekrarı 🔍",
                targetWords = listOf(
                    ReviewWordItem("Doctor", "daktır", "Doktor", "🩺", "İpucu: Hastalandığımızda bizi iyileştiren meslek!"),
                    ReviewWordItem("Winter", "vin-tır", "Kış Mevsimi", "⛄", "İpucu: Kar yağan ve kardan adam yaptığımız mevsim!"),
                    ReviewWordItem("Breakfast", "brekfıst", "Kahvaltı", "🍳", "İpucu: Güne enerjik başladığımız sabah öğünü!"),
                    ReviewWordItem("Science", "sayıns", "Fen / Bilim", "🔬", "İpucu: Deneyler yapıp evreni keşfettiğimiz alan!")
                )
            )
        }
    }

    fun getPassportStampForGrade(grade: GradeLevel, unitId: Int): PassportStampStage {
        return when (grade) {
            GradeLevel.GRADE_1 -> PassportStampStage(
                id = "passport_g1_u$unitId",
                grade = grade,
                unitId = unitId,
                stampTitleTr = "1. Sınıf Minik Kaşif Vizesi ⭐",
                stampEmoji = "🎖️",
                questions = listOf(
                    PassportQuestion("What color is an apple?", "Elma hangi renktir?", listOf("Red 🍎", "Blue 🔵", "Black ⚫"), 0),
                    PassportQuestion("How do you say 'Merhaba'?", "'Merhaba' İngilizce nedir?", listOf("Bye 👋", "Hello 👋", "Sleep 😴"), 1)
                )
            )
            GradeLevel.GRADE_2 -> PassportStampStage(
                id = "passport_g2_u$unitId",
                grade = grade,
                unitId = unitId,
                stampTitleTr = "2. Sınıf Okul Yıldızı Vizesi 🏆",
                stampEmoji = "🌟",
                questions = listOf(
                    PassportQuestion("What do you write with?", "Neyle yazı yazarsın?", listOf("Pencil ✏️", "Fork 🍴", "Shoes 👟"), 0),
                    PassportQuestion("Which one is your family?", "Hangisi aile üyesidir?", listOf("Tree 🌳", "Sister 👧", "Desk 🪑"), 1)
                )
            )
            GradeLevel.GRADE_3 -> PassportStampStage(
                id = "passport_g3_u$unitId",
                grade = grade,
                unitId = unitId,
                stampTitleTr = "3. Sınıf Şehir Rehberi Vizesi 🗺️",
                stampEmoji = "🧭",
                questions = listOf(
                    PassportQuestion("Where do you sleep?", "Nerede uyursun?", listOf("Kitchen 🍳", "Bedroom 🛏️", "Garage 🚗"), 1),
                    PassportQuestion("What do you take when it rains?", "Yağmur yağınca ne alırsın?", listOf("Umbrella 🌂", "Ball ⚽", "T-shirt 👕"), 0)
                )
            )
            GradeLevel.GRADE_4 -> PassportStampStage(
                id = "passport_g4_u$unitId",
                grade = grade,
                unitId = unitId,
                stampTitleTr = "4. Sınıf Dünya Vatandaşı Vizesi 👑",
                stampEmoji = "👑",
                questions = listOf(
                    PassportQuestion("Who works in a hospital?", "Hastanede kim çalışır?", listOf("Pilot 👨‍✈️", "Doctor 👩‍⚕️", "Chef 👨‍🍳"), 1),
                    PassportQuestion("Which one is a morning routine?", "Hangisi bir sabah rutini?", listOf("Wash face 🚿", "Go to bed 🛌", "Watch stars 🌌"), 0)
                )
            )
        }
    }

    fun getRoleplaySnapshotForGrade(grade: GradeLevel, unitId: Int): RoleplaySnapshotStage {
        return when (grade) {
            GradeLevel.GRADE_1 -> RoleplaySnapshotStage(
                id = "roleplay_g1_u$unitId",
                grade = grade,
                unitId = unitId,
                scenarioTitleTr = "Sınıfta Tanışma Molası 👋",
                scenarioSituationTr = "Yeni bir arkadaşın yanına geldi ve gülümsedi. Ona kendini tanıt!",
                phraseToSayEn = "Hello! My name is Ali!",
                phonetic = "helo may neym iz ali",
                phraseToSayTr = "Merhaba! Benim adım Ali!",
                acceptedKeywords = listOf("hello", "name", "ali", "hi", "i am"),
                npcEmoji = "👦"
            )
            GradeLevel.GRADE_2 -> RoleplaySnapshotStage(
                id = "roleplay_g2_u$unitId",
                grade = grade,
                unitId = unitId,
                scenarioTitleTr = "Kırtasiyede Kalem İsteme ✏️",
                scenarioSituationTr = "Kırtasiyedesin ve bir mavi kalem istiyorsun:",
                phraseToSayEn = "One blue pencil, please!",
                phonetic = "van blu pensıl pliiz",
                phraseToSayTr = "Bir mavi kurşun kalem, lütfen!",
                acceptedKeywords = listOf("pencil", "blue", "please", "one"),
                npcEmoji = "🏪"
            )
            GradeLevel.GRADE_3 -> RoleplaySnapshotStage(
                id = "roleplay_g3_u$unitId",
                grade = grade,
                unitId = unitId,
                scenarioTitleTr = "Kafede Meyve Suyu Siparişi 🧃",
                scenarioSituationTr = "Park kafesinde garson yanına geldi:",
                phraseToSayEn = "Can I have orange juice?",
                phonetic = "ken ay hev orınc cuus",
                phraseToSayTr = "Portakal suyu alabilir miyim?",
                acceptedKeywords = listOf("orange", "juice", "can i have", "please"),
                npcEmoji = "🥤"
            )
            GradeLevel.GRADE_4 -> RoleplaySnapshotStage(
                id = "roleplay_g4_u$unitId",
                grade = grade,
                unitId = unitId,
                scenarioTitleTr = "Uluslararası Arkadaş Kulübü 🌍",
                scenarioSituationTr = "Yabancı bir arkadaşın nereden geldiğini soruyor:",
                phraseToSayEn = "I am from Turkey! Nice to meet you!",
                phonetic = "ay em fram törkiy nays tu miit yu",
                phraseToSayTr = "Ben Türkiye'denim! Tanıştığıma memnun oldum!",
                acceptedKeywords = listOf("turkey", "i am", "from", "nice", "meet"),
                npcEmoji = "🌏"
            )
        }
    }

    fun getTraceLettersForGrade(grade: GradeLevel, unitId: Int): List<TraceLetterStage> {
        return when (grade) {
            GradeLevel.GRADE_1 -> listOf(
                TraceLetterStage("trace_g1_1", grade, unitId, "A", "Apple (Elma)", "🍎", "Parmağını yukarıdan aşağıya iki çizgi ve ortadan bir köprü yaparak kaydır!"),
                TraceLetterStage("trace_g1_2", grade, unitId, "C", "Cat (Kedi)", "🐱", "Parmağınla kocaman bir yarım ay çemberi çiz!"),
                TraceLetterStage("trace_g1_3", grade, unitId, "D", "Dog (Köpek)", "🐶", "Düz bir çizgi ve yanına koca bir göbek çiz!"),
                TraceLetterStage("trace_g1_4", grade, unitId, "S", "Sun (Güneş)", "☀️", "Yılan gibi kıvrılan tatlı bir S harfi çiz!")
            )
            GradeLevel.GRADE_2 -> listOf(
                TraceLetterStage("trace_g2_1", grade, unitId, "BOOK", "Kitap", "📖", "Sırayla B - O - O - K harflerini parmağınla çiz!"),
                TraceLetterStage("trace_g2_2", grade, unitId, "DESK", "Sıra / Masa", "🪑", "Sırayla D - E - S - K harflerini tamamla!"),
                TraceLetterStage("trace_g2_3", grade, unitId, "BLUE", "Mavi", "🔵", "Mavi boya fırçasıyla B - L - U - E harflerini parlat!"),
                TraceLetterStage("trace_g2_4", grade, unitId, "FISH", "Balık", "🐟", "F - I - S - H harflerini suda yüzdürerek çiz!")
            )
            GradeLevel.GRADE_3 -> listOf(
                TraceLetterStage("trace_g3_1", grade, unitId, "HOUSE", "Ev", "🏠", "H - O - U - S - E harflerini çatısıyla tamamla!"),
                TraceLetterStage("trace_g3_2", grade, unitId, "TRAIN", "Tren", "🚂", "T - R - A - I - N vagon harflerini çiz!"),
                TraceLetterStage("trace_g3_3", grade, unitId, "CLOUD", "Bulut", "☁️", "C - L - O - U - D harflerini gökyüzünde oluştur!"),
                TraceLetterStage("trace_g3_4", grade, unitId, "SMILE", "Gülümse", "😊", "S - M - I - L - E harflerini neşeyle çiz!")
            )
            GradeLevel.GRADE_4 -> listOf(
                TraceLetterStage("trace_g4_1", grade, unitId, "DOCTOR", "Doktor", "🩺", "D - O - C - T - O - R harflerini parmakla çiz!"),
                TraceLetterStage("trace_g4_2", grade, unitId, "WINTER", "Kış Mevsimi", "⛄", "W - I - N - T - E - R karlı harflerini çiz!"),
                TraceLetterStage("trace_g4_3", grade, unitId, "PLANET", "Gezegen", "🪐", "P - L - A - N - E - T uzay harflerini çiz!"),
                TraceLetterStage("trace_g4_4", grade, unitId, "CHEESE", "Peynir", "🧀", "C - H - E - E - S - E lezzetli harflerini tamamla!")
            )
        }
    }

    fun getCultureFactForGrade(grade: GradeLevel, unitId: Int): CultureFactStage {
        return when (grade) {
            GradeLevel.GRADE_1 -> CultureFactStage(
                id = "culture_g1_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Kırmızı İki Katlı Otobüsler! 🚌🇬🇧",
                factBodyTr = "İngiltere'nin başkenti Londra'da otobüsler iki katlıdır ve parlak kırmızı renktedir! Çocuklar en üst kata çıkıp yolu izlemeye bayılırlar.",
                factEnglishPhrase = "Red Double Decker Bus",
                phonetic = "red dabıl dekır bas",
                emojiIllustration = "🇬🇧🚌💂"
            )
            GradeLevel.GRADE_2 -> CultureFactStage(
                id = "culture_g2_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Renkli Yağmur Çizmeleri (Wellies) 👢🌧️",
                factBodyTr = "İngiltere'de çok sık yağmur yağdığı için tüm çocukların rengarenk yağmur çizmeleri vardır. İngilizler bu çizmelere 'Wellies' derler ve su birikintilerinde zıplamayı çok severler!",
                factEnglishPhrase = "Jumping in the rain!",
                phonetic = "campiñ in dı reyn",
                emojiIllustration = "🌧️👢🌈"
            )
            GradeLevel.GRADE_3 -> CultureFactStage(
                id = "culture_g3_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Tarihi Big Ben Saat Kulesi 🕰️🇬🇧",
                factBodyTr = "Londra'daki dev saat kulesi Big Ben tam 160 yaşındadır! İçindeki büyük çan o kadar ağırdır ki çaldığında tüm nehir kıyısı yankılanır.",
                factEnglishPhrase = "Listen to the Big Ben bell!",
                phonetic = "lisın tu dı big ben bel",
                emojiIllustration = "🕰️🏛️🔔"
            )
            GradeLevel.GRADE_4 -> CultureFactStage(
                id = "culture_g4_u$unitId",
                grade = grade,
                unitId = unitId,
                titleTr = "Meşhur Saat 5 Çayı (Afternoon Tea) ☕🧁",
                factBodyTr = "İngiltere'de her gün saat 5 civarında aileler ve arkadaşlar bir araya gelip çay içerler. Çayın yanında minik kekler ve çıtır kurabiyeler ikram edilir!",
                factEnglishPhrase = "Time for tea and cake!",
                phonetic = "taym for tii end keyk",
                emojiIllustration = "☕🧁🫖"
            )
        }
    }
}
