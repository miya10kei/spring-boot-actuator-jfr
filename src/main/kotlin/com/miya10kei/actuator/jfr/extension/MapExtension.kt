package com.miya10kei.actuator.jfr.extension

internal fun <K, V> MutableMap<K, V>.get(key: K, thenRemove: Boolean = true): V? =
  this[key]?.also { if (thenRemove) this.remove(key) }
