package uniandes.isis3510.rewereable.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uniandes.isis3510.rewereable.util.connectivity.NetworkStatus

@Composable
fun ConnectivityBanner(
    networkStatus: NetworkStatus,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = networkStatus == NetworkStatus.Unavailable,
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "No internet connection"
                )

                Text(
                    text = "No internet connection. Showing cached data when available.",
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}