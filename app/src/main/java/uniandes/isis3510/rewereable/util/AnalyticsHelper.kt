package uniandes.isis3510.rewereable.util

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

object AnalyticsHelper {
    private val analytics = Firebase.analytics

    // --- EVENTOS DE NAVEGACIÓN (Tipo 5) ---
    fun logScreenView(screenName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screenName)
        }
    }

    // --- EVENTOS DE PRODUCTO (Tipo 4 & 5) ---
    fun logProductView(productId: String, styles: List<String>, price: Double) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, styles.joinToString(","))
            putDouble(FirebaseAnalytics.Param.PRICE, price)
            putString(FirebaseAnalytics.Param.CURRENCY, "COP")
        }
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_ITEM, bundle)
    }

    // --- EVENTOS DE CONVERSIÓN Y CHAT (Tipo 3) ---
    fun logStartChat(productId: String, sellerId: String) {
        analytics.logEvent("start_chat_attempt") {
            param("product_id", productId)
            param("seller_id", sellerId)
        }
    }

    fun logMessageSent(chatId: String, hasProductContext: Boolean) {
        analytics.logEvent("message_sent") {
            param("chat_id", chatId)
            param("has_context", if (hasProductContext) 1L else 0L)
        }
    }

    // --- EVENTOS DE EMBUDO (Funnel - Tipo 3) ---
    fun logFunnelStep(stepName: String, funnelName: String) {
        analytics.logEvent("funnel_step") {
            param("step_name", stepName)
            param("funnel_name", funnelName)
        }
    }
}