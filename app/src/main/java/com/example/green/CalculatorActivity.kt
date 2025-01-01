package com.example.green

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CalculatorActivity : AppCompatActivity() {
    private lateinit var startLocationField: EditText
    private lateinit var endLocationField: EditText
    private lateinit var transportTypeSpinner: Spinner
    private lateinit var suggestionsRecyclerView: RecyclerView
    private lateinit var adapter: SuggestionsAdapter
    private lateinit var resultText: TextView
    private val transportEmissionFactors = mapOf(
        "Car" to 0.121,
        "Bus" to 0.028,
        "Train" to 0.014,
        "Plane" to 0.250
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        startLocationField = findViewById(R.id.inputStartLocation)
        endLocationField = findViewById(R.id.inputEndLocation)
        transportTypeSpinner = findViewById(R.id.transportTypeSpinner)
        suggestionsRecyclerView = findViewById(R.id.suggestionsRecyclerView)
        resultText = findViewById(R.id.resultText)

        val calculateButton: Button = findViewById(R.id.calculateButton)
        calculateButton.setOnClickListener { calculateCarbonFootprint() }

        setupTransportTypeSpinner()
        setupRecyclerView()
        setupTextWatchers()
    }

    private fun setupTransportTypeSpinner() {
        val transportTypes = listOf("Car", "Bus", "Train", "Plane")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, transportTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        transportTypeSpinner.adapter = adapter
    }

    private fun setupRecyclerView() {
        adapter = SuggestionsAdapter { suggestion ->
            val activeField = when {
                startLocationField.hasFocus() -> startLocationField
                endLocationField.hasFocus() -> endLocationField
                else -> return@SuggestionsAdapter // No active field
            }

            activeField.setText(suggestion.display_name)
            suggestionsRecyclerView.adapter = null // Clear suggestions after selection
        }

        suggestionsRecyclerView.layoutManager = LinearLayoutManager(this)
        suggestionsRecyclerView.adapter = adapter
    }

    private fun setupTextWatchers() {
        startLocationField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotBlank() && startLocationField.hasFocus()) {
                    fetchSuggestions(query)
                }
            }
        })

        endLocationField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                if (query.isNotBlank() && endLocationField.hasFocus()) {
                    fetchSuggestions(query)
                }
            }
        })
    }

    private fun fetchSuggestions(query: String) {
        RetrofitInstance.api.searchLocations(query)
            .enqueue(object : Callback<List<LocationSuggestion>> {
                override fun onResponse(
                    call: Call<List<LocationSuggestion>>,
                    response: Response<List<LocationSuggestion>>
                ) {
                    if (response.isSuccessful) {
                        val suggestions = response.body() ?: emptyList()
                        adapter.updateSuggestions(suggestions)
                    } else {
                        Toast.makeText(
                            this@CalculatorActivity,
                            "Error: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<LocationSuggestion>>, t: Throwable) {
                    Toast.makeText(
                        this@CalculatorActivity,
                        "Failed to fetch suggestions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun fetchAndCalculateDistance(
        startLocation: String,
        endLocation: String,
        callback: (Double?) -> Unit
    ) {
        fetchCoordinates(startLocation) { startCoordinates ->
            if (startCoordinates == null) {
                callback(null)
                return@fetchCoordinates
            }

            fetchCoordinates(endLocation) { endCoordinates ->
                if (endCoordinates == null) {
                    callback(null)
                    return@fetchCoordinates
                }

                DistanceRetrofitInstance.api.calculateDistance(
                    apiKey = "5b3ce3597851110001cf62485ca5d08955794382b8ff3c88a2c77037",
                    startCoordinates = startCoordinates,
                    endCoordinates = endCoordinates
                ).enqueue(object : Callback<DistanceResponse> {
                    override fun onResponse(
                        call: Call<DistanceResponse>,
                        response: Response<DistanceResponse>
                    ) {
                        if (response.isSuccessful) {
                            val distance = response.body()?.routes?.firstOrNull()?.distance ?: 0.0
                            callback(distance / 1000) // Convert meters to kilometers
                        } else {
                            callback(null)
                        }
                    }

                    override fun onFailure(call: Call<DistanceResponse>, t: Throwable) {
                        callback(null)
                    }
                })
            }
        }
    }

    private fun calculateCarbonFootprint() {
        val startLocation = startLocationField.text.toString()
        val endLocation = endLocationField.text.toString()
        val selectedTransport = transportTypeSpinner.selectedItem.toString()

        if (startLocation.isBlank() || endLocation.isBlank()) {
            Toast.makeText(this, "Please enter both start and end locations.", Toast.LENGTH_SHORT)
                .show()
            return
        }

        fetchAndCalculateDistance(startLocation, endLocation) { distance ->
            if (distance == null) {
                Toast.makeText(this, "Failed to calculate distance.", Toast.LENGTH_SHORT).show()
                return@fetchAndCalculateDistance
            }

            val emissionFactor = transportEmissionFactors[selectedTransport] ?: 0.0
            val carbonFootprint = distance * emissionFactor

            val result = "Distance: ${"%.2f".format(distance)} km\n" +
                    "Carbon Footprint for $selectedTransport: ${"%.2f".format(carbonFootprint)} kg CO2"
            resultText.text = result
        }
    }

    private fun fetchCoordinates(location: String, callback: (String?) -> Unit) {
        RetrofitInstance.api.searchLocations(location).enqueue(object : Callback<List<LocationSuggestion>> {
            override fun onResponse(call: Call<List<LocationSuggestion>>, response: Response<List<LocationSuggestion>>) {
                if (response.isSuccessful && response.body()?.isNotEmpty() == true) {
                    val suggestion = response.body()?.firstOrNull()
                    val coordinates = suggestion?.let { "${it.lon},${it.lat}" }
                    callback(coordinates)
                } else {
                    callback(null)
                    Toast.makeText(this@CalculatorActivity, "No valid suggestions found for $location.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LocationSuggestion>>, t: Throwable) {
                callback(null)
                Toast.makeText(this@CalculatorActivity, "Failed to fetch coordinates for $location.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
