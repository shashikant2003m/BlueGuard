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

class WaterQualityActivity : AppCompatActivity() {

    private lateinit var progressOverlay: View
    private lateinit var progress: LottieAnimationView

    private lateinit var tvBeachTitle: TextView
    private lateinit var tvQualityStatus: TextView
    private lateinit var tvWaterParameters: TextView
    private lateinit var tvPollutionLevels: TextView
    private lateinit var tvSwimmingAdvice: TextView
    private lateinit var tvRecentTests: TextView

    private lateinit var ivBeach: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_water_quality)

        progressOverlay = findViewById(R.id.progressOverlay)
        progress = findViewById(R.id.progress)

        ivBeach = findViewById(R.id.beachImage)

        val backButton = findViewById<CardView>(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvBeachTitle = findViewById(R.id.tvBeachTitle)
        tvQualityStatus = findViewById(R.id.tvQualityStatus)
        tvWaterParameters = findViewById(R.id.tvWaterParameters)
        tvPollutionLevels = findViewById(R.id.tvPollutionLevels)
        tvSwimmingAdvice = findViewById(R.id.tvSwimmingAdvice)
        tvRecentTests = findViewById(R.id.tvRecentTests)

        val beach = intent.getStringExtra("EXTRA_BEACH_NAME") ?: "Baga Beach"
        tvBeachTitle.text = "Water Quality: $beach"

        setBeachImage(beach)

        val prompt = """
        You are a water quality analyst. For "$beach", provide detailed water quality information.
        Use latest generally-known info (no extra prose).
        EXACT OUTPUT FORMAT (no extra labels, no markdown):
        QUALITY_STATUS: <2-3 sentences about overall water quality rating>
        WATER_PARAMETERS: <5 bullet points about pH, temperature, clarity, etc. separated by " • ">
        POLLUTION_LEVELS: <4-5 bullet points about bacteria, chemicals, debris separated by " • ">
        SWIMMING_ADVICE: <3-4 bullet points about safe swimming conditions separated by " • ">
        RECENT_TESTS: <3-4 bullet points about recent water quality tests and dates separated by " • ">
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
                        this@WaterQualityActivity,
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

                    tvQualityStatus.text = pick("QUALITY_STATUS")
                    tvWaterParameters.text = pick("WATER_PARAMETERS").replace(" • ", "\n• ")
                    tvPollutionLevels.text = pick("POLLUTION_LEVELS").replace(" • ", "\n• ")
                    tvSwimmingAdvice.text = pick("SWIMMING_ADVICE").replace(" • ", "\n• ")
                    tvRecentTests.text = pick("RECENT_TESTS").replace(" • ", "\n• ")

                } catch (e: Exception) {
                    Toast.makeText(
                        this@WaterQualityActivity,
                        "Parsing failed",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("OpenRouterParse", "Failed to parse JSON", e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                hideLoader()
                Toast.makeText(
                    this@WaterQualityActivity,
                    "Failed: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}