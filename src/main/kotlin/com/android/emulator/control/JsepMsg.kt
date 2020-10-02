// Code generated by Wire protocol buffer compiler, do not edit.
// Source: android.emulation.control.JsepMsg in rtc_service.proto
package com.android.emulator.control

import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireField
import com.squareup.wire.internal.sanitize
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class JsepMsg(
  /**
   * The unique identifier of this connection. You will have to use the
   * same identifier when sending/receiving messages. The server will
   * generate a guid when receiving the start message.
   */
  @field:WireField(
    tag = 1,
    adapter = "com.android.emulator.control.RtcId#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val id: RtcId? = null,
  /**
   * The JSON payload. This usually can be directly handled by the
   * Javascript library.
   *
   * The dictionary can contain the following properties
   *
   * - bye:
   *        You can hang up now. No new message expected for you.
   *        The server has stopped the RTC stream.
   *
   * - start:
   *        An RTCConfiguration dictionary providing options to
   *        configure the new connection. This can include the
   *        turn configuration the serve is using. This dictionary can be
   *        passed in directly to the
   *        [RTCPeerConnection](https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection)
   *        object.
   *
   * - candidate:
   *        The WebRTC API's RTCIceCandidateInit dictionary, which
   *        contains the information needed to fundamentally describe an
   *        RTCIceCandidate. See
   *        [RTCIceCandidate](https://developer.mozilla.org/en-US/docs/Web/API/RTCIceCandidate)
   *        and [Session
   *        Lifetime](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API/Session_lifetime)
   *        for more details.
   *
   * - sdp:
   *        RTCSessionDescriptionInit dictionary containing the values
   *        to that can be assigned to a
   *       
   * [RTCSessionDescription](https://developer.mozilla.org/en-US/docs/Web/API/RTCSessionDescription)
   */
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#STRING",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val message: String = "",
  unknownFields: ByteString = ByteString.EMPTY
) : Message<JsepMsg, JsepMsg.Builder>(ADAPTER, unknownFields) {
  override fun newBuilder(): Builder {
    val builder = Builder()
    builder.id = id
    builder.message = message
    builder.addUnknownFields(unknownFields)
    return builder
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is JsepMsg) return false
    if (unknownFields != other.unknownFields) return false
    if (id != other.id) return false
    if (message != other.message) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + id.hashCode()
      result = result * 37 + message.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (id != null) result += """id=$id"""
    result += """message=${sanitize(message)}"""
    return result.joinToString(prefix = "JsepMsg{", separator = ", ", postfix = "}")
  }

  fun copy(
    id: RtcId? = this.id,
    message: String = this.message,
    unknownFields: ByteString = this.unknownFields
  ): JsepMsg = JsepMsg(id, message, unknownFields)

  class Builder : Message.Builder<JsepMsg, Builder>() {
    @JvmField
    var id: RtcId? = null

    @JvmField
    var message: String = ""

    /**
     * The unique identifier of this connection. You will have to use the
     * same identifier when sending/receiving messages. The server will
     * generate a guid when receiving the start message.
     */
    fun id(id: RtcId?): Builder {
      this.id = id
      return this
    }

    /**
     * The JSON payload. This usually can be directly handled by the
     * Javascript library.
     *
     * The dictionary can contain the following properties
     *
     * - bye:
     *        You can hang up now. No new message expected for you.
     *        The server has stopped the RTC stream.
     *
     * - start:
     *        An RTCConfiguration dictionary providing options to
     *        configure the new connection. This can include the
     *        turn configuration the serve is using. This dictionary can be
     *        passed in directly to the
     *       
     * [RTCPeerConnection](https://developer.mozilla.org/en-US/docs/Web/API/RTCPeerConnection)
     *        object.
     *
     * - candidate:
     *        The WebRTC API's RTCIceCandidateInit dictionary, which
     *        contains the information needed to fundamentally describe an
     *        RTCIceCandidate. See
     *        [RTCIceCandidate](https://developer.mozilla.org/en-US/docs/Web/API/RTCIceCandidate)
     *        and [Session
     *       
     * Lifetime](https://developer.mozilla.org/en-US/docs/Web/API/WebRTC_API/Session_lifetime)
     *        for more details.
     *
     * - sdp:
     *        RTCSessionDescriptionInit dictionary containing the values
     *        to that can be assigned to a
     *       
     * [RTCSessionDescription](https://developer.mozilla.org/en-US/docs/Web/API/RTCSessionDescription)
     */
    fun message(message: String): Builder {
      this.message = message
      return this
    }

    override fun build(): JsepMsg = JsepMsg(
      id = id,
      message = message,
      unknownFields = buildUnknownFields()
    )
  }

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<JsepMsg> = object : ProtoAdapter<JsepMsg>(
      FieldEncoding.LENGTH_DELIMITED, 
      JsepMsg::class, 
      "type.googleapis.com/android.emulation.control.JsepMsg", 
      PROTO_3, 
      null
    ) {
      override fun encodedSize(value: JsepMsg): Int {
        var size = value.unknownFields.size
        if (value.id != null) size += RtcId.ADAPTER.encodedSizeWithTag(1, value.id)
        if (value.message != "") size += ProtoAdapter.STRING.encodedSizeWithTag(2, value.message)
        return size
      }

      override fun encode(writer: ProtoWriter, value: JsepMsg) {
        if (value.id != null) RtcId.ADAPTER.encodeWithTag(writer, 1, value.id)
        if (value.message != "") ProtoAdapter.STRING.encodeWithTag(writer, 2, value.message)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): JsepMsg {
        var id: RtcId? = null
        var message: String = ""
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> id = RtcId.ADAPTER.decode(reader)
            2 -> message = ProtoAdapter.STRING.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return JsepMsg(
          id = id,
          message = message,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: JsepMsg): JsepMsg = value.copy(
        id = value.id?.let(RtcId.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
