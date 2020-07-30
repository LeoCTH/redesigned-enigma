package com.leocth.redesignedenigma.util

import com.google.common.math.IntMath.pow

object I18nHelper {
    val ordersOfMagnitudeSuffix = listOf("","k","M","G","T","P","E","Z","Y")

    fun getNumericShortForm(n: Long): String {
        var log = 0
        var f : Long = n
        while (f >= 10000) {
            ++log
            f /= 1000
        }
        return "${f/1000.0}${ordersOfMagnitudeSuffix[log+1]}"
    }
}