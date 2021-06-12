package com.miya10kei.actuator.jfr

import com.miya10kei.actuator.jfr.domain.FlightRecorder
import com.miya10kei.actuator.jfr.domain.LaunchOption
import com.miya10kei.actuator.jfr.domain.RecordSettingFactory
import com.miya10kei.actuator.jfr.domain.Recording
import com.miya10kei.actuator.jfr.extension.toInputStreamResource
import org.slf4j.LoggerFactory
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint
import org.springframework.core.io.InputStreamResource
import org.springframework.http.CacheControl
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.MediaType.APPLICATION_OCTET_STREAM
import org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@RestControllerEndpoint(id = "jfr")
class FlightRecorderEndpoint(private val flightRecorder: FlightRecorder) {

  companion object {
    @Suppress("JAVA_CLASS_ON_COMPANION")
    private val logger = LoggerFactory.getLogger(javaClass.enclosingClass)
    private val notFoundEntity = ResponseEntity.notFound().build<Unit>()
  }

//  init {
//    Runtime.getRuntime().addShutdownHook(Thread {
//      keep.values.map { it.destination }.forEach { it.delete() }
//    })
//  }

  @GetMapping("/recordings", produces = [APPLICATION_JSON_VALUE])
  fun records(): List<Recording> =
    this.flightRecorder.listRecordings()

  @GetMapping("/recordings/{id}", produces = [APPLICATION_JSON_VALUE])
  fun records(@PathVariable("id") id: Long): ResponseEntity<*> =
    this.flightRecorder.findRecording(id)
      ?.let { ResponseEntity.ok().body(it) }
      ?: notFoundEntity

  @PostMapping(consumes = [APPLICATION_JSON_VALUE], produces = [APPLICATION_JSON_VALUE])
  fun record(): Recording {
    // TODO
    return this.flightRecorder.record(RecordSettingFactory.create(), LaunchOption())
  }

  @GetMapping("/recordings/{id}/file", produces = [APPLICATION_OCTET_STREAM_VALUE])
  fun download(
    @PathVariable("id") id: Long,
    @RequestParam("deleteAfterDownload", defaultValue = "true") deleteAfterDownload: Boolean
  ): ResponseEntity<*> {
    val recording = this.flightRecorder.findRecording(id) ?: return notFoundEntity

    if (deleteAfterDownload) flightRecorder.deleteRecording(id)

    return recording
      .ifDestinationDoesNotExist { return notFoundEntity }
      .ifStatusIsOngoing { return ResponseEntity.accepted().body("Flight Recorder(#$id) is ongoing!") }
      .toDownloadResponseEntity(deleteAfterDownload)
  }

  private inline fun Recording.ifDestinationDoesNotExist(block: (Recording) -> Recording): Recording {
    if (!this.destination.exists()) block(this)
    return this
  }

  private inline fun Recording.ifStatusIsOngoing(block: (Recording) -> Recording): Recording {
    if (!this.destination.exists()) block(this)
    return this
  }

  private fun Recording.toDownloadResponseEntity(deleteAfterDownload: Boolean): ResponseEntity<InputStreamResource> =
    ResponseEntity.ok()
      .contentType(APPLICATION_OCTET_STREAM)
      .contentLength(this.destination.length())
      .cacheControl(CacheControl.noStore().mustRevalidate())
      .header(CONTENT_DISPOSITION, ContentDisposition.attachment().filename(this.destination.name).build().toString())
      .body(this.destination.toInputStreamResource(deleteAfterDownload))
}
