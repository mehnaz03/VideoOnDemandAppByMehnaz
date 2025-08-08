package com.mehnaz.videoondemandapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData

// LiveData class to observe network connectivity status changes
class NetworkStatusLiveData(private val context: Context) : LiveData<Boolean>() {

    // Get the ConnectivityManager system service
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // NetworkCallback to listen for network availability changes
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        // Called when network becomes available
        override fun onAvailable(network: Network) {
            postValue(true)  // Update LiveData with true (network connected)
        }

        // Called when network is lost
        override fun onLost(network: Network) {
            postValue(false) // Update LiveData with false (network disconnected)
        }
    }

    // Called when LiveData becomes active (has at least one observer)
    override fun onActive() {
        super.onActive()
        // Check current network connectivity and post initial value
        val network = connectivityManager.activeNetwork
        val isConnected = network?.let {
            connectivityManager.getNetworkCapabilities(it)
                ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
        } ?: false
        postValue(isConnected) // Post current connection status

        // Build a NetworkRequest for internet-capable networks
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        // Register the network callback to listen for changes
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    // Called when LiveData has no active observers
    override fun onInactive() {
        super.onInactive()
        // Unregister the network callback to avoid memory leaks
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}
