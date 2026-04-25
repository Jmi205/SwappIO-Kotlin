package uniandes.isis3510.rewereable.util.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class AndroidConnectivityObserver(
    context: Context
) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun observe(): Flow<NetworkStatus> {
        return callbackFlow {
            trySend(getCurrentStatus())

            val callback = object : ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    trySend(getCurrentStatus())
                }

                override fun onLost(network: Network) {
                    trySend(getCurrentStatus())
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    val hasInternet =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

                    trySend(
                        if (hasInternet) NetworkStatus.Available
                        else NetworkStatus.Unavailable
                    )
                }

                override fun onUnavailable() {
                    trySend(NetworkStatus.Unavailable)
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }
        }.distinctUntilChanged()
    }

    private fun getCurrentStatus(): NetworkStatus {
        val network = connectivityManager.activeNetwork
            ?: return NetworkStatus.Unavailable

        val capabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return NetworkStatus.Unavailable

        val hasInternet =
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        return if (hasInternet) NetworkStatus.Available
        else NetworkStatus.Unavailable
    }
}