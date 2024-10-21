package com.aura.data.repository

import com.aura.data.call.TransferInfo
import com.aura.data.call.UserInfo
import com.aura.data.network.UserClient
import com.aura.data.response.AccountsResponseItem
import com.aura.data.response.LoginResponse
import com.aura.data.response.TransferResponse
import com.aura.domain.model.AccountResponseModel
import com.aura.domain.model.GrantResponseModel
import com.aura.domain.model.TransferResponseModel
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import retrofit2.Response

@ExtendWith(MockitoExtension::class)
class UserRepositoryTest {

    @Mock
    private lateinit var userMock: UserClient

    @Mock
    private lateinit var repository : UserRepository

    @BeforeEach
    fun initRepo(){
        repository = UserRepository(userMock)
    }

    @Test
    fun callUserVerification() = runTest {
        //Arrange
        val mockLoginResponse = LoginResponse(true)
        val response = Response.success(mockLoginResponse)

        whenever(userMock.pushConnexionRequest(UserInfo("id", "password"))).thenReturn(response)

        // When the repository emits a value
        val items = repository.callUserVerification("id", "password").toList() // Returns the first item in the flow
        val listOfRequire = listOf(Result.Loading, Result.Success(GrantResponseModel(true)))


        // Then check it's the expected item
        assertEquals(listOfRequire, items)
    }

    @Test
    fun callUserVerificationFalse() = runTest {
        //Arrange
        val mockLoginResponse = LoginResponse(false)
        val response = Response.success(mockLoginResponse)

        whenever(userMock.pushConnexionRequest(UserInfo("id", "password"))).thenReturn(response)

        // When the repository emits a value
        val items = repository.callUserVerification("id", "password").toList() // Returns the first item in the flow
        val listOfRequire = listOf(Result.Loading, Result.Success(GrantResponseModel(false)))


        // Then check it's the expected item
        assertEquals(listOfRequire, items)
    }

    @Test
    fun callUserVerificationThrow() = runTest {
        //Arrange
        val errorResponseBody = """{"error": "Internal Server Error"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<LoginResponse>(500, errorResponseBody)

        whenever(userMock.pushConnexionRequest(UserInfo("id", "password"))).thenReturn(response)

        // When the repository emits a value
        val items = repository.callUserVerification("id", "password").toList() // Returns the first item in the flow
        val listOfRequire = listOf(Result.Loading, Result.Failure("Invalid data"))


        // Then check it's the expected item
        assertEquals(listOfRequire, items)
    }

    @Test
    fun getUserAccounts_Success() = runTest { //No need For False, because toDomain Model is mocked (result same that accounts)
        // Arrange
        val mockAccountsItems = listOf(
            AccountsResponseItem(balance = 2.0, id = "id", main = true)
        )
        val response = Response.success(mockAccountsItems)

        whenever(userMock.getAccounts("id")).thenReturn(response)

        // Act
        val items = repository.getUserAccounts("id").toList()

        // Simulate To Domain Model
        val expectedAccountsModels = mockAccountsItems.map {
            AccountResponseModel(balance = it.balance, id = it.id, main = it.main)
        }
        val expected = listOf(
            Result.Loading,
            Result.Success(expectedAccountsModels)
        )

        // Assert
        assertEquals(expected, items)
    }

    @Test
    fun getUserAccounts_Failure() = runTest {
        // Arrange
        val errorResponseBody = """{"error": "Not Found"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<List<AccountsResponseItem>>(404, errorResponseBody)

        whenever(userMock.getAccounts("id")).thenReturn(response)

        // Act
        val items = repository.getUserAccounts("id").toList()

        // Assert
        val expected = listOf(
            Result.Loading,
            Result.Failure("Invalid data") // Error message returned
        )
        assertEquals(expected, items)
    }



    @Test
    fun askForTransfer_Success() = runTest {
        // Arrange
        val mockTransferResponse = TransferResponse(true)
        val response = Response.success(mockTransferResponse)

        whenever(userMock.pushTransfer(TransferInfo("sender", "recipient", 100.0))).thenReturn(response)

        // Act
        val items = repository.askForTransfer("sender", "recipient", 100.0).toList()

        // Conversion manuelle en TransferResponseModel pour le test
        val expectedTransferModel = TransferResponseModel(true)
        val expected = listOf(
            Result.Loading,
            Result.Success(expectedTransferModel)
        )

        // Assert
        assertEquals(expected, items)
    }

    @Test
    fun askForTransfer_SuccessFalse() = runTest {
        // Arrange
        val mockTransferResponse = TransferResponse(false)
        val response = Response.success(mockTransferResponse)

        whenever(userMock.pushTransfer(TransferInfo("sender", "recipient", 100.0))).thenReturn(response)

        // Act
        val items = repository.askForTransfer("sender", "recipient", 100.0).toList()

        // Conversion manuelle en TransferResponseModel pour le test
        val expectedTransferModel = TransferResponseModel(false)
        val expected = listOf(
            Result.Loading,
            Result.Success(expectedTransferModel)
        )

        // Assert
        assertEquals(expected, items)
    }

    @Test
    fun askForTransfer_Failure() = runTest {
        // Arrange
        val errorResponseBody = """{"error": "Transfer Failed"}"""
            .toResponseBody("application/json".toMediaTypeOrNull())
        val response = Response.error<TransferResponse>(400, errorResponseBody)

        whenever(userMock.pushTransfer(TransferInfo("sender", "recipient", 100.0))).thenReturn(response)

        // Act
        val items = repository.askForTransfer("sender", "recipient", 100.0).toList()

        // Assert
        val expected = listOf(
            Result.Loading,
            Result.Failure("Recipient Name Invalid") // Error message returned
        )
        assertEquals(expected, items)
    }

}