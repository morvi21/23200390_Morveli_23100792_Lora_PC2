package com.example.prcticacalificada2.ui.asteroides

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Formatos de presentación: máximo dos decimales y siempre con unidad. */
object AsteroidFormat {

    private val symbols = DecimalFormatSymbols(Locale.US)
    private val number = DecimalFormat("#,##0.##", symbols)
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun meters(value: Double?): String = value?.let { "${number.format(it)} m" } ?: "Sin dato"
    fun kmh(value: Double?): String = value?.let { "${number.format(it)} km/h" } ?: "Sin dato"
    fun km(value: Double?): String = value?.let { "${number.format(it)} km" } ?: "Sin dato"

    fun date(isoDate: String): String =
        runCatching { LocalDate.parse(isoDate).format(dateFormatter) }.getOrDefault(isoDate)

    fun yesNo(value: Boolean): String = if (value) "Sí" else "No"
}
