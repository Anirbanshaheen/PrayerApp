package com.bitbytestudio.autosilentprayerapp.utils

import androidx.annotation.ColorRes
import com.bitbytestudio.autosilentprayerapp.R

enum class ColorType(@ColorRes val color: Int) {
    WHITE(color = R.color.white),
    BLACK(color = R.color.black),
    GREEN(color = R.color.light_green),
    RED(color = R.color.light_red)
}