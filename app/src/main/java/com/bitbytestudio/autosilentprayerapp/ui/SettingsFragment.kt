package com.bitbytestudio.autosilentprayerapp.ui

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bitbytestudio.autosilentprayerapp.R
import com.bitbytestudio.autosilentprayerapp.databinding.FragmentSettingsBinding
import com.bitbytestudio.autosilentprayerapp.prefs.DataStorePreference
import com.bitbytestudio.autosilentprayerapp.prefs.DataStorePreference.Companion.IS_DND_ENABLED
import com.bitbytestudio.autosilentprayerapp.prefs.Prefs
import com.bitbytestudio.autosilentprayerapp.receiver.PrayersAlertReceiver
import com.bitbytestudio.autosilentprayerapp.ui.HomeFragment.Companion.WORKER_NAME
import com.bitbytestudio.autosilentprayerapp.ui.HomeFragment.Companion.WORKER_TAG
import com.bitbytestudio.autosilentprayerapp.utils.updateLocale
import com.bitbytestudio.autosilentprayerapp.worker.PrayersWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var binding : FragmentSettingsBinding
    private lateinit var webView: WebView
    private val bottomSheetDialog = AboutBottomSheetFragmentDialog()

    @Inject
    lateinit var prefs: Prefs
    @Inject
    lateinit var dataStorePreference: DataStorePreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //prefs = Prefs(requireActivity())
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

        binding.settingsTB.backgroundTintList = ColorStateList.valueOf(prefs.statusBarColor)

        lifecycleScope.launch {
            dataStorePreference.getPreference(IS_DND_ENABLED, true).collectLatest {
                Log.d("fahad007", "is DND mode Enable : ${it}")
                binding.dndSwitch.isChecked = it
            }
        }
        //binding.dndSwitch.isChecked = prefs.isDndEnabled

        clickListener()
        selectedLanguage()
    }

    private fun clickListener() {
        binding.dndSwitch.setOnCheckedChangeListener { _, isChecked ->
            //prefs.isDndEnabled = isChecked
            dataStorePreference.savePreference(IS_DND_ENABLED, isChecked)
        }

        binding.privacyPolicyCV.setOnClickListener {
            loadWebView()
        }

        binding.englishBtn.setOnClickListener {
            prefs.appLanguage = "en"
            requireActivity().updateLocale(Locale("en"), false)
            selectedLanguage()
        }
        binding.banglaBtn.setOnClickListener {
            prefs.appLanguage = "bn"
            requireActivity().updateLocale(Locale("bn"), false)
            selectedLanguage()
        }
        binding.arabicBtn.setOnClickListener {
            prefs.appLanguage = "ar"
            requireActivity().updateLocale(Locale("ar"), false)
            selectedLanguage()
        }

        binding.aboutCV.setOnClickListener {
            bottomSheetDialog.show(childFragmentManager, "aboutBottomSheet")
        }

    }

    private fun selectedLanguage() {
        when(prefs.appLanguage){
            "en" ->{
                binding.englishBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dim_green))
                binding.englishBtn.setTextColor(requireActivity().getColor(R.color.black))
                binding.banglaBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dark_green))
                binding.banglaBtn.setTextColor(requireActivity().getColor(R.color.white))
                binding.arabicBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dark_green))
                binding.arabicBtn.setTextColor(requireActivity().getColor(R.color.white))
            }
            "bn" ->{
                binding.banglaBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dim_green))
                binding.banglaBtn.setTextColor(requireActivity().getColor(R.color.black))
                binding.englishBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dark_green))
                binding.englishBtn.setTextColor(requireActivity().getColor(R.color.white))
                binding.arabicBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dark_green))
                binding.arabicBtn.setTextColor(requireActivity().getColor(R.color.white))
            }
            "ar" ->{
                binding.arabicBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dim_green))
                binding.arabicBtn.setTextColor(requireActivity().getColor(R.color.black))
                binding.banglaBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dark_green))
                binding.banglaBtn.setTextColor(requireActivity().getColor(R.color.white))
                binding.englishBtn.backgroundTintList = ColorStateList.valueOf(requireActivity().getColor(R.color.dark_green))
                binding.englishBtn.setTextColor(requireActivity().getColor(R.color.white))
            }
        }
    }

    private fun loadWebView() {
        binding.webView.visibility = View.VISIBLE
        binding.privacyPolicyCV.visibility = View.GONE
        binding.aboutCV.visibility = View.GONE
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