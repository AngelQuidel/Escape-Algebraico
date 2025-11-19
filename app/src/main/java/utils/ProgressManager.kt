package com.example.escapealgebraico.utils

import android.content.Context

object ProgressManager {

    private const val PREFS_NAME = "escape_progress"
    private const val KEY_NIVEL = "nivel_guardado"

    fun guardarNivel(context: Context, nivel: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_NIVEL, nivel).apply()
    }

    fun cargarNivel(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_NIVEL, 1)
    }
}
