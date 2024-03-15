package it.carmelolagamba.giuridimath.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.giuridimath.R
import it.carmelolagamba.giuridimath.databinding.FragmentSettingsBinding
import it.carmelolagamba.giuridimath.service.PreferencesService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 0.1
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var preferencesService: PreferencesService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val hours = preferencesService.findDayHoursCustomValue()
        val minutes = preferencesService.findDayMinutesCustomValue()

        binding.settingsTimePickerHours.minValue = 0
        binding.settingsTimePickerHours.maxValue = 24
        binding.settingsTimePickerHours.value = hours
        binding.settingsTimePickerMinutes.minValue = 0
        binding.settingsTimePickerMinutes.maxValue = 60
        binding.settingsTimePickerMinutes.value = minutes

        binding.settingsBottomLayout.visibility = View.GONE

        binding.settingsTimePickerHours.setOnValueChangedListener { _, _, _hours ->
            if (_hours == hours) {
                binding.settingsBottomLayout.visibility = View.GONE
            } else {
                binding.settingsBottomLayout.visibility = View.VISIBLE
            }

        }

        binding.settingsTimePickerMinutes.setOnValueChangedListener { _, _, _minutes ->
            if (_minutes == minutes) {
                binding.settingsBottomLayout.visibility = View.GONE
            } else {
                binding.settingsBottomLayout.visibility = View.VISIBLE
            }
        }


        binding.settingsSaveButton.setOnClickListener {
            preferencesService.updateDayHoursCustomValue(binding.settingsTimePickerHours.value)
            preferencesService.updateDayMinutesCustomValue(binding.settingsTimePickerMinutes.value)

        }

        binding.resetButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getText(R.string.dialog_reset_title))
            builder.setMessage(resources.getText(R.string.dialog_reset_description))

            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                lifecycleScope.launch {
                    preferencesService.resetAll()
                    resetAll()
                }
            }

            builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            }

            builder.show()
        }

        return root
    }

    private fun resetAll()  {
        binding.settingsTimePickerHours.value = preferencesService.findDayHoursCustomValue()
        binding.settingsTimePickerMinutes.value = preferencesService.findDayMinutesCustomValue()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}