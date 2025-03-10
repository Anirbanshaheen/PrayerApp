package com.bitbytestudio.autosilentprayerapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bitbytestudio.autosilentprayerapp.databinding.AboutBottomsheetDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

        binding.emailTV.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/html"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("anirbanshaheen97@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback about Prayer App")
            intent.putExtra(Intent.EXTRA_TEXT, "Feedback")

            startActivity(Intent.createChooser(intent, "Choose app"))
        }
    }
}