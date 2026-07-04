package com.example.drivesafe

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drivesafe.view.LoginActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentTesting {

    @get:Rule
    val composeRule = createAndroidComposeRule<LoginActivity>()

    @Test
    fun loginScreen_acceptsInput() {
        composeRule.onNodeWithTag("email")
            .performTextInput("test@gmail.com")

        composeRule.onNodeWithTag("password")
            .performTextInput("password123")
    }

    @Test
    fun loginScreen_signUpButtonExists() {
        composeRule.onNodeWithTag("signup")
            .performClick()
    }
}