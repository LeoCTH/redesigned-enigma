package com.leocth.redesignedenigma.util

object I18nHelper {
    val ORDERS_OF_MAGNITUDE_SUFFIX = listOf("","k","M","G","T","P","E","Z","Y")

    fun getNumericShortForm(n: Long): String {
        var log = 0
        var f : Long = n
        while (f >= 10000) {
            ++log
            f /= 1000
        }
        return "${f/1000.0}${ORDERS_OF_MAGNITUDE_SUFFIX[log+1]}"
    }
}