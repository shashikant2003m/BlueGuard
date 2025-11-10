package com.example.blueguard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.blueguard.data.*
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.firebase.auth.FirebaseAuth // Import FirebaseAuth
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedBeach: String? = null

    // Firebase Auth
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var tvGreeting: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvSuitability: TextView
    private lateinit var tvConditionSummary: TextView
    private lateinit var tvToughness: TextView
    private lateinit var tvForecastDuration: TextView
    private lateinit var cardExplore: View
    private lateinit var cardAlerts: View
    private lateinit var cardWater: View
    private lateinit var cardForecast: View
    private lateinit var btnLogout: ImageButton // Logout Button

    // Loader views
    private lateinit var progressOverlay: View
    private lateinit var progress: LottieAnimationView
    private lateinit var cardAiRecommendation: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

        // Initialize views
        tvGreeting = findViewById(R.id.tvGreeting)
        tvLocation = findViewById(R.id.tvLocation)
        val autoCompleteBeaches: MaterialAutoCompleteTextView = findViewById(R.id.autoCompleteBeaches)
        btnLogout = findViewById(R.id.btnLogout) // Find the logout button

        tvSuitability = findViewById(R.id.tvSuitability)
        tvConditionSummary = findViewById(R.id.tvConditionSummary)
        tvToughness = findViewById(R.id.tvToughness)
        tvForecastDuration = findViewById(R.id.tvForecastDuration)

        cardExplore = findViewById(R.id.cardExploreBeaches)
        cardAlerts = findViewById(R.id.cardAlerts)
        cardWater = findViewById(R.id.cardWaterQuality)
        cardForecast = findViewById(R.id.cardForecast)

        cardAiRecommendation = findViewById(R.id.cardAiRecommendation)

        // Loader views
        progressOverlay = findViewById(R.id.progressOverlay)
        progress = findViewById(R.id.progress)

        val userName = getUserName()
        tvGreeting.text = if (userName.isNotEmpty()) "Hello, $userName 👋" else "Hello, User 👋"

        // Initial state
        tvLocation.text = "Please select a beach location"
        showMessage("Please select a beach to proceed")
        setCardsEnabled(false)
        cardAiRecommendation.visibility = View.GONE

        setupBeachDropdown(autoCompleteBeaches, tvLocation)
        setupNavigationCards()
        setupLogoutButton() // Call the new logout setup function
    }

    private fun setupLogoutButton() {
        btnLogout.setOnClickListener {
            firebaseAuth.signOut() // Sign out the user
            val intent = Intent(this, PhoneLoginActivity::class.java)
            // Clear the activity stack to prevent the user from going back to the dashboard
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Close the DashboardActivity
        }
    }

    private fun getUserName(): String = sharedPreferences.getString("user_name", "") ?: ""

    // ... (the rest of your DashboardActivity code remains the same)
//highlight-next-line
    private fun setupBeachDropdown(
        autoComplete: MaterialAutoCompleteTextView,
        tvLocation: TextView
    ) {
        val items = listOf(
            // Goa
            "🏝 Goa (Most Famous for Beaches)",
            "Baga Beach", "Calangute Beach", "Anjuna Beach", "Vagator Beach", "Palolem Beach", "Miramar Beach",
            // Maharashtra
            "🏝 Maharashtra",
            "Juhu Beach", "Girgaum Chowpatty", "Aksa Beach", "Kashid Beach",
            "Ganpatipule Beach", "Tarkarli Beach",
            // Kerala
            "🏝 Kerala",
            "Kovalam Beach", "Varkala Beach", "Marari Beach",
            "Cherai Beach", "Bekal Beach",
            // Tamil Nadu
            "🏝 Tamil Nadu",
            "Marina Beach", "Elliot's Beach",
            "Mahabalipuram Beach", "Kanyakumari Beach", "Dhanushkodi Beach",
            // Andaman & Nicobar
            "🏝 Andaman & Nicobar Islands",
            "Radhanagar Beach", "Elephant Beach",
            "Vijaynagar Beach", "Corbyn's Cove",
            // Lakshadweep
            "🏝 Lakshadweep Islands",
            "Agatti Beach", "Bangaram Beach", "Kadmat Beach", "Minicoy Beach",
            // Odisha
            "🏝 Odisha",
            "Puri Beach", "Chandrabhaga Beach", "Gopalpur Beach",
            // West Bengal
            "🏝 West Bengal",
            "Digha Beach", "Mandarmani Beach", "Shankarpur Beach",
            // Karnataka
            "🏝 Karnataka",
            "Gokarna Beach", "Murudeshwar Beach", "Karwar Beach"
        )

        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            items
        ) {
            override fun areAllItemsEnabled(): Boolean = false
            override fun isEnabled(position: Int): Boolean {
                val it = getItem(position)
                return it?.startsWith("🏝") == false
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                val text = getItem(position) ?: ""
                if (text.startsWith("🏝")) {
                    view.setTextColor(Color.GRAY)
                    view.setTypeface(view.typeface, Typeface.BOLD)
                    view.isEnabled = false
                } else {
                    view.setTextColor(Color.BLACK)
                    view.setTypeface(null, Typeface.NORMAL)
                    view.isEnabled = true
                }
                return view
            }
        }

        autoComplete.setAdapter(adapter)
        autoComplete.threshold = 0
        autoComplete.setOnClickListener { autoComplete.showDropDown() }

        autoComplete.setOnItemClickListener { parent, _, position, _ ->
            val selected = parent.getItemAtPosition(position).toString()
            if (!selected.startsWith("🏝")) {
                tvLocation.text = "Your location: $selected"
                selectedBeach = selected

                // Enable the cards now
                setCardsEnabled(true)

                // Fetch AI suitability analysis
                fetchBeachSuitability(selected)

                Toast.makeText(this, "Selected: $selected", Toast.LENGTH_SHORT).show()
            } else {
                autoComplete.showDropDown()
            }
        }
    }

    private fun setupNavigationCards() {
        cardExplore.setOnClickListener {
            selectedBeach?.let {
                val intent = Intent(this, ExploreBeachActivity::class.java)
                intent.putExtra("EXTRA_BEACH_NAME", it)
                startActivity(intent)
            }
        }
        cardAlerts.setOnClickListener {
            selectedBeach?.let {
                val intent = Intent(this, AlertsWarningsActivity::class.java)
                intent.putExtra("EXTRA_BEACH_NAME", it)
                startActivity(intent)
            }
        }
        cardWater.setOnClickListener {
            selectedBeach?.let {
                val intent = Intent(this, WaterQualityActivity::class.java)
                intent.putExtra("EXTRA_BEACH_NAME", it)
                startActivity(intent)
            }
        }
        cardForecast.setOnClickListener {
            showMessage("Suitability Forecast clicked")
        }
    }

    private fun fetchBeachSuitability(beach: String) {
        showLoader()

        val prompt = """
            You are a beach safety analyst. For "$beach", analyze current conditions and provide safety assessment.
            EXACT OUTPUT FORMAT (no extra text, no markdown):
            SUITABILITY: <Safe/Moderate/Extreme>
            DESCRIPTION: <2-3 sentences about current conditions, waves, weather, swimming safety>
            TOUGHNESS: <1-5 (number only)>
        """.trimIndent()

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
                Log.d("DashboardAPI", rawString)

                if (!rawString.trim().startsWith("{")) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "API Error: $rawString",
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

                    val suitability = pick("SUITABILITY")
                    val description = pick("DESCRIPTION")
                    val toughnessNum = pick("TOUGHNESS").toIntOrNull() ?: 3

                    updateSuitabilityUI(suitability, description, toughnessNum)
                    cardAiRecommendation.visibility = View.VISIBLE

                } catch (e: Exception) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Failed to parse response",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("DashboardParse", "Parse error", e)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                hideLoader()
                Toast.makeText(
                    this@DashboardActivity,
                    "Network Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("DashboardAPI", "Failure", t)
            }
        })
    }

    private fun updateSuitabilityUI(suitability: String, description: String, toughnessLevel: Int) {
        // Update suitability with emoji and color
        val (emoji, color) = when (suitability.lowercase()) {
            "safe" -> "🟢" to "#2E7D32"
            "moderate" -> "🟡" to "#F57C00"
            "extreme" -> "🔴" to "#D32F2F"
            else -> "⚪" to "#616161"
        }

        tvSuitability.text = "Suitability: $emoji ${suitability.capitalize()}"
        tvSuitability.setTextColor(Color.parseColor(color))

        // Update description
        tvConditionSummary.text = description

        // Update toughness with stars
        val stars = "⭐".repeat(toughnessLevel.coerceIn(1, 5))
        val levelText = when (toughnessLevel) {
            1 -> "Very Easy"
            2 -> "Easy"
            3 -> "Moderate"
            4 -> "Challenging"
            5 -> "Extreme"
            else -> "Moderate"
        }
        tvToughness.text = "Toughness Level: $stars $levelText"

        // Forecast remains static as requested
        tvForecastDuration.text = "Forecast: Safe for next 6 hours"
    }

    private fun setCardsEnabled(enabled: Boolean) {
        val alpha = if (enabled) 1f else 0.5f
        listOf(cardExplore, cardAlerts, cardWater, cardForecast).forEach {
            it.isEnabled = enabled
            it.alpha = alpha
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

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
