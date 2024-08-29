package org.druidanet.druidnetbeta.utils

import android.content.Context

fun Context.getResourceId(filename:String): Int {
    val resID = this.resources.getIdentifier(filename, "drawable", this.packageName)
    return resID
}