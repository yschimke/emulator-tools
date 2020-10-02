// Code generated by Wire protocol buffer compiler, do not edit.
// Source: android.emulation.control.AudioPacket in emulator_controller.proto
package com.android.emulator.control

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class AudioPacket(
  @field:WireField(
    tag = 1,
    adapter = "com.android.emulator.control.AudioFormat#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val format: AudioFormat? = null,
  /**
   * Unix epoch in us when this frame was captured.
   */
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#UINT64",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val timestamp: Long = 0L,
  /**
   * Contains a sample in the given audio format.
   */
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#BYTES",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val audio: ByteString = ByteString.EMPTY,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<AudioPacket, AudioPacket.Builder>(ADAPTER, unknownFields) {
  override fun newBuilder(): Builder {
    val builder = Builder()
    builder.format = format
    builder.timestamp = timestamp
    builder.audio = audio
    builder.addUnknownFields(unknownFields)
    return builder
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is AudioPacket) return false
    if (unknownFields != other.unknownFields) return false
    if (format != other.format) return false
    if (timestamp != other.timestamp) return false
    if (audio != other.audio) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + format.hashCode()
      result = result * 37 + timestamp.hashCode()
      result = result * 37 + audio.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (format != null) result += """format=$format"""
    result += """timestamp=$timestamp"""
    result += """audio=$audio"""
    return result.joinToString(prefix = "AudioPacket{", separator = ", ", postfix = "}")
  }

  fun copy(
    format: AudioFormat? = this.format,
    timestamp: Long = this.timestamp,
    audio: ByteString = this.audio,
    unknownFields: ByteString = this.unknownFields
  ): AudioPacket = AudioPacket(format, timestamp, audio, unknownFields)

  class Builder : Message.Builder<AudioPacket, Builder>() {
    @JvmField
    var format: AudioFormat? = null

    @JvmField
    var timestamp: Long = 0L

    @JvmField
    var audio: ByteString = ByteString.EMPTY

    fun format(format: AudioFormat?): Builder {
      this.format = format
      return this
    }

    /**
     * Unix epoch in us when this frame was captured.
     */
    fun timestamp(timestamp: Long): Builder {
      this.timestamp = timestamp
      return this
    }

    /**
     * Contains a sample in the given audio format.
     */
    fun audio(audio: ByteString): Builder {
      this.audio = audio
      return this
    }

    override fun build(): AudioPacket = AudioPacket(
      format = format,
      timestamp = timestamp,
      audio = audio,
      unknownFields = buildUnknownFields()
    )
  }

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<AudioPacket> = object : ProtoAdapter<AudioPacket>(
      FieldEncoding.LENGTH_DELIMITED, 
      AudioPacket::class, 
      "type.googleapis.com/android.emulation.control.AudioPacket", 
      PROTO_3, 
      null
    ) {
      override fun encodedSize(value: AudioPacket): Int {
        var size = value.unknownFields.size
        if (value.format != null) size += AudioFormat.ADAPTER.encodedSizeWithTag(1, value.format)
        if (value.timestamp != 0L) size += ProtoAdapter.UINT64.encodedSizeWithTag(2,
            value.timestamp)
        if (value.audio != ByteString.EMPTY) size += ProtoAdapter.BYTES.encodedSizeWithTag(3,
            value.audio)
        return size
      }

      override fun encode(writer: ProtoWriter, value: AudioPacket) {
        if (value.format != null) AudioFormat.ADAPTER.encodeWithTag(writer, 1, value.format)
        if (value.timestamp != 0L) ProtoAdapter.UINT64.encodeWithTag(writer, 2, value.timestamp)
        if (value.audio != ByteString.EMPTY) ProtoAdapter.BYTES.encodeWithTag(writer, 3,
            value.audio)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): AudioPacket {
        var format: AudioFormat? = null
        var timestamp: Long = 0L
        var audio: ByteString = ByteString.EMPTY
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> format = AudioFormat.ADAPTER.decode(reader)
            2 -> timestamp = ProtoAdapter.UINT64.decode(reader)
            3 -> audio = ProtoAdapter.BYTES.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return AudioPacket(
          format = format,
          timestamp = timestamp,
          audio = audio,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: AudioPacket): AudioPacket = value.copy(
        format = value.format?.let(AudioFormat.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
