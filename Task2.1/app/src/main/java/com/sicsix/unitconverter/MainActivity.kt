@file:OptIn(ExperimentalMaterial3Api::class)

package com.sicsix.unitconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sicsix.unitconverter.ui.theme.UnitConverterTheme

// Main Activity that sets the content view
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThemedSurface()
        }
    }
}

// Interface defining a unit type that includes the display name a function that must be implemented to convert to another unit
interface UnitType {
    val displayName: String
    fun convert(value: Double, toUnit: UnitType): Double
}

// An Enum of weight units that implements UnitType
enum class WeightUnit(override val displayName: String, private val conversionToKg: Double) : UnitType {
    // Each enum constant represents a weight unit with its name and conversion rate to kilograms
    Ounce("ounces", 0.0283495),
    Pound("pounds", 0.453592),
    Ton("short tons", 907.185),
    Gram("gram", 0.001),
    Kilogram("kilograms", 1.0),
    MetricTon("metric tons", 1000.0);

    // Converts a weight from this unit to another unit
    override fun convert(value: Double, toUnit: UnitType): Double {
        // Ensure that toUnit is a WeightUnit
        if (toUnit !is WeightUnit)
            throw IllegalArgumentException("Incompatible unit type")

        // Convert from the current unit to kg, then from kg to the target unit
        val valueInKg = value * conversionToKg
        return valueInKg / toUnit.conversionToKg
    }
}

// An Enum of distance units that implements UnitType
enum class DistanceUnit(override val displayName: String, private val conversionToMetres: Double) : UnitType {
    // Each enum constant represents a distance unit with its name and conversion rate to metres
    Inches("inches", 0.0254),
    Feet("feet", 0.3048),
    Yards("yards", 0.9144),
    Miles("miles", 1609.34),
    Centimetres("centimetres", 0.01),
    Metres("metres", 1.0),
    Kilometres("kilometres", 1000.0);

    // Converts a distance from this unit to another unit
    override fun convert(value: Double, toUnit: UnitType): Double {
        // Ensure that toUnit is a DistanceUnit
        if (toUnit !is DistanceUnit)
            throw IllegalArgumentException("Incompatible unit type")

        // Convert from the current unit to metres, then from metres to the target unit
        val valueInMetres = value * conversionToMetres
        return valueInMetres / toUnit.conversionToMetres
    }
}

// An Enum of temperature units that implements UnitType
enum class TemperatureUnit(
    override val displayName: String,
    private val convertToKelvin: (Double) -> Double,
    private val convertFromKelvin: (Double) -> Double
) : UnitType {
    // Each enum constant represents a temperature unit with its name and two anonymous functions to convert to and from kelvin
    Kelvin("kelvin", { it }, { it }),
    Celsius("Celsius", { it + 273.15 }, { it - 273.15 }),
    Fahrenheit("Fahrenheit", { (it - 32) * 5 / 9 + 273.15 }, { (it - 273.15) * 9 / 5 + 32 });

    // Converts a temperature from this unit to another unit
    override fun convert(value: Double, toUnit: UnitType): Double {
        // Ensure that toUnit is a TemperatureUnit
        if (toUnit !is TemperatureUnit)
            throw IllegalArgumentException("Incompatible unit type")

        // Convert the input value to kelvin first, then to the target unit
        return toUnit.convertFromKelvin(this.convertToKelvin(value))
    }
}

// Wraps the app in the theme and applies a themed background
@Composable
@Preview
fun ThemedSurface() {
    UnitConverterTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            UnitConverterApp()
        }
    }
}

// Main Composable function for the unit converter app
@Composable
fun UnitConverterApp() {
    // State variables to hold the user input and output result
    var inputValue by remember { mutableStateOf("") }
    var resultValue by remember { mutableStateOf("") }
    // Holds the list of possible options available for the "From" dropdown
    val fromUnitOptions: List<UnitType> = DistanceUnit.entries + WeightUnit.entries + TemperatureUnit.entries
    // Holds the list of possible options for the "To" dropdown, this is mutable as the entries will be changed based on the "From" selection
    var toUnitOptions: List<UnitType> by remember { mutableStateOf(DistanceUnit.entries.drop(1)) }
    // State variables to hold whether the dropdown boxes are expanded
    var fromExpanded by remember { mutableStateOf(false) }
    var toExpanded by remember { mutableStateOf(false) }
    // State variables to hold the user selected "From" and "To" units
    var fromUnit by remember { mutableStateOf(fromUnitOptions[0]) }
    var toUnit by remember { mutableStateOf(toUnitOptions[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Unit Converter",
            style = MaterialTheme.typography.headlineLarge
        )

        // Input field for entering the value to be converted
        TextField(
            value = inputValue,
            onValueChange = {
                inputValue = it
                resultValue = ""
            },
            label = { Text("Enter value") }
        )

        // Dropdown box to select the "From" unit
        ExposedDropdownMenuBox(
            expanded = fromExpanded,
            onExpandedChange = { fromExpanded = it }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = fromUnit.displayName,
                onValueChange = { },
                label = { Text("Convert from") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fromExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = fromExpanded,
                onDismissRequest = { fromExpanded = false }
            ) {
                // Populates the dropdown box with the available "From" options
                fromUnitOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.displayName) },
                        onClick = {
                            // Update the selected unit and close the dropdown box
                            fromUnit = selectionOption
                            fromExpanded = false
                            resultValue = ""
                            // Update the available "To Unit" options based on the selected unit type
                            toUnitOptions = when (fromUnit) {
                                is DistanceUnit -> DistanceUnit.entries
                                is WeightUnit -> WeightUnit.entries
                                is TemperatureUnit -> TemperatureUnit.entries
                                else -> throw IllegalArgumentException("Unsupported unit type")
                            }
                            // Remove the currently selected "From Unit" from the "To Unit" list
                            toUnitOptions = toUnitOptions.filter { o -> o != fromUnit }
                            // Set the selected "To Unit" to the first available unit
                            toUnit = toUnitOptions[0]
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        // Dropdown box to select the "To" unit
        ExposedDropdownMenuBox(
            expanded = toExpanded,
            onExpandedChange = { toExpanded = it }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                readOnly = true,
                value = toUnit.displayName,
                onValueChange = { },
                label = { Text("Convert to") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = toExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
            )
            ExposedDropdownMenu(
                expanded = toExpanded,
                onDismissRequest = { toExpanded = false }
            ) {
                toUnitOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.displayName) },
                        onClick = {
                            // Update the selected unit and close the dropdown box
                            toUnit = selectionOption
                            toExpanded = false
                            resultValue = ""
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        // Button to perform the calculation and update the result
        Button(onClick = {
            // Tries to convert the input value, and if it fails returns invalid input, this would occur if a valid number was not entered
            resultValue = try {
                // Convert the input value to a double and call the convert function on the enum constant, passing in the "To" unit enum
                fromUnit.convert(inputValue.toDouble(), toUnit).toString()
            } catch (e: NumberFormatException) {
                "Invalid input"
            }
        }) {
            Text("Convert")
        }

        Text(
            text = "Result:",
            style = MaterialTheme.typography.headlineMedium
        )

        // Text box to display the result
        Text(
            text = resultValue,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}