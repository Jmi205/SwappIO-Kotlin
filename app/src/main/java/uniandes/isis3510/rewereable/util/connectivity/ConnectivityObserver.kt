package uniandes.isis3510.rewereable.util.connectivity

import kotlinx.coroutines.flow.Flow

interface ConnectivityObserver {
    fun observe(): Flow<NetworkStatus>
}

enum class NetworkStatus {
    Available,
    Unavailable
}