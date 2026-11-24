package com.example.data

import com.example.model.MlbbHero
import com.example.model.MlbbLane
import com.example.model.MlbbRole

object MlbbHeroCatalog {

    val heroes: List<MlbbHero> = listOf(
        // Assassins & Junglers
        MlbbHero("fanny", "Fanny", "فنی", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE), "تحرک فوق‌العاده • برست"),
        MlbbHero("hayabusa", "Hayabusa", "هایابوسا", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE), "تک‌هدف • فرار سایه‌ای"),
        MlbbHero("ling", "Ling", "لینگ", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE), "حرکت روی دیوار • کریتیکال"),
        MlbbHero("gusion", "Gusion", "گوشن", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE, MlbbLane.MID), "کمبو سریع • مجیک برست"),
        MlbbHero("lancelot", "Lancelot", "لنسلوت", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE), "دش پی‌درپی • ایمیونیتی"),
        MlbbHero("suyou", "Suyou", "سویو", MlbbRole.ASSASSIN, MlbbRole.FIGHTER, listOf(MlbbLane.JUNGLE, MlbbLane.EXP), "دو فرم • برست و کنترل"),
        MlbbHero("nolan", "Nolan", "نولان", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE), "شکاف فضایی • تحرک بالا"),
        MlbbHero("joy", "Joy", "جوی", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE, MlbbLane.EXP), "ریتمیک • مجیک برست"),
        MlbbHero("benedetta", "Benedetta", "بندتا", MlbbRole.ASSASSIN, MlbbRole.FIGHTER, listOf(MlbbLane.EXP, MlbbLane.JUNGLE), "دش مداوم • ضد سی‌سی"),
        MlbbHero("helcurt", "Helcurt", "هلکورت", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE, MlbbLane.ROAM), "تاریکی نقشه • برست"),
        MlbbHero("karina", "Karina", "کارینا", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE), "ریست کیل • ترو دمیج"),
        MlbbHero("saber", "Saber", "سیبر", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE, MlbbLane.ROAM), "قفل تک‌هدف • ضد اسکوئیشی"),
        MlbbHero("natalia", "Natalia", "ناتالیا", MlbbRole.ASSASSIN, null, listOf(MlbbLane.ROAM, MlbbLane.JUNGLE), "نامرئی • سایلنس"),
        MlbbHero("aamon", "Aamon", "آمون", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE), "استتار • مجیک برست"),
        MlbbHero("selena", "Selena", "سلنا", MlbbRole.ASSASSIN, MlbbRole.MAGE, listOf(MlbbLane.MID, MlbbLane.ROAM), "استان طولانی • ویژن"),
        MlbbHero("hanzo", "Hanzo", "هانزو", MlbbRole.ASSASSIN, null, listOf(MlbbLane.JUNGLE), "روح نینجا • سرقت باف"),
        MlbbHero("yi_sun_shin", "Yi Sun-shin", "یی سان شین", MlbbRole.ASSASSIN, MlbbRole.MARKSMAN, listOf(MlbbLane.JUNGLE), "ویژن سراسری • تعویض سلاح"),

        // Fighters & EXP Laners
        MlbbHero("lukas", "Lukas", "لوکاس", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.JUNGLE), "فرم هیولا • دوام و دمیج"),
        MlbbHero("cici", "Cici", "سی‌سی", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP), "کایتینگ • ضد تانک"),
        MlbbHero("arlott", "Arlott", "آرلوت", MlbbRole.FIGHTER, MlbbRole.ASSASSIN, listOf(MlbbLane.EXP, MlbbLane.ROAM), "ریست دش با سی‌سی"),
        MlbbHero("yu_zhong", "Yu Zhong", "یو ژونگ", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP), "اژدهای سیاه • اسپل‌ومپ"),
        MlbbHero("terizla", "Terizla", "تریزلا", MlbbRole.FIGHTER, MlbbRole.TANK, listOf(MlbbLane.EXP), "تیم‌فایت سنگین • کاهش دمیج"),
        MlbbHero("paquito", "Paquito", "پاکیتو", MlbbRole.FIGHTER, MlbbRole.ASSASSIN, listOf(MlbbLane.EXP, MlbbLane.JUNGLE), "کمبوی بوکس • شیلد"),
        MlbbHero("chou", "Chou", "چو", MlbbRole.FIGHTER, null, listOf(MlbbLane.ROAM, MlbbLane.EXP), "پیک‌آف • ایمیون سی‌سی"),
        MlbbHero("lapu_lapu", "Lapu-Lapu", "لاپو لاپو", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP), "ورود به بک‌لاین • شیلد"),
        MlbbHero("khaleed", "Khaleed", "خالد", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.ROAM), "روتیشن سریع • هیل شن"),
        MlbbHero("ruby", "Ruby", "روبی", MlbbRole.FIGHTER, MlbbRole.TANK, listOf(MlbbLane.EXP, MlbbLane.ROAM, MlbbLane.GOLD), "اسپل‌ومپ • سی‌سی مداوم"),
        MlbbHero("phoveus", "Phoveus", "فوئوس", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP), "ضد هیروهای دش‌دار"),
        MlbbHero("alpha", "Alpha", "آلفا", MlbbRole.FIGHTER, null, listOf(MlbbLane.JUNGLE, MlbbLane.EXP), "ترو دمیج • کول‌داون کم"),
        MlbbHero("julian", "Julian", "جولیان", MlbbRole.FIGHTER, MlbbRole.MAGE, listOf(MlbbLane.JUNGLE, MlbbLane.EXP, MlbbLane.MID), "کمبوی تقویتی • انعطاف‌پذیر"),
        MlbbHero("yin", "Yin", "یین", MlbbRole.FIGHTER, MlbbRole.ASSASSIN, listOf(MlbbLane.JUNGLE, MlbbLane.EXP), "دوئل ۱ به ۱ در قلمرو"),
        MlbbHero("dyrroth", "Dyrroth", "دایروت", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.JUNGLE), "شکستن آرمور • ارلی قوی"),
        MlbbHero("x_borg", "X.Borg", "ایکس‌بورگ", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.JUNGLE), "زره اضافه • ترو دمیج"),
        MlbbHero("thamuz", "Thamuz", "تاموز", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.JUNGLE), "دوئل سنگین • ترو دمیج"),
        MlbbHero("esmeralda", "Esmeralda", "اسمرالدا", MlbbRole.TANK, MlbbRole.MAGE, listOf(MlbbLane.EXP), "دزدی شیلد • تانکی"),
        MlbbHero("martis", "Martis", "مارتیس", MlbbRole.FIGHTER, null, listOf(MlbbLane.JUNGLE, MlbbLane.EXP), "ضد سی‌سی • اسنوبال"),
        MlbbHero("guinevere", "Guinevere", "گوینور", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.JUNGLE), "ناک‌آپ منطقه‌ای • مجیک"),
        MlbbHero("freya", "Freya", "فریا", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.JUNGLE, MlbbLane.GOLD), "اتک اسپید • شیلد بالا"),
        MlbbHero("badang", "Badang", "بادانگ", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.ROAM), "دیوار سنگی • استان پیاپی"),
        MlbbHero("sun", "Sun", "سان", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.JUNGLE), "کلون • پوش سریع تاور"),
        MlbbHero("argus", "Argus", "آرگوس", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP), "نامیرایی ۴ ثانیه‌ای • پوش"),
        MlbbHero("zilong", "Zilong", "زیلونگ", MlbbRole.FIGHTER, MlbbRole.ASSASSIN, listOf(MlbbLane.EXP), "اسپلیت پوش • برست"),
        MlbbHero("alucard", "Alucard", "آلوکارد", MlbbRole.FIGHTER, MlbbRole.ASSASSIN, listOf(MlbbLane.JUNGLE), "تعقیب • لایف‌استیل"),
        MlbbHero("minsi", "Minsitthar", "مینسیتار", MlbbRole.FIGHTER, null, listOf(MlbbLane.EXP, MlbbLane.ROAM), "ضد بلینک و دش • هوک"),
        MlbbHero("roger", "Roger", "راجر", MlbbRole.FIGHTER, MlbbRole.MARKSMAN, listOf(MlbbLane.JUNGLE, MlbbLane.GOLD), "گرگینه • اکزکیوت"),
        MlbbHero("bane", "Bane", "بین", MlbbRole.FIGHTER, MlbbRole.MAGE, listOf(MlbbLane.JUNGLE, MlbbLane.EXP), "پوش تاور • برست منطقه‌ای"),

        // Tanks & Roamers
        MlbbHero("tigreal", "Tigreal", "تیگریل", MlbbRole.TANK, null, listOf(MlbbLane.ROAM), "ست تیم‌فایت • سی‌سی قوی"),
        MlbbHero("khufra", "Khufra", "خفرا", MlbbRole.TANK, null, listOf(MlbbLane.ROAM), "ضد دش • ورود ناگهانی"),
        MlbbHero("atlas", "Atlas", "اطلس", MlbbRole.TANK, null, listOf(MlbbLane.ROAM), "جمع کردن ۵ نفره • فریز"),
        MlbbHero("franco", "Franco", "فرانکو", MlbbRole.TANK, null, listOf(MlbbLane.ROAM), "هوک • ساپرس ضد ایمیون"),
        MlbbHero("minotaur", "Minotaur", "مینوتور", MlbbRole.TANK, MlbbRole.SUPPORT, listOf(MlbbLane.ROAM), "هیل تیمی • ناک‌آپ"),
        MlbbHero("chip", "Chip", "چیپ", MlbbRole.SUPPORT, MlbbRole.TANK, listOf(MlbbLane.ROAM), "پورتال تلپورت تیمی"),
        MlbbHero("edith", "Edith", "ادیت", MlbbRole.TANK, MlbbRole.MARKSMAN, listOf(MlbbLane.EXP, MlbbLane.ROAM), "سی‌سی + تبدیل به مارکسمن"),
        MlbbHero("fredrinn", "Fredrinn", "فردرین", MlbbRole.TANK, MlbbRole.FIGHTER, listOf(MlbbLane.JUNGLE, MlbbLane.EXP), "تانک جانگل • تانت و برست"),
        MlbbHero("hylos", "Hylos", "هایلوس", MlbbRole.TANK, null, listOf(MlbbLane.ROAM, MlbbLane.EXP), "اچ‌پی بسیار بالا • راندن دشمن"),
        MlbbHero("gatotkaca", "Gatotkaca", "گاتوتکاچا", MlbbRole.TANK, MlbbRole.FIGHTER, listOf(MlbbLane.ROAM, MlbbLane.EXP), "ضد فیزیکال • تانت و پرش"),
        MlbbHero("gloo", "Gloo", "گلو", MlbbRole.TANK, null, listOf(MlbbLane.EXP, MlbbLane.ROAM), "چسبیدن به هدف • بازتولید"),
        MlbbHero("baxia", "Baxia", "باکسیا", MlbbRole.TANK, null, listOf(MlbbLane.JUNGLE, MlbbLane.ROAM), "ضد هیل ذاتی • روتیشن"),
        MlbbHero("akai", "Akai", "آکای", MlbbRole.TANK, null, listOf(MlbbLane.ROAM, MlbbLane.JUNGLE), "پین به دیوار • زون لورد"),
        MlbbHero("barats", "Barats", "باراتس", MlbbRole.TANK, MlbbRole.FIGHTER, listOf(MlbbLane.JUNGLE, MlbbLane.EXP), "غول‌پیکر • ساپرس"),
        MlbbHero("lolita", "Lolita", "لولیتا", MlbbRole.SUPPORT, MlbbRole.TANK, listOf(MlbbLane.ROAM), "سپر بازتاب پرتابه‌ها"),
        MlbbHero("johnson", "Johnson", "جانسون", MlbbRole.TANK, MlbbRole.SUPPORT, listOf(MlbbLane.ROAM), "تبدیل به ماشین • کمبو"),
        MlbbHero("belerick", "Belerick", "بلریک", MlbbRole.TANK, null, listOf(MlbbLane.ROAM, MlbbLane.EXP), "ضد اتک اسپید • بازتاب دمیج"),
        MlbbHero("carmilla", "Carmilla", "کارمیلا", MlbbRole.SUPPORT, MlbbRole.TANK, listOf(MlbbLane.ROAM), "لینک دمیج و سی‌سی تیمی"),
        MlbbHero("uranus", "Uranus", "اورانوس", MlbbRole.TANK, null, listOf(MlbbLane.EXP), "ریجن بی‌وقفه • کات لین"),

        // Mages & Mid Laners
        MlbbHero("zhuxin", "Zhuxin", "ژوکسین", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "کنترل مداوم • جابه‌جایی دشمن"),
        MlbbHero("valentina", "Valentina", "والنتینا", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "کپی آلتیمیت حریف"),
        MlbbHero("novaria", "Novaria", "نواریا", MlbbRole.MAGE, null, listOf(MlbbLane.MID, MlbbLane.ROAM), "اسنایپ دوربرد • ویژن"),
        MlbbHero("vexana", "Vexana", "وگسانا", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "شوالیه لورد • سی‌سی آسان"),
        MlbbHero("pharsa", "Pharsa", "فارسا", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "های‌گراند • روتیشن پرنده"),
        MlbbHero("kagura", "Kagura", "کاگورا", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "چتر • پیوریفای و برست"),
        MlbbHero("lunox", "Lunox", "لونوکس", MlbbRole.MAGE, null, listOf(MlbbLane.MID, MlbbLane.JUNGLE), "ضد تانک • ایمیون طلایی"),
        MlbbHero("luo_yi", "Luo Yi", "لوئو یی", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "تلپورت تیمی • واکنش یین‌یانگ"),
        MlbbHero("xavier", "Xavier", "خاویر", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "لیزر سراسری • دفاع بیس"),
        MlbbHero("harith", "Harith", "هاریت", MlbbRole.MAGE, null, listOf(MlbbLane.GOLD, MlbbLane.MID), "دش سریع • شیلد و دمیج"),
        MlbbHero("cecilion", "Cecilion", "سسیلیون", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "هیولای لیت گیم • استک مانا"),
        MlbbHero("kadita", "Kadita", "کادیتا", MlbbRole.MAGE, MlbbRole.ASSASSIN, listOf(MlbbLane.MID, MlbbLane.ROAM), "کمین • برست آبی"),
        MlbbHero("nana", "Nana", "نانا", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "تبدیل مولینا • جان دوم"),
        MlbbHero("aurora", "Aurora", "آرورا", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "فریز منطقه‌ای • جان یخی"),
        MlbbHero("yve", "Yve", "ایو", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "زون کردن با صفحه شطرنج"),
        MlbbHero("lylia", "Lylia", "لیلیا", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "پوک ارلی • بازگشت زمان"),
        MlbbHero("valir", "Valir", "والیر", MlbbRole.MAGE, null, listOf(MlbbLane.MID, MlbbLane.ROAM), "ضد تانک و فایتر • عقب‌رانی"),
        MlbbHero("chang_e", "Chang'e", "چانگ‌ای", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "پوش و سرقت باف • رگبار"),
        MlbbHero("odette", "Odette", "اودت", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "دمیج وسیع تیم‌فایت"),
        MlbbHero("eudora", "Eudora", "یودورا", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "وان‌شات سریع • استان"),
        MlbbHero("zhask", "Zhask", "ژاسک", MlbbRole.MAGE, null, listOf(MlbbLane.MID), "احضار نایتمر • پوش"),
        MlbbHero("faramis", "Faramis", "فارامیس", MlbbRole.SUPPORT, MlbbRole.MAGE, listOf(MlbbLane.MID, MlbbLane.ROAM), "جان اضافه تیمی در فایت"),

        // Marksmen & Gold Laners
        MlbbHero("beatrix", "Beatrix", "بئاتریکس", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "۴ سلاح مختلف • انعطاف‌پذیر"),
        MlbbHero("moskov", "Moskov", "موسکوف", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "تلپورت جهانی • پین به دیوار"),
        MlbbHero("claude", "Claude", "کلود", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "تحرک بالا • دمیج تیم‌فایت"),
        MlbbHero("karrie", "Karrie", "کری", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "ضد تانک واقعی • ترو دمیج"),
        MlbbHero("brody", "Brody", "برودی", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "ارلی قوی • برست تک‌ضربه"),
        MlbbHero("melissa", "Melissa", "ملیسا", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "سپر ضد اساسین • عروسک"),
        MlbbHero("ixia", "Ixia", "ایگزیا", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "رگبار مخروطی تیم‌فایت"),
        MlbbHero("wanwan", "Wanwan", "وان‌وان", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "پرش مداوم • آلتیمیت ایمیون"),
        MlbbHero("granger", "Granger", "گرنجر", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD, MlbbLane.JUNGLE), "پوک دوربرد • بدون مانا"),
        MlbbHero("clint", "Clint", "کلینت", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "پوک لین قوی • برد بلند"),
        MlbbHero("bruno", "Bruno", "برونو", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "کریتیکال سنگین ارلی تا لیت"),
        MlbbHero("natan", "Natan", "ناتان", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "مجیک دمیج • پوش سریع"),
        MlbbHero("lesley", "Lesley", "لزلی", MlbbRole.MARKSMAN, MlbbRole.ASSASSIN, listOf(MlbbLane.GOLD), "ترو دمیج کریتیکال • اسنایپ"),
        MlbbHero("layla", "Layla", "لیلا", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "بیشترین برد بازی در لیت گیم"),
        MlbbHero("miya", "Miya", "میا", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "نامرئی • اتک اسپید لیت گیم"),
        MlbbHero("hanabi", "Hanabi", "هانابی", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "ضد سی‌سی با شیلد • باونس"),
        MlbbHero("irithel", "Irithel", "ایریتل", MlbbRole.MARKSMAN, null, listOf(MlbbLane.GOLD), "شلیک حین حرکت • کریتیکال"),
        MlbbHero("popol_and_kupa", "Popol and Kupa", "پوپول و کوپا", MlbbRole.MARKSMAN, null, listOf(MlbbLane.JUNGLE, MlbbLane.GOLD), "پوش تاور • تله ویژن"),

        // Supports
        MlbbHero("mathilda", "Mathilda", "ماتیلدا", MlbbRole.SUPPORT, MlbbRole.ASSASSIN, listOf(MlbbLane.ROAM), "دش رایگان برای هم‌تیمی"),
        MlbbHero("angela", "Angela", "آنجلا", MlbbRole.SUPPORT, null, listOf(MlbbLane.ROAM), "حفاظت جهانی • شیلد و هیل"),
        MlbbHero("floryn", "Floryn", "فلورین", MlbbRole.SUPPORT, null, listOf(MlbbLane.ROAM), "هیل جهانی ضد آنتی‌هیل"),
        MlbbHero("estes", "Estes", "استس", MlbbRole.SUPPORT, null, listOf(MlbbLane.ROAM), "هیل گروهی فوق‌العاده"),
        MlbbHero("diggie", "Diggie", "دیگی", MlbbRole.SUPPORT, null, listOf(MlbbLane.ROAM), "پاک‌کننده سی‌سی تیمی"),
        MlbbHero("rafaela", "Rafaela", "رافائلا", MlbbRole.SUPPORT, null, listOf(MlbbLane.ROAM), "سرعت حرکت • ضد اسلو"),
        MlbbHero("kaja", "Kaja", "کاجا", MlbbRole.SUPPORT, MlbbRole.FIGHTER, listOf(MlbbLane.ROAM), "ساپرس و کشیدن هدف")
    )

    fun filterHeroes(
        query: String,
        role: MlbbRole?,
        lane: MlbbLane?
    ): List<MlbbHero> {
        val normalized = query.trim().lowercase()
        return heroes.filter { hero ->
            val matchesRole = role == null || hero.primaryRole == role || hero.secondaryRole == role
            val matchesLane = lane == null || lane in hero.lanes
            val matchesQuery = normalized.isEmpty() ||
                hero.nameEn.lowercase().contains(normalized) ||
                hero.nameFa.contains(normalized) ||
                hero.specialtyFa.contains(normalized)
            matchesRole && matchesLane && matchesQuery
        }
    }
}
