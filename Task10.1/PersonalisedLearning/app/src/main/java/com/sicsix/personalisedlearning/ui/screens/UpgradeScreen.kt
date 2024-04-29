package com.sicsix.personalisedlearning.ui.screens

import android.icu.util.CurrencyAmount
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.wallet.contract.TaskResultContracts.GetPaymentDataResult
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton
import com.sicsix.personalisedlearning.utility.Payments
import com.sicsix.personalisedlearning.viewmodels.PaymentUiState
import com.sicsix.personalisedlearning.viewmodels.UpgradeViewModel
import java.util.Currency

@Composable
fun UpgradeScreen(viewModel: UpgradeViewModel = viewModel()) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp, 40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Column(
                Modifier
                    .padding(0.dp, 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Upgrade",
                    style = MaterialTheme.typography.displayLarge
                )
                Text(
                    text = "your experience",
                    style = MaterialTheme.typography.displaySmall
                )
            }

            LazyColumn {
                items(UpgradeOption.entries) { option ->
                    UpgradeCard(
                        upgradePackage = option.upgradePackage,
                        description = option.description,
                        cost = option.cost,
                        isBestSeller = option.isBestSeller,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

enum class UpgradeOption(val upgradePackage: String, val description: String, val cost: CurrencyAmount, val isBestSeller: Boolean) {
    Starter("Starter", "Faster quiz generation", CurrencyAmount(5, Currency.getInstance("AUD")), false),
    Intermediate("Intermediate", "Improved quiz quality", CurrencyAmount(8, Currency.getInstance("AUD")), true),
    Advanced("Advanced", "Priority quiz generation", CurrencyAmount(12, Currency.getInstance("AUD")), false)
}

@Composable
fun UpgradeCard(
    upgradePackage: String,
    description: String,
    cost: CurrencyAmount,
    isBestSeller: Boolean,
    viewModel: UpgradeViewModel
) {
    // Collect the payment state which changes based on the user's payment status (e.g. available, processing, etc.)
    val payState = viewModel.paymentUiState.collectAsState()

    // Store the current user plan in a mutable state
    var currentPlan by rememberSaveable { mutableStateOf(viewModel.getUserPlan()) }

    // Creates a launcher to request payment data from the user
    val paymentDataLauncher = rememberLauncherForActivityResult(contract = GetPaymentDataResult()) { taskResult ->
        when (taskResult.status.statusCode) {
            // If the payment data is successfully retrieved, set the payment data in the view model and update the user's plan
            CommonStatusCodes.SUCCESS -> {
                taskResult.result?.let {
                    viewModel.setPaymentData(it)
                    viewModel.updateUserPlan(upgradePackage)
                    currentPlan = upgradePackage
                }
            }
        }
    }

    ElevatedCard(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = if (currentPlan == upgradePackage) CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) else
            CardDefaults.elevatedCardColors()
    )
    {
        Column(
            Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(
                Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = upgradePackage,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {
                    if (isBestSeller) {
                        Card(Modifier.padding(bottom = 4.dp)) {
                            Text(
                                text = "Best Seller!",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(10.dp, 3.dp)
                            )
                        }
                    }
                    Text(
                        text = "${cost.currency.symbol}${cost.number}",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = "per month",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            if (payState.value == PaymentUiState.Available && currentPlan == "Free") {
                PayButton(
                    onClick = {
                        // Launches the payment data task to retrieve the user's payment data
                        val task = viewModel.getLoadPaymentDataTask(cost)
                        task.addOnCompleteListener(paymentDataLauncher::launch)
                    },
                    allowedPaymentMethods = Payments.allowedPaymentMethods.toString(),
                    modifier = Modifier.fillMaxWidth(),
                    theme = ButtonTheme.Light,
                    type = ButtonType.Subscribe
                )
            }
        }
    }
}