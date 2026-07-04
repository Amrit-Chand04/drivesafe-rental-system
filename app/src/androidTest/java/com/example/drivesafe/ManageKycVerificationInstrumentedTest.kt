package com.example.drivesafe

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drivesafe.view.ManageKycVerification
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManageKycVerificationInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ManageKycVerification>()

    @Test
    fun testAllFilter_isSelectedByDefault() {
        composeRule.onNodeWithTag("filter_All")
            .assertIsSelected()
    }

    @Test
    fun testPendingFilter_selectsPendingChip() {
        composeRule.onNodeWithTag("filter_Pending")
            .performClick()

        composeRule.onNodeWithTag("filter_Pending")
            .assertIsSelected()

        composeRule.onNodeWithTag("filter_All")
            .assertIsNotSelected()
    }
}
