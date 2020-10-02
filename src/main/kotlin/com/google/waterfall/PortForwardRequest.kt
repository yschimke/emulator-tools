// Code generated by Wire protocol buffer compiler, do not edit.
// Source: waterfall.PortForwardRequest in waterfall.proto
package com.google.waterfall

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

class PortForwardRequest(
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#BOOL",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val rebind: Boolean = false,
  @field:WireField(
    tag = 4,
    adapter = "com.google.waterfall.ForwardSession#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val session: ForwardSession? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<PortForwardRequest, PortForwardRequest.Builder>(ADAPTER, unknownFields) {
  override fun newBuilder(): Builder {
    val builder = Builder()
    builder.rebind = rebind
    builder.session = session
    builder.addUnknownFields(unknownFields)
    return builder
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is PortForwardRequest) return false
    if (unknownFields != other.unknownFields) return false
    if (rebind != other.rebind) return false
    if (session != other.session) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + rebind.hashCode()
      result = result * 37 + session.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    result += """rebind=$rebind"""
    if (session != null) result += """session=$session"""
    return result.joinToString(prefix = "PortForwardRequest{", separator = ", ", postfix = "}")
  }

  fun copy(
    rebind: Boolean = this.rebind,
    session: ForwardSession? = this.session,
    unknownFields: ByteString = this.unknownFields
  ): PortForwardRequest = PortForwardRequest(rebind, session, unknownFields)

  class Builder : Message.Builder<PortForwardRequest, Builder>() {
    @JvmField
    var rebind: Boolean = false

    @JvmField
    var session: ForwardSession? = null

    fun rebind(rebind: Boolean): Builder {
      this.rebind = rebind
      return this
    }

    fun session(session: ForwardSession?): Builder {
      this.session = session
      return this
    }

    override fun build(): PortForwardRequest = PortForwardRequest(
      rebind = rebind,
      session = session,
      unknownFields = buildUnknownFields()
    )
  }

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<PortForwardRequest> = object : ProtoAdapter<PortForwardRequest>(
      FieldEncoding.LENGTH_DELIMITED, 
      PortForwardRequest::class, 
      "type.googleapis.com/waterfall.PortForwardRequest", 
      PROTO_3, 
      null
    ) {
      override fun encodedSize(value: PortForwardRequest): Int {
        var size = value.unknownFields.size
        if (value.rebind != false) size += ProtoAdapter.BOOL.encodedSizeWithTag(3, value.rebind)
        if (value.session != null) size += ForwardSession.ADAPTER.encodedSizeWithTag(4,
            value.session)
        return size
      }

      override fun encode(writer: ProtoWriter, value: PortForwardRequest) {
        if (value.rebind != false) ProtoAdapter.BOOL.encodeWithTag(writer, 3, value.rebind)
        if (value.session != null) ForwardSession.ADAPTER.encodeWithTag(writer, 4, value.session)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): PortForwardRequest {
        var rebind: Boolean = false
        var session: ForwardSession? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            3 -> rebind = ProtoAdapter.BOOL.decode(reader)
            4 -> session = ForwardSession.ADAPTER.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return PortForwardRequest(
          rebind = rebind,
          session = session,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: PortForwardRequest): PortForwardRequest = value.copy(
        session = value.session?.let(ForwardSession.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
