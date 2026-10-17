package com.example.data.curriculum

import com.example.data.model.CardRarity
import com.example.data.model.HologramCardItem
import com.example.data.model.LeaderboardUser
import com.example.data.model.LeagueTier

object ExtraGameFeaturesData {

    // ==========================================
    // 3D HOLOGRAPHIC TRADING CARDS COLLECTION
    // ==========================================
    val allHologramCards: List<HologramCardItem> by lazy {
        listOf(
            // LEGENDARY CARDS (Efsanevi 🌟)
            HologramCardItem(
                id = "card_vader",
                nameEn = "Lord Vader Lego",
                nameTr = "Galaktik Lord Vader",
                descriptionEn = "Master of the Dark Side and Crimson Plasma Saber.",
                descriptionTr = "Karanlık tarafın ve kırmızı ışın kılıcının efsanevi ustası.",
                emoji = "⚔️",
                rarity = CardRarity.LEGENDARY,
                powerLevel = 99,
                element = "Galactic Dark"
            ),
            HologramCardItem(
                id = "card_phoenix",
                nameEn = "Solar Phoenix",
                nameTr = "Güneş Ankası",
                descriptionEn = "Born from stellar fire, speaks radiant words of light.",
                descriptionTr = "Yıldız ateşinden doğan, ışık saçan efsanevi anka kuşu.",
                emoji = "🔥",
                rarity = CardRarity.LEGENDARY,
                powerLevel = 95,
                element = "Solar Fire"
            ),
            HologramCardItem(
                id = "card_golden_dragon",
                nameEn = "Golden Star Dragon",
                nameTr = "Altın Yıldız Ejderhası",
                descriptionEn = "Guardian of all English star fruits and wisdom.",
                descriptionTr = "Tüm İngilizce yıldız meyvelerinin ve bilgeliğin koruyucusu.",
                emoji = "🐉",
                rarity = CardRarity.LEGENDARY,
                powerLevel = 98,
                element = "Cosmic Gold"
            ),
            HologramCardItem(
                id = "card_yoda",
                nameEn = "Master Yoda",
                nameTr = "Bilge Usta Yoda",
                descriptionEn = "Wise teacher of words and ancient green lightsaber.",
                descriptionTr = "Kelimelerin kadim yeşil kılıçlı bilge öğretmeni.",
                emoji = "🟢",
                rarity = CardRarity.LEGENDARY,
                powerLevel = 96,
                element = "Light Force"
            ),

            // EPIC CARDS (Epik 🟣)
            HologramCardItem(
                id = "card_dragon",
                nameEn = "Baby Drako",
                nameTr = "Yavru Drako Ejderha",
                descriptionEn = "Cute baby dragon learning new English words every day!",
                descriptionTr = "Her gün yeni İngilizce kelimeler öğrenen sevimli yavru ejderha!",
                emoji = "🐲",
                rarity = CardRarity.EPIC,
                powerLevel = 82,
                element = "Cosmic Spark"
            ),
            HologramCardItem(
                id = "card_cyber_knight",
                nameEn = "Cyber Knight",
                nameTr = "Siber Neon Şövalye",
                descriptionEn = "Armored champion equipped with digital plasma blade.",
                descriptionTr = "Dijital plazma kılıçlı zırhlı siber şampiyon.",
                emoji = "🤖",
                rarity = CardRarity.EPIC,
                powerLevel = 85,
                element = "Neon Tech"
            ),
            HologramCardItem(
                id = "card_galaxy_tiger",
                nameEn = "Cosmic Tiger",
                nameTr = "Kozmik Kaplan",
                descriptionEn = "Fastest runner in the Andromeda star system.",
                descriptionTr = "Andromeda yıldız sisteminin en hızlı koşan kaplanı.",
                emoji = "🐯",
                rarity = CardRarity.EPIC,
                powerLevel = 80,
                element = "Stellar Swift"
            ),
            HologramCardItem(
                id = "card_star_unicorn",
                nameEn = "Rainbow Unicorn",
                nameTr = "Gökkuşağı Tekboynuz",
                descriptionEn = "Spreads joy, magic colors and sweet phonics.",
                descriptionTr = "Neşe, sihirli renkler ve tatlı sesler saçar.",
                emoji = "🦄",
                rarity = CardRarity.EPIC,
                powerLevel = 78,
                element = "Rainbow Magic"
            ),

            // RARE CARDS (Nadir 🔵)
            HologramCardItem(
                id = "card_star",
                nameEn = "Super Star Hero",
                nameTr = "Süper Yıldız Kahramanı",
                descriptionEn = "Shining hero of first and second grade lessons.",
                descriptionTr = "Birinci ve ikinci sınıf derslerinin parlayan kahramanı.",
                emoji = "⭐",
                rarity = CardRarity.RARE,
                powerLevel = 65,
                element = "Starlight"
            ),
            HologramCardItem(
                id = "card_droid",
                nameEn = "BB-8 Mini Droid",
                nameTr = "Mini Robot BB-8",
                descriptionEn = "Loyal astromech companion helping in pronunciation.",
                descriptionTr = "Telaffuzda yardım eden sadık robot dost.",
                emoji = "🤖",
                rarity = CardRarity.RARE,
                powerLevel = 62,
                element = "Tech Circuit"
            ),
            HologramCardItem(
                id = "card_xwing",
                nameEn = "Starfighter Pilot",
                nameTr = "Yıldız Savaşçısı Pilot",
                descriptionEn = "Acrobatic pilot soaring through space nebulas.",
                descriptionTr = "Uzay nebulalarında süzülen akrobatik pilot.",
                emoji = "🚀",
                rarity = CardRarity.RARE,
                powerLevel = 68,
                element = "Aero Flight"
            ),
            HologramCardItem(
                id = "card_wizard",
                nameEn = "Starlight Wizard",
                nameTr = "Yıldızışığı Büyücüsü",
                descriptionEn = "Casts sentence-building spells with magic wand.",
                descriptionTr = "Sihirli asasıyla cümle kurma büyüleri yapar.",
                emoji = "🧙‍♂️",
                rarity = CardRarity.RARE,
                powerLevel = 64,
                element = "Mystic"
            ),

            // COMMON CARDS (Sıradan ⚪)
            HologramCardItem(
                id = "card_poko_owl",
                nameEn = "Poko the Wise Owl",
                nameTr = "Bilge Baykuş Poko",
                descriptionEn = "Your friendly English guide in every adventure.",
                descriptionTr = "Her maceradaki sevimli İngilizce rehberin.",
                emoji = "🦉",
                rarity = CardRarity.COMMON,
                powerLevel = 50,
                element = "Wisdom"
            ),
            HologramCardItem(
                id = "card_happy_puppy",
                nameEn = "Cosmo Puppy",
                nameTr = "Kozmik Yavru Köpek",
                descriptionEn = "Energetic friend who loves playing word games.",
                descriptionTr = "Kelime oyunları oynamayı seven enerjik dost.",
                emoji = "🐶",
                rarity = CardRarity.COMMON,
                powerLevel = 45,
                element = "Nature"
            ),
            HologramCardItem(
                id = "card_magic_apple",
                nameEn = "Golden Apple",
                nameTr = "Altın Elma",
                descriptionEn = "Delicious fruit that gives star fruit energy.",
                descriptionTr = "Yıldız meyvesi enerjisi veren lezzetli meyve.",
                emoji = "🍎",
                rarity = CardRarity.COMMON,
                powerLevel = 42,
                element = "Nature"
            ),
            HologramCardItem(
                id = "card_space_rocket",
                nameEn = "Sky Rocket",
                nameTr = "Gökyüzü Roketi",
                descriptionEn = "Blasts off to new grades and units!",
                descriptionTr = "Yeni sınıflara ve ünitelere roket hızıyla uçar!",
                emoji = "🛸",
                rarity = CardRarity.COMMON,
                powerLevel = 48,
                element = "Space"
            )
        )
    }

    // ==========================================
    // GALAXY LEAGUE RIVALS & LEADERBOARD
    // ==========================================
    fun generateLeaderboardUsers(userPoints: Int, userName: String = "Sen (Kahraman)"): List<LeaderboardUser> {
        val botRivals = listOf(
            LeaderboardUser(1, "Efe Yıldız ⭐", "🚀", userPoints + 60, false, LeagueTier.GOLD),
            LeaderboardUser(2, "Zeynep Jedi ⚔️", "👑", userPoints + 35, false, LeagueTier.GOLD),
            LeaderboardUser(3, "Ada & Drako 🐲", "🐉", userPoints + 15, false, LeagueTier.GOLD),
            LeaderboardUser(4, userName, "🦸", userPoints, true, LeagueTier.GOLD),
            LeaderboardUser(5, "Leo Space 🌌", "🧑‍🚀", maxOf(40, userPoints - 25), false, LeagueTier.GOLD),
            LeaderboardUser(6, "Mia Panda 🐼", "🎮", maxOf(30, userPoints - 50), false, LeagueTier.SILVER),
            LeaderboardUser(7, "Can Rocket 🛸", "🤖", maxOf(20, userPoints - 75), false, LeagueTier.SILVER),
            LeaderboardUser(8, "Elif Kaşif 🌸", "🦄", maxOf(10, userPoints - 100), false, LeagueTier.BRONZE)
        )
        return botRivals.sortedByDescending { it.points }.mapIndexed { index, user ->
            user.copy(rank = index + 1)
        }
    }
}
