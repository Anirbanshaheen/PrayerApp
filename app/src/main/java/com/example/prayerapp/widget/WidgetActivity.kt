package com.example.prayerapp.widget

//import android.os.Bundle
//import android.view.Window
//import android.view.WindowManager
//import androidx.appcompat.app.AppCompatActivity
//import com.example.prayerapp.R
//import com.example.prayerapp.databinding.ActivityWidgetBinding
//import com.example.prayerapp.utils.ColorType
//import com.example.prayerapp.utils.HawkManager
//import com.example.prayerapp.utils.IntentManager
//import com.example.prayerapp.utils.SnackbarManager
//import com.example.prayerapp.utils.rotateClockwiseAnimated
//import com.example.prayerapp.utils.vibratePhone
//import dagger.hilt.android.AndroidEntryPoint
//
//
//@AndroidEntryPoint
//class WidgetActivity : AppCompatActivity() {
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    //                                     overrides                                              //
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//
//    private lateinit var binding: ActivityWidgetBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityWidgetBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        enableFullScreenMode(window = window)
//        configMenuClickListeners()
//        configDetailsClickListeners()
//        configColorClickListeners()
//        chooseSelectedTextColor()
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    //                                      configs                                               //
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * handle the action of menu clickable views.
//     */
//    private fun configMenuClickListeners() = with(binding) {
//        clDetails.setOnClickListener {
//            if(elDetails.isExpanded()) {
//                elDetails.collapse()
//                imgDetailsArrow.rotateClockwiseAnimated(degrees = 0f, duration = 150)
//            } else {
//                elDetails.expand()
//                imgDetailsArrow.rotateClockwiseAnimated(degrees = 180f, duration = 150)
//            }
//        }
//        clLanguage.setOnClickListener {
//            if(elLanguage.isExpanded()) {
//                elLanguage.collapse()
//                imgLanguageArrow.rotateClockwiseAnimated(degrees = 0f, duration = 150)
//            } else {
//                elLanguage.expand()
//                imgLanguageArrow.rotateClockwiseAnimated(degrees = 180f, duration = 150)
//            }
//        }
//        clColor.setOnClickListener {
//            if(elColor.isExpanded()) {
//                elColor.collapse()
//                imgColorArrow.rotateClockwiseAnimated(degrees = 0f, duration = 150)
//            } else {
//                elColor.expand()
//                imgColorArrow.rotateClockwiseAnimated(degrees = 180f, duration = 150)
//            }
//        }
//        clStar.setOnClickListener {
//            IntentManager.rateIntent(
//                context = this@WidgetActivity,
//                view = binding.root
//            )
//        }
//        clShare.setOnClickListener {
//            IntentManager.shareTextIntent(
//                context = this@WidgetActivity,
//                view = binding.root,
//                title = getString(R.string.introduce_to_friends),
//                description = getString(R.string.share_app_with_friends)
//            )
//        }
//        clExit.setOnClickListener {
//            finish()
//        }
//    }
//
//    /**
//     * handle the action of details clickable views.
//     */
//    private fun configDetailsClickListeners() = with(binding) {
//        imgZekrRefresh.setOnClickListener {
//            vibratePhone()
//            imgZekrRefresh.rotateClockwiseAnimated(
//                degrees = if(imgZekrRefresh.rotation == 0f) 360f else 0f, duration = 150
//            )
//            HawkManager.saveZekr(zekr = 0)
//            IntentManager.resetZekrIntent(context = this@WidgetActivity)
//            SnackbarManager.showSnackbar(
//                context = this@WidgetActivity,
//                view = binding.root,
//                message = getString(R.string.zekr_has_been_reset)
//            )
//        }
//        imgSalavatRefresh.setOnClickListener {
//            vibratePhone()
//            imgSalavatRefresh.rotateClockwiseAnimated(
//                degrees = if(imgSalavatRefresh.rotation == 0f) 360f else 0f, duration = 150
//            )
//            HawkManager.saveSalavat(salavat = 0)
//            IntentManager.resetSalavatIntent(context = this@WidgetActivity)
//            SnackbarManager.showSnackbar(
//                context = this@WidgetActivity,
//                view = binding.root,
//                message = getString(R.string.salavat_has_been_reset)
//            )
//        }
//        imgTasbihatRefresh.setOnClickListener {
//            vibratePhone()
//            imgTasbihatRefresh.rotateClockwiseAnimated(
//                degrees = if(imgTasbihatRefresh.rotation == 0f) 360f else 0f, duration = 150
//            )
//            HawkManager.apply {
//                saveTasbihatAA(tasbihatAA = 0)
//                saveTasbihatSA(tasbihatSA = 0)
//                saveTasbihatHA(tasbihatHA = 0)
//            }
//            IntentManager.resetTasbihatIntent(context = this@WidgetActivity)
//            SnackbarManager.showSnackbar(
//                context = this@WidgetActivity,
//                view = binding.root,
//                message = getString(R.string.tasbihat_has_been_reset)
//            )
//        }
//    }
//
//    /**
//     * handle the action of color clickable views.
//     */
//    private fun configColorClickListeners() = with(binding) {
//        rbTextWhite.setOnClickListener {
//            HawkManager.saveTextColor(color = ColorType.WHITE)
//            IntentManager.apply {
//                changeSalavatColorIntent(context = this@WidgetActivity)
//                changeZekrColorIntent(context = this@WidgetActivity)
//                changeTasbihatColorIntent(context = this@WidgetActivity)
//            }
//        }
//        rbTextBlack.setOnClickListener {
//            HawkManager.saveTextColor(color = ColorType.BLACK)
//            IntentManager.apply {
//                changeSalavatColorIntent(context = this@WidgetActivity)
//                changeZekrColorIntent(context = this@WidgetActivity)
//                changeTasbihatColorIntent(context = this@WidgetActivity)
//            }
//        }
//        rbTextGreen.setOnClickListener {
//            HawkManager.saveTextColor(color = ColorType.GREEN)
//            IntentManager.apply {
//                changeSalavatColorIntent(context = this@WidgetActivity)
//                changeZekrColorIntent(context = this@WidgetActivity)
//                changeTasbihatColorIntent(context = this@WidgetActivity)
//            }
//        }
//        rbTextRed.setOnClickListener {
//            HawkManager.saveTextColor(color = ColorType.RED)
//            IntentManager.apply {
//                changeSalavatColorIntent(context = this@WidgetActivity)
//                changeZekrColorIntent(context = this@WidgetActivity)
//                changeTasbihatColorIntent(context = this@WidgetActivity)
//            }
//        }
//    }
//
//    /**
//     * update the UI of change widget text color according to getting saved text color from hawk.
//     */
//    private fun chooseSelectedTextColor() = with(binding) {
//        HawkManager.getTextColor().let {
//            rbTextWhite.isChecked = it == ColorType.WHITE
//            rbTextBlack.isChecked = it == ColorType.BLACK
//            rbTextGreen.isChecked = it == ColorType.GREEN
//            rbTextRed.isChecked = it == ColorType.RED
//        }
//    }
//
//    /**
//     * enable the full screen mode for activity.
//     */
//    private fun enableFullScreenMode(window: Window) {
//        window.apply {
//            setFlags(
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//            )
//        }
//    }
//
//}