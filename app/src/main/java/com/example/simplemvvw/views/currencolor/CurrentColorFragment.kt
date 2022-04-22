package com.example.simplemvvw.views.currencolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplemvvw.databinding.FragmentCurrentColorBinding
import com.example.simplemvvw.views.base.BaseFragment
import com.example.simplemvvw.views.base.BaseScreen
import com.example.simplemvvw.views.base.BaseViewModel
import com.example.simplemvvw.views.base.screenViewModel

class CurrentColorFragment: BaseFragment() {

    // no arguments for this screen
    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCurrentColorBinding.inflate(inflater,container, false)

        viewModel.currentColor.observe(viewLifecycleOwner) {
            binding.colorView.setBackgroundColor(it.value)
        }

        binding.changeColorButton.setOnClickListener {
            viewModel.changeColor()
        }

        return binding.root

    }
}