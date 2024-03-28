package it.carmelolagamba.giuridimath.ui.calculator

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.skydoves.balloon.ArrowPositionRules
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import dagger.hilt.android.AndroidEntryPoint
import it.carmelolagamba.giuridimath.R
import it.carmelolagamba.giuridimath.databinding.FragmentCalculatorBinding
import it.carmelolagamba.giuridimath.service.PreferencesService
import it.carmelolagamba.giuridimath.ui.GMTime
import java.lang.NumberFormatException
import javax.inject.Inject


/**
 * @author carmelolg
 * @since version 0.1
 */
@AndroidEntryPoint
class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null

    private val binding get() = _binding!!

    @Inject
    lateinit var preferencesService: PreferencesService


    var daysLabel: String = ""
    var hoursLabel: String = ""
    var minutesLabel: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val hours = preferencesService.findDayHoursCustomValue()
        val minutes = preferencesService.findDayMinutesCustomValue()


        daysLabel = resources.getText(R.string.home_result_days_label).toString()
        hoursLabel = resources.getText(R.string.home_result_hours_label).toString()
        minutesLabel = resources.getText(R.string.home_result_minutes_label).toString()

        resetUI()
        hoursBehaviour(hours, minutes)

        Log.d("GM", binding.hours.text.toString())

        binding.currentDayHours.text = resources.getString(R.string.home_current_day_work_label, hours, minutes)

        binding.cleanButton.setOnClickListener {
            resetUI()
        }

        val balloon = Balloon.Builder(requireContext())
            .setWidthRatio(0.8f)
            .setHeight(BalloonSizeSpec.WRAP)
            .setText(requireContext().resources.getText(R.string.disclaimer_info))
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

    private fun hoursBehaviour(hours: Int, minutes: Int) {
        binding.hours.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.hours.setSelection(binding.hours.length())
        binding.hours.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    if (binding.hours.length() > 0) {
                        val gmTime = calculate(binding.hours.text.toString().toFloat(), hours, minutes)

                        binding.result.text = "${gmTime.days} $daysLabel, ${gmTime.hours} $hoursLabel, ${gmTime.minutes} $minutesLabel"
                        binding.resultContainer.visibility = View.VISIBLE
                        binding.cleanButton.visibility = View.VISIBLE

                    } else {
                        binding.resultContainer.visibility = View.GONE
                        binding.cleanButton.visibility = View.GONE
                        showError()
                    }
                } catch (ex: NumberFormatException) {
                    binding.resultContainer.visibility = View.GONE
                    binding.cleanButton.visibility = View.GONE
                    showError()
                }

                if (view != null) {
                    requireContext().hideKeyboard(binding.hours)
                }
                true
            } else {
                false
            }
        }
    }
    private fun calculate(totalHours: Float, hours: Int, minutes: Int): GMTime {

        //val oneMinute = 0.016667
        val allWorkDayMinutes = (hours * 60) + minutes


        // Days
        val convertedWorkDay = (totalHours * 60) / allWorkDayMinutes
        val workedDays = convertedWorkDay.toInt()

        // Hours and minutes
        val convertedTotalDaysDecimal = convertedWorkDay - workedDays
        val convertedTotalHours = (allWorkDayMinutes / (1 / convertedTotalDaysDecimal)) / 60
        var workedHours = convertedTotalHours.toInt()

        var workedMinutes =
            Math.round((allWorkDayMinutes / (1 / convertedTotalDaysDecimal)) - (convertedTotalHours.toInt() * 60))

        if (workedMinutes == 60) {
            workedMinutes = 0
            workedHours += 1
        }

        return GMTime(workedDays, workedHours, workedMinutes)

    }

    private fun showError(){
        Toast.makeText(
            requireContext(),
            resources.getText(R.string.home_result_error),
            Toast.LENGTH_LONG
        ).show()
    }
    private fun resetUI(){
        binding.hours.text.clear()
        binding.resultContainer.visibility = View.GONE
        binding.cleanButton.visibility = View.GONE
        binding.result.text = resources.getText(R.string.home_result_loading).toString()
    }
    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}