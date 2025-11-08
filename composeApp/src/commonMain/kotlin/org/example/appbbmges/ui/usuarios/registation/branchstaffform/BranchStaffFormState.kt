package org.example.appbbmges.ui.usuarios.registation.branchstaffform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

@Composable
fun rememberBranchStaffFormState(): BranchStaffFormState {
    return remember { BranchStaffFormState() }
}

class BranchStaffFormState {
    var data by mutableStateOf(BranchStaffFormData())
        private set
    var errors by mutableStateOf(FormErrors())
    var currentStep by mutableStateOf(BranchStaffFormStep.PERSONAL_INFO)
        private set

    fun updateData(newData: BranchStaffFormData) {
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
            BranchStaffFormStep.PERSONAL_INFO -> BranchStaffFormValidation.validatePersonalInfo(data)
            BranchStaffFormStep.ADDRESS_INFO -> BranchStaffFormValidation.validateAddressInfo(data)
            BranchStaffFormStep.ADDITIONAL_INFO -> BranchStaffFormValidation.validateAdditionalInfo(data)
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
                BranchStaffFormStep.PERSONAL_INFO -> BranchStaffFormStep.ADDRESS_INFO
                BranchStaffFormStep.ADDRESS_INFO -> BranchStaffFormStep.ADDITIONAL_INFO
                BranchStaffFormStep.ADDITIONAL_INFO -> BranchStaffFormStep.CONFIRMATION
                BranchStaffFormStep.CONFIRMATION -> currentStep
            }
            return true
        }
        return false
    }

    fun previousStep() {
        currentStep = when (currentStep) {
            BranchStaffFormStep.ADDRESS_INFO -> BranchStaffFormStep.PERSONAL_INFO
            BranchStaffFormStep.ADDITIONAL_INFO -> BranchStaffFormStep.ADDRESS_INFO
            BranchStaffFormStep.CONFIRMATION -> BranchStaffFormStep.ADDITIONAL_INFO
            BranchStaffFormStep.PERSONAL_INFO -> currentStep
        }
    }
}