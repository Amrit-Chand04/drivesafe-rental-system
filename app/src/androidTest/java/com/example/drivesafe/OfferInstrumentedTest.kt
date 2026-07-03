package com.example.drivesafe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drivesafe.view.OffersActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OfferInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<OffersActivity>()

    @Test
    fun testCreateOfferButton_opensCreateOfferDialog() {
        composeRule.onNodeWithTag("createOfferButton")
            .performClick()

        composeRule.onNodeWithTag("saveButton")
            .assertIsDisplayed()
    }

    @Test
    fun testCancelButton_closesCreateOfferDialog() {
        composeRule.onNodeWithTag("createOfferButton")
            .performClick()

        composeRule.onNodeWithTag("titleField")
            .performTextInput("Summer Sale")

        composeRule.onNodeWithTag("cancelButton")
            .performClick()

        composeRule.onNodeWithTag("saveButton")
            .assertDoesNotExist()
    }
}
