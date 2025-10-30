package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import org.example.appbbmges.RoleEntity

enum class RoleFormStep {
    INFO,
    PERMISSIONS,
    CONFIRMATION
}

sealed class RoleFormState {
    object Idle : RoleFormState()
    object Loading : RoleFormState()
    data class Error(val message: String) : RoleFormState()
    object Success : RoleFormState()
}

data class RoleValidationResult(
    val isValid: Boolean,
    val nameError: String? = null,
    val permissionError: String? = null
)

data class RoleWithPermissions(
    val role: RoleEntity,
    val permissions: List<String>
)