package com.example.drivesafe.viewmodel

import com.example.drivesafe.model.OfferModel
import com.example.drivesafe.repo.OfferRepo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class OfferViewModelTest {

    private lateinit var repo: OfferRepo
    private lateinit var viewModel: OfferViewModel
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private fun dateString(daysFromToday: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, daysFromToday)
        return sdf.format(calendar.time)
    }

    private fun validModel() = OfferModel(
        title = "Summer Sale",
        description = "Get discount now",
        discount = 20,
        startDate = dateString(0),
        endDate = dateString(10)
    )

    @Before
    fun setUp() {
        repo = mock()
        viewModel = OfferViewModel(repo)
    }

    @Test
    fun `createOffer blocks when an offer is already active`() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<OfferModel>?) -> Unit>(0)
            callback(true, "Loaded", listOf(validModel().copy(id = "active1")))
            null
        }.`when`(repo).getOffers(any())
        viewModel.loadOffers()

        viewModel.createOffer(validModel()) { _, _ -> }

        assertEquals(
            "An offer is already active. Delete it before creating a new one.",
            viewModel.toast.value
        )
        verify(repo, never()).createOffer(any(), any())
    }

    @Test
    fun `createOffer succeeds and calls repo`() {
        val model = validModel()
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Offer created successfully")
            null
        }.`when`(repo).createOffer(eq(model), any())

        var successResult = false
        var messageResult = ""
        viewModel.createOffer(model) { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Offer created successfully", messageResult)
        assertEquals("Offer created successfully", viewModel.toast.value)
        verify(repo).createOffer(eq(model), any())
    }

    @Test
    fun `loadOffers populates the offers list from repo`() {
        val offer = validModel().copy(id = "o1")
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String, List<OfferModel>?) -> Unit>(0)
            callback(true, "Loaded", listOf(offer))
            null
        }.`when`(repo).getOffers(any())

        viewModel.loadOffers()

        assertEquals(listOf(offer), viewModel.offers.value)
    }

    @Test
    fun `deleteOffer succeeds and calls repo`() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(1)
            callback(true, "Offer deleted succesfully")
            null
        }.`when`(repo).deleteOffer(eq("id1"), any())

        var successResult = false
        var messageResult = ""
        viewModel.deleteOffer("id1") { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("Offer deleted succesfully", messageResult)
        verify(repo).deleteOffer(eq("id1"), any())
    }
}
