package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.audio.AudioService
import com.example.audio.SpeechRecognitionService
import com.example.audio.SpeechState
import com.example.ui.components.PokoMascot
import com.example.ui.components.PokoMood
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.CandyRed
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.PlayfulPink
import com.example.ui.theme.PlayfulPurple
import com.example.ui.theme.SunGold
import com.example.ui.theme.SunOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val phoneticLookupMap = mapOf(
    "i" to "ay", "am" to "em", "my" to "may", "name" to "neym", "is" to "iz",
    "hello" to "helo", "hi" to "hay", "good" to "gud", "morning" to "morniñ",
    "night" to "nayt", "sunshine" to "sanşayn", "sun" to "san", "six" to "siks",
    "seven" to "sevın", "five" to "fayv", "eight" to "eyt", "four" to "for",
    "three" to "tri", "two" to "tu", "one" to "van", "ten" to "ten",
    "meow" to "miyav", "woof" to "vuf", "bark" to "bark", "roar" to "roor",
    "cat" to "ket", "cats" to "kets", "dog" to "dog", "dogs" to "dogz",
    "puppy" to "papi", "lion" to "layın", "bird" to "börd", "fish" to "fiş",
    "monkey" to "manki", "rabbit" to "rebit", "duck" to "dak", "elephant" to "elıfınt",
    "dinosaur" to "daynızor", "tiger" to "taygır", "bear" to "beır", "cow" to "kav",
    "horse" to "hors", "sheep" to "şiip", "frog" to "frog",
    "red" to "red", "blue" to "blu", "yellow" to "yelov", "green" to "griin",
    "pink" to "pink", "purple" to "pörpıl", "orange" to "orınc", "black" to "blek",
    "white" to "vayt", "brown" to "bravn",
    "apple" to "epıl", "banana" to "bınana", "pizza" to "pitsa", "milk" to "milk",
    "water" to "votır", "cake" to "keyk", "ice" to "ays", "cream" to "kriim",
    "cookie" to "kuki", "cheese" to "çiiz", "bread" to "bred", "egg" to "eg",
    "yes" to "yes", "no" to "no", "please" to "pliiz", "thank" to "tenk", "you" to "yu",
    "here" to "hiır", "are" to "ar", "welcome" to "velkım", "bye" to "bay",
    "jump" to "camp", "run" to "ran", "walk" to "vok", "dance" to "dens",
    "sing" to "siñ", "clap" to "klep", "hands" to "hendz", "fly" to "flay",
    "sleep" to "sliip", "eat" to "iit", "drink" to "drink", "play" to "pley",
    "happy" to "hepi", "sad" to "sed", "brave" to "breyv", "strong" to "stroñ",
    "fast" to "fast", "slow" to "slov", "big" to "big", "small" to "smol",
    "hero" to "hiro", "super" to "supır", "jedi" to "ceday", "star" to "star",
    "car" to "kar", "ball" to "bol", "doll" to "dol", "robot" to "robıt",
    "book" to "buk", "pencil" to "pensıl", "desk" to "desk", "chair" to "çeır",
    "school" to "skuul", "teacher" to "tiiçır", "friend" to "frend", "family" to "femıli",
    "mom" to "mam", "dad" to "ded", "brother" to "bradır", "sister" to "sistır",
    "baby" to "beybi", "love" to "lav", "like" to "layk", "can" to "ken",
    "see" to "sii", "look" to "luk", "have" to "hev", "want" to "vant",
    "cold" to "kold", "hot" to "hat", "warm" to "vorm", "sunny" to "sani",
    "rainy" to "reyni", "windy" to "vindi", "snowy" to "snovi",
    "head" to "hed", "shoulders" to "şoldırz", "knees" to "niiz", "toes" to "toz",
    "eyes" to "ayz", "ears" to "iırz", "mouth" to "mavt", "nose" to "noz"
)

fun getPhoneticReading(english: String, explicitPhonetic: String = ""): String {
    if (explicitPhonetic.isNotBlank()) return explicitPhonetic
    val clean = english.replace(Regex("[^a-zA-Z0-9' ]"), "").trim()
    val words = clean.split(Regex("\\s+"))
    val phoneticWords = words.map { w ->
        val lower = w.lowercase().replace("'", "")
        phoneticLookupMap[lower] ?: lower
    }
    return phoneticWords.joinToString(" ")
}

data class AnswerChoice(
    val english: String,
    val turkish: String,
    val emoji: String = "",
    val phonetic: String = ""
) {
    val displayPhonetic: String
        get() = getPhoneticReading(english, phonetic)
}

data class BuddyDialogueTopic(
    val id: String,
    val categoryId: String = "ALL",
    val pokoEnglish: String,
    val pokoTurkish: String,
    val expectedKeywords: List<String>,
    val quickAnswerSuggestions: List<AnswerChoice>,
    val praiseResponse: String,
    val praiseTurkish: String,
    val reactionEmoji: String
)

enum class BuddyTopicCategory(val title: String, val emoji: String, val id: String) {
    ALL("Rastgele Sürpriz", "🎲", "ALL"),
    GREETING("Selamlaşma & İsim", "👋", "GREETING"),
    ANIMALS("Sevimli Hayvanlar", "🐾", "ANIMALS"),
    ACTIONS("Beden & Hareket", "🤸", "ACTIONS"),
    FOOD("Yiyecek & Meyve", "🍕", "FOOD"),
    COLORS_TOYS("Renkler & Oyuncak", "🎨", "COLORS_TOYS"),
    DAILY_LIFE("Günlük Hayat & Aile", "☀️", "DAILY_LIFE"),
    SUPERHERO("Süper Güçler & Duygu", "🦸", "SUPERHERO")
}

// Interactive Roleplay Adventure Step
data class RoleplayStep(
    val pokoSpeechEn: String,
    val pokoSpeechTr: String,
    val expectedKeywords: List<String>,
    val choices: List<AnswerChoice>,
    val visualItemEmoji: String,
    val outcomePraiseEn: String,
    val outcomePraiseTr: String
)

data class RoleplayScenario(
    val id: String,
    val titleTr: String,
    val titleEn: String,
    val iconEmoji: String,
    val badgeName: String,
    val steps: List<RoleplayStep>
)

enum class BuddyTalkTab {
    CHAT,
    ROLEPLAY
}

@Composable
fun PokoBuddyTalkScreen(
    audioService: AudioService,
    speechService: SpeechRecognitionService,
    userCoins: Int,
    equippedCostumeId: String,
    onRewardCoins: (Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val speechState by speechService.speechState.collectAsState()

    var activeTab by remember { mutableStateOf(BuddyTalkTab.CHAT) }
    var selectedCategory by remember { mutableStateOf(BuddyTopicCategory.ALL) }

    // ----------------------------------------------------
    // COMPREHENSIVE 1ST-GRADE DIALOGUE POOL (35+ Child-friendly Questions)
    // ----------------------------------------------------
    val allDialogueTopics = remember {
        listOf(
            // --- GREETING & NAME ---
            BuddyDialogueTopic(
                id = "greeting_name",
                categoryId = "GREETING",
                pokoEnglish = "Hello my friend! What is your name?",
                pokoTurkish = "Merhaba arkadaşım! Senin adın ne?",
                expectedKeywords = listOf("name", "i am", "hello", "hi", "ali", "can", "efe", "elif", "ayse", "ahmet", "poko"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("I am Ali 👦", "Ben Ali'yim", "👦"),
                    AnswerChoice("My name is Efe ⭐", "Benim adım Efe", "⭐"),
                    AnswerChoice("Hello Poko! 👋", "Merhaba Poko!", "👋")
                ),
                praiseResponse = "Nice to meet you! You have a wonderful name!",
                praiseTurkish = "Tanıştığıma çok memnun oldum! Harika bir ismin var! 🌟",
                reactionEmoji = "👋"
            ),
            BuddyDialogueTopic(
                id = "good_morning_smile",
                categoryId = "GREETING",
                pokoEnglish = "Say 'Good morning' with a big smile!",
                pokoTurkish = "Kocaman bir gülümsemeyle 'Good morning' de!",
                expectedKeywords = listOf("good morning", "morning", "good", "hello", "hi"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Good morning! ☀️", "Günaydın!", "☀️"),
                    AnswerChoice("Good morning Poko! 🌅", "Günaydın Poko!", "🌅"),
                    AnswerChoice("Hello sunshine! ✨", "Merhaba güneş!", "✨")
                ),
                praiseResponse = "Good morning sunshine! Have a joyful day!",
                praiseTurkish = "Günaydın canım arkadaşım! Neşe dolu bir gün olsun! ☀️",
                reactionEmoji = "☀️"
            ),
            BuddyDialogueTopic(
                id = "age_how_old",
                categoryId = "GREETING",
                pokoEnglish = "How old are you? Six or seven?",
                pokoTurkish = "Kaç yaşındasın? 6 mı 7 mi?",
                expectedKeywords = listOf("six", "seven", "five", "eight", "6", "7", "5"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("I am six! (6) 🎂", "Altı yaşındayım!", "🎂"),
                    AnswerChoice("I am seven! (7) 🎉", "Yedi yaşındayım!", "🎉"),
                    AnswerChoice("I am growing! 🌟", "Hızla büyüyorum!", "🌟")
                ),
                praiseResponse = "You are growing so fast and learning so well!",
                praiseTurkish = "Çok hızlı büyüyorsun ve harika öğreniyorsun! 🎂",
                reactionEmoji = "🎂"
            ),

            // --- ANIMALS & SOUNDS ---
            BuddyDialogueTopic(
                id = "cat_sound",
                categoryId = "ANIMALS",
                pokoEnglish = "What does a cat say? Meow or Woof?",
                pokoTurkish = "Kedi nasıl ses çıkarır? Miyav mı hav mı?",
                expectedKeywords = listOf("meow", "cat", "woof", "yes"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Meow meow! 🐱", "Miyav miyav!", "🐱"),
                    AnswerChoice("A cat says meow! 🐾", "Kedi miyav der!", "🐾"),
                    AnswerChoice("Cute cat! 😻", "Sevimli kedi!", "😻")
                ),
                praiseResponse = "Meow! Yes! Cats are super soft and sweet!",
                praiseTurkish = "Miyav! Doğru! Kediler çok yumuşak ve tatlıdır! 🐱",
                reactionEmoji = "🐱"
            ),
            BuddyDialogueTopic(
                id = "dog_bark",
                categoryId = "ANIMALS",
                pokoEnglish = "Can you bark like a puppy? Woof woof!",
                pokoTurkish = "Köpekçik gibi havlayabilir misin? Hav hav!",
                expectedKeywords = listOf("woof", "dog", "bark", "puppy", "yes"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Woof woof! 🐶", "Hav hav!", "🐶"),
                    AnswerChoice("I love dogs! 🐾", "Köpekleri severim!", "🐾"),
                    AnswerChoice("Good puppy! 🦴", "Aferin köpekçik!", "🦴")
                ),
                praiseResponse = "Woof woof! You are the sweetest puppy friend!",
                praiseTurkish = "Hav hav! Sen en tatlı köpek dostusun! 🐶",
                reactionEmoji = "🐶"
            ),
            BuddyDialogueTopic(
                id = "lion_roar_strong",
                categoryId = "ANIMALS",
                pokoEnglish = "Can you roar like a big lion? Roar!",
                pokoTurkish = "Büyük bir aslan gibi kükreyebilir misin? Roar!",
                expectedKeywords = listOf("roar", "lion", "big", "strong", "yes"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Roar! 🦁", "Rooaaar!", "🦁"),
                    AnswerChoice("I am a brave lion! 💪", "Cesur bir aslanım!", "💪"),
                    AnswerChoice("King lion! 👑", "Kral aslan!", "👑")
                ),
                praiseResponse = "ROAAAR! Wow! You are as brave as the King of the Jungle!",
                praiseTurkish = "ROAAAR! Vay canına! Ormanlar kralı kadar cesursun! 🦁",
                reactionEmoji = "🦁"
            ),
            BuddyDialogueTopic(
                id = "bird_fly_sky",
                categoryId = "ANIMALS",
                pokoEnglish = "Can birds fly high in the sky? Yes or no?",
                pokoTurkish = "Kuşlar gökyüzünde uçar mı? Evet mi hayır mı?",
                expectedKeywords = listOf("yes", "fly", "bird", "sky"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Yes, they fly! 🐦", "Evet, uçarlar!", "🐦"),
                    AnswerChoice("High in the sky! ☁️", "Gökyüzünde yüksekte!", "☁️"),
                    AnswerChoice("Fly bird fly! 🕊️", "Uç kuşum uç!", "🕊️")
                ),
                praiseResponse = "Flap flap! Birds fly with colorful wings!",
                praiseTurkish = "Pır pır! Kuşlar rengarenk kanatlarıyla uçar! 🐦",
                reactionEmoji = "🐦"
            ),
            BuddyDialogueTopic(
                id = "frog_sound",
                categoryId = "ANIMALS",
                pokoEnglish = "What sound does a green frog make? Ribbit!",
                pokoTurkish = "Yeşil kurbağa nasıl ses çıkarır? Vırak!",
                expectedKeywords = listOf("ribbit", "frog", "jump", "green"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Ribbit ribbit! 🐸", "Vırak vırak!", "🐸"),
                    AnswerChoice("Green frog! 🍀", "Yeşil kurbağa!", "🍀"),
                    AnswerChoice("Jump frog! 💧", "Zıpla kurbağa!", "💧")
                ),
                praiseResponse = "Ribbit! You sound just like a cheerful little frog!",
                praiseTurkish = "Vırak! Tıpkı neşeli minik bir kurbağa gibisin! 🐸",
                reactionEmoji = "🐸"
            ),

            // --- ACTIONS & BODY PARTS ---
            BuddyDialogueTopic(
                id = "clap_hands_action",
                categoryId = "ACTIONS",
                pokoEnglish = "Can you clap your hands? Clap clap!",
                pokoTurkish = "Ellerini çırpabilir misin? Çap çap!",
                expectedKeywords = listOf("clap", "hands", "yes", "can", "clap clap"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Yes, I can clap! 👏", "Evet, çırparım!", "👏"),
                    AnswerChoice("Clap clap! 👏", "Çap çap!", "👏"),
                    AnswerChoice("Look at me! ✨", "Bana bak!", "✨")
                ),
                praiseResponse = "Clap clap clap! That is music to my ears!",
                praiseTurkish = "Şak şak şak! Alkışların kulağa müzik gibi geliyor! 👏",
                reactionEmoji = "👏"
            ),
            BuddyDialogueTopic(
                id = "touch_nose_action",
                categoryId = "ACTIONS",
                pokoEnglish = "Touch your nose! Beep beep!",
                pokoTurkish = "Burnuna dokun! Düt düt!",
                expectedKeywords = listOf("nose", "touch", "beep", "my nose", "here"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("My nose! 👃", "Benim burnum!", "👃"),
                    AnswerChoice("Beep beep! 🚗", "Düt düt!", "🚗"),
                    AnswerChoice("Here it is! ✨", "İşte burada!", "✨")
                ),
                praiseResponse = "Beep beep! You found your cute little nose!",
                praiseTurkish = "Düt düt! Sevimli burnunu hemen buldun! 👃",
                reactionEmoji = "👃"
            ),
            BuddyDialogueTopic(
                id = "jump_rabbit_action",
                categoryId = "ACTIONS",
                pokoEnglish = "Can you jump high like a rabbit? Boing boing!",
                pokoTurkish = "Tavşan gibi yükseğe zıplayabilir misin? Zıp zıp!",
                expectedKeywords = listOf("jump", "rabbit", "yes", "boing", "high"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Jump jump! 🐰", "Zıp zıp!", "🐰"),
                    AnswerChoice("I can jump high! 🦘", "Yükseğe zıplarım!", "🦘"),
                    AnswerChoice("Boing boing! ⚡", "Hop hop!", "⚡")
                ),
                praiseResponse = "Boing! You jump so high like a superhero!",
                praiseTurkish = "Hop! Bir süper kahraman gibi yükseğe zıplıyorsun! 🐰",
                reactionEmoji = "🐰"
            ),
            BuddyDialogueTopic(
                id = "dance_happy_action",
                categoryId = "ACTIONS",
                pokoEnglish = "If you are happy, say: 'Let's dance!'",
                pokoTurkish = "Eğer mutluysan 'Let's dance' (Dans edelim) de!",
                expectedKeywords = listOf("dance", "let's dance", "happy", "yes"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Let's dance! 💃", "Hadi dans edelim!", "💃"),
                    AnswerChoice("I love dancing! 🕺", "Dansı çok severim!", "🕺"),
                    AnswerChoice("Play music! 🎵", "Müziği aç!", "🎵")
                ),
                praiseResponse = "Music on! Dance and shake your feet with joy!",
                praiseTurkish = "Müzik başladı! Sevinçle dans et ve neşelen! 💃",
                reactionEmoji = "💃"
            ),
            BuddyDialogueTopic(
                id = "count_fingers_action",
                categoryId = "ACTIONS",
                pokoEnglish = "How many fingers on one hand? Five or ten?",
                pokoTurkish = "Bir elde kaç parmak vardır? Beş mi on mu?",
                expectedKeywords = listOf("five", "5", "fingers", "hand"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Five! (5) ✋", "Beş! (5)", "✋"),
                    AnswerChoice("Five fingers! 🖐️", "Beş parmak!", "🖐️"),
                    AnswerChoice("One, two, three, four, five! 🔢", "1, 2, 3, 4, 5!", "🔢")
                ),
                praiseResponse = "Bingo! High five with all five fingers! ✋",
                praiseTurkish = "Tebrikler! Beş parmağınla beşlik çak! ✋",
                reactionEmoji = "✋"
            ),

            // --- FOOD & FRUITS ---
            BuddyDialogueTopic(
                id = "ice_cream_sweet",
                categoryId = "FOOD",
                pokoEnglish = "Do you like sweet ice cream? Yes or no?",
                pokoTurkish = "Tatlı dondurmayı sever misin? Evet mi hayır mı?",
                expectedKeywords = listOf("yes", "ice cream", "cream", "like", "love", "no"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Yes, I love ice cream! 🍦", "Evet, dondurmayı çok severim!", "🍦"),
                    AnswerChoice("Yummy ice cream! 😋", "Nefis dondurma!", "😋"),
                    AnswerChoice("Chocolate please! 🍫", "Çikolatalı lütfen!", "🍫")
                ),
                praiseResponse = "Yummy! Ice cream is cold, sweet, and delicious!",
                praiseTurkish = "Nefis! Dondurma soğuk, tatlı ve çok lezzetlidir! 🍦",
                reactionEmoji = "🍦"
            ),
            BuddyDialogueTopic(
                id = "pizza_or_banana",
                categoryId = "FOOD",
                pokoEnglish = "Do you want yummy pizza or banana?",
                pokoTurkish = "Nefis pizza mı muz mu istersin?",
                expectedKeywords = listOf("pizza", "banana", "both", "yes", "eat"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Pizza please! 🍕", "Pizza lütfen!", "🍕"),
                    AnswerChoice("Banana please! 🍌", "Muz lütfen!", "🍌"),
                    AnswerChoice("Both are delicious! 😋", "İkisi de nefis!", "😋")
                ),
                praiseResponse = "Delicious choice! Healthy food makes you grow strong!",
                praiseTurkish = "Harika seçim! Sağlıklı yemekler seni güçlü büyütür! 🍕",
                reactionEmoji = "🍕"
            ),
            BuddyDialogueTopic(
                id = "drink_water_healthy",
                categoryId = "FOOD",
                pokoEnglish = "Are you thirsty? Let's drink fresh water!",
                pokoTurkish = "Susadın mı? Hadi taze su içelim!",
                expectedKeywords = listOf("water", "drink", "yes", "thirsty", "milk"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Water please! 💧", "Su lütfen!", "💧"),
                    AnswerChoice("Cold water! 🧊", "Soğuk su!", "🧊"),
                    AnswerChoice("I drink water! 🥛", "Su içerim!", "🥛")
                ),
                praiseResponse = "Glug glug! Water keeps your brain sharp and bright!",
                praiseTurkish = "Lıkır lıkır! Su zihnini pırıl pırıl ve enerjik tutar! 💧",
                reactionEmoji = "💧"
            ),
            BuddyDialogueTopic(
                id = "apple_red_fruit",
                categoryId = "FOOD",
                pokoEnglish = "What color is a crunchy apple? Red or blue?",
                pokoTurkish = "Kıtır kıtır elma hangi renktir? Kırmızı mı mavi mi?",
                expectedKeywords = listOf("red", "apple", "green", "yes"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Red! 🍎", "Kırmızı!", "🍎"),
                    AnswerChoice("Red apple! ❤️", "Kırmızı elma!", "❤️"),
                    AnswerChoice("Sweet red! 🍏", "Tatlı kırmızı!", "🍏")
                ),
                praiseResponse = "Crunch! Red and green apples are full of vitamins!",
                praiseTurkish = "Kıtır kıtır! Kırmızı ve yeşil elmalar vitamin doludur! 🍎",
                reactionEmoji = "🍎"
            ),

            // --- COLORS & TOYS ---
            BuddyDialogueTopic(
                id = "sun_color_yellow",
                categoryId = "COLORS_TOYS",
                pokoEnglish = "What color is the sun? Yellow or green?",
                pokoTurkish = "Güneş hangi renktir? Sarı mı yeşil mi?",
                expectedKeywords = listOf("yellow", "sun", "gold", "bright"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Yellow! ☀️", "Sarı!", "☀️"),
                    AnswerChoice("It is yellow! 💛", "O sarıdır!", "💛"),
                    AnswerChoice("Bright yellow! 🌞", "Parlak sarı!", "🌞")
                ),
                praiseResponse = "Yes! The sun is warm and shining yellow in the sky!",
                praiseTurkish = "Evet! Güneş gökyüzünde sıcacık ve sapsarı parıldar! ☀️",
                reactionEmoji = "☀️"
            ),
            BuddyDialogueTopic(
                id = "favorite_toy_car_ball",
                categoryId = "COLORS_TOYS",
                pokoEnglish = "What is your favorite toy? A fast car or a ball?",
                pokoTurkish = "En sevdiğin oyuncak hangisi? Hızlı araba mı top mu?",
                expectedKeywords = listOf("car", "ball", "toy", "teddy", "doll", "robot"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("A fast car! 🚗", "Hızlı araba!", "🚗"),
                    AnswerChoice("A bouncy ball! ⚽", "Zıplayan top!", "⚽"),
                    AnswerChoice("My teddy bear! 🧸", "Oyuncak ayım!", "🧸")
                ),
                praiseResponse = "Vroom! Toys are so much fun to play and share!",
                praiseTurkish = "Vın vın! Oyuncaklarla oynamak ve paylaşmak çok eğlenceli! 🚗",
                reactionEmoji = "🚗"
            ),
            BuddyDialogueTopic(
                id = "car_sound_vroom",
                categoryId = "COLORS_TOYS",
                pokoEnglish = "Can you make a car sound? Vroom vroom!",
                pokoTurkish = "Araba sesi çıkarabilir misin? Vın vın!",
                expectedKeywords = listOf("vroom", "beep", "car", "fast"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Vroom vroom! 🏎️", "Vın vın!", "🏎️"),
                    AnswerChoice("Beep beep! 🚗", "Düt düt!", "🚗"),
                    AnswerChoice("Fast race car! 🏁", "Hızlı yarış arabası!", "🏁")
                ),
                praiseResponse = "VROOOM! You are a super fast race driver!",
                praiseTurkish = "VIIIN! Sen süper hızlı bir yarış pilotusun! 🏎️",
                reactionEmoji = "🏎️"
            ),
            BuddyDialogueTopic(
                id = "color_blue_sky",
                categoryId = "COLORS_TOYS",
                pokoEnglish = "Look at the sky! Is it blue or red?",
                pokoTurkish = "Gökyüzüne bak! Mavi mi kırmızı mı?",
                expectedKeywords = listOf("blue", "sky", "yes"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Blue! 💙", "Mavi!", "💙"),
                    AnswerChoice("Blue sky! ☁️", "Mavi gökyüzü!", "☁️"),
                    AnswerChoice("Beautiful blue! 🌌", "Güzel mavi!", "🌌")
                ),
                praiseResponse = "Spot on! The sky is wide and blue!",
                praiseTurkish = "Tam isabet! Gökyüzü uçsuz bucaksız ve masmavidir! 💙",
                reactionEmoji = "💙"
            ),

            // --- DAILY LIFE & FAMILY ---
            BuddyDialogueTopic(
                id = "love_family_hug",
                categoryId = "DAILY_LIFE",
                pokoEnglish = "Do you love Mom and Dad?",
                pokoTurkish = "Annene ve babana sarılmayı sever misin?",
                expectedKeywords = listOf("family", "mom", "dad", "love", "yes", "i love"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("I love my family! ❤️", "Ailemi çok seviyorum!", "❤️"),
                    AnswerChoice("I love Mom and Dad! 👨‍👩‍👧", "Annemi ve babamı seviyorum!", "👨‍👩‍👧"),
                    AnswerChoice("Big warm hug! 🤗", "Kocaman sarılma!", "🤗")
                ),
                praiseResponse = "Aww! Family love is the greatest superpower in the world!",
                praiseTurkish = "Harika! Aile sevgisi dünyadaki en büyük süper güçtür! ❤️",
                reactionEmoji = "❤️"
            ),
            BuddyDialogueTopic(
                id = "weather_sunny_rainy",
                categoryId = "DAILY_LIFE",
                pokoEnglish = "How is the weather? Sunny or rainy?",
                pokoTurkish = "Hava nasıl? Güneşli mi yağmurlu mu?",
                expectedKeywords = listOf("sunny", "rainy", "cloudy", "windy", "weather", "sun", "rain"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("It is sunny! ☀️", "Güneşli!", "☀️"),
                    AnswerChoice("It is rainy! 🌧️", "Yağmurlu!", "🌧️"),
                    AnswerChoice("Rainbow day! 🌈", "Gökkuşağı günü!", "🌈")
                ),
                praiseResponse = "Great observation! Put on your smile for every weather!",
                praiseTurkish = "Harika gözlem! Her havada yüzünden gülücük eksik olmasın! ☀️",
                reactionEmoji = "☀️"
            ),
            BuddyDialogueTopic(
                id = "good_night_stars",
                categoryId = "DAILY_LIFE",
                pokoEnglish = "Look at the stars! Say: 'Good night!'",
                pokoTurkish = "Yıldızlara bak! 'Good night' (İyi geceler) de!",
                expectedKeywords = listOf("good night", "night", "good", "stars", "sleep"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Good night! 🌙", "İyi geceler!", "🌙"),
                    AnswerChoice("Sweet dreams! ⭐", "Tatlı rüyalar!", "⭐"),
                    AnswerChoice("Sleep tight! 😴", "Mışıl mışıl uyu!", "😴")
                ),
                praiseResponse = "Good night little star! Sleep well and dream big!",
                praiseTurkish = "İyi geceler minik yıldızım! Tatlı tatlı uyu ve güzel rüyalar gör! 🌙",
                reactionEmoji = "🌙"
            ),
            BuddyDialogueTopic(
                id = "ready_to_play_game",
                categoryId = "DAILY_LIFE",
                pokoEnglish = "Are you ready to play a game? Yes or no?",
                pokoTurkish = "Eğlenceli bir oyuna hazır mısın? Evet mi hayır mı?",
                expectedKeywords = listOf("yes", "ready", "play", "game", "let's play"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("Yes, let's play! 🎮", "Evet, oynayalım!", "🎮"),
                    AnswerChoice("I am ready! 🚀", "Hazırım!", "🚀"),
                    AnswerChoice("Let's go! 🌟", "Hadi başlayalım!", "🌟")
                ),
                praiseResponse = "Woohoo! Let's play and have the best English adventure!",
                praiseTurkish = "Harika! Hadi oynayalım ve en eğlenceli macerayı yaşayalım! 🎮",
                reactionEmoji = "🎮"
            ),

            // --- SUPERHERO & FEELINGS ---
            BuddyDialogueTopic(
                id = "how_are_you_feeling",
                categoryId = "SUPERHERO",
                pokoEnglish = "How are you today? Are you happy or energetic?",
                pokoTurkish = "Bugün nasılsın? Mutlu ve enerjik misin?",
                expectedKeywords = listOf("happy", "great", "fine", "good", "yes", "super"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("I am happy! 😄", "Çok mutluyum!", "😄"),
                    AnswerChoice("I am energetic! ⚡", "Enerji doluyum!", "⚡"),
                    AnswerChoice("I am great! 🚀", "Harikayım!", "🚀")
                ),
                praiseResponse = "Yay! Your bright energy makes the whole room sparkle!",
                praiseTurkish = "Harika! Işıl ışıl enerjin tüm odayı aydınlatıyor! 😄",
                reactionEmoji = "😄"
            ),
            BuddyDialogueTopic(
                id = "super_strength_power",
                categoryId = "SUPERHERO",
                pokoEnglish = "Show me your muscles! Say: 'I am strong!'",
                pokoTurkish = "Kaslarını göster! 'I am strong!' (Ben güçlüyüm) de!",
                expectedKeywords = listOf("strong", "i am strong", "super", "muscle", "power"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("I am strong! 💪", "Ben güçlüyüm!", "💪"),
                    AnswerChoice("Super strength! ⚡", "Süper güç!", "⚡"),
                    AnswerChoice("I am a hero! 🦸‍♂️", "Ben kahramanım!", "🦸‍♂️")
                ),
                praiseResponse = "POW! You are as strong as a galactic superhero!",
                praiseTurkish = "GÜÇ SENDE! Galaktik bir süper kahraman kadar güçlüsün! 💪",
                reactionEmoji = "💪"
            ),
            BuddyDialogueTopic(
                id = "run_fast_speed",
                categoryId = "SUPERHERO",
                pokoEnglish = "Can you run very fast like a cheetah?",
                pokoTurkish = "Çita gibi çok hızlı koşabilir misin?",
                expectedKeywords = listOf("run", "fast", "speed", "yes", "cheetah"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("I run fast! ⚡", "Hızlı koşarım!", "⚡"),
                    AnswerChoice("Super speed! 🚀", "Süper hız!", "🚀"),
                    AnswerChoice("Zoom zoom! 🐆", "Vın vın!", "🐆")
                ),
                praiseResponse = "ZOOM! You have supersonic speed power!",
                praiseTurkish = "VIZZZT! Süpersonik hız gücün var! 🚀",
                reactionEmoji = "⚡"
            ),
            BuddyDialogueTopic(
                id = "high_five_champion",
                categoryId = "SUPERHERO",
                pokoEnglish = "You are an English superstar! Say: 'High five!'",
                pokoTurkish = "Sen harika bir İngilizce yıldızısın! Beşlik çak: 'High five!'",
                expectedKeywords = listOf("high five", "five", "high", "super", "yes"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("High five! ✋", "Beşlik çak!", "✋"),
                    AnswerChoice("Yes, high five! 🌟", "Evet, beşlik!", "🌟"),
                    AnswerChoice("We are champions! 🏆", "Biz şampiyonuz!", "🏆")
                ),
                praiseResponse = "HIGH FIVE! ✋ You are the coolest buddy in the universe!",
                praiseTurkish = "BEŞLİK ÇAK! ✋ Sen evrendeki en tatlı arkadaşsın! ⭐",
                reactionEmoji = "⭐"
            ),
            BuddyDialogueTopic(
                id = "star_wars_saber_fun",
                categoryId = "SUPERHERO",
                pokoEnglish = "Look at my glowing lightsaber! Do you like Star Wars?",
                pokoTurkish = "Işıldayan ışın kılıcıma bak! Star Wars sever misin?",
                expectedKeywords = listOf("yes", "star wars", "star", "wars", "lightsaber", "jedi", "vader", "kylo", "love"),
                quickAnswerSuggestions = listOf(
                    AnswerChoice("I love Star Wars! ⚔️", "Star Wars'u çok seviyorum!", "⚔️"),
                    AnswerChoice("I love Jedi! 🌟", "Jedi'yı seviyorum!", "🌟"),
                    AnswerChoice("Cool lightsaber! ⚡", "Harika ışın kılıcı!", "⚡")
                ),
                praiseResponse = "The Force is so strong with you! We are galactic best friends forever!",
                praiseTurkish = "Güç seninle dopdolu! Sonsuza dek galaktik en iyi dostlarız! ⚔️✨",
                reactionEmoji = "⚔️"
            )
        )
    }

    // ----------------------------------------------------
    // DYNAMIC & RANDOMIZED TOPIC QUEUE (No more fixed repetitive sequence!)
    // ----------------------------------------------------
    var currentTopicIndex by remember { mutableIntStateOf(0) }
    var activeDialogueQueue by remember {
        mutableStateOf(allDialogueTopics.shuffled())
    }

    // Function to filter and shuffle topics on demand
    fun filterAndShuffleQueue(cat: BuddyTopicCategory) {
        selectedCategory = cat
        val filtered = if (cat == BuddyTopicCategory.ALL) {
            allDialogueTopics
        } else {
            allDialogueTopics.filter { it.categoryId == cat.id }
        }
        activeDialogueQueue = filtered.shuffled()
        currentTopicIndex = 0
    }

    val currentTopic = activeDialogueQueue.getOrElse(currentTopicIndex % activeDialogueQueue.size.coerceAtLeast(1)) {
        allDialogueTopics.first()
    }

    // ----------------------------------------------------
    // ROLEPLAY ADVENTURES (Duolingo & Buddy.ai Interactive Scenarios)
    // ----------------------------------------------------
    val roleplayScenarios = remember {
        listOf(
            RoleplayScenario(
                id = "jedi_training",
                titleTr = "Galaktik Işın Kılıcı Eğitimi",
                titleEn = "Galactic Lightsaber Academy",
                iconEmoji = "⚔️",
                badgeName = "Galaktik Jedi Ustası 🌟",
                steps = listOf(
                    RoleplayStep(
                        pokoSpeechEn = "Greetings young hero! Ignite your lightsaber! Say: 'May the force be with you!'",
                        pokoSpeechTr = "Selam genç kahraman! Işın kılıcını ateşle! 'May the force be with you!' de!",
                        expectedKeywords = listOf("force", "may the force", "jedi", "hero", "yes"),
                        choices = listOf(
                            AnswerChoice("May the force be with you! ⚔️", "Güç seninle olsun!", "⚔️"),
                            AnswerChoice("I am a Jedi! 🌟", "Ben bir Jedi'yım!", "🌟")
                        ),
                        visualItemEmoji = "⚔️✨",
                        outcomePraiseEn = "BZZT! Your lightsaber glows with blazing power! The Force is strong with you!",
                        outcomePraiseTr = "VIZZZT! Işın kılıcın alev gibi parıldıyor! Güç seninle çok kuvvetli! ⚡"
                    ),
                    RoleplayStep(
                        pokoSpeechEn = "Awesome! Choose your galactic mission: Fly the X-Wing or defend the Starship?",
                        pokoSpeechTr = "Harika! Galaktik görevini seç: X-Wing uçur veya Yıldız Gemisini savun?",
                        expectedKeywords = listOf("fly", "wing", "defend", "starship", "ship"),
                        choices = listOf(
                            AnswerChoice("Fly the X-Wing! 🚀", "X-Wing uçur!", "🚀"),
                            AnswerChoice("Defend the Starship! 🛡️", "Yıldız Gemisini savun!", "🛡️")
                        ),
                        visualItemEmoji = "🚀⚔️",
                        outcomePraiseEn = "Mission accomplished! You are the greatest Jedi Champion in the galaxy!",
                        outcomePraiseTr = "Görev başarıyla tamamlandı! Galaksinin en büyük Jedi Şampiyonu sensin! 🌟👑"
                    )
                )
            ),
            RoleplayScenario(
                id = "space_pizza",
                titleTr = "Uzay Pizzacısı",
                titleEn = "Space Pizza Diner",
                iconEmoji = "🍕",
                badgeName = "Usta Uzay Şefi 👨‍🍳",
                steps = listOf(
                    RoleplayStep(
                        pokoSpeechEn = "Welcome to Space Pizza! What topping do you want? Cheese or mushrooms?",
                        pokoSpeechTr = "Uzay Pizzacısına hoş geldin! Üzerine ne istersin? Peynir mi mantar mı?",
                        expectedKeywords = listOf("cheese", "mushroom", "mushrooms", "pizza"),
                        choices = listOf(
                            AnswerChoice("Cheese please! 🧀", "Peynir lütfen!", "🧀"),
                            AnswerChoice("Mushrooms! 🍄", "Mantar!", "🍄")
                        ),
                        visualItemEmoji = "🍕",
                        outcomePraiseEn = "Yummy choice! Sizzling hot cheese added to your pizza!",
                        outcomePraiseTr = "Nefis seçim! Pizzana sıcacık eriyen peynir eklendi! 🧀"
                    ),
                    RoleplayStep(
                        pokoSpeechEn = "Great! And what would you like to drink? Juice or milk?",
                        pokoSpeechTr = "Harika! Yanında ne içmek istersin? Meyve suyu mu süt mü?",
                        expectedKeywords = listOf("juice", "milk", "drink", "orange"),
                        choices = listOf(
                            AnswerChoice("Orange Juice! 🧃", "Portakal Suyu!", "🧃"),
                            AnswerChoice("Cold Milk! 🥛", "Soğuk Süt!", "🥛")
                        ),
                        visualItemEmoji = "🧃",
                        outcomePraiseEn = "Perfect combo! Here is your galactic feast! Bon appetit!",
                        outcomePraiseTr = "Mükemmel ikili! İşte galaktik ziyafetin! Afiyet olsun! 🍕🧃"
                    )
                )
            ),
            RoleplayScenario(
                id = "pet_rescue",
                titleTr = "Sevimli Yavru Köpek Kurtarma",
                titleEn = "Cute Puppy Rescue",
                iconEmoji = "🐶",
                badgeName = "Kahraman Hayvansever 🐾",
                steps = listOf(
                    RoleplayStep(
                        pokoSpeechEn = "Look! A tiny puppy is in the rain! Call him: Say 'Come here puppy!'",
                        pokoSpeechTr = "Bak! Yağmurda minik bir yavru köpek var! Onu çağır: 'Come here puppy!' de!",
                        expectedKeywords = listOf("come", "here", "puppy", "dog", "come here"),
                        choices = listOf(
                            AnswerChoice("Come here puppy! 🐶", "Buraya gel yavru köpek!", "🐶"),
                            AnswerChoice("Hello puppy! 🐾", "Merhaba köpekçik!", "🐾")
                        ),
                        visualItemEmoji = "🌧️🐶",
                        outcomePraiseEn = "Woof! He wagged his tail and ran to us safely!",
                        outcomePraiseTr = "Hav hav! Kuyruğunu salladı ve güvenle yanımıza koştu! 🐾"
                    ),
                    RoleplayStep(
                        pokoSpeechEn = "He is cold! Should we give him a warm blanket or delicious dog food?",
                        pokoSpeechTr = "Çok üşümüş! Ona sıcak bir battaniye mi verelim lezzetli mama mı?",
                        expectedKeywords = listOf("blanket", "food", "warm", "eat"),
                        choices = listOf(
                            AnswerChoice("Warm blanket! 🧶", "Sıcak battaniye!", "🧶"),
                            AnswerChoice("Delicious food! 🍖", "Lezzetli mama!", "🍖")
                        ),
                        visualItemEmoji = "🐶❤️",
                        outcomePraiseEn = "He is so happy! You saved our best friend today!",
                        outcomePraiseTr = "O çok mutlu! Bugün en iyi dostumuzu kurtardın! 🐶🌟"
                    )
                )
            )
        )
    }

    var selectedScenarioIndex by remember { mutableIntStateOf(0) }
    var currentRoleplayStepIndex by remember { mutableIntStateOf(0) }
    val activeScenario = roleplayScenarios[selectedScenarioIndex % roleplayScenarios.size]
    val activeRoleplayStep = activeScenario.steps[currentRoleplayStepIndex.coerceIn(0, activeScenario.steps.size - 1)]

    // Synchronized Speech & Subtitle States (Ensures 100% Turkish translation for ALL speech)
    var currentSpeechEnglish by remember { mutableStateOf(currentTopic.pokoEnglish) }
    var currentSpeechTurkish by remember { mutableStateOf(currentTopic.pokoTurkish) }

    var showPraiseFeedback by remember { mutableStateOf(false) }
    var praiseMessageEn by remember { mutableStateOf("") }
    var praiseMessageTr by remember { mutableStateOf("") }
    var mascotMood by remember { mutableStateOf(PokoMood.HAPPY) }
    var isMicrophoneActive by remember { mutableStateOf(false) }

    // Ambient Star & Bokeh animations
    val infiniteTransition = rememberInfiniteTransition(label = "buddy_studio_anim")
    val studioLightPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "studio_light"
    )

    val micPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_pulse"
    )

    fun handlePositiveOutcome(praiseEn: String, praiseTr: String, onNextStep: () -> Unit) {
        isMicrophoneActive = false
        mascotMood = PokoMood.CELEBRATING
        showPraiseFeedback = true
        praiseMessageEn = praiseEn
        praiseMessageTr = praiseTr
        currentSpeechEnglish = praiseEn
        currentSpeechTurkish = praiseTr
        audioService.playCorrectSound()
        audioService.playCoinSound()
        onRewardCoins(15)

        coroutineScope.launch {
            delay(250)
            audioService.speakEnglish(praiseEn) {
                coroutineScope.launch {
                    delay(1500)
                    onNextStep()
                }
            }
        }
    }

    fun handleRetrySpeech() {
        isMicrophoneActive = false
        mascotMood = PokoMood.HAPPY
        val retryEn = "Let's try once more! You can do it!"
        val retryTr = "Hadi bir kez daha dene! Sen yapabilirsin! 💪"
        currentSpeechEnglish = retryEn
        currentSpeechTurkish = retryTr
        audioService.speakEnglish(retryEn)
    }

    // Permission launcher for speech recognition
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            audioService.stopSpeech()
            val targetKey = if (activeTab == BuddyTalkTab.CHAT) {
                currentTopic.expectedKeywords.firstOrNull() ?: "hello"
            } else {
                activeRoleplayStep.expectedKeywords.firstOrNull() ?: "yes"
            }
            speechService.startListening(targetKey) { eval ->
                if (eval.isPassed || eval.spokenText.isNotBlank()) {
                    if (activeTab == BuddyTalkTab.CHAT) {
                        handlePositiveOutcome(currentTopic.praiseResponse, currentTopic.praiseTurkish) {
                            if (currentTopicIndex < activeDialogueQueue.size - 1) {
                                currentTopicIndex++
                            } else {
                                audioService.playFanfareSound()
                                activeDialogueQueue = activeDialogueQueue.shuffled()
                                currentTopicIndex = 0
                            }
                        }
                    } else {
                        handlePositiveOutcome(activeRoleplayStep.outcomePraiseEn, activeRoleplayStep.outcomePraiseTr) {
                            if (currentRoleplayStepIndex < activeScenario.steps.size - 1) {
                                currentRoleplayStepIndex++
                            } else {
                                audioService.playFanfareSound()
                                onRewardCoins(30)
                                currentRoleplayStepIndex = 0
                            }
                        }
                    }
                } else {
                    handleRetrySpeech()
                }
            }
            isMicrophoneActive = true
            mascotMood = PokoMood.THINKING
        }
    }

    fun startListeningWithPermission() {
        val targetKey = if (activeTab == BuddyTalkTab.CHAT) {
            currentTopic.expectedKeywords.firstOrNull() ?: "hello"
        } else {
            activeRoleplayStep.expectedKeywords.firstOrNull() ?: "yes"
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            audioService.stopSpeech()
            speechService.startListening(targetKey) { eval ->
                if (eval.isPassed || eval.spokenText.isNotBlank()) {
                    if (activeTab == BuddyTalkTab.CHAT) {
                        handlePositiveOutcome(currentTopic.praiseResponse, currentTopic.praiseTurkish) {
                            if (currentTopicIndex < activeDialogueQueue.size - 1) {
                                currentTopicIndex++
                            } else {
                                audioService.playFanfareSound()
                                activeDialogueQueue = activeDialogueQueue.shuffled()
                                currentTopicIndex = 0
                            }
                        }
                    } else {
                        handlePositiveOutcome(activeRoleplayStep.outcomePraiseEn, activeRoleplayStep.outcomePraiseTr) {
                            if (currentRoleplayStepIndex < activeScenario.steps.size - 1) {
                                currentRoleplayStepIndex++
                            } else {
                                audioService.playFanfareSound()
                                onRewardCoins(30)
                                currentRoleplayStepIndex = 0
                            }
                        }
                    }
                } else {
                    handleRetrySpeech()
                }
            }
            isMicrophoneActive = true
            mascotMood = PokoMood.THINKING
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // When topic changes in Chat mode, Poko speaks with synchronized subtitles
    LaunchedEffect(currentTopic.id, activeTab) {
        if (activeTab == BuddyTalkTab.CHAT) {
            showPraiseFeedback = false
            currentSpeechEnglish = currentTopic.pokoEnglish
            currentSpeechTurkish = currentTopic.pokoTurkish
            mascotMood = PokoMood.TALKING
            audioService.stopSpeech()
            delay(300)
            audioService.speakEnglish(currentTopic.pokoEnglish) {
                mascotMood = PokoMood.HAPPY
            }
        }
    }

    // When step changes in Roleplay mode, Poko speaks scenario prompt
    LaunchedEffect(currentRoleplayStepIndex, selectedScenarioIndex, activeTab) {
        if (activeTab == BuddyTalkTab.ROLEPLAY) {
            showPraiseFeedback = false
            currentSpeechEnglish = activeRoleplayStep.pokoSpeechEn
            currentSpeechTurkish = activeRoleplayStep.pokoSpeechTr
            mascotMood = PokoMood.TALKING
            audioService.stopSpeech()
            delay(300)
            audioService.speakEnglish(activeRoleplayStep.pokoSpeechEn) {
                mascotMood = PokoMood.HAPPY
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            audioService.stopSpeech()
            speechService.cancelListening()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF312E81),
                        Color(0xFF4C1D95)
                    )
                )
            )
            .testTag("poko_buddy_talk_screen")
    ) {
        // 1. Studio Lighting & Floating Stars Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Overhead Spotlight onto Poko
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFDE047).copy(alpha = 0.22f * studioLightPulse), Color.Transparent),
                    center = Offset(w * 0.5f, h * 0.35f),
                    radius = w * 0.55f
                ),
                center = Offset(w * 0.5f, h * 0.35f),
                radius = w * 0.55f
            )

            // Sparkle Stars
            listOf(
                Offset(w * 0.12f, h * 0.15f),
                Offset(w * 0.85f, h * 0.18f),
                Offset(w * 0.22f, h * 0.65f),
                Offset(w * 0.78f, h * 0.68f),
                Offset(w * 0.5f, h * 0.82f)
            ).forEach { pos ->
                drawCircle(Color.White.copy(alpha = 0.8f), radius = 3.5f, center = pos)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP BAR: Navigation, Coins, Tab Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(42.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Odaya Dön",
                        tint = Color.White
                    )
                }

                // Dual Mode Switcher: "💬 Canlı Sohbet" & "🎭 Rol Yapma"
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.35f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF818CF8).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (activeTab == BuddyTalkTab.CHAT) Color(0xFF6366F1) else Color.Transparent,
                            modifier = Modifier.clickable { activeTab = BuddyTalkTab.CHAT }
                        ) {
                            Text(
                                text = "💬 Canlı Sohbet",
                                fontWeight = FontWeight.Black,
                                color = if (activeTab == BuddyTalkTab.CHAT) Color.White else Color(0xFFC7D2FE),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (activeTab == BuddyTalkTab.ROLEPLAY) SunGold else Color.Transparent,
                            modifier = Modifier.clickable { activeTab = BuddyTalkTab.ROLEPLAY }
                        ) {
                            Text(
                                text = "🎭 Rol Oyunu",
                                fontWeight = FontWeight.Black,
                                color = if (activeTab == BuddyTalkTab.ROLEPLAY) Color(0xFF78350F) else Color(0xFFC7D2FE),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Coin Counter Badge
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFEF3C7),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFBBF24))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 14.sp)
                        Text(
                            text = "$userCoins",
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB45309),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ----------------------------------------------------
            // CHAT CATEGORY SELECTOR & SHUFFLE ROW (Only in Chat Mode)
            // ----------------------------------------------------
            if (activeTab == BuddyTalkTab.CHAT) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Quick Shuffle Button
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = SunGold,
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFB45309)),
                        modifier = Modifier
                            .clickable {
                                audioService.playPopSound()
                                activeDialogueQueue = activeDialogueQueue.shuffled()
                                currentTopicIndex = (currentTopicIndex + 1) % activeDialogueQueue.size
                            }
                            .testTag("buddy_shuffle_topics_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "🎲", fontSize = 14.sp)
                            Text(
                                text = "Farklı Soru",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF78350F),
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Category Filter Pills
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(BuddyTopicCategory.entries) { cat ->
                            val isSelected = selectedCategory == cat
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) Color(0xFF6366F1) else Color.White.copy(alpha = 0.15f),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, Color.White) else null,
                                modifier = Modifier.clickable {
                                    audioService.playPopSound()
                                    filterAndShuffleQueue(cat)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(text = cat.emoji, fontSize = 12.sp)
                                    Text(
                                        text = cat.title,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color(0xFFC7D2FE),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }

            // ----------------------------------------------------
            // 100% SYNCHRONIZED TURKISH SUBTITLE & SPEECH CARD (HER KONUŞMAYA ALT YAZI)
            // ----------------------------------------------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
                    .shadow(10.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF818CF8).copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Live Subtitle Indicator Pill
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFDCFCE7),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFF22C55E), CircleShape)
                                )
                                Text(
                                    text = "CANLI TÜRKÇE ALT YAZI",
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF15803D),
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Speech Controls (English and Turkish TTS)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Listen English
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFEEF2FF),
                                modifier = Modifier.clickable {
                                    audioService.stopSpeech()
                                    audioService.speakEnglish(currentSpeechEnglish)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "İngilizce Dinle",
                                        tint = Color(0xFF4F46E5),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "İngilizce",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4F46E5),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Listen Turkish
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFFEF2F2),
                                modifier = Modifier.clickable {
                                    audioService.stopSpeech()
                                    audioService.speakTurkish(currentSpeechTurkish)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(text = "🇹🇷", fontSize = 12.sp)
                                    Text(
                                        text = "Türkçe",
                                        fontWeight = FontWeight.Bold,
                                        color = CandyRed,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Synchronized English Speech (Large & Clear)
                    Text(
                        text = currentSpeechEnglish,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            fontSize = 16.sp,
                            lineHeight = 21.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 100% Guaranteed Turkish Subtitle Banner
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🗣️", fontSize = 13.sp)
                            Text(
                                text = currentSpeechTurkish,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF4338CA),
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ----------------------------------------------------
            // CENTER: GIANT LIVING POKO MASCOT (Size 185.dp)
            // ----------------------------------------------------
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Interactive Poko Mascot
                PokoMascot(
                    size = 185.dp,
                    mood = mascotMood,
                    costumeId = equippedCostumeId,
                    onMascotClick = {
                        audioService.stopSpeech()
                        val msgEn = "I am listening to you! Speak into the microphone!"
                        val msgTr = "Seni dinliyorum canım arkadaşım! Mikrofona doğru konuş!"
                        currentSpeechEnglish = msgEn
                        currentSpeechTurkish = msgTr
                        audioService.speakEnglish(msgEn)
                    },
                    modifier = Modifier.clickable {
                        audioService.playPopSound()
                    }
                )

                // Praise Floating Celebration Popup
                Column(
                    modifier = Modifier.align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showPraiseFeedback,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF22C55E),
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            shadowElevation = 10.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(text = "🌟", fontSize = 22.sp)
                                Column {
                                    Text(
                                        text = "Harika Konuştun! +15 Altın 🪙",
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = praiseMessageTr.ifBlank { "Tebrikler!" },
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFEF08A),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // QUICK SUGGESTIONS / SCENARIO CHOICES WITH TURKISH TRANSLATION
            // ----------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (activeTab == BuddyTalkTab.CHAT) "💡 Cevap Seçenekleri (Söyle veya Dokun):" else "🎭 Rol Yapma Seçenekleri:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFC7D2FE),
                        fontSize = 11.sp
                    )
                )

                if (activeTab == BuddyTalkTab.ROLEPLAY) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SunOrange.copy(alpha = 0.25f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SunOrange)
                    ) {
                        Text(
                            text = "${activeScenario.iconEmoji} ${activeScenario.titleTr}",
                            fontWeight = FontWeight.Black,
                            color = SunGold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val currentChoices = if (activeTab == BuddyTalkTab.CHAT) {
                currentTopic.quickAnswerSuggestions
            } else {
                activeRoleplayStep.choices
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentChoices) { choice ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.18f),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFC7D2FE).copy(alpha = 0.7f)),
                        modifier = Modifier
                            .clickable {
                                audioService.stopSpeech()
                                // Update subtitles to show child's answer immediately
                                currentSpeechEnglish = choice.english
                                currentSpeechTurkish = "${choice.turkish} (${choice.english})"
                                audioService.speakEnglish(choice.english) {
                                    // Trigger positive outcome with Poko's praise
                                    coroutineScope.launch {
                                        delay(400)
                                        if (activeTab == BuddyTalkTab.CHAT) {
                                            handlePositiveOutcome(currentTopic.praiseResponse, currentTopic.praiseTurkish) {
                                                if (currentTopicIndex < activeDialogueQueue.size - 1) {
                                                    currentTopicIndex++
                                                } else {
                                                    audioService.playFanfareSound()
                                                    activeDialogueQueue = activeDialogueQueue.shuffled()
                                                    currentTopicIndex = 0
                                                }
                                            }
                                        } else {
                                            handlePositiveOutcome(activeRoleplayStep.outcomePraiseEn, activeRoleplayStep.outcomePraiseTr) {
                                                if (currentRoleplayStepIndex < activeScenario.steps.size - 1) {
                                                    currentRoleplayStepIndex++
                                                } else {
                                                    audioService.playFanfareSound()
                                                    onRewardCoins(30)
                                                    currentRoleplayStepIndex = 0
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = choice.english,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            if (choice.displayPhonetic.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF0369A1).copy(alpha = 0.55f),
                                    border = androidx.compose.foundation.BorderStroke(0.7.dp, Color(0xFF38BDF8).copy(alpha = 0.8f))
                                ) {
                                    Text(
                                        text = "🗣️ /${choice.displayPhonetic}/",
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF67E8F9),
                                        fontSize = 10.5.sp,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = choice.turkish,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFDE047),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ----------------------------------------------------
            // BOTTOM CONTROLS: 3D MICROPHONE & NAVIGATION
            // ----------------------------------------------------
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isMicrophoneActive) {
                    Text(
                        text = "🎧 Poko dinliyor... İngilizce konuş!",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFDE047),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Next / Shuffle button
                    IconButton(
                        onClick = {
                            audioService.stopSpeech()
                            audioService.playPopSound()
                            if (activeTab == BuddyTalkTab.CHAT) {
                                currentTopicIndex = (currentTopicIndex + 1) % activeDialogueQueue.size
                            } else {
                                selectedScenarioIndex = (selectedScenarioIndex + 1) % roleplayScenarios.size
                                currentRoleplayStepIndex = 0
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sıradaki / Başka Soru",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Big 3D Tactile Microphone Action Button
                    Surface(
                        shape = CircleShape,
                        color = if (isMicrophoneActive) Color(0xFFEF4444) else LeafGreen,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 4.dp,
                            color = if (isMicrophoneActive) Color(0xFFFCA5A5) else Color(0xFF86EFAC)
                        ),
                        shadowElevation = 10.dp,
                        modifier = Modifier
                            .size(72.dp)
                            .scale(if (isMicrophoneActive) micPulse else 1f)
                            .clickable {
                                if (isMicrophoneActive) {
                                    speechService.stopListening()
                                    isMicrophoneActive = false
                                    mascotMood = PokoMood.HAPPY
                                } else {
                                    startListeningWithPermission()
                                }
                            }
                            .testTag("buddy_mic_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Konuş",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Replay Poko voice button
                    IconButton(
                        onClick = {
                            audioService.stopSpeech()
                            if (activeTab == BuddyTalkTab.CHAT) {
                                audioService.speakEnglish(currentTopic.pokoEnglish)
                            } else {
                                audioService.speakEnglish(activeRoleplayStep.pokoSpeechEn)
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Tekrar Dinle",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isMicrophoneActive) "Durdurmak için tıkla" else "Konuşmak için mikrofona dokun",
                    color = Color(0xFFCBD5E1),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
