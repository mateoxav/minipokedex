package com.accesodatos.minipokedex.core.util

fun String.capFirst(): String = replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
