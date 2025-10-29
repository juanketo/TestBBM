package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import org.example.appbbmges.RoleEntity

// ==================== ENUMS ====================

enum class RoleFormStep {
    INFO,
    PERMISSIONS,
    FRANCHISES,
    CONFIRMATION
}

// ==================== ESTADOS ====================

sealed class RoleFormState {
    object Idle : RoleFormState()
    object Loading : RoleFormState()
    data class Error(val message: String) : RoleFormState()
    object Success : RoleFormState()
}

// ==================== MODELOS DE DATOS ====================

data class RoleValidationResult(
    val isValid: Boolean,
    val nameError: String? = null,
    val permissionError: String? = null,
    val franchiseError: String? = null
)

data class RoleWithPermissions(
    val role: RoleEntity,
    val permissions: List<String>
)

data class RoleFormData(
    val name: String = "",
    val description: String = "",
    val selectedPermissions: Set<String> = emptySet(),
    val selectedFranchises: Set<Long> = emptySet(),
    val assignToAllFranchises: Boolean = false
)

// ==================== VALIDADOR ====================

object RoleValidator {

    fun validateName(
        name: String,
        existingRoles: List<RoleEntity>,
        excludeId: Long? = null
    ): RoleValidationResult {
        val trimmedName = name.trim()

        val nameError = when {
            trimmedName.isEmpty() ->
                "El nombre del rol es obligatorio"
            trimmedName.length < 3 ->
                "Debe tener al menos 3 caracteres"
            trimmedName.length > 50 ->
                "Demasiado largo (máximo 50 caracteres)"
            !trimmedName.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) ->
                "Solo letras y espacios"
            existingRoles.any {
                it.name.trim().equals(trimmedName, ignoreCase = true) && it.id != excludeId
            } -> "Ya existe un rol con ese nombre"
            else -> null
        }

        return RoleValidationResult(
            isValid = nameError == null,
            nameError = nameError
        )
    }

    fun validatePermissions(
        selectedPermissions: Set<String>
    ): RoleValidationResult {
        val permissionError = if (selectedPermissions.isEmpty())
            "Debes seleccionar al menos un permiso"
        else null

        return RoleValidationResult(
            isValid = permissionError == null,
            permissionError = permissionError
        )
    }

    fun validateFranchises(
        selectedFranchises: Set<Long>,
        assignToAllFranchises: Boolean
    ): RoleValidationResult {
        val franchiseError = if (!assignToAllFranchises && selectedFranchises.isEmpty())
            "Debes seleccionar al menos una franquicia"
        else null

        return RoleValidationResult(
            isValid = franchiseError == null,
            franchiseError = franchiseError
        )
    }

    fun validateComplete(
        name: String,
        existingRoles: List<RoleEntity>,
        selectedPermissions: Set<String>,
        selectedFranchises: Set<Long>,
        assignToAllFranchises: Boolean,
        excludeId: Long? = null
    ): RoleValidationResult {
        val nameValidation = validateName(name, existingRoles, excludeId)
        val permissionValidation = validatePermissions(selectedPermissions)
        val franchiseValidation = validateFranchises(selectedFranchises, assignToAllFranchises)

        return RoleValidationResult(
            isValid = nameValidation.isValid &&
                    permissionValidation.isValid &&
                    franchiseValidation.isValid,
            nameError = nameValidation.nameError,
            permissionError = permissionValidation.permissionError,
            franchiseError = franchiseValidation.franchiseError
        )
    }
}

// ==================== UTILIDADES ====================

/**
 * Formatea un nombre de permiso para mostrar en UI
 * Ejemplo: "USUARIOS_CREAR" -> "Usuarios Crear"
 */
fun formatPermissionName(permission: String): String {
    return permission
        .replace("_", " ")
        .split(" ")
        .joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { it.uppercase() }
        }
}