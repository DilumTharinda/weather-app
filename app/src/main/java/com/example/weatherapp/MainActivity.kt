package com.example.weatherapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val API_KEY = "fca6af1b9fd19f8d4c68ffc0ee026fc2" // <-- put your OpenWeatherMap key here

    private lateinit var cityInput: EditText
    private lateinit var searchButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var resultLayout: LinearLayout
    private lateinit var cityNameText: TextView
    private lateinit var temperatureText: TextView
    private lateinit var conditionText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityInput = findViewById(R.id.cityInput)
        searchButton = findViewById(R.id.searchButton)
        progressBar = findViewById(R.id.progressBar)
        errorText = findViewById(R.id.errorText)
        resultLayout = findViewById(R.id.resultLayout)
        cityNameText = findViewById(R.id.cityNameText)
        temperatureText = findViewById(R.id.temperatureText)
        conditionText = findViewById(R.id.conditionText)
        humidityText = findViewById(R.id.humidityText)
        windText = findViewById(R.id.windText)

        searchButton.setOnClickListener {
            val city = cityInput.text.toString().trim()
            if (city.isEmpty()) {
                showError("Please enter a city name.")
                return@setOnClickListener
            }
            fetchWeather(city)
        }
    }

    private fun fetchWeather(city: String) {
        showLoading()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getWeather(city, API_KEY)
                if (response.isSuccessful) {
                    val weather = response.body()
                    if (weather != null) {
                        showResult(weather)
                    } else {
                        showError("Unexpected empty response from server.")
                    }
                } else {
                    when (response.code()) {
                        404 -> showError("City not found. Please check the spelling.")
                        401 -> showError("Invalid API key.")
                        else -> showError("Server error (code ${response.code()}). Try again later.")
                    }
                }
            } catch (e: IOException) {
                showError("Network error. Please check your internet connection.")
            } catch (e: Exception) {
                showError("Something went wrong: ${e.localizedMessage}")
            }
        }
    }

    private fun showLoading() {
        progressBar.visibility = android.view.View.VISIBLE
        errorText.visibility = android.view.View.GONE
        resultLayout.visibility = android.view.View.GONE
    }

    private fun showResult(weather: WeatherResponse) {
        progressBar.visibility = android.view.View.GONE
        errorText.visibility = android.view.View.GONE
        resultLayout.visibility = android.view.View.VISIBLE

        val condition = weather.weather.firstOrNull()?.main ?: "Unknown"
        val windKmh = weather.wind.speed * 3.6

        cityNameText.text = weather.name
        temperatureText.text = "${weather.main.temp.toInt()}°C"
        conditionText.text = condition
        humidityText.text = "Humidity: ${weather.main.humidity}%"
        windText.text = "Wind Speed: ${"%.1f".format(windKmh)} km/h"
    }

    private fun showError(message: String) {
        progressBar.visibility = android.view.View.GONE
        resultLayout.visibility = android.view.View.GONE
        errorText.visibility = android.view.View.VISIBLE
        errorText.text = message
    }
}