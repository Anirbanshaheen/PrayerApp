package com.example.prayerapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.prayerapp.R
import com.example.prayerapp.databinding.FragmentSettingsBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding : FragmentSettingsBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clickListener()
    }

    private fun clickListener() {
        binding.privacyPolicyLayout.setOnClickListener {
            loadWebView()
        }

        binding.aboutLayout.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext())
            bottomSheetDialog.setContentView(R.layout.about_bottomsheet_dialog)
            bottomSheetDialog.show()
        }

    }

    private fun loadWebView() {
        binding.webView.visibility = View.VISIBLE
        binding.privacyPolicyLayout.visibility = View.GONE
        binding.aboutLayout.visibility = View.GONE
        binding.webView.loadUrl("https://docs.google.com/document/d/1NEuJhX84bb_dqU2296VS-5Unk1Sl2YBQA5o6qk0Q7yc/edit")
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
    }

    companion object {
        fun newInstance() = SettingsFragment().apply {
            arguments = Bundle().apply {

            }
        }
    }
}