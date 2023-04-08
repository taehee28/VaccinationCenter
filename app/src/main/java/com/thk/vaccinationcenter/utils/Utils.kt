package com.thk.vaccinationcenter.utils

import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import com.naver.maps.geometry.LatLng

inline fun <reified T> T.logd(message: String) = Log.d(T::class.java.simpleName, message)

fun showToast(context: Context, message: String) = Toast.makeText(context, message, Toast.LENGTH_LONG).show()
fun showToast(context: Context, @StringRes resId: Int) = Toast.makeText(context, resId, Toast.LENGTH_LONG).show()

fun Location.toLatLng(): LatLng = LatLng(this)