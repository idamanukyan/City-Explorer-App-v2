package com.example.homework4.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.homework4.TemperatureUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val tempUnitKey = stringPreferencesKey("temperature_unit")

    val temperatureUnitFlow: Flow<TemperatureUnit> = context.dataStore.data.map { preferences ->
        val unitName = preferences[tempUnitKey] ?: TemperatureUnit.Celsius.name
        TemperatureUnit.valueOf(unitName)
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { preferences ->
            preferences[tempUnitKey] = unit.name
        }
    }
}
