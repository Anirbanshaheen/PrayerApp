package com.example.prayerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prayerapp.databinding.AboutBottomsheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AboutBottomSheetFragmentDialog : BottomSheetDialogFragment() {

    private lateinit var binding: AboutBottomsheetDialogBinding
    //var listener: GenericInterfaceListener<String>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AboutBottomsheetDialogBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clickListener()
    }

    private fun clickListener() {
//        binding.titleTv.setOnClickListener {
//            listener?.clickListener("Hello")
//        }
    }
}