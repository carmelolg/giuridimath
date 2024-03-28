package it.carmelolagamba.giuridimath.ui.dailycalculator

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.giuridimath.R
import it.carmelolagamba.giuridimath.databinding.FragmentDailyCalculatorBinding
import it.carmelolagamba.giuridimath.service.PreferencesService
import it.carmelolagamba.giuridimath.service.UtilService
import it.carmelolagamba.giuridimath.ui.GMTime
import javax.inject.Inject
import kotlin.math.abs


/**
 * @author carmelolg
 * @since version 0.1
 */
@AndroidEntryPoint
class DailyCalculatorFragment : Fragment() {

    private var _binding: FragmentDailyCalculatorBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var preferencesService: PreferencesService

    @Inject
    lateinit var utilService: UtilService


    private var daysLabel: String = ""
    private var hoursLabel: String = ""
    private var minutesLabel: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDailyCalculatorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val hours = preferencesService.findDayHoursCustomValue()
        val minutes = preferencesService.findDayMinutesCustomValue()
        val minimumHours = preferencesService.findDayMinimumHoursCustomValue()
        val minimumMinutes = preferencesService.findDayMinimumMinutesCustomValue()

        daysLabel = resources.getText(R.string.home_result_days_label).toString()
        hoursLabel = resources.getText(R.string.home_result_hours_label).toString()
        minutesLabel = resources.getText(R.string.home_result_minutes_label).toString()

        resetUI()

        binding.currentDayHours.text =
            resources.getString(R.string.home_current_day_work_label, hours, minutes)

        binding.cleanButton.setOnClickListener {
            resetUI()
        }

        binding.calculateButton.setOnClickListener {
            binding.resultPermitContainer.visibility = View.GONE
            val result = calculate(hours, minutes, minimumHours, minimumMinutes)
            binding.result.text =
                resources.getString(R.string.daily_result, result.hours, result.minutes)

            binding.resultContainer.visibility = View.VISIBLE
            binding.cleanButton.visibility = View.VISIBLE
        }

        val balloon = Balloon.Builder(requireContext())
            .setWidthRatio(0.8f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText(
                requireContext().resources.getString(
                    R.string.disclaimer_permit_info,
                    minimumHours,
                    minimumMinutes
                )
            )
            .setTextColorResource(R.color.white)
            .setTextSize(16f)
            .setTextGravity(Gravity.CENTER_VERTICAL)
            .setIconDrawableResource(R.drawable.ic_info)
            .setIconColor(resources.getColor(R.color.white, requireContext().theme))
            .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
            .setArrowSize(12)
            .setArrowPosition(0.5f)
            .setPadding(8)
            .setCornerRadius(0f)
            .setBackgroundColorResource(R.color.fifth)
            .setBalloonAnimation(BalloonAnimation.FADE)
            .setLifecycleOwner(this)
            .build()

        binding.infoIcon.setOnClickListener {
            balloon.showAlignBottom(binding.infoIcon)
        }

        balloon.setOnBalloonClickListener {
            balloon.dismiss()
        }

        return root
    }

    private fun calculate(
        hours: Int,
        minutes: Int,
        minimumHours: Int,
        minimumMinutes: Int
    ): GMTime {

        val ingressHour = binding.dailyIngressHours.value
        val ingressMinute = binding.dailyIngressMinutes.value
        val breakHours = binding.dailyBreakHours.value
        val breakMinutes = binding.dailyBreakMinutes.value
        val permitHours = binding.dailyPermitHours.value
        val permitMinutes = binding.dailyPermitMinutes.value

        var exitHours =
            (ingressHour + hours + breakHours) % 24
        var exitMinutes =
            if (ingressMinute + minutes + breakMinutes > 60) {
                exitHours += (ingressMinute + minutes + breakMinutes) / 60
                (ingressMinute + minutes + breakMinutes) % 60
            } else {
                ingressMinute + minutes + breakMinutes
            }

        // Manage permit
        exitHours -= permitHours + if (exitMinutes - permitMinutes < 0) 1 else 0
        exitMinutes = (exitMinutes - permitMinutes).mod(60)


        val minusHours = if(exitMinutes < ingressMinute) 1 else 0
        val totalMinutes =
            (((exitHours - ingressHour - minusHours) * 60) + (exitMinutes - ingressMinute).mod(60)) - (breakHours * 60 + breakMinutes)


        if (binding.launchTicketCheckbox.isChecked && totalMinutes < (minimumHours * 60 + minimumMinutes)) {
            val addingMinutes = (minimumHours * 60 + minimumMinutes) - totalMinutes
            val addingHours = addingMinutes / 60

            val realPermitHours =
                binding.dailyPermitHours.value - (addingHours + if (binding.dailyPermitMinutes.value - addingMinutes < 0) 1 else 0)
            val realPermitMinutes = (binding.dailyPermitMinutes.value - addingMinutes).mod(60)

            exitHours += addingHours + if (exitMinutes + addingMinutes >= 60) 1 else 0
            exitMinutes = (exitMinutes + addingMinutes) % 60

            binding.resultPermitContainer.visibility = View.VISIBLE
            binding.permitResult.text =
                resources.getString(R.string.daily_result, realPermitHours, realPermitMinutes)
        }

        return GMTime(0, exitHours, exitMinutes)
    }

    private fun resetUI() {
        binding.resultContainer.visibility = View.GONE
        binding.resultPermitContainer.visibility = View.GONE
        binding.cleanButton.visibility = View.GONE
        binding.result.text = resources.getText(R.string.home_result_loading).toString()
        initAllValues()
    }

    private fun initAllValues() {

        val numberFormatter: NumberPicker.Formatter = utilService.getNumberClockFormatter()
        val breakHours = preferencesService.findDayBreakHoursCustomValue()
        val breakMinutes = preferencesService.findDayBreakMinutesCustomValue()

        binding.dailyIngressHours.minValue = 0
        binding.dailyIngressHours.maxValue = 23
        binding.dailyIngressHours.value = 8
        binding.dailyIngressHours.setFormatter(numberFormatter)
        binding.dailyIngressMinutes.minValue = 0
        binding.dailyIngressMinutes.maxValue = 59
        binding.dailyIngressMinutes.setFormatter(numberFormatter)

        binding.dailyBreakHours.minValue = 0
        binding.dailyBreakHours.maxValue = 23
        binding.dailyBreakHours.value = breakHours
        binding.dailyBreakHours.setFormatter(numberFormatter)

        binding.dailyBreakMinutes.minValue = 0
        binding.dailyBreakMinutes.maxValue = 59
        binding.dailyBreakMinutes.value = breakMinutes
        binding.dailyBreakMinutes.setFormatter(numberFormatter)


        binding.dailyPermitHours.minValue = 0
        binding.dailyPermitHours.maxValue = 23
        binding.dailyPermitHours.setFormatter(numberFormatter)

        binding.dailyPermitMinutes.minValue = 0
        binding.dailyPermitMinutes.maxValue = 59
        binding.dailyPermitMinutes.setFormatter(numberFormatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}