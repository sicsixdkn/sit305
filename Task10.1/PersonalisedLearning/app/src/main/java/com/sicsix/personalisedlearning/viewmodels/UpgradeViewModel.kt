package com.sicsix.personalisedlearning.viewmodels

import android.content.Context
import android.icu.util.CurrencyAmount
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient
import com.sicsix.personalisedlearning.api.RetrofitInstance
import com.sicsix.personalisedlearning.models.UserPlan
import com.sicsix.personalisedlearning.utility.Payments
import com.sicsix.personalisedlearning.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class UpgradeViewModel @Inject constructor(
    @ApplicationContext context: Context, private val userPreferences: UserPreferences
) : ViewModel() {
    // API instance to make network requests
    private val api = RetrofitInstance.api

    private val _paymentUiState: MutableStateFlow<PaymentUiState> = MutableStateFlow(PaymentUiState.NotStarted)
    val paymentUiState: StateFlow<PaymentUiState> = _paymentUiState.asStateFlow()

    // A client for interacting with the Google Pay API.
    private val paymentsClient: PaymentsClient = Payments.createPaymentsClient(context)

    init {
        // Verify the user's ability to pay with Google Pay when the view model is created.
        viewModelScope.launch {
            verifyGooglePayReadiness()
        }
    }

    /**
     * Get the user's plan.
     */
    fun getUserPlan(): String {
        return userPreferences.getUserDetails()!!.plan
    }

    /**
     * Update the user's plan.
     */
    fun updateUserPlan(plan: String) {
        viewModelScope.launch {
            val response = api.updateUserBilling("Bearer ${userPreferences.getJWTToken()}", UserPlan(plan))
            if (response.isSuccessful) {
                val user = userPreferences.getUserDetails()
                userPreferences.setUserDetails(user!!.copy(plan = plan))
            }
        }
    }


    /**
     * Determine the user's ability to pay with a payment method supported by your app and display
     * a Google Pay payment button.
    ) */
    private suspend fun verifyGooglePayReadiness() {
        val newUiState: PaymentUiState = try {
            if (fetchCanUseGooglePay()) {
                PaymentUiState.Available
            } else {
                PaymentUiState.Error(CommonStatusCodes.ERROR)
            }
        } catch (exception: ApiException) {
            PaymentUiState.Error(exception.statusCode, exception.message)
        }

        _paymentUiState.update { newUiState }
    }

    /**
     * Determine the user's ability to pay with a payment method supported by your app.
    ) */
    private suspend fun fetchCanUseGooglePay(): Boolean {
        val request = IsReadyToPayRequest.fromJson(Payments.isReadyToPayRequest().toString())
        return paymentsClient.isReadyToPay(request).await()
    }

    /**
     * Creates a [Task] that starts the payment process with the transaction details included.
     *
     * @return a [Task] with the payment information.
    ) */
    fun getLoadPaymentDataTask(price: CurrencyAmount): Task<PaymentData> {
        val paymentDataRequestJson = Payments.getPaymentDataRequest(price)
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())
        return paymentsClient.loadPaymentData(request)
    }

    /**
     * Set the payment data in the view model.
     *
     * @param paymentData the payment data to set.
     */
    fun setPaymentData(paymentData: PaymentData) {
        val payState = extractPaymentBillingName(paymentData)?.let {
            PaymentUiState.PaymentCompleted(payerName = it)
        } ?: PaymentUiState.Error(CommonStatusCodes.INTERNAL_ERROR)

        _paymentUiState.update { payState }
    }

    /**
     * Extract the billing name from the payment data.
     *
     * @param paymentData the payment data to extract the billing name from.
     * @return the billing name.
     */
    private fun extractPaymentBillingName(paymentData: PaymentData): String? {
        val paymentInformation = paymentData.toJson()

        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")
            val billingName = paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name")
            Log.d("BillingName", billingName)

            // Logging token string.
            Log.d(
                "Google Pay token", paymentMethodData.getJSONObject("tokenizationData").getString("token")
            )

            return billingName
        } catch (error: JSONException) {
            Log.e("handlePaymentSuccess", "Error: $error")
        }

        return null
    }
}

// Represents the state of the transaction
abstract class PaymentUiState internal constructor() {
    object NotStarted : PaymentUiState()
    object Available : PaymentUiState()
    data class PaymentCompleted(val payerName: String) : PaymentUiState()
    data class Error(val code: Int, val message: String? = null) : PaymentUiState()
}

    