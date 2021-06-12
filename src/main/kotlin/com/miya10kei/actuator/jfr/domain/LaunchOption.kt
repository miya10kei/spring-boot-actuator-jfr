package com.miya10kei.actuator.jfr.domain

import java.io.File
import java.time.Duration
import java.time.Instant

class LaunchOption(
  val recordingName: String = "flightrecorder-${Instant.now().epochSecond}",
  val destination: File = File("/tmp/$recordingName.jfr"),
  val delay: Duration = Duration.ZERO,
  val duration: Duration = Duration.ofMinutes(10L),
)
