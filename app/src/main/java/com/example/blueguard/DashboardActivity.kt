package com.example.blueguard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class DashboardActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var selectedBeach: String? = null // null until selected

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        tvGreeting = findViewById(R.id.tvGreeting)
        tvLocation = findViewById(R.id.tvLocation)
        val autoCompleteBeaches: MaterialAutoCompleteTextView = findViewById(R.id.autoCompleteBeaches)

        tvSuitability = findViewById(R.id.tvSuitability)
        tvConditionSummary = findViewById(R.id.tvConditionSummary)
        tvToughness = findViewById(R.id.tvToughness)
        tvForecastDuration = findViewById(R.id.tvForecastDuration)

        cardExplore = findViewById(R.id.cardExploreBeaches)
        cardAlerts = findViewById(R.id.cardAlerts)
        cardWater = findViewById(R.id.cardWaterQuality)
        cardForecast = findViewById(R.id.cardForecast)

        val userName = getUserName()
        tvGreeting.text = if (userName.isNotEmpty()) "Hello, $userName 👋" else "Hello, User 👋"

        // Initial state
        tvLocation.text = "Please select a beach location"
        showMessage("Please select a beach to proceed")
        setCardsEnabled(false)

        setupBeachDropdown(autoCompleteBeaches, tvLocation)
        setupNavigationCards()
    }

    private fun getUserName(): String = sharedPreferences.getString("user_name", "") ?: ""

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
            "Marina Beach", "Elliot’s Beach",
            "Mahabalipuram Beach", "Kanyakumari Beach", "Dhanushkodi Beach",
            // Andaman & Nicobar
            "🏝 Andaman & Nicobar Islands",
            "Radhanagar Beach", "Elephant Beach",
            "Vijaynagar Beach", "Corbyn’s Cove",
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

                // Show suitability details (dummy for now, can link to API)
                updateSuitability(selected)

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

    private fun updateSuitability(beach: String) {
        // Later you can call your API here, for now show static demo data
        tvSuitability.text = "Suitability: 🟢 Safe"
        tvConditionSummary.text = "$beach is calm, good for swimming and walking."
        tvToughness.text = "Toughness Level: ⭐⭐ Easy"
        tvForecastDuration.text = "Forecast: Safe for next 6 hours"
    }

    private fun setCardsEnabled(enabled: Boolean) {
        val alpha = if (enabled) 1f else 0.5f
        listOf(cardExplore, cardAlerts, cardWater, cardForecast).forEach {
            it.isEnabled = enabled
            it.alpha = alpha
        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
