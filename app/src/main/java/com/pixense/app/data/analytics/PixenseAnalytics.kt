package com.pixense.app.data.analytics

import android.util.Log

object PixenseAnalytics {
    private const val TAG = "PixenseAnalytics"

    fun logEvent(eventName: String, params: Map<String, Any> = emptyMap()) {
        val paramStr = if (params.isNotEmpty()) " with params: $params" else ""
        Log.d(TAG, "[Analytics Event] $eventName$paramStr")
    }
}
