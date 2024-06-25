import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        Column {
            DropDownListScreen()
        }
    }
}


@Composable
fun DropDownListScreen() {
    var selectedOption by remember { mutableStateOf(TypeOfTransport.EMPTY) }
    val options = listOf(TypeOfTransport.EMPTY, TypeOfTransport.CARS, TypeOfTransport.MOTORCYCLES)

    Column(modifier = Modifier.padding(16.dp)) {
        DropdownMenuForTypeTransport(selectedOption, options, onOptionSelected = { option ->
            selectedOption = option
        })
        Spacer(modifier = Modifier.height(16.dp))
        ContentBasedOnSelection(selectedOption)
    }
}

@Composable
fun ContentBasedOnSelection(selectedType: TypeOfTransport) {
    when (selectedType) {
        TypeOfTransport.CARS -> {
            CalculationForPassengerCars()
        }
        TypeOfTransport.MOTORCYCLES -> Text(TypeOfTransport.MOTORCYCLES.nameType)
        else -> Text("Unknown option")
    }
}

@Composable
fun DropdownMenuForTypeTransport(selectedOption: TypeOfTransport, options: List<TypeOfTransport>, onOptionSelected: (TypeOfTransport) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedOption.nameType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Вид транспортного средства") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = option.nameType)
                }
            }
        }
    }
}


@Composable
fun DropdownMenuManufacturerCountry(selectedOption: ManufacturerCountry, options: List<ManufacturerCountry>, onOptionSelected: (ManufacturerCountry) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = selectedOption.value,
            onValueChange = {},
            readOnly = true,
            label = { Text("Страна изготовитель") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(imageVector = if (expanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = option.value)
                }
            }
        }
    }
}


@Composable
fun CalculationForPassengerCars() {

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        var manufacturerCountry by remember { mutableStateOf(ManufacturerCountry.EMPTY) }
        val options = listOf(ManufacturerCountry.EMPTY, ManufacturerCountry.FIRST, ManufacturerCountry.SECOND)

        DropdownMenuManufacturerCountry(manufacturerCountry, options, onOptionSelected = { option ->
            manufacturerCountry = option
        })

        Spacer(modifier = Modifier.height(16.dp))

        var text by remember { mutableStateOf("") }
        var isError by remember { mutableStateOf(false) }

        TextField(
            value = text,
            onValueChange = { newText ->
                if (newText.isEmpty() || newText.all { it.isDigit() }) {
                    text = newText
                    isError = false
                } else {
                    isError = true
                }
            },
            label = { Text("Срок эксплуатации ТС") },
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )
        if (isError) {
            Text(
                text = "Only integer values are allowed",
                //color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.body2
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        val lifetime = text.toIntOrNull() ?: 0
        var i1 = calculationOfCoefficientI1(manufacturerCountry, lifetime)
        Text(
            "Значение И1: $i1",
            style = TextStyle(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        var mileageText by remember { mutableStateOf("") }

        TextField(
            value = mileageText,
            onValueChange = { newText ->
                mileageText = newText
            },
            label = { Text("Пробег транспартного средства в тыс. км") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        val mileage = mileageText.toFloatOrNull() ?: 0f
        var i2 = calculationOfCoefficientI2(manufacturerCountry, i1, mileage)
        Text(
            "Значение И2: $i2",
            style = TextStyle(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        val Q = (i1 * lifetime) + (i2 * mileage)

        Text("($lifetime * $i1) + ($mileage * $i2) = $Q")
        Text("(Д * И1) + (П * И2) = Q")
        Text("Д - Срок эксплуатации")
        Text("П - Пробег")
    }

}

fun calculationOfCoefficientI1(manufacturerCountry: ManufacturerCountry, lifetime: Int = 0) : Float {

    var i1: Float = 0f;

    when(manufacturerCountry){
        ManufacturerCountry.FIRST -> {
            when{
                lifetime == 0 -> { i1 = 0f }
                lifetime < 5 -> { i1 = 0.28f }
                lifetime in 5..12 -> { i1 = 0.34f }
                lifetime > 12 -> { i1 = 0.4f }

            }
        }
        ManufacturerCountry.SECOND -> {
            when{
                lifetime == 0 -> { i1 = 0f }
                lifetime < 5 -> { i1 = 0.27f }
                lifetime in 5..12 -> { i1 = 0.35f }
                lifetime > 12 -> { i1 = 0.45f }
            }
        }
        ManufacturerCountry.EMPTY -> { i1 = 0f }
    }

    return i1
}

fun calculationOfCoefficientI2(manufacturerCountry: ManufacturerCountry, i1: Float, mileage: Float = 0f) : Float {

    var i2: Float = 0f;

    when(manufacturerCountry){

        ManufacturerCountry.EMPTY -> {

        }
        ManufacturerCountry.FIRST -> {
            when(i1){
                0.28f -> {
                    when{
                        mileage == 0f ->            { i2 = 0f }
                        mileage < 10f ->            { i2 = 1.9f }
                        mileage in 10f..15f -> { i2 = 1.3f }
                        mileage in 15f..20f -> { i2 = 1.1f }
                        mileage in 20f..25f -> { i2 = 1f }
                        mileage in 25f..35f -> { i2 = 0.8f }
                        mileage > 35f ->            { i2 = 0.6f }
                    }
                }
                0.34f -> {
                    when{
                        mileage == 0f ->            { i2 = 0f }
                        mileage < 10f ->            { i2 = 1.9f }
                        mileage in 10f..15f -> { i2 = 1.3f }
                        mileage in 15f..20f -> { i2 = 1.2f }
                        mileage in 20f..25f -> { i2 = 1.05f }
                        mileage in 25f..35f -> { i2 = 0.9f }
                        mileage > 35f ->            { i2 = 0.7f }
                    }
                }
                0.4f -> {
                    when{
                        mileage == 0f ->            { i2 = 0f }
                        mileage < 10f ->            { i2 = 2.2f }
                        mileage in 10f..15f -> { i2 = 1.5f }
                        mileage in 15f..20f -> { i2 = 1.3f }
                        mileage in 20f..25f -> { i2 = 1.2f }
                        mileage in 25f..35f -> { i2 = 1f }
                        mileage > 35f ->            { i2 = 0.8f }
                    }
                }
            }
        }
        ManufacturerCountry.SECOND -> {
            when(i1){
                0.27f -> {
                    when{
                        mileage == 0f ->            { i2 = 0f }
                        mileage < 10f ->            { i2 = 1.8f }
                        mileage in 10f..15f -> { i2 = 1.2f }
                        mileage in 15f..20f -> { i2 = 1.05f }
                        mileage in 20f..25f -> { i2 = 0.95f }
                        mileage in 25f..35f -> { i2 = 0.75f }
                        mileage > 35f ->            { i2 = 0.65f }
                    }
                }
                0.35f -> {
                    when{
                        mileage == 0f ->            { i2 = 0f }
                        mileage < 10f ->            { i2 = 2f }
                        mileage in 10f..15f -> { i2 = 1.4f }
                        mileage in 15f..20f -> { i2 = 1.3f }
                        mileage in 20f..25f -> { i2 = 1.15f }
                        mileage in 25f..35f -> { i2 = 1f }
                        mileage > 35f ->            { i2 = 0.85f }
                    }
                }
                0.45f -> {
                    when{
                        mileage == 0f ->            { i2 = 0f }
                        mileage < 10f ->            { i2 = 2.4f }
                        mileage in 10f..15f -> { i2 = 1.7f }
                        mileage in 15f..20f -> { i2 = 1.6f }
                        mileage in 20f..25f -> { i2 = 1.3f }
                        mileage in 25f..35f -> { i2 = 1.1f }
                        mileage > 35f ->            { i2 = 0.9f }
                    }
                }
            }
        }
    }
    return i2
}