package com.example.emergencyambulanceoptimizer

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var textToSpeech: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textToSpeech = TextToSpeech(this) {
            textToSpeech.language = Locale.US
        }

        setContent {
            AmbulanceOptimizerApp()
        }
    }

    override fun onDestroy() {
        textToSpeech.stop()
        textToSpeech.shutdown()
        super.onDestroy()
    }

    private fun speak(message: String) {
        textToSpeech.speak(
            message,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
    }

    @Composable
    fun AmbulanceOptimizerApp() {

        var routeSelected by remember {
            mutableStateOf(false)
        }

        var journeyStarted by remember {
            mutableStateOf(false)
        }

        var journeyCompleted by remember {
            mutableStateOf(false)
        }

        var progress by remember {
            mutableStateOf(0)
        }

        var selectedRoute by remember {
            mutableStateOf("Not selected")
        }

        var estimatedTime by remember {
            mutableStateOf("--")
        }

        var trafficStatus by remember {
            mutableStateOf("Moderate")
        }

        var systemMessage by remember {
            mutableStateOf(
                "Ready for emergency route optimization."
            )
        }

        // =============================================
        // SIMULATED AMBULANCE MOVEMENT
        // =============================================

        LaunchedEffect(journeyStarted) {

            if (journeyStarted && !journeyCompleted) {

                progress = 0

                while (progress < 100) {

                    delay(1000)

                    progress += 10

                    estimatedTime =
                        when {
                            progress < 30 -> "14 minutes"
                            progress < 50 -> "11 minutes"
                            progress < 70 -> "8 minutes"
                            progress < 90 -> "4 minutes"
                            else -> "1 minute"
                        }

                    trafficStatus =
                        when {
                            progress < 40 -> "Low"
                            progress < 70 -> "Moderate"
                            else -> "Low"
                        }

                    systemMessage =
                        "Ambulance is moving on $selectedRoute. Progress: $progress%"

                    if (progress == 50) {

                        systemMessage =
                            "Traffic update received. Route remains optimal."

                        speak(
                            "Traffic update. Current route remains optimal."
                        )
                    }
                }

                journeyCompleted = true

                estimatedTime = "Arrived"

                systemMessage =
                    "Ambulance has reached the selected hospital."

                speak(
                    "Ambulance has arrived at the hospital."
                )
            }
        }

        MaterialTheme {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {

                // =============================================
                // TITLE
                // =============================================

                Text(
                    text = "🚑 Emergency Ambulance Optimizer",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "AI-based Emergency Route Assistance",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // =============================================
                // AMBULANCE STATUS
                // =============================================

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "🚑 Ambulance Status",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            if (journeyStarted && !journeyCompleted)
                                "🟢 Ambulance: On Emergency Journey"
                            else if (journeyCompleted)
                                "🔵 Ambulance: Arrived"
                            else
                                "🟢 Ambulance: Available"
                        )

                        Text("📍 Location: Current Location")

                        Text(
                            "🚨 Emergency Priority: HIGH"
                        )

                        Text(
                            "📡 System: Monitoring Routes"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // =============================================
                // EMERGENCY DETAILS
                // =============================================

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "🚨 Emergency Details",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            "Patient: Emergency Case"
                        )

                        Text(
                            "Priority: HIGH"
                        )

                        Text(
                            "Condition: Critical"
                        )

                        Text(
                            "Destination: Nearest Suitable Hospital"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // =============================================
                // HOSPITAL STATUS
                // =============================================

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "🏥 Hospital Status",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            "🟢 Hospital: Available"
                        )

                        Text(
                            "🛏️ Emergency Beds: Available"
                        )

                        Text(
                            "👨‍⚕️ Emergency Department: Ready"
                        )

                        Text(
                            "📍 Destination: Suitable Hospital"
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // =============================================
                // FIND FASTEST ROUTE
                // =============================================

                Button(
                    onClick = {

                        routeSelected = true
                        journeyStarted = false
                        journeyCompleted = false
                        progress = 0

                        selectedRoute = "Route C"
                        estimatedTime = "16 minutes"

                        systemMessage =
                            "AI rejected Route A because of a road closure and selected Route C as the fastest safe route."

                        speak(
                            "Route A is blocked. Route C has been selected as the fastest available safe route. Estimated travel time is 16 minutes."
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        "🚨 Find Fastest Route"
                    )
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                // =============================================
                // ROUTE RESULT
                // =============================================

                if (routeSelected) {

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "🤖 AI Route Recommendation",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            AmbulanceMap()

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                "✅ Selected Route: $selectedRoute"
                            )

                            Text(
                                "⏱️ Estimated Time: $estimatedTime"
                            )

                            Text(
                                "🚦 Traffic: $trafficStatus"
                            )

                            Text(
                                "🚧 Road Closure: Route A Blocked"
                            )

                            Text(
                                "🏥 Hospital: Available"
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                "✓ AI selected the fastest available safe route"
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // =============================================
                    // JOURNEY PROGRESS
                    // =============================================

                    if (journeyStarted || journeyCompleted) {

                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text = "🚑 Emergency Journey",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                Text(
                                    "📊 Journey Progress: $progress%"
                                )

                                Text(
                                    "⏱️ ETA: $estimatedTime"
                                )

                                Text(
                                    "🚦 Current Traffic: $trafficStatus"
                                )

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                if (journeyCompleted) {

                                    Text(
                                        "✅ Ambulance reached the hospital"
                                    )

                                } else {

                                    Text(
                                        "🚑 Ambulance is travelling to hospital..."
                                    )
                                }
                            }
                        }

                        Spacer(
                            modifier = Modifier.height(16.dp)
                        )
                    }

                    // =============================================
                    // AVAILABLE ROUTES
                    // =============================================

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                text = "🛣️ Route Analysis",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                "🔴 Route A"
                            )

                            Text(
                                "⏱️ 12 minutes"
                            )

                            Text(
                                "🚦 Traffic: Low"
                            )

                            Text(
                                "🚧 Road Closure: BLOCKED ❌"
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                "🟠 Route B"
                            )

                            Text(
                                "⏱️ 18 minutes"
                            )

                            Text(
                                "🚦 Traffic: Moderate"
                            )

                            Text(
                                "🚧 Road Closure: None"
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                "🟢 Route C"
                            )

                            Text(
                                "⏱️ 16 minutes"
                            )

                            Text(
                                "🚦 Traffic: Low"
                            )

                            Text(
                                "🚧 Road Closure: None"
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                "🤖 AI Decision:"
                            )

                            Text(
                                "Route A was rejected because of a road closure. Route C was selected as the fastest available safe route."
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    // =============================================
                    // START JOURNEY
                    // =============================================

                    if (!journeyStarted && !journeyCompleted) {

                        Button(
                            onClick = {

                                journeyStarted = true
                                journeyCompleted = false
                                progress = 0

                                systemMessage =
                                    "Emergency journey started using Route C."

                                speak(
                                    "Emergency journey started. Follow Route C to the hospital."
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                "🚑 Start Emergency Journey"
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    // =============================================
                    // VOICE + RECALCULATE
                    // =============================================

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Button(
                            onClick = {

                                speak(
                                    "Continue on Route C. Estimated travel time is $estimatedTime. Traffic is $trafficStatus."
                                )

                                systemMessage =
                                    "Voice navigation activated for $selectedRoute."

                            },
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                "🔊 Voice"
                            )
                        }

                        OutlinedButton(
                            onClick = {

                                selectedRoute = "Route C"
                                estimatedTime = "16 minutes"

                                systemMessage =
                                    "Route recalculated using traffic and road-closure information."

                                speak(
                                    "Route recalculated. Route C remains the fastest available route."
                                )

                            },
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                "🔄 Recalculate"
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // =============================================
                // SYSTEM MESSAGE
                // =============================================

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "📢 System Message",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            systemMessage
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                // =============================================
                // PROTOTYPE INFORMATION
                // =============================================

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = "ℹ️ Prototype Information",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            "This prototype evaluates route time, traffic and road-closure conditions to select a suitable emergency route."
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            "The journey simulation demonstrates ambulance movement, route progress, ETA updates and hospital arrival."
                        )
                    }
                }
            }
        }
    }

    // =========================================================
    // MAP
    // =========================================================

    @Composable
    fun AmbulanceMap() {

        AndroidView(

            factory = { context ->

                MapLibre.getInstance(context)

                MapView(context).apply {

                    onCreate(null)

                    getMapAsync { map ->

                        map.setStyle(
                            "https://demotiles.maplibre.org/style.json"
                        ) {

                            // =================================
                            // LOCATIONS
                            // =================================

                            val ambulanceLocation =
                                LatLng(
                                    17.3850,
                                    78.4867
                                )

                            val hospitalLocation =
                                LatLng(
                                    17.4000,
                                    78.4800
                                )

                            // =================================
                            // CAMERA
                            // =================================

                            val centerLatitude =
                                (17.3850 + 17.4000) / 2

                            val centerLongitude =
                                (78.4867 + 78.4800) / 2

                            map.cameraPosition =
                                CameraPosition.Builder()
                                    .target(
                                        LatLng(
                                            centerLatitude,
                                            centerLongitude
                                        )
                                    )
                                    .zoom(12.5)
                                    .build()

                            // =================================
                            // AMBULANCE
                            // =================================

                            val ambulanceSource =
                                GeoJsonSource(
                                    "ambulance-source",
                                    """
                                    {
                                      "type": "Feature",
                                      "geometry": {
                                        "type": "Point",
                                        "coordinates": [
                                          78.4867,
                                          17.3850
                                        ]
                                      }
                                    }
                                    """.trimIndent()
                                )

                            map.style?.addSource(
                                ambulanceSource
                            )

                            val ambulanceLayer =
                                CircleLayer(
                                    "ambulance-layer",
                                    "ambulance-source"
                                )

                            ambulanceLayer.setProperties(

                                PropertyFactory.circleRadius(
                                    10f
                                ),

                                PropertyFactory.circleColor(
                                    "#1976D2"
                                ),

                                PropertyFactory.circleStrokeColor(
                                    "#FFFFFF"
                                ),

                                PropertyFactory.circleStrokeWidth(
                                    3f
                                )
                            )

                            map.style?.addLayer(
                                ambulanceLayer
                            )

                            // =================================
                            // HOSPITAL
                            // =================================

                            val hospitalSource =
                                GeoJsonSource(
                                    "hospital-source",
                                    """
                                    {
                                      "type": "Feature",
                                      "geometry": {
                                        "type": "Point",
                                        "coordinates": [
                                          78.4800,
                                          17.4000
                                        ]
                                      }
                                    }
                                    """.trimIndent()
                                )

                            map.style?.addSource(
                                hospitalSource
                            )

                            val hospitalLayer =
                                CircleLayer(
                                    "hospital-layer",
                                    "hospital-source"
                                )

                            hospitalLayer.setProperties(

                                PropertyFactory.circleRadius(
                                    10f
                                ),

                                PropertyFactory.circleColor(
                                    "#D32F2F"
                                ),

                                PropertyFactory.circleStrokeColor(
                                    "#FFFFFF"
                                ),

                                PropertyFactory.circleStrokeWidth(
                                    3f
                                )
                            )

                            map.style?.addLayer(
                                hospitalLayer
                            )

                            // =================================
                            // ROUTE A - BLOCKED
                            // =================================

                            val routeASource =
                                GeoJsonSource(
                                    "route-a-source",
                                    """
                                    {
                                      "type": "Feature",
                                      "geometry": {
                                        "type": "LineString",
                                        "coordinates": [
                                          [78.4867,17.3850],
                                          [78.4855,17.3880],
                                          [78.4840,17.3910],
                                          [78.4830,17.3940],
                                          [78.4815,17.3970],
                                          [78.4800,17.4000]
                                        ]
                                      }
                                    }
                                    """.trimIndent()
                                )

                            map.style?.addSource(
                                routeASource
                            )

                            val routeALayer =
                                LineLayer(
                                    "route-a-layer",
                                    "route-a-source"
                                )

                            routeALayer.setProperties(

                                PropertyFactory.lineColor(
                                    "#D32F2F"
                                ),

                                PropertyFactory.lineWidth(
                                    4f
                                ),

                                PropertyFactory.lineOpacity(
                                    0.5f
                                ),

                                PropertyFactory.lineDasharray(
                                    arrayOf(
                                        2f,
                                        2f
                                    )
                                )
                            )

                            map.style?.addLayer(
                                routeALayer
                            )

                            // =================================
                            // ROUTE B
                            // =================================

                            val routeBSource =
                                GeoJsonSource(
                                    "route-b-source",
                                    """
                                    {
                                      "type": "Feature",
                                      "geometry": {
                                        "type": "LineString",
                                        "coordinates": [
                                          [78.4867,17.3850],
                                          [78.4890,17.3890],
                                          [78.4880,17.3930],
                                          [78.4850,17.3970],
                                          [78.4800,17.4000]
                                        ]
                                      }
                                    }
                                    """.trimIndent()
                                )

                            map.style?.addSource(
                                routeBSource
                            )

                            val routeBLayer =
                                LineLayer(
                                    "route-b-layer",
                                    "route-b-source"
                                )

                            routeBLayer.setProperties(

                                PropertyFactory.lineColor(
                                    "#FF9800"
                                ),

                                PropertyFactory.lineWidth(
                                    4f
                                ),

                                PropertyFactory.lineOpacity(
                                    0.6f
                                )
                            )

                            map.style?.addLayer(
                                routeBLayer
                            )

                            // =================================
                            // ROUTE C - SELECTED
                            // =================================

                            val routeCSource =
                                GeoJsonSource(
                                    "route-c-source",
                                    """
                                    {
                                      "type": "Feature",
                                      "geometry": {
                                        "type": "LineString",
                                        "coordinates": [
                                          [78.4867,17.3850],
                                          [78.4900,17.3865],
                                          [78.4910,17.3900],
                                          [78.4890,17.3940],
                                          [78.4850,17.3980],
                                          [78.4800,17.4000]
                                        ]
                                      }
                                    }
                                    """.trimIndent()
                                )

                            map.style?.addSource(
                                routeCSource
                            )

                            val routeCLayer =
                                LineLayer(
                                    "route-c-layer",
                                    "route-c-source"
                                )

                            routeCLayer.setProperties(

                                PropertyFactory.lineColor(
                                    "#2E7D32"
                                ),

                                PropertyFactory.lineWidth(
                                    7f
                                ),

                                PropertyFactory.lineOpacity(
                                    0.95f
                                )
                            )

                            map.style?.addLayer(
                                routeCLayer
                            )
                        }
                    }
                }
            },

            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
    }
}