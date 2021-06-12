package com.miya10kei.actuator.jfr.extension

import org.springframework.core.io.InputStreamResource
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

internal fun File.deleteOnCloseInputStream(): InputStream =
  DeleteOnCloseInputStream(this)

internal fun File.toInputStreamResource(deleteOnClose: Boolean): InputStreamResource =
  InputStreamResource(
    BufferedInputStream(
      if (deleteOnClose) this.deleteOnCloseInputStream() else this.inputStream()
    )
  )

private class DeleteOnCloseInputStream(private val file: File) : FileInputStream(file) {
  override fun close() {
    super.close()
    this.file.delete()
  }
}
