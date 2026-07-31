package org.druidanet.druidnet.utils

import android.net.ConnectivityManager
import android.net.NetworkCapabilities


fun ConnectivityManager.isConnected(): Boolean {
    val network = this.activeNetwork ?: return false
    val capabilities = this.getNetworkCapabilities(network) ?: return false
    val hasInternetCapability =
        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    val hasTransportCapability = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
    return hasInternetCapability && hasTransportCapability
}
