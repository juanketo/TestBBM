package org.example.appbbmges.ui.usuarios.registation.studentsform

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

object DateUtils {

    fun calculateAge(birthDateString: String): Double? {

        if (birthDateString.length != 10 || !birthDateString.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
            return null
        }

        val parts = birthDateString.split("-")
        val year = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val day = parts[2].toIntOrNull() ?: return null

        if (!isValidDate(year, month, day)) return null

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val birthDate = LocalDate(year, month, day)

        if (birthDate > currentDate) return null

        var ageYears = currentDate.year - birthDate.year

        if (currentDate.monthNumber < birthDate.monthNumber ||
            (currentDate.monthNumber == birthDate.monthNumber && currentDate.dayOfMonth < birthDate.dayOfMonth)) {
            ageYears--
        }

        var ageMonths = if (currentDate.monthNumber >= birthDate.monthNumber) {
            currentDate.monthNumber - birthDate.monthNumber
        } else {
            12 + currentDate.monthNumber - birthDate.monthNumber
        }

        if (currentDate.dayOfMonth < birthDate.dayOfMonth) {
            ageMonths--
            if (ageMonths < 0) {
                ageMonths = 11
                ageYears--
            }
        }

        val ageDays = if (currentDate.dayOfMonth >= birthDate.dayOfMonth) {
            currentDate.dayOfMonth - birthDate.dayOfMonth
        } else {

            val previousMonth = if (currentDate.monthNumber == 1) 12 else currentDate.monthNumber - 1
            val previousYear = if (currentDate.monthNumber == 1) currentDate.year - 1 else currentDate.year
            val daysInPreviousMonth = getDaysInMonth(previousYear, previousMonth)
            daysInPreviousMonth - birthDate.dayOfMonth + currentDate.dayOfMonth
        }

        val decimalAge = ageYears + (ageMonths / 12.0) + (ageDays / 365.25)

        return decimalAge
    }

    fun calculateDetailedAge(birthDateString: String): Triple<Int, Int, Int>? {
        if (birthDateString.length != 10 || !birthDateString.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
            return null
        }

        val parts = birthDateString.split("-")
        val year = parts[0].toIntOrNull() ?: return null
        val month = parts[1].toIntOrNull() ?: return null
        val day = parts[2].toIntOrNull() ?: return null

        if (!isValidDate(year, month, day)) return null

        val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val birthDate = LocalDate(year, month, day)

        if (birthDate > currentDate) return null

        var ageYears = currentDate.year - birthDate.year
        var ageMonths = currentDate.monthNumber - birthDate.monthNumber
        var ageDays = currentDate.dayOfMonth - birthDate.dayOfMonth

        if (ageDays < 0) {
            ageMonths--
            val previousMonth = if (currentDate.monthNumber == 1) 12 else currentDate.monthNumber - 1
            val previousYear = if (currentDate.monthNumber == 1) currentDate.year - 1 else currentDate.year
            ageDays += getDaysInMonth(previousYear, previousMonth)
        }

        if (ageMonths < 0) {
            ageYears--
            ageMonths += 12
        }

        return Triple(ageYears, ageMonths, ageDays)
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        return when (month) {
            2 -> if (isLeapYear(year)) 29 else 28
            4, 6, 9, 11 -> 30
            else -> 31
        }
    }

    private fun isValidDate(year: Int, month: Int, day: Int): Boolean {
        if (month < 1 || month > 12) return false
        if (day < 1) return false
        return day <= getDaysInMonth(year, month)
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun formatDateFromMillis(millis: Long): String {
        val instant = Instant.fromEpochMilliseconds(millis)
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

        return "${localDate.year}-" +
                "${localDate.monthNumber.toString().padStart(2, '0')}-" +
                localDate.dayOfMonth.toString().padStart(2, '0')
    }

    fun formatDateForDisplay(isoDate: String): String {
        if (isoDate.length != 10) return isoDate
        val parts = isoDate.split("-")
        if (parts.size != 3) return isoDate
        return "${parts[2]}/${parts[1]}/${parts[0]}"
    }

    fun convertToDisplayFormat(date: String): String {
        return if (date.matches("\\d{4}-\\d{2}-\\d{2}".toRegex())) {
            formatDateForDisplay(date)
        } else {
            date
        }
    }
}

object CURPGenerator {
    fun generate(
        firstName: String,
        lastNamePaternal: String,
        lastNameMaternal: String,
        birthDate: String,
        gender: String
    ): String {
        println("Generando CURP para: $firstName $lastNamePaternal $lastNameMaternal")
        if (firstName.isBlank() || lastNamePaternal.isBlank() || birthDate.length != 10) return ""

        val parts = birthDate.split("-")
        if (parts.size != 3) return ""

        val year = parts[0].takeLast(2)
        val month = parts[1].padStart(2, '0')
        val day = parts[2].padStart(2, '0')

        val vowels = "AEIOU"
        val consonants = "BCDFGHJKLMNPQRSTVWXYZ"
        val firstLetter = lastNamePaternal.first().uppercaseChar()
        val firstVowel = lastNamePaternal.drop(1).firstOrNull { it.uppercaseChar() in vowels }?.uppercaseChar() ?: 'X'
        val secondLetter = if (lastNameMaternal.isNotBlank()) lastNameMaternal.first().uppercaseChar() else 'X'
        val cleanFirstName = firstName.trim().split(" ").firstOrNull { it.isNotBlank() } ?: return ""
        val thirdLetter = cleanFirstName.first().uppercaseChar()
        val sexLetter = when (gender.lowercase()) {
            "masculino", "hombre", "m" -> "H"
            "femenino", "mujer", "f" -> "M"
            else -> "H"
        }
        val state = "DF"
        val firstConsonant = lastNamePaternal.drop(1).firstOrNull { it.uppercaseChar() in consonants }?.uppercaseChar() ?: 'X'
        val secondConsonant = if (lastNameMaternal.isNotBlank()) {
            lastNameMaternal.drop(1).firstOrNull { it.uppercaseChar() in consonants }?.uppercaseChar() ?: 'X'
        } else 'X'
        val thirdConsonant = cleanFirstName.drop(1).firstOrNull { it.uppercaseChar() in consonants }?.uppercaseChar() ?: 'X'

        // ✅ Compatible con Kotlin Multiplatform
        val randomDigits = Random.nextInt(0, 100).toString().padStart(2, '0')

        return "$firstLetter$firstVowel$secondLetter$thirdLetter$year$month${day}$sexLetter$state$firstConsonant$secondConsonant$thirdConsonant$randomDigits"
    }
}

object TextFormatters {
    fun formatName(input: String): String {
        if (input.isBlank()) return ""
        return input.trim()
            .split("\\s+".toRegex())
            .filter { it.isNotBlank() }
            .joinToString(" ") { word ->
                word.lowercase().replaceFirstChar { it.titlecase() }
            }
    }

    fun cleanAndFormatNameInput(input: String): String {
        val filtered = input.filter { it.isLetter() || it.isWhitespace() }
        val singleSpaced = filtered.replace("\\s{2,}".toRegex(), " ")
        val noLeadingSpace = if (singleSpaced.startsWith(" ")) {
            singleSpaced.substring(1)
        } else singleSpaced

        return formatRealTime(noLeadingSpace)
    }

    private fun formatRealTime(input: String): String {
        if (input.isBlank()) return ""

        val words = input.split(" ")
        return words.mapIndexed { index, word ->
            if (word.isNotBlank()) {
                word.lowercase().replaceFirstChar { it.titlecase() }
            } else word
        }.joinToString(" ")
    }

    fun cleanNameInput(input: String): String {
        val filtered = input.filter { it.isLetter() || it.isWhitespace() }
        return filtered.replace("\\s{2,}".toRegex(), " ")
    }
}