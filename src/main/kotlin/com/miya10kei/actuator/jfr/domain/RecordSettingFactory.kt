package com.miya10kei.actuator.jfr.domain

import jdk.jfr.Configuration

class RecordSettingFactory {
  companion object {
    fun create(): Map<String, String> {
      return Configuration.getConfiguration("default").settings
    }
  }
}
