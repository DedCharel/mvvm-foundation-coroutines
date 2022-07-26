package com.example.simplemvvw.views.changecolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplemvvw.R
import com.example.simplemvvw.databinding.FragmentChangeColorBinding
import com.example.foundation.views.HasScreenTitle
import com.example.foundation.views.BaseFragment
import com.example.foundation.views.BaseScreen
import com.example.foundation.views.screenViewModel
import com.example.simplemvvw.views.onTryAgain
import com.example.simplemvvw.views.renderSimpleResult


class ChangeColorFragment: BaseFragment(), HasScreenTitle {

    /**
     * This screen has 1 argument: color ID to be displayed as selected.
     */
    class Screen(
        val currentColorId: Long
    ) : BaseScreen
    override val viewModel by screenViewModel<ChangeColorViewModel>()

    /**
     * Example of dynamic screen title
     */
    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentChangeColorBinding.inflate(inflater, container, false)

        val adapter = ColorsAdapter(viewModel)
        setupLayoutManager(binding, adapter)

        binding.saveButton.setOnClickListener { viewModel.onSavePressed() }
        binding.cancelButton.setOnClickListener { viewModel.onCancelPressed() }

        viewModel.viewState.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(binding.root, result) { viewState ->
                adapter.items = viewState.colorList
                binding.saveButton.visibility = if (viewState.showSaveButton) View.VISIBLE else View.INVISIBLE
                binding.cancelButton.visibility = if (viewState.showCancelButton) View.VISIBLE else View.INVISIBLE
                binding.saveProgressBar.visibility = if (viewState.showSaveProgressBar) View.VISIBLE else View.GONE
            }
        }
        viewModel.screenTitle.observe(viewLifecycleOwner) {
            // if screen title is changed -> need to notify activity about updates
            notifyScreenUpdates()
        }

        onTryAgain(binding.root){
            viewModel.tryAgain()
        }
        return binding.root
    }

    private fun setupLayoutManager(binding: FragmentChangeColorBinding, adapter: ColorsAdapter) {
        // waiting for list width
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.root.width
                val itemWidth = resources.getDimensionPixelSize(R.dimen.item_width)
                val columns = width / itemWidth
                binding.colorsRecyclerView.adapter = adapter
                binding.colorsRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
            }
        })
    }
}