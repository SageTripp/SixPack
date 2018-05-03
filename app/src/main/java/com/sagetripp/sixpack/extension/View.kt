package com.sagetripp.sixpack.extension

import android.widget.TextView

fun TextView.empty(): Boolean = text.isNullOrEmpty()
