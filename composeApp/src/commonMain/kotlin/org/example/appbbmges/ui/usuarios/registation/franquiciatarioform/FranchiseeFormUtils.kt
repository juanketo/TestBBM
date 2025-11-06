package org.example.appbbmges.ui.usuarios.registation.franquiciatarioform

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object FranchiseeFormUtils {

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

    fun getCurrentDate(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = now.year.toString()
        val month = now.monthNumber.toString().padStart(2, '0')
        val day = now.dayOfMonth.toString().padStart(2, '0')
        return "$year-$month-$day"
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
}

object UserCredentialsGenerator {

    fun generateBranchCode(franchiseName: String): String {
        if (franchiseName.isBlank()) return "HUE"

        val cleaned = franchiseName.trim()
            .uppercase()
            .filter { it.isLetter() }

        return when {
            cleaned.length >= 3 -> cleaned.take(3)
            cleaned.length == 2 -> cleaned + "X"
            cleaned.length == 1 -> cleaned + "XX"
            else -> "HUE"
        }
    }

    fun generateUsername(
        firstName: String,
        lastNamePaternal: String,
        lastNameMaternal: String,
        branchCode: String,
        companyAcronym: String = "BBM"
    ): String {
        if (firstName.isBlank() || lastNamePaternal.isBlank() || lastNameMaternal.isBlank()) {
            return ""
        }

        val firstLetterPaternal = lastNamePaternal.first().uppercaseChar()
        val firstLetterMaternal = lastNameMaternal.first().uppercaseChar()
        val firstTwoLettersName = firstName.take(2).uppercase()

        val randomSuffix = kotlin.random.Random.nextInt(10, 100)

        return "$branchCode$firstLetterPaternal$firstLetterMaternal$firstTwoLettersName$companyAcronym$randomSuffix"
    }

    fun generatePassword(
        firstName: String,
        lastNamePaternal: String,
        lastNameMaternal: String
    ): String {
        if (firstName.isBlank() || lastNamePaternal.isBlank() || lastNameMaternal.isBlank()) {
            return ""
        }

        val firstLetterName = firstName.first().uppercaseChar()
        val secondLetterName = firstName.drop(1).firstOrNull()?.lowercaseChar() ?: 'a'
        val firstLetterPaternal = lastNamePaternal.first().uppercaseChar()
        val firstLetterMaternal = lastNameMaternal.first().lowercaseChar()

        val dateCode = FranchiseeFormUtils.getCurrentDate().replace("-", "")
        val randomSuffix = kotlin.random.Random.nextInt(10, 100)

        return "$firstLetterName$secondLetterName$firstLetterPaternal$firstLetterMaternal$dateCode$randomSuffix"
    }
}