package com.example.drivesafe

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.drivesafe.view.AdminManageVehicle
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdminManageVehicleInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<AdminManageVehicle>()

    @Test
    fun testAddVehicleButton_opensAddVehicleDialog() {
        composeRule.onNodeWithTag("addVehicleButton")
            .performClick()

        composeRule.onNodeWithTag("saveButton")
            .assertIsDisplayed()
    }

    @Test
    fun testCancelButton_closesAddVehicleDialog() {
        composeRule.onNodeWithTag("addVehicleButton")
            .performClick()

        composeRule.onNodeWithTag("vehicleNameField")
            .performTextInput("Swift")

        composeRule.onNodeWithTag("cancelButton")
            .performClick()

        composeRule.onNodeWithTag("saveButton")
            .assertDoesNotExist()
    }
}
