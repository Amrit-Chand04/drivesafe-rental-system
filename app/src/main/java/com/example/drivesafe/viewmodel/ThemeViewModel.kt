package com.example.drivesafe.viewmodel

import androidx.lifecycle.ViewModel
import com.example.drivesafe.ui.theme.AppThemeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ThemeViewModel : ViewModel() {

    private val _selectedTheme = MutableStateFlow(AppThemeState.theme.value)
    val selectedTheme: StateFlow<String> = _selectedTheme.asStateFlow()

    fun setTheme(theme: String) {
        _selectedTheme.value = theme
        AppThemeState.theme.value = theme
    }
}
