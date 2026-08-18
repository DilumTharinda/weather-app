package com.example.weatherapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * Main activity for the Weather App.
 * Handles user input for city name, fetches weather data using Retrofit,
 * and displays the results or error messages.
 */

// Implemented core logic, API data binding and error handling for MainActivity by Person 3
class MainActivity : AppCompatActivity() {

    private val apiKey = "fca6af1b9fd19f8d4c68ffc0ee026fc2" // <-- put your OpenWeatherMap key here

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
                showError(getString(R.string.error_empty_city))
                return@setOnClickListener
            }
            fetchWeather(city)
        }
    }

    /**
     * Fetches weather data for the specified city from the OpenWeatherMap API.
     *
     * @param city The name of the city entered by the user.
     */
    private fun fetchWeather(city: String) {
        showLoading()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getWeather(city, apiKey)
                if (response.isSuccessful) {
                    val weather = response.body()
                    if (weather != null) {
                        showResult(weather)
                    } else {
                        showError(getString(R.string.error_empty_response))
                    }
                } else {
                    when (response.code()) {
                        404 -> showError(getString(R.string.error_city_not_found))
                        401 -> showError(getString(R.string.error_invalid_key))
                        else -> showError(getString(R.string.error_server, response.code()))
                    }
                }
            } catch (e: IOException) {
                showError(getString(R.string.error_network, e.message ?: "Please check connection"))
            } catch (e: Exception) {
                showError(getString(R.string.error_unknown, e.localizedMessage ?: "Something went wrong"))
            }
        }
    }

    /**
     * Displays a loading state by showing the progress bar and hiding other elements.
     */
    private fun showLoading() {
        progressBar.visibility = android.view.View.VISIBLE
        errorText.visibility = android.view.View.GONE
        resultLayout.visibility = android.view.View.GONE
    }

    /**
     * Updates the UI with the fetched weather data.
     *
     * @param weather The [WeatherResponse] object containing current weather details.
     */
    private fun showResult(weather: WeatherResponse) {
        progressBar.visibility = android.view.View.GONE
        errorText.visibility = android.view.View.GONE
        resultLayout.visibility = android.view.View.VISIBLE

        val condition = weather.weather.firstOrNull()?.main ?: "Unknown"
        val windKmh = weather.wind.speed * 3.6

        cityNameText.text = weather.name
        temperatureText.text = getString(R.string.temp_format, weather.main.temp.toInt())
        conditionText.text = condition
        humidityText.text = getString(R.string.humidity_format, weather.main.humidity)
        windText.text = getString(R.string.wind_speed_format, windKmh)
    }

    /**
     * Displays an error message and hides the loading and result views.
     *
     * @param message The error message to display to the user.
     */
    private fun showError(message: String) {
        progressBar.visibility = android.view.View.GONE
        resultLayout.visibility = android.view.View.GONE
        errorText.visibility = android.view.View.VISIBLE
        errorText.text = message
    }
}