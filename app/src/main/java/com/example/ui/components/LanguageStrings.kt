package com.example.ui.components

/**
 * Localization dictionary supporting English (default) and Gujarati (ગુજરાતી).
 */
object LanguageStrings {

    fun appTitle(lang: String): String = if (lang == "gu") "નટ સોર્ટ" else "Nut Sort"
    fun level(lang: String, num: Int): String = if (lang == "gu") "લેવલ $num" else "Level $num"
    fun sortSameColor(lang: String): String = if (lang == "gu") "એક જ રંગના નટ ગોઠવો" else "SORT THE SAME COLOR"
    fun victory(lang: String): String = if (lang == "gu") "વિજય! શાનદાર!" else "VICTORY!"
    fun levelCompleted(lang: String): String = if (lang == "gu") "લેવલ પૂરું થયું!" else "Level Completed!"
    fun playNext(lang: String): String = if (lang == "gu") "આગળનું લેવલ" else "NEXT LEVEL"
    fun restart(lang: String): String = if (lang == "gu") "ફરી શરૂ" else "Restart"
    fun undo(lang: String): String = if (lang == "gu") "પાછું લો" else "Undo"
    fun extraBolt(lang: String): String = if (lang == "gu") "+બોલ્ટ" else "+Bolt"
    fun hint(lang: String): String = if (lang == "gu") "સંકેત" else "Hint"
    fun settings(lang: String): String = if (lang == "gu") "સેટિંગ્સ" else "Settings"
    fun levelSelect(lang: String): String = if (lang == "gu") "લેવલ પસંદ કરો" else "Select Level"
    fun sound(lang: String): String = if (lang == "gu") "અવાજ (સાઉન્ડ)" else "Sound Effects"
    fun vibration(lang: String): String = if (lang == "gu") "કંપન (વાઇબ્રેશન)" else "Vibration (Haptics)"
    fun language(lang: String): String = if (lang == "gu") "ભાષા: ગુજરાતી" else "Language: English"
    fun moves(lang: String, count: Int): String = if (lang == "gu") "ચાલ: $count" else "Moves: $count"
    fun coins(lang: String): String = if (lang == "gu") "સિક્કા" else "Coins"
    fun earnedCoins(lang: String, amount: Int): String = if (lang == "gu") "+$amount સિક્કા મળ્યા!" else "+$amount Coins Earned!"
    fun unlockNewLevels(lang: String): String = if (lang == "gu") "અમર્યાદિત લેવલ્સ રમો" else "Unlimited Levels"
    fun hintNotFound(lang: String): String = if (lang == "gu") "કોઈ સંભવિત ચાલ નથી! +બોલ્ટ વાપરો." else "No moves left! Try +Bolt or Undo."
    fun extraBoltLimit(lang: String): String = if (lang == "gu") "આ લેવલમાં વધુ બોલ્ટ ઉમેરી શકાતો નથી" else "Cannot add more bolts this level"
    fun outOfCoins(lang: String): String = if (lang == "gu") "પૂરતા સિક્કા નથી! (20 સિક્કા જરૂરી)" else "Not enough coins! (20 coins required)"
    fun extraBoltAdded(lang: String): String = if (lang == "gu") "નવો બોલ્ટ ઉમેરાયો!" else "New empty bolt added!"
    fun noMovesToUndo(lang: String): String = if (lang == "gu") "પાછું લેવા માટે કોઈ ચાલ નથી" else "No moves to undo"
    fun sortedProgress(lang: String, completed: Int, total: Int): String = if (lang == "gu") "સોર્ટ થયેલ: $completed / $total" else "Sorted: $completed / $total"
    fun levelLocked(lang: String, level: Int): String = if (lang == "gu") "લેવલ $level લોક છે! અનલોક કરવા માટે પહેલા લેવલ ${level - 1} પૂર્ણ કરો." else "Level $level is locked! Complete Level ${level - 1} to unlock."
    fun darkMode(lang: String): String = if (lang == "gu") "ડાર્ક મોડ" else "Dark Mode"
    fun lightMode(lang: String): String = if (lang == "gu") "લાઇટ મોડ" else "Light Mode"
    fun themeMode(lang: String, isDark: Boolean): String = if (lang == "gu") (if (isDark) "ડાર્ક મોડ" else "લાઇટ મોડ") else (if (isDark) "Dark Mode" else "Light Mode")
    fun mysteryNutDesc(lang: String): String = if (lang == "gu") "રહસ્યમય નટ: ઉપરનો નટ હટાવો અને રંગ જાણો!" else "Mystery Nut: Remove top nut to reveal color!"

    fun tutorialStep1(lang: String): String = if (lang == "gu") "👉 પહેલા બોલ્ટ પર ટેપ કરી નટ ઊંચકો, પછી ખાલી બોલ્ટ પર મૂકો!" else "👉 Tap Bolt 1 to lift Blue nut, then tap the empty bolt!"
    fun tutorialStep2(lang: String): String = if (lang == "gu") "👉 હવે પીળો નટ પહેલા બોલ્ટ પર મૂકો!" else "👉 Now tap Bolt 2 to move Yellow nut onto Bolt 1!"
    fun tutorialStep3(lang: String): String = if (lang == "gu") "👉 બાકી રહેલો બ્લુ નટ બીજા બોલ્ટ પર મૂકી પૂર્ણ કરો!" else "👉 Now move the Blue nut to finish the level!"
}
