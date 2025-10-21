package org.example.appbbmges.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.appbbmges.ui.sessions.SessionManager

class SimpleNavController(initialScreen: Screen) {
    private val _currentScreen = MutableStateFlow(initialScreen)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val backStack: MutableList<Screen> = mutableListOf()

    fun navigateTo(screen: Screen, clearBackStack: Boolean = false) {
        if (clearBackStack) {
            backStack.clear()
        } else {
            if (_currentScreen.value != screen) {
                backStack.add(_currentScreen.value)
            }
        }
        _currentScreen.update { screen }
    }

    fun navigateBack(): Boolean {
        return if (backStack.isNotEmpty()) {
            _currentScreen.update { backStack.last() }
            backStack.removeLast()
            true
        } else {
            println("No hay pantallas en el backStack")
            false
        }
    }

    fun navigateToRoute(route: String, clearBackStack: Boolean = false) {
        val userId = SessionManager.userId
        val franchiseId = SessionManager.franchiseId

        val screen = when (route) {
            "login" -> Screen.Login
            "dashboard" -> {
                if (franchiseId != null) {
                    Screen.Dashboard(userId = userId, franchiseId = franchiseId)
                } else {
                    println("Error: No hay sesión activa para navegar al dashboard")
                    return
                }
            }
            "franquicias" -> Screen.Franquicias(userId = userId)
            "usuarios" -> Screen.Usuarios(userId = userId)
            "disciplinas_horarios" -> Screen.DisciplinasHorarios(userId = userId)
            "productos" -> Screen.Productos(userId = userId)
            "eventos_promociones" -> Screen.EventosPromociones(userId = userId)
            "settings" -> Screen.Settings(userId = userId)
            else -> {
                println("Ruta no reconocida: $route")
                return
            }
        }
        navigateTo(screen, clearBackStack)
    }

    fun canNavigateBack(): Boolean {
        return backStack.isNotEmpty()
    }

    fun navigateAndClearBackStack(screen: Screen) {
        backStack.clear()
        _currentScreen.update { screen }
    }

    fun getCurrentScreen(): Screen {
        return _currentScreen.value
    }

    fun isCurrentScreen(screen: Screen): Boolean {
        return _currentScreen.value == screen
    }

    fun clearBackStack() {
        backStack.clear()
    }

    fun getBackStackSize(): Int {
        return backStack.size
    }
}