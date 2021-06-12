package com.miya10kei.actuator.jfr.domain

import java.io.File
import java.time.Duration
import java.time.ZonedDateTime

data class Recording(
  val id: Long,
  val name: String,
  val duration: Duration,
  val destination: File,
  val startTime: ZonedDateTime,
  val stopTime: ZonedDateTime
) {
  val status: Status
    get() = if (stopTime.isBefore(ZonedDateTime.now())) Status.DONE else Status.ONGOING
}

enum class Status {
  ONGOING, DONE
}
