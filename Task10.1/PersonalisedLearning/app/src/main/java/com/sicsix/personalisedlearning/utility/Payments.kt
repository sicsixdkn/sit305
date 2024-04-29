package com.sicsix.personalisedlearning.utility

import android.content.Context
import android.icu.util.CurrencyAmount
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.WalletConstants
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Utility class to handle payments through Google Pay.
 * */
class Payments {
    companion object {
        // API version for the Google Pay API in use.
        private val baseRequest = JSONObject()
            .put("apiVersion", 2)
            .put("apiVersionMinor", 0)

        /**
         * Gateway specification, set to example for testing purposes.
         */
        private fun gatewayTokenizationSpecification(): JSONObject {
            return JSONObject().apply {
                put("type", "PAYMENT_GATEWAY")
                put(
                    "parameters", JSONObject(
                        mapOf(
                            "gateway" to "example",
                            "gatewayMerchantId" to "exampleGatewayMerchantId"
                        )
                    )
                )
            }
        }

        // Supported payment card networks
        private val allowedCardNetworks = JSONArray(
            listOf(
                "AMEX",
                "DISCOVER",
                "INTERAC",
                "JCB",
                "MASTERCARD",
                "VISA"
            )
        )

        // Supported payment card authentication methods
        private val allowedCardAuthMethods = JSONArray(
            listOf(
                "PAN_ONLY",
                "CRYPTOGRAM_3DS"
            )
        )

        /**
         * JSON Object containing allowed payment methods, the gateway used, and instructions for what user data is required.
         */
        private fun baseCardPaymentMethod(): JSONObject =
            JSONObject()
                .put("type", "CARD")
                .put(
                    "parameters", JSONObject()
                        .put("allowedAuthMethods", allowedCardAuthMethods)
                        .put("allowedCardNetworks", allowedCardNetworks)
                        .put("billingAddressRequired", true)
                        .put(
                            "billingAddressParameters", JSONObject()
                                .put("format", "FULL")
                        )
                )
                .put("tokenizationSpecification", gatewayTokenizationSpecification())

        // JSON Array containing the allowed payment methods
        val allowedPaymentMethods: JSONArray = JSONArray().put(baseCardPaymentMethod())

        /**
         * Creates a new [PaymentsClient] instance.
         *
         * @param context the application context.
         * @return a new [PaymentsClient] instance.
         */
        fun createPaymentsClient(context: Context): PaymentsClient {
            val walletOptions = Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                .build()

            return Wallet.getPaymentsClient(context, walletOptions)
        }

        /**
         * Creates a new [JSONObject] containing the request to check if the user is ready to pay.
         *
         * @return a new [JSONObject].
         */
        fun isReadyToPayRequest(): JSONObject? =
            try {
                baseRequest
                    .put("allowedPaymentMethods", allowedPaymentMethods)
            } catch (e: JSONException) {
                null
            }

        /**
         * Creates a new [JSONObject] containing the transaction information, including price and currency.
         *
         * @param price the price of the transaction.
         * @return a new [JSONObject].
         */
        private fun getTransactionInfo(price: CurrencyAmount): JSONObject =
            JSONObject()
                .put("totalPrice", (price.number.toFloat() * 100).toString())
                .put("totalPriceStatus", "FINAL")
                .put("countryCode", "AU")
                .put("currencyCode", price.currency.currencyCode)

        // JSON Object containing the merchant information
        private val merchantInfo: JSONObject =
            JSONObject().put("merchantName", "Personalised Learning")

        /**
         * Creates a new [JSONObject] containing the request to get payment data from the user.
         *
         * @param price the price of the transaction.
         * @return a new [JSONObject] with the complete transaction information required to proceed.
         */
        fun getPaymentDataRequest(price: CurrencyAmount): JSONObject =
            baseRequest
                .put("allowedPaymentMethods", allowedPaymentMethods)
                .put("transactionInfo", getTransactionInfo(price))
                .put("merchantInfo", merchantInfo)
                .put("shippingAddressRequired", true)
                .put(
                    "shippingAddressParameters", JSONObject()
                        .put("phoneNumberRequired", false)
                        .put("allowedCountryCodes", JSONArray(listOf("AU")))
                )
    }
}