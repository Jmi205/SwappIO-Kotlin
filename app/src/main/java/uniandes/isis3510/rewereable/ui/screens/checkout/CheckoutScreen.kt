package uniandes.isis3510.rewereable.ui.screens.checkout

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import uniandes.isis3510.rewereable.ui.theme.GlassBackground
import uniandes.isis3510.rewereable.util.CardNumberVisualTransformation
import uniandes.isis3510.rewereable.util.ExpiryDateVisualTransformation
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel,
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val cardNumber by viewModel.cardNumber.collectAsState()
    val cardHolder by viewModel.cardHolder.collectAsState()
    val document by viewModel.document.collectAsState()
    val expiryDate by viewModel.expiryDate.collectAsState()
    val cvv by viewModel.cvv.collectAsState()

    val productName by viewModel.productName.collectAsState()
    val productPrice by viewModel.productPrice.collectAsState()
    val productImage by viewModel.productImage.collectAsState()
    val sellerName by viewModel.sellerName.collectAsState()

    // Formateador para el botón final
    val currencyFormatter = remember { DecimalFormat("$#,###") }

    val isFormValid = cardNumber.length == 16 &&
            cardHolder.isNotBlank() &&
            document.isNotBlank() &&
            expiryDate.length == 4 &&
            cvv.length >= 3

    LaunchedEffect(uiState) {
        if (uiState is CheckoutUiState.Success) {
            Toast.makeText(context, "Payment successful!", Toast.LENGTH_LONG).show()
            onSuccess()
        }
    }

    val glassModifier = Modifier
        .background(GlassBackground, RoundedCornerShape(16.dp))
        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- Header ---
            Row(
                modifier = Modifier.fillMaxWidth().background(GlassBackground).padding(top = 48.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.4f))) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Checkout", style = MaterialTheme.typography.titleLarge, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // --- Order Summary Card ---
                Text("Order Summary", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Row(
                    modifier = Modifier.fillMaxWidth().then(glassModifier).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = productImage,
                        contentDescription = "Product",
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Seller: $sellerName", fontSize = 14.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(currencyFormatter.format(productPrice), fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
                    }
                }

                Divider(color = Color.LightGray.copy(alpha = 0.5f))

                // --- Payment Details ---
                Text("Payment Details", fontWeight = FontWeight.Bold, color = Color.DarkGray)

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { if (it.filter { char -> char.isDigit() }.length <= 16) viewModel.cardNumber.value = it.filter { char -> char.isDigit() } },
                    label = { Text("Card Number") },
                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                    visualTransformation = CardNumberVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = uiState !is CheckoutUiState.Loading
                )

                OutlinedTextField(
                    value = cardHolder,
                    onValueChange = { viewModel.cardHolder.value = it },
                    label = { Text("Card Holder Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = uiState !is CheckoutUiState.Loading
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { if (it.filter { char -> char.isDigit() }.length <= 4) viewModel.expiryDate.value = it.filter { char -> char.isDigit() } },
                        label = { Text("MM/YY") },
                        visualTransformation = ExpiryDateVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        enabled = uiState !is CheckoutUiState.Loading
                    )

                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { if (it.filter { char -> char.isDigit() }.length <= 4) viewModel.cvv.value = it.filter { char -> char.isDigit() } },
                        label = { Text("CVV") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        enabled = uiState !is CheckoutUiState.Loading
                    )
                }

                OutlinedTextField(
                    value = document,
                    onValueChange = { if (it.filter { char -> char.isDigit() }.length <= 11) viewModel.document.value = it.filter { char -> char.isDigit() } },
                    label = { Text("ID Document (CC/CE)") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = uiState !is CheckoutUiState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // --- Sticky Pay Button ---
            Box(
                modifier = Modifier.fillMaxWidth().background(Color.White).padding(16.dp)
            ) {
                Button(
                    onClick = { viewModel.processPayment() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = isFormValid && uiState !is CheckoutUiState.Loading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (uiState is CheckoutUiState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Lock, contentDescription = "Secure", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pay ${currencyFormatter.format(productPrice)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}