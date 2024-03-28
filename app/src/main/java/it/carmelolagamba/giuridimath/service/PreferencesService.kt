package it.carmelolagamba.giuridimath.service

import android.util.Log
import it.carmelolagamba.giuridimath.GiuridiMathApplication
import it.carmelolagamba.giuridimath.persistence.DBFactory
import it.carmelolagamba.giuridimath.persistence.Preferences
import javax.inject.Inject

class PreferencesService @Inject constructor() {

    fun findAll(): List<Preferences> {
        return DBFactory.getDatabase(GiuridiMathApplication.context).preferencesDao().getAll()
    }

    fun findDayHoursCustomValue(): Int {
        val preferences = findPreferenceByKey(DAY_HOURS_CUSTOM_VALUE)
        return preferences?.value?.toInt() ?: 8
    }

    fun updateDayHoursCustomValue(time: Int) {
        updateByValue(DAY_HOURS_CUSTOM_VALUE, time.toString())
    }

    fun findDayMinutesCustomValue(): Int {
        val preferences = findPreferenceByKey(DAY_MINUTES_CUSTOM_VALUE)
        return preferences?.value?.toInt() ?: 0
    }

    fun updateDayMinutesCustomValue(time: Int) {
        updateByValue(DAY_MINUTES_CUSTOM_VALUE, time.toString())
    }

    fun findDayBreakHoursCustomValue(): Int {
        val preferences = findPreferenceByKey(DAY_BREAK_HOURS_CUSTOM_VALUE)
        return preferences?.value?.toInt() ?: 1
    }

    fun updateDayBreakHoursCustomValue(time: Int) {
        updateByValue(DAY_BREAK_HOURS_CUSTOM_VALUE, time.toString())
    }

    fun findDayBreakMinutesCustomValue(): Int {
        val preferences = findPreferenceByKey(DAY_BREAK_MINUTES_CUSTOM_VALUE)
        return preferences?.value?.toInt() ?: 0
    }

    fun updateDayBreakMinutesCustomValue(time: Int) {
        updateByValue(DAY_BREAK_MINUTES_CUSTOM_VALUE, time.toString())
    }

    fun findDayMinimumHoursCustomValue(): Int {
        val preferences = findPreferenceByKey(DAY_MINIMUM_HOURS_CUSTOM_VALUE)
        return preferences?.value?.toInt() ?: 4
    }

    fun updateDayMinimumHoursCustomValue(time: Int) {
        updateByValue(DAY_MINIMUM_HOURS_CUSTOM_VALUE, time.toString())
    }

    fun findDayMinimumMinutesCustomValue(): Int {
        val preferences = findPreferenceByKey(DAY_MINIMUM_MINUTES_CUSTOM_VALUE)
        return preferences?.value?.toInt() ?: 0
    }

    fun updateDayMinimumMinutesCustomValue(time: Int) {
        updateByValue(DAY_MINIMUM_MINUTES_CUSTOM_VALUE, time.toString())
    }

    fun resetAll() {
        DBFactory.getDatabase(GiuridiMathApplication.context).preferencesDao().deleteAll()
    }

    private fun findPreferenceByKey(key: String): Preferences? {
        return try {
            DBFactory.getDatabase(GiuridiMathApplication.context).preferencesDao().getByKey(key)
        } catch (ex: NoSuchElementException) {
            Log.d("SYT", "No preference setted for $key")
            null
        }
    }

    /**
     * @param preferences the preferences to update
     * @return the preferences updated
     */
    private fun upsert(preferences: Preferences): Preferences {
        return run {
            DBFactory.getDatabase(GiuridiMathApplication.context).preferencesDao()
                .update(preferences)
            preferences
        }
    }

    private fun updateByValue(key: String, value: String) {
        val preference: Preferences? = findPreferenceByKey(key)

        if (preference != null) {
            preference.value = value
            upsert(preference)
        } else {
            insert(Preferences(null, key, value))
        }
    }

    private fun insert(preferences: Preferences) {
        DBFactory.getDatabase(GiuridiMathApplication.context).preferencesDao()
            .insertAll(preferences)
    }

    companion object {

        @JvmStatic
        val DAY_HOURS_CUSTOM_VALUE: String = "day_hours_custom_value"

        @JvmStatic
        val DAY_MINUTES_CUSTOM_VALUE: String = "day_minutes_custom_value"

        @JvmStatic
        val DAY_BREAK_MINUTES_CUSTOM_VALUE: String = "day_break_minutes_custom_value"

        @JvmStatic
        val DAY_BREAK_HOURS_CUSTOM_VALUE: String = "day_break_hours_custom_value"

        @JvmStatic
        val DAY_MINIMUM_MINUTES_CUSTOM_VALUE: String = "day_minimum_minutes_custom_value"

        @JvmStatic
        val DAY_MINIMUM_HOURS_CUSTOM_VALUE: String = "day_minimum_hours_custom_value"

    }

}