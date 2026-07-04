package com.example.drivesafe

import android.view.WindowManager
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drivesafe.view.ChangePasswordActivity
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChangePasswordInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ChangePasswordActivity>()

    private fun isToast(): Matcher<Root> = object : TypeSafeMatcher<Root>() {
        override fun describeTo(description: Description) {
            description.appendText("is toast")
        }

        override fun matchesSafely(root: Root): Boolean {
            val type = root.windowLayoutParams.get().type
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                val windowToken = root.decorView.windowToken
                val appToken = root.decorView.applicationWindowToken
                return windowToken === appToken
            }
            return false
        }
    }

    @Test
    fun testChangePassword_showsError_whenFieldsAreBlank() {
        composeRule.onNodeWithTag("changePasswordButton")
            .performClick()

        onView(withText("All fields are required"))
            .inRoot(isToast())
            .check(matches(isDisplayed()))
    }

    @Test
    fun testChangePassword_showsError_whenPasswordsDoNotMatch() {
        composeRule.onNodeWithTag("oldPasswordField")
            .performTextInput("oldpass123")

        composeRule.onNodeWithTag("newPasswordField")
            .performTextInput("newpass123")

        composeRule.onNodeWithTag("confirmPasswordField")
            .performTextInput("different123")

        composeRule.onNodeWithTag("changePasswordButton")
            .performClick()

        onView(withText("New Password and Confirm Password do not match"))
            .inRoot(isToast())
            .check(matches(isDisplayed()))
    }
}
