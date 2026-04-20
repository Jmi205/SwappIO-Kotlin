package uniandes.isis3510.rewereable.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val symbols = DecimalFormatSymbols().apply { groupingSeparator = '.' }
        val formatter = DecimalFormat("#,###", symbols)

        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val number = originalText.toLongOrNull() ?: 0L
        val formattedText = formatter.format(number)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 0) return 0
                val rawTextBeforeOffset = originalText.substring(0, offset)
                val formattedBeforeOffset = formatter.format(rawTextBeforeOffset.toLongOrNull() ?: 0L)
                return formattedBeforeOffset.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                val textBeforeOffset = formattedText.substring(0, offset.coerceAtMost(formattedText.length))
                return textBeforeOffset.count { it.isDigit() }
            }
        }

        return TransformedText(AnnotatedString(formattedText), offsetMapping)
    }
}