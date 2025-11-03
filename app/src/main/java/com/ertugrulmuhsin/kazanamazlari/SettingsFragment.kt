package com.ertugrulmuhsin.kazanamazlari

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onStop() {
        super.onStop()
        context?.let { NotificationScheduler.scheduleNotification(it) }
    }
}
