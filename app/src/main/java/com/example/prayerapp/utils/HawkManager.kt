package com.example.prayerapp.utils

import android.content.Context
import com.example.prayerapp.prefs.Prefs
import com.example.prayerapp.utils.Constants.SALAVAT
import com.example.prayerapp.utils.Constants.SALAVAT_DAY
import com.example.prayerapp.utils.Constants.TASBIHAT_AA
import com.example.prayerapp.utils.Constants.TASBIHAT_HA
import com.example.prayerapp.utils.Constants.TASBIHAT_SA
import com.example.prayerapp.utils.Constants.TEXT_COLOR
import com.example.prayerapp.utils.Constants.ZEKR
import com.example.prayerapp.utils.Constants.ZEKR_DAY
import javax.inject.Inject

class HawkManager(context: Context) {

    @Inject
    lateinit var prefs: Prefs

    fun saveSalavat(salavat: Int) = prefs.save(salavat,SALAVAT)

    /**
     * get salavat from hawk.
     */
    fun getSalavat(): Int = prefs.get<Int>(SALAVAT)?:0

    /**
     * save the increased salavat to hawk then return it.
     */
    fun increaseSalavat(): Int {
        val currentSalavat = getSalavat()
        saveSalavat(salavat = currentSalavat + 1)
        return currentSalavat + 1
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                    salavat day                                             //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * save salavat day to hawk.
     */
    fun saveSalavatDay(day: String) = prefs.save(day, SALAVAT_DAY)

    /**
     * get salavat day from hawk.
     */
    fun getSalavatDay(): String = prefs.get<String>(SALAVAT_DAY).orEmpty()

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                        zekr                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * save zekr to hawk.
     */
    fun saveZekr(zekr: Int) = prefs.save(zekr,ZEKR)

    /**
     * get zekr from hawk.
     */
    fun getZekr(): Int = prefs.get<Int>(ZEKR)?:0

    /**
     * save the increased zekr to hawk then return it.
     */
    fun increaseZekr(): Int {
        val currentZekr = getZekr()
        saveZekr(zekr = currentZekr + 1)
        return currentZekr + 1
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                      zekr day                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * save zekr day to hawk.
     */
    fun saveZekrDay(day: String) = prefs.save(day, ZEKR_DAY)

    /**
     * get zekr day from hawk.
     */
    fun getZekrDay(): String = prefs.get<String>(ZEKR_DAY).orEmpty()

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                      tasbihat                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * save tasbihatAA to hawk.
     */
    fun saveTasbihatAA(tasbihatAA: Int) = prefs.save(tasbihatAA, TASBIHAT_AA)

    /**
     * get tasbihatAA from hawk.
     */
    fun getTasbihatAA(): Int = prefs.get<Int>(TASBIHAT_AA)?:0

    /**
     * save the increased tasbihatAA to hawk then return it.
     */
    fun increaseTasbihatAA(): Int {
        val currentTasbihatAA = getTasbihatAA()
        return if(currentTasbihatAA < 34) {
            saveTasbihatAA(tasbihatAA = currentTasbihatAA + 1)
            currentTasbihatAA + 1
        } else {
            currentTasbihatAA
        }
    }

    /**
     * save tasbihatSA to hawk.
     */
    fun saveTasbihatSA(tasbihatSA: Int) = prefs.save(tasbihatSA, TASBIHAT_SA)

    /**
     * get tasbihatSA from hawk.
     */
    fun getTasbihatSA(): Int = prefs.get<Int>(TASBIHAT_SA)?:0

    /**
     * save the increased tasbihatSA to hawk then return it.
     */
    fun increaseTasbihatSA(): Int {
        val currentTasbihatSA = getTasbihatSA()
        return if(currentTasbihatSA < 33) {
            saveTasbihatSA(tasbihatSA = currentTasbihatSA + 1)
            currentTasbihatSA + 1
        } else {
            currentTasbihatSA
        }
    }

    /**
     * save tasbihatHA to hawk.
     */
    fun saveTasbihatHA(tasbihatHA: Int) = prefs.save(tasbihatHA, TASBIHAT_HA)

    /**
     * get tasbihatHA from hawk.
     */
    fun getTasbihatHA(): Int = prefs.get<Int>(TASBIHAT_HA)?:0

    /**
     * save the increased tasbihatHA to hawk then return it.
     */
    fun increaseTasbihatHA(): Int {
        val currentTasbihatHA = getTasbihatHA()
        return if(currentTasbihatHA < 33) {
            saveTasbihatHA(tasbihatHA = currentTasbihatHA + 1)
            currentTasbihatHA + 1
        } else {
            currentTasbihatHA
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                       color                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * save widget text color to hawk.
     */
    fun saveTextColor(color: ColorType) = prefs.save(color, TEXT_COLOR)

    /**
     * get widget text color from hawk.
     */
    fun getTextColor(): ColorType = prefs.get<ColorType>(TEXT_COLOR) ?: ColorType.WHITE

}