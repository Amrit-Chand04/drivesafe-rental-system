package com.example.drivesafe.viewmodel

import android.app.Application
import com.example.drivesafe.model.VehicleFirebaseModel
import com.example.drivesafe.model.VehicleModel
import com.example.drivesafe.repo.VehicleRepo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

class VehicleViewModelTest {

    private lateinit var repo: VehicleRepo
    private lateinit var viewModel: VehicleViewModel

    @Before
    fun setUp() {
        repo = mock()
        viewModel = VehicleViewModel(mock<Application>(), repo)
    }

    @Test
    fun `addVehicle succeeds and calls repo`() {
        val vehicle = VehicleModel(name = "Swift", type = "Car", number = "BA 1 PA 1234")
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Vehicle added successfully")
            null
        }.`when`(repo).addVehicle(eq(vehicle), any())

        var successResult = false
        var messageResult = ""
        viewModel.addVehicle(vehicle) { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Vehicle added successfully", messageResult)
        verify(repo).addVehicle(eq(vehicle), any())
    }

    @Test
    fun `updateVehicle succeeds and calls repo`() {
        val vehicle = VehicleFirebaseModel(vehicleId = "v1", name = "Swift")
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Vehicle updated successfully")
            null
        }.`when`(repo).updateVehicle(eq(vehicle), any())

        var successResult = false
        var messageResult = ""
        viewModel.updateVehicle(vehicle) { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Vehicle updated successfully", messageResult)
        verify(repo).updateVehicle(eq(vehicle), any())
    }

    @Test
    fun `deleteVehicle succeeds and removes it from the list`() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Vehicle deleted successfully")
            null
        }.`when`(repo).deleteVehicle(eq("v1"), any())

        var successResult = false
        var messageResult = ""
        viewModel.deleteVehicle("v1") { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Vehicle deleted successfully", messageResult)
        verify(repo).deleteVehicle(eq("v1"), any())
    }
}