package com.example.prayerapp.utils

import androidx.annotation.ColorRes
import com.example.prayerapp.R

enum class ColorType(@ColorRes val color: Int) {
    WHITE(color = R.color.white),
    BLACK(color = R.color.black),
    GREEN(color = R.color.light_green),
    RED(color = R.color.light_red)
}