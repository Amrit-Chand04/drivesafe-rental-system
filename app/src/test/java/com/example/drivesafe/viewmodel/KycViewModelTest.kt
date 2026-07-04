package com.example.drivesafe.viewmodel

import android.app.Application
import android.net.Uri
import com.example.drivesafe.model.KycFirebaseModel
import com.example.drivesafe.model.KycModel
import com.example.drivesafe.repo.KycRepo
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

class KycViewModelTest {

    private lateinit var repo: KycRepo
    private lateinit var viewModel: KycViewModel
    private val someUri: Uri = mock()

    private fun validModel() = KycModel(
        name = "John Doe",
        phone = "9800000000",
        doc = someUri,
        photo = someUri
    )

    @Before
    fun setUp() {
        repo = mock()
        viewModel = KycViewModel(mock<Application>(), repo)
    }

    @Test
    fun `submitKyc blocks when a required field is missing`() {
        viewModel.submitKyc(validModel().copy(name = "")) { _, _ -> }

        assertEquals("Enter name", viewModel.toast.value)
        verify(repo, never()).submitKyc1(any(), any(), any(), any(), any())
    }

    @Test
    fun `submitKyc succeeds and calls repo`() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean, String) -> Unit>(4)
            callback(true, "KYC Submitted Successfully")
            null
        }.`when`(repo).submitKyc1(eq("John Doe"), eq("9800000000"), any(), any(), any())

        var successResult = false
        var messageResult = ""
        viewModel.submitKyc(validModel()) { success, message ->
            successResult = success
            messageResult = message
        }

        assertTrue(successResult)
        assertEquals("KYC Submitted Successfully", messageResult)
        assertEquals("KYC Submitted Successfully", viewModel.toast.value)
    }

    @Test
    fun `loadMyKycRecord populates record from repo`() {
        val record = KycFirebaseModel(uid = "u1", name = "John", status = "pending")
        doAnswer { invocation ->
            val callback = invocation.getArgument<(KycFirebaseModel?) -> Unit>(0)
            callback(record)
            null
        }.`when`(repo).getMyKycRecord(any())

        viewModel.loadMyKycRecord()

        assertEquals(record, viewModel.myKycRecord.value)
    }

    @Test
    fun `updateKycStatus approves and calls repo`() {
        doAnswer { invocation ->
            val callback = invocation.getArgument<(List<KycFirebaseModel>) -> Unit>(0)
            callback(listOf(KycFirebaseModel(uid = "u1", status = "pending")))
            null
        }.`when`(repo).getAllKycRecords(any())
        viewModel.loadAllKycRecords()

        doAnswer { invocation ->
            val callback = invocation.getArgument<(Boolean) -> Unit>(3)
            callback(true)
            null
        }.`when`(repo).updateKycStatus(eq("u1"), eq("approved"), any(), any())

        viewModel.updateKycStatus("u1", "approved")

        verify(repo).updateKycStatus(eq("u1"), eq("approved"), any(), any())
        assertEquals("approved", viewModel.allKycList.value.first { it.uid == "u1" }.status)
    }
}
