package org.example.appbbmges.ui.sessions

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.appbbmges.data.Repository
import org.example.appbbmges.ui.permissions.PermissionHelper

object SessionManager {
    private var _userId: Long? = null
    private var _franchiseId: Long? = null
    private var _permissionHelper: PermissionHelper? = null
    private var _repository: Repository? = null

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Inactive)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    val userId: Long?
        get() = _userId

    val franchiseId: Long?
        get() = _franchiseId

    val permissionHelper: PermissionHelper?
        get() = _permissionHelper

    fun initSession(
        userId: Long,
        franchiseId: Long,
        repository: Repository
    ) {
        try {
            _userId = userId
            _franchiseId = franchiseId
            _repository = repository

            _permissionHelper = PermissionHelper(
                userId = userId,
                franchiseId = franchiseId,
                repository = repository
            )

            _permissionHelper?.loadPermissions()

            if (_permissionHelper?.isLoaded?.value == true) {
                _sessionState.value = SessionState.Active(userId, franchiseId)
            } else {
                _sessionState.value = SessionState.Error("Error al cargar permisos")
            }
        } catch (e: Exception) {
            _sessionState.value = SessionState.Error("Error al inicializar sesión: ${e.message}")
        }
    }

    fun isSessionActive(): Boolean {
        return _userId != null &&
                _franchiseId != null &&
                _permissionHelper?.isLoaded?.value == true
    }

    fun clearSession() {
        _permissionHelper?.clearPermissions()
        _userId = null
        _franchiseId = null
        _permissionHelper = null
        _repository = null
        _sessionState.value = SessionState.Inactive
    }

    fun requirePermissionHelper(): PermissionHelper {
        return _permissionHelper ?: throw IllegalStateException("Sesión no iniciada. Llama a initSession() primero.")
    }

    fun requireUserId(): Long {
        return _userId ?: throw IllegalStateException("Usuario no autenticado")
    }

    fun requireFranchiseId(): Long {
        return _franchiseId ?: throw IllegalStateException("Franquicia no seleccionada")
    }

    fun getSessionInfo(): String {
        return buildString {
            appendLine("=== Información de Sesión ===")
            appendLine("Usuario ID: ${_userId ?: "No autenticado"}")
            appendLine("Franquicia ID: ${_franchiseId ?: "No seleccionada"}")
            appendLine("Estado: ${_sessionState.value}")
            appendLine("Permisos cargados: ${_permissionHelper?.isLoaded?.value ?: false}")
            if (_permissionHelper != null) {
                appendLine("\n${_permissionHelper?.getPermissionsSummary()}")
            }
        }
    }
}

sealed class SessionState {
    object Inactive : SessionState()
    data class Active(val userId: Long, val franchiseId: Long) : SessionState()
    data class Error(val message: String) : SessionState()
}