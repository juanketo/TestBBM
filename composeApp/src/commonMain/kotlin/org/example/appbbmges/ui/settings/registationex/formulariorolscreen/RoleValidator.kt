package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import org.example.appbbmges.RoleEntity

object RoleValidator {

    fun validateName(
        name: String,
        existingRoles: List<RoleEntity>,
        excludeId: Long? = null
    ): RoleValidationResult {
        val nameError = when {
            name.isEmpty() -> "El nombre del rol es obligatorio"
            name.length < 3 -> "Debe tener al menos 3 caracteres"
            name.length > 50 -> "Demasiado largo (máximo 50 caracteres)"
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) -> "Solo letras y espacios"
            existingRoles.any { it.name.equals(name, ignoreCase = true) && it.id != excludeId } ->
                "Ya existe un rol con ese nombre"
            else -> null
        }

        return RoleValidationResult(isValid = nameError == null, nameError = nameError)
    }

    fun validatePermissions(selectedPermissions: Set<String>): RoleValidationResult {
        val permissionError = if (selectedPermissions.isEmpty())
            "Debes seleccionar al menos un permiso" else null

        return RoleValidationResult(isValid = permissionError == null, permissionError = permissionError)
    }

    fun validateComplete(
        name: String,
        existingRoles: List<RoleEntity>,
        selectedPermissions: Set<String>,
        excludeId: Long? = null
    ): RoleValidationResult {
        val nameValidation = validateName(name, existingRoles, excludeId)
        val permissionValidation = validatePermissions(selectedPermissions)

        return RoleValidationResult(
            isValid = nameValidation.isValid && permissionValidation.isValid,
            nameError = nameValidation.nameError,
            permissionError = permissionValidation.permissionError
        )
    }
}