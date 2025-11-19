package org.example.appbbmges.ui.usuarios.registation.administrativeform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

@Composable
fun rememberAdministrativeFormState(): AdministrativeFormState {
    return remember { AdministrativeFormState() }
}

class AdministrativeFormState {
    var data by mutableStateOf(AdministrativeFormData())
        private set
    var errors by mutableStateOf(AdministrativeFormErrors())
    var currentStep by mutableStateOf(AdministrativeFormStep.PERSONAL_INFO)
        private set

    fun updateData(newData: AdministrativeFormData) {
        data = newData
        errors = errors.copy(general = null)
    }

    fun generateCredentials() {
        if (data.firstName.isBlank() || data.lastNamePaternal.isBlank() || data.lastNameMaternal.isBlank()) {
            return
        }

        val newUsername = UserCredentialsGenerator.generateUsername(
            firstName = data.firstName,
            lastNamePaternal = data.lastNamePaternal,
            lastNameMaternal = data.lastNameMaternal
        )

        val newPassword = UserCredentialsGenerator.generatePassword(
            firstName = data.firstName,
            lastNamePaternal = data.lastNamePaternal,
            lastNameMaternal = data.lastNameMaternal
        )

        updateData(data.copy(
            username = newUsername,
            password = newPassword
        ))
    }

    fun validateCurrentStep(): Boolean {
        errors = when (currentStep) {
            AdministrativeFormStep.PERSONAL_INFO -> AdministrativeFormValidation.validatePersonalInfo(data)
            AdministrativeFormStep.PROFESSIONAL_INFO -> AdministrativeFormValidation.validateProfessionalInfo(data)
            AdministrativeFormStep.ADDRESS_INFO -> AdministrativeFormValidation.validateAddressInfo(data)
            AdministrativeFormStep.ADDITIONAL_INFO -> AdministrativeFormValidation.validateAdditionalInfo(data)
            else -> AdministrativeFormErrors()
        }
        return errors.isValid
    }

    fun setGeneralError(error: String?) {
        errors = errors.copy(general = error)
    }

    fun nextStep(): Boolean {
        if (validateCurrentStep()) {
            currentStep = when (currentStep) {
                AdministrativeFormStep.PERSONAL_INFO -> AdministrativeFormStep.PROFESSIONAL_INFO
                AdministrativeFormStep.PROFESSIONAL_INFO -> AdministrativeFormStep.ADDRESS_INFO
                AdministrativeFormStep.ADDRESS_INFO -> AdministrativeFormStep.ADDITIONAL_INFO
                AdministrativeFormStep.ADDITIONAL_INFO -> AdministrativeFormStep.CONFIRMATION
                AdministrativeFormStep.CONFIRMATION -> currentStep
            }
            return true
        }
        return false
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            AdministrativeFormStep.PROFESSIONAL_INFO -> AdministrativeFormStep.PERSONAL_INFO
            AdministrativeFormStep.ADDRESS_INFO -> AdministrativeFormStep.PROFESSIONAL_INFO
            AdministrativeFormStep.ADDITIONAL_INFO -> AdministrativeFormStep.ADDRESS_INFO
            AdministrativeFormStep.CONFIRMATION -> AdministrativeFormStep.ADDITIONAL_INFO
            AdministrativeFormStep.PERSONAL_INFO -> currentStep
        }
    }
}