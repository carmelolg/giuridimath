package it.carmelolagamba.giuridimath.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import it.carmelolagamba.giuridimath.databinding.FragmentCalculatorBinding

class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.textGallery.text = "This is calculator Fragment"

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}