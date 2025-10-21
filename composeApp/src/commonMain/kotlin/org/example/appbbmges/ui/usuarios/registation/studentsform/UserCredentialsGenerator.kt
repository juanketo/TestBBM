package org.example.appbbmges.ui.usuarios.registation.studentsform

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random

object UserCredentialsGenerator {

    fun generateUsername(
        firstName: String,
        lastNamePaternal: String,
        lastNameMaternal: String,
        companyAcronym: String = "BBM",
        branchCode: String = "HUE"
    ): String {
        if (firstName.isBlank() || lastNamePaternal.isBlank() || lastNameMaternal.isBlank()) {
            return ""
        }

        val firstLetterPaternal = lastNamePaternal.first().uppercaseChar()
        val firstLetterMaternal = lastNameMaternal.first().uppercaseChar()
        val firstTwoLettersName = firstName.take(2).uppercase()

        val randomSuffix = Random.nextInt(10, 100)

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

        val dateCode = getCurrentDate()
        val randomSuffix = Random.nextInt(10, 100)

        return "$firstLetterName$secondLetterName$firstLetterPaternal$firstLetterMaternal$dateCode$randomSuffix"
    }

    private fun getCurrentDate(): String {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val year = now.year.toString()
        val month = now.monthNumber.toString().padStart(2, '0')
        val day = now.dayOfMonth.toString().padStart(2, '0')
        return "$year$month$day"
    }
}
