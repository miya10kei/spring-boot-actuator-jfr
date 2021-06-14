package com.miya10kei.actuator.jfr.domain

import jdk.jfr.FlightRecorder
import java.time.Duration
import java.time.ZoneId
import java.util.concurrent.ConcurrentHashMap
import jdk.jfr.Recording as JfrRecording
import jdk.jfr.FlightRecorder as JdkFlightRecorder

class FlightRecorder {

  private val recordings: MutableMap<Long, Recording> = ConcurrentHashMap()

  fun record(setting: Map<String, String>, option: LaunchOption): Recording =
    JfrRecording(setting)
      .startWithOption(option)
      .toRecording()
      .apply { recordings[this.id] = this }

  fun findRecording(id: Long): Recording? =
    this.recordings[id]
      ?: JdkFlightRecorder.getFlightRecorder().recordings
        .find { it.id == id }
        ?.toRecording()

  fun listRecordings(): List<Recording> {
    val recordings = this.recordings.values.toList()
    val jdkRecordings = JdkFlightRecorder.getFlightRecorder().recordings
      .filter { jdkRecord -> !recordings.any { it.id == jdkRecord.id } }
      .map { it.toRecording() }
    return (recordings + jdkRecordings).sortedBy { it.id }
  }

  fun deleteRecording(id: Long) {
    this.recordings.remove(id)
  }
}

private fun JfrRecording.toRecording() =
  Recording(
    id = this.id,
    name = this.name,
    duration = this.duration,
    destination = this.destination.toFile(),
    startTime = this.startTime.atZone(ZoneId.systemDefault()),
    stopTime = this.stopTime.atZone(ZoneId.systemDefault())
  )

private fun JfrRecording.startWithOption(option: LaunchOption): JfrRecording {
  this.name = option.recordingName
  this.destination = option.destination.toPath()
  this.duration = option.duration

  if (option.delay == Duration.ZERO) this.start()
  else this.scheduleStart(option.delay)

  return this
}
