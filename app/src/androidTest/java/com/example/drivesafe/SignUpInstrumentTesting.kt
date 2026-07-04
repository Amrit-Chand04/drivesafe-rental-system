package com.example.drivesafe

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drivesafe.view.SignUpActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpInstrumentTesting {

    @get:Rule
    val composeRule = createAndroidComposeRule<SignUpActivity>()

    @Test
    fun signUpScreen_acceptsInput() {
        composeRule.onNodeWithTag("fullName")
            .performTextInput("John Doe")

        composeRule.onNodeWithTag("signupEmail")
            .performTextInput("john@gmail.com")

        composeRule.onNodeWithTag("phone")
            .performTextInput("9800000000")

        composeRule.onNodeWithTag("signupPassword")
            .performTextInput("password123")

        composeRule.onNodeWithTag("confirmPassword")
            .performTextInput("password123")
    }

    @Test
    fun signUpButton_canBeClicked() {
        composeRule.onNodeWithTag("signupButton")
            .performClick()
    }
}