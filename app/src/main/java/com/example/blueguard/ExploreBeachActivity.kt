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

class ExploreBeachActivity : AppCompatActivity() {

    private lateinit var progressOverlay: View
    private lateinit var progress: LottieAnimationView

    private lateinit var tvBeachTitle: TextView
    private lateinit var tvOverview: TextView
    private lateinit var tvHighlights: TextView
    private lateinit var tvBestTime: TextView
    private lateinit var tvSafety: TextView
    private lateinit var tvNearby: TextView

    private lateinit var ivBeach: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore_beach)

        // Loader overlay + animation
        progressOverlay = findViewById(R.id.progressOverlay)
        progress = findViewById(R.id.progress)

        ivBeach = findViewById(R.id.beachImage)


        val backButton = findViewById<CardView>(R.id.backButton) // Or use binding.backButton

        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()

            // finish()
        }

        tvBeachTitle = findViewById(R.id.tvBeachTitle)
        tvOverview = findViewById(R.id.tvOverview)
        tvHighlights = findViewById(R.id.tvHighlights)
        tvBestTime = findViewById(R.id.tvBestTime)
        tvSafety = findViewById(R.id.tvSafety)
        tvNearby = findViewById(R.id.tvNearby)

        val beach = intent.getStringExtra("EXTRA_BEACH_NAME") ?: "Baga Beach"
        tvBeachTitle.text = "Explore: $beach"

        setBeachImage(beach)

        val prompt = """
        You are a beach guide. For "$beach", return compact, tourist-friendly info.
        Use latest generally-known info (no extra prose).
        EXACT OUTPUT FORMAT (no extra labels, no markdown):
        OVERVIEW: <2-3 sentences> and one image of beach
        HIGHLIGHTS: <5 short bullet points separated by " • ">
        BEST_TIME to visit: <1 short line>
        SAFETY: <3-4 short bullet points separated by " • ">
        NEARBY: <3-5 nearby attractions separated by " • " and give location context>
    """.trimIndent()

        askGeminiViaOpenRouter(prompt)
    }


    private fun setBeachImage(beachName: String) {
        val resourceName = beachName
            .replace(" ", "")
            .replace("’", "")
            .replace("'", "")
            .replace("(", "")
            .replace(")", "")
            .lowercase()

        val resId = resources.getIdentifier(resourceName, "drawable", packageName)

        if (resId != 0) {
            ivBeach.setImageResource(resId)
        } else {
            ivBeach.setImageResource(R.drawable.default_image) // fallback image
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
                        this@ExploreBeachActivity,
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

                    tvOverview.text = pick("OVERVIEW")
                    tvHighlights.text = pick("HIGHLIGHTS").replace(" • ", "\n• ")
                    tvBestTime.text = pick("BEST_TIME")
                    tvSafety.text = pick("SAFETY").replace(" • ", "\n• ")
                    tvNearby.text = pick("NEARBY").replace(" • ", "\n• ")

                } catch (e: Exception) {
                    Toast.makeText(
                        this@ExploreBeachActivity,
                        "Parsing failed",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("OpenRouterParse", "Failed to parse JSON", e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                hideLoader()
                Toast.makeText(
                    this@ExploreBeachActivity,
                    "Failed: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}
