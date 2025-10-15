package com.example.blueguard

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.blueguard.data.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.ImageView
import androidx.cardview.widget.CardView

class AlertsWarningsActivity : AppCompatActivity() {

    private lateinit var progressOverlay: View
    private lateinit var progress: LottieAnimationView

    private lateinit var tvBeachTitle: TextView
    private lateinit var tvCurrentAlerts: TextView
    private lateinit var tvWeatherWarnings: TextView
    private lateinit var tvMarineHazards: TextView
    private lateinit var tvEmergencyContacts: TextView
    private lateinit var tvPrecautions: TextView

    private lateinit var ivBeach: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerts_warnings)

        progressOverlay = findViewById(R.id.progressOverlay)
        progress = findViewById(R.id.progress)

        ivBeach = findViewById(R.id.beachImage)

        val backButton = findViewById<CardView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvBeachTitle = findViewById(R.id.tvBeachTitle)
        tvCurrentAlerts = findViewById(R.id.tvCurrentAlerts)
        tvWeatherWarnings = findViewById(R.id.tvWeatherWarnings)
        tvMarineHazards = findViewById(R.id.tvMarineHazards)
        tvEmergencyContacts = findViewById(R.id.tvEmergencyContacts)
        tvPrecautions = findViewById(R.id.tvPrecautions)

        val beach = intent.getStringExtra("EXTRA_BEACH_NAME") ?: "Baga Beach"
        tvBeachTitle.text = "Alerts: $beach"

        setBeachImage(beach)

        val prompt = """
        You are a beach safety expert. For "$beach", provide current safety information.
        Use latest generally-known info (no extra prose).
        EXACT OUTPUT FORMAT (no extra labels, no markdown):
        CURRENT_ALERTS: <2-3 sentences about any active alerts or all-clear status>
        WEATHER_WARNINGS: <4-5 bullet points about weather conditions separated by " • ">
        MARINE_HAZARDS: <4-5 bullet points about rip currents, jellyfish, etc. separated by " • ">
        EMERGENCY_CONTACTS: <3-4 important contact numbers with labels separated by " • ">
        PRECAUTIONS: <4-5 safety precautions separated by " • ">
        """.trimIndent()

        askGeminiViaOpenRouter(prompt)
    }

    private fun setBeachImage(beachName: String) {
        val resourceName = beachName
            .replace(" ", "")
            .replace("'", "")
            .replace("'", "")
            .replace("(", "")
            .replace(")", "")
            .lowercase()

        val resId = resources.getIdentifier(resourceName, "drawable", packageName)

        if (resId != 0) {
            ivBeach.setImageResource(resId)
        } else {
            ivBeach.setImageResource(R.drawable.default_image)
        }
    }

    private fun showLoader() {
        progressOverlay.visibility = View.VISIBLE
        progress.playAnimation()
    }

    private fun hideLoader() {
        progress.cancelAnimation()
        progressOverlay.visibility = View.GONE
    }

    private fun askGeminiViaOpenRouter(prompt: String) {
        showLoader()

        val request = OpenRouterRequest(
            messages = listOf(OpenRouterMessage("user", prompt))
        )

        val yourApiKey = BuildConfig.GEMINI_API_KEY

        OpenRouterClient.api.getChatCompletion(
            token = "Bearer $yourApiKey",
            referer = "http://localhost",
            request = request
        ).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                hideLoader()

                val rawString = response.body()?.string() ?: response.errorBody()?.string() ?: ""
                Log.d("OpenRouterRaw", rawString)

                if (!rawString.trim().startsWith("{")) {
                    Toast.makeText(
                        this@AlertsWarningsActivity,
                        "API returned: $rawString",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }

                try {
                    val gson = com.google.gson.Gson()
                    val parsed = gson.fromJson(rawString, OpenRouterResponse::class.java)
                    val raw = parsed.choices.firstOrNull()?.message?.content?.trim() ?: ""

                    val lines = raw.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
                    fun pick(prefix: String): String {
                        return lines.firstOrNull { it.startsWith(prefix, ignoreCase = true) }
                            ?.substringAfter(":")
                            ?.trim()
                            ?: "N/A"
                    }

                    tvCurrentAlerts.text = pick("CURRENT_ALERTS")
                    tvWeatherWarnings.text = pick("WEATHER_WARNINGS").replace(" • ", "\n• ")
                    tvMarineHazards.text = pick("MARINE_HAZARDS").replace(" • ", "\n• ")
                    tvEmergencyContacts.text = pick("EMERGENCY_CONTACTS").replace(" • ", "\n• ")
                    tvPrecautions.text = pick("PRECAUTIONS").replace(" • ", "\n• ")

                } catch (e: Exception) {
                    Toast.makeText(
                        this@AlertsWarningsActivity,
                        "Parsing failed",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("OpenRouterParse", "Failed to parse JSON", e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                hideLoader()
                Toast.makeText(
                    this@AlertsWarningsActivity,
                    "Failed: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}