package it.carmelolagamba.giuridimath.ui.settings

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.giuridimath.R
import it.carmelolagamba.giuridimath.databinding.FragmentSettingsBinding
import it.carmelolagamba.giuridimath.service.PreferencesService
import it.carmelolagamba.giuridimath.service.UtilService
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

    @Inject
    lateinit var utilService: UtilService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val numberFormatter : NumberPicker.Formatter = utilService.getNumberClockFormatter()
        var hours = preferencesService.findDayHoursCustomValue()
        var minutes = preferencesService.findDayMinutesCustomValue()
        var breakHours = preferencesService.findDayBreakHoursCustomValue()
        var breakMinutes = preferencesService.findDayBreakMinutesCustomValue()
        var minimumHours = preferencesService.findDayMinimumHoursCustomValue()
        var minimumMinutes = preferencesService.findDayMinimumMinutesCustomValue()



        binding.settingsTimePickerHours.minValue = 0
        binding.settingsTimePickerHours.maxValue = 24
        binding.settingsTimePickerHours.value = hours
        binding.settingsTimePickerHours.setFormatter(numberFormatter)
        binding.settingsTimePickerMinutes.minValue = 0
        binding.settingsTimePickerMinutes.maxValue = 59
        binding.settingsTimePickerMinutes.value = minutes
        binding.settingsTimePickerMinutes.setFormatter(numberFormatter)

        binding.settingsTimePickerBreakHours.minValue = 0
        binding.settingsTimePickerBreakHours.maxValue = 24
        binding.settingsTimePickerBreakHours.value = breakHours
        binding.settingsTimePickerBreakHours.setFormatter(numberFormatter)
        binding.settingsTimePickerBreakMinutes.minValue = 0
        binding.settingsTimePickerBreakMinutes.maxValue = 59
        binding.settingsTimePickerBreakMinutes.value = breakMinutes
        binding.settingsTimePickerBreakMinutes.setFormatter(numberFormatter)


        binding.settingsTimePickerMinimumHours.minValue = 0
        binding.settingsTimePickerMinimumHours.maxValue = 24
        binding.settingsTimePickerMinimumHours.value = minimumHours
        binding.settingsTimePickerMinimumHours.setFormatter(numberFormatter)
        binding.settingsTimePickerMinimumMinutes.minValue = 0
        binding.settingsTimePickerMinimumMinutes.maxValue = 59
        binding.settingsTimePickerMinimumMinutes.value = minimumMinutes
        binding.settingsTimePickerMinimumMinutes.setFormatter(numberFormatter)

        binding.settingsBottomLayout.visibility = View.GONE

        binding.settingsTimePickerHours.setOnValueChangedListener { _, _, _hours ->
            binding.settingsBottomLayout.visibility = View.VISIBLE
        }

        binding.settingsTimePickerMinutes.setOnValueChangedListener { _, _, _minutes ->
            binding.settingsBottomLayout.visibility = View.VISIBLE
        }

        binding.settingsTimePickerBreakHours.setOnValueChangedListener { _, _, _hours ->
            binding.settingsBottomLayout.visibility = View.VISIBLE
        }

        binding.settingsTimePickerBreakMinutes.setOnValueChangedListener { _, _, _minutes ->
            binding.settingsBottomLayout.visibility = View.VISIBLE
        }

        binding.settingsTimePickerMinimumHours.setOnValueChangedListener { _, _, _hours ->
            binding.settingsBottomLayout.visibility = View.VISIBLE
        }

        binding.settingsTimePickerMinimumMinutes.setOnValueChangedListener { _, _, _minutes ->
            binding.settingsBottomLayout.visibility = View.VISIBLE
        }


        binding.settingsSaveButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(resources.getText(R.string.dialog_save_title))
            builder.setMessage(resources.getText(R.string.dialog_save_description))

            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                lifecycleScope.launch {
                    preferencesService.updateDayHoursCustomValue(binding.settingsTimePickerHours.value)
                    preferencesService.updateDayMinutesCustomValue(binding.settingsTimePickerMinutes.value)
                    preferencesService.updateDayBreakHoursCustomValue(binding.settingsTimePickerBreakHours.value)
                    preferencesService.updateDayBreakMinutesCustomValue(binding.settingsTimePickerBreakMinutes.value)
                    preferencesService.updateDayMinimumHoursCustomValue(binding.settingsTimePickerMinimumHours.value)
                    preferencesService.updateDayMinimumMinutesCustomValue(binding.settingsTimePickerMinimumMinutes.value)

                    hours = binding.settingsTimePickerHours.value
                    minutes = binding.settingsTimePickerMinutes.value
                    breakHours = binding.settingsTimePickerBreakHours.value
                    breakMinutes = binding.settingsTimePickerBreakMinutes.value
                    minimumHours = binding.settingsTimePickerMinimumHours.value
                    minimumMinutes = binding.settingsTimePickerMinimumMinutes.value

                    Toast.makeText(requireContext(), resources.getText(R.string.dialog_save_toast_result), Toast.LENGTH_LONG).show()

                    binding.settingsBottomLayout.visibility = View.GONE

                }
            }

            builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            }

            builder.show()
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
        binding.settingsTimePickerBreakHours.value = preferencesService.findDayBreakHoursCustomValue()
        binding.settingsTimePickerBreakMinutes.value = preferencesService.findDayBreakMinutesCustomValue()
        binding.settingsTimePickerMinimumHours.value = preferencesService.findDayMinimumHoursCustomValue()
        binding.settingsTimePickerMinimumMinutes.value = preferencesService.findDayMinimumMinutesCustomValue()
        binding.settingsBottomLayout.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}