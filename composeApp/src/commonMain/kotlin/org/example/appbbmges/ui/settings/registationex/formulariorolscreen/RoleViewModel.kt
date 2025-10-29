package org.example.appbbmges.ui.settings.registationex.formulariorolscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.appbbmges.FranchiseEntity
import org.example.appbbmges.RoleEntity
import org.example.appbbmges.data.Repository

class RoleViewModel(private val repository: Repository) : ViewModel() {

    // ==================== ESTADO DE LA LISTA ====================

    var rolesWithPermissions by mutableStateOf<List<RoleWithPermissions>>(emptyList())
        private set

    var isLoadingList by mutableStateOf(true)
        private set

    var listError by mutableStateOf<String?>(null)
        private set

    // ==================== ESTADO DEL FORMULARIO ====================

    var currentStep by mutableStateOf(RoleFormStep.INFO)
        private set

    var formState by mutableStateOf<RoleFormState>(RoleFormState.Idle)
        private set

    var formData by mutableStateOf(RoleFormData())
        private set

    var validationResult by mutableStateOf(RoleValidationResult(true))
        private set

    var editingRole by mutableStateOf<RoleEntity?>(null)
        private set

    var allFranchises by mutableStateOf<List<FranchiseEntity>>(emptyList())
        private set

    // ==================== INICIALIZACIÓN ====================

    init {
        loadRoles()
        loadFranchises()
    }

    // ==================== OPERACIONES DE CARGA ====================

    fun loadRoles() {
        viewModelScope.launch {
            isLoadingList = true
            listError = null
            try {
                val roles = repository.getAllRoles()
                rolesWithPermissions = roles.map { role ->
                    val permissions = repository.getRolePermissions(role.id)
                    RoleWithPermissions(role, permissions)
                }
            } catch (e: Exception) {
                listError = "Error al cargar roles: ${e.message}"
                println(listError)
            } finally {
                isLoadingList = false
            }
        }
    }

    /**
     * Carga todas las franquicias disponibles
     */
    private fun loadFranchises() {
        viewModelScope.launch {
            try {
                allFranchises = repository.getAllFranchises()
            } catch (e: Exception) {
                println("Error al cargar franquicias: ${e.message}")
            }
        }
    }

    // ==================== OPERACIONES DEL FORMULARIO ====================

    /**
     * Inicia el formulario para crear un nuevo rol
     */
    fun startNewRole() {
        editingRole = null
        formData = RoleFormData()
        currentStep = RoleFormStep.INFO
        formState = RoleFormState.Idle
        validationResult = RoleValidationResult(true)
    }

    /**
     * Inicia el formulario para editar un rol existente
     */
    fun startEditRole(role: RoleEntity) {
        viewModelScope.launch {
            editingRole = role
            try {
                val permissions = repository.getRolePermissions(role.id)
                val franchises = repository.getFranchisesByRole(role.id)

                formData = RoleFormData(
                    name = role.name,
                    description = role.description ?: "",
                    selectedPermissions = permissions.toSet(),
                    selectedFranchises = franchises.map { it.id }.toSet(),
                    assignToAllFranchises = franchises.size == allFranchises.size && allFranchises.isNotEmpty()
                )
                currentStep = RoleFormStep.INFO
                formState = RoleFormState.Idle
                validationResult = RoleValidationResult(true)
            } catch (e: Exception) {
                formState = RoleFormState.Error("Error al cargar datos del rol: ${e.message}")
            }
        }
    }

    /**
     * Cancela el formulario y limpia el estado
     */
    fun cancelForm() {
        editingRole = null
        formData = RoleFormData()
        currentStep = RoleFormStep.INFO
        formState = RoleFormState.Idle
        validationResult = RoleValidationResult(true)
    }

    // ==================== ACTUALIZACIÓN DE DATOS ====================

    fun updateRoleName(name: String) {
        formData = formData.copy(name = name)
        if (validationResult.nameError != null) {
            validationResult = validationResult.copy(nameError = null)
        }
    }

    fun updateRoleDescription(description: String) {
        formData = formData.copy(description = description)
    }

    fun togglePermission(permission: String, checked: Boolean) {
        formData = formData.copy(
            selectedPermissions = if (checked) {
                formData.selectedPermissions + permission
            } else {
                formData.selectedPermissions - permission
            }
        )
        if (checked && validationResult.permissionError != null) {
            validationResult = validationResult.copy(permissionError = null)
        }
    }

    fun toggleFranchise(franchiseId: Long, checked: Boolean) {
        formData = formData.copy(
            selectedFranchises = if (checked) {
                formData.selectedFranchises + franchiseId
            } else {
                formData.selectedFranchises - franchiseId
            }
        )
        if (checked && validationResult.franchiseError != null) {
            validationResult = validationResult.copy(franchiseError = null)
        }
    }

    fun toggleAssignToAllFranchises(checked: Boolean) {
        formData = formData.copy(
            assignToAllFranchises = checked,
            selectedFranchises = if (checked) emptySet() else formData.selectedFranchises
        )
        if (checked) {
            validationResult = validationResult.copy(franchiseError = null)
        }
    }

    // ==================== NAVEGACIÓN ENTRE PASOS ====================

    /**
     * Valida el paso actual antes de continuar
     */
    private fun validateCurrentStep(): Boolean {
        val result = when (currentStep) {
            RoleFormStep.INFO -> RoleValidator.validateName(
                formData.name,
                rolesWithPermissions.map { it.role },
                editingRole?.id
            )
            RoleFormStep.PERMISSIONS -> RoleValidator.validatePermissions(
                formData.selectedPermissions
            )
            RoleFormStep.FRANCHISES -> RoleValidator.validateFranchises(
                formData.selectedFranchises,
                formData.assignToAllFranchises
            )
            RoleFormStep.CONFIRMATION -> RoleValidator.validateComplete(
                formData.name,
                rolesWithPermissions.map { it.role },
                formData.selectedPermissions,
                formData.selectedFranchises,
                formData.assignToAllFranchises,
                editingRole?.id
            )
        }
        validationResult = result
        return result.isValid
    }

    /**
     * Avanza al siguiente paso del formulario
     */
    fun proceedToNext() {
        if (!validateCurrentStep()) return

        when (currentStep) {
            RoleFormStep.INFO -> {
                currentStep = RoleFormStep.PERMISSIONS
                validationResult = validationResult.copy(permissionError = null)
            }
            RoleFormStep.PERMISSIONS -> {
                currentStep = RoleFormStep.FRANCHISES
                validationResult = validationResult.copy(franchiseError = null)
            }
            RoleFormStep.FRANCHISES -> {
                currentStep = RoleFormStep.CONFIRMATION
            }
            RoleFormStep.CONFIRMATION -> {
                saveRole()
            }
        }
    }

    /**
     * Retrocede al paso anterior del formulario
     */
    fun goToPreviousStep() {
        currentStep = when (currentStep) {
            RoleFormStep.PERMISSIONS -> RoleFormStep.INFO
            RoleFormStep.FRANCHISES -> RoleFormStep.PERMISSIONS
            RoleFormStep.CONFIRMATION -> RoleFormStep.FRANCHISES
            else -> RoleFormStep.INFO
        }
        validationResult = RoleValidationResult(true)
    }

    // ==================== GUARDAR ROL ====================

    /**
     * Guarda o actualiza el rol en la base de datos
     */
    private fun saveRole() {
        if (!validateCurrentStep()) return

        formState = RoleFormState.Loading

        viewModelScope.launch {
            try {
                if (editingRole != null) {
                    updateExistingRole()
                } else {
                    createNewRole()
                }

                formState = RoleFormState.Success
                loadRoles()
                cancelForm()
            } catch (e: Exception) {
                formState = RoleFormState.Error(
                    "Error al ${if (editingRole != null) "actualizar" else "guardar"} rol: ${e.message}"
                )
            }
        }
    }

    /**
     * Crea un nuevo rol con sus permisos y franquicias
     */
    private suspend fun createNewRole() {
        // Insertar rol
        repository.insertRole(
            formData.name.trim(),
            formData.description.ifBlank { null }
        )

        // Buscar el rol recién creado
        val createdRole = repository.getAllRoles().find {
            it.name.equals(formData.name.trim(), ignoreCase = true)
        } ?: throw Exception("No se pudo encontrar el rol creado")

        // Asignar permisos
        formData.selectedPermissions.forEach { permission ->
            repository.insertRolePermission(createdRole.id, permission)
        }

        // Asignar franquicias
        assignFranchisesToRole(createdRole.id)
    }

    /**
     * Actualiza un rol existente con sus permisos y franquicias
     */
    private suspend fun updateExistingRole() {
        val role = editingRole ?: throw Exception("No hay rol para actualizar")

        // Actualizar información básica del rol
        repository.updateRole(
            role.id,
            formData.name.trim(),
            formData.description.ifBlank { null }
        )

        // Actualizar permisos (eliminar todos y reinsertar)
        repository.deleteRolePermissions(role.id)
        formData.selectedPermissions.forEach { permission ->
            repository.insertRolePermission(role.id, permission)
        }

        // Actualizar franquicias (eliminar todas y reinsertar)
        val currentFranchises = repository.getFranchisesByRole(role.id)
        currentFranchises.forEach { franchise ->
            repository.removeRoleFromFranchise(role.id, franchise.id)
        }
        assignFranchisesToRole(role.id)
    }

    /**
     * Asigna franquicias a un rol según la configuración
     */
    private suspend fun assignFranchisesToRole(roleId: Long) {
        if (formData.assignToAllFranchises) {
            allFranchises.forEach { franchise ->
                repository.assignRoleToFranchise(roleId, franchise.id)
            }
        } else {
            formData.selectedFranchises.forEach { franchiseId ->
                repository.assignRoleToFranchise(roleId, franchiseId)
            }
        }
    }

    // ==================== ELIMINAR ROL ====================

    /**
     * Elimina un rol y todas sus relaciones
     */
    fun deleteRole(role: RoleEntity) {
        viewModelScope.launch {
            try {
                repository.deleteRolePermissions(role.id)
                repository.deleteRole(role.id)
                loadRoles()
            } catch (e: Exception) {
                listError = "Error al eliminar rol: ${e.message}"
                println(listError)
            }
        }
    }
}