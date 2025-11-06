package org.example.appbbmges.ui.usuarios.registation.franquiciatarioform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

@Composable
fun rememberFranchiseeFormState(): FranchiseeFormState {
    return remember { FranchiseeFormState() }
}

class FranchiseeFormState {
    var data by mutableStateOf(FranchiseeFormData())
        private set
    var errors by mutableStateOf(FormErrors())
    var currentStep by mutableStateOf(FranchiseeFormStep.PERSONAL_INFO)
        private set

    fun updateData(newData: FranchiseeFormData) {
        data = newData
        errors = errors.copy(general = null)
    }

    fun generateCredentials(franchiseName: String) {
        if (data.firstName.isBlank() || data.lastNamePaternal.isBlank() || data.lastNameMaternal.isBlank()) {
            return
        }

        val branchCode = UserCredentialsGenerator.generateBranchCode(franchiseName)

        val newUsername = UserCredentialsGenerator.generateUsername(
            firstName = data.firstName,
            lastNamePaternal = data.lastNamePaternal,
            lastNameMaternal = data.lastNameMaternal,
            branchCode = branchCode
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
            FranchiseeFormStep.PERSONAL_INFO -> FranchiseeFormValidation.validatePersonalInfo(data)
            FranchiseeFormStep.ADDRESS_INFO -> FranchiseeFormValidation.validateAddressInfo(data)
            FranchiseeFormStep.ADDITIONAL_INFO -> FranchiseeFormValidation.validateAdditionalInfo(data)
            else -> FormErrors()
        }
        return errors.isValid
    }

    fun setGeneralError(error: String?) {
        errors = errors.copy(general = error)
    }

    fun nextStep(): Boolean {
        if (validateCurrentStep()) {
            currentStep = when (currentStep) {
                FranchiseeFormStep.PERSONAL_INFO -> FranchiseeFormStep.ADDRESS_INFO
                FranchiseeFormStep.ADDRESS_INFO -> FranchiseeFormStep.ADDITIONAL_INFO
                FranchiseeFormStep.ADDITIONAL_INFO -> FranchiseeFormStep.CONFIRMATION
                FranchiseeFormStep.CONFIRMATION -> currentStep
            }
            return true
        }
        return false
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            FranchiseeFormStep.ADDRESS_INFO -> FranchiseeFormStep.PERSONAL_INFO
            FranchiseeFormStep.ADDITIONAL_INFO -> FranchiseeFormStep.ADDRESS_INFO
            FranchiseeFormStep.CONFIRMATION -> FranchiseeFormStep.ADDITIONAL_INFO
            FranchiseeFormStep.PERSONAL_INFO -> currentStep
        }
    }
}