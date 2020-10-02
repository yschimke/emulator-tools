// Code generated by Wire protocol buffer compiler, do not edit.
// Source: android.emulation.control.Image in emulator_controller.proto
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
import kotlin.Deprecated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.hashCode
import kotlin.jvm.JvmField
import okio.ByteString

class Image(
  @field:WireField(
    tag = 1,
    adapter = "com.android.emulator.control.ImageFormat#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val format: ImageFormat? = null,
  /**
   * width is contained in format.
   */
  @Deprecated(message = "width is deprecated")
  @field:WireField(
    tag = 2,
    adapter = "com.squareup.wire.ProtoAdapter#UINT32",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val width: Int = 0,
  /**
   * height is contained in format.
   */
  @Deprecated(message = "height is deprecated")
  @field:WireField(
    tag = 3,
    adapter = "com.squareup.wire.ProtoAdapter#UINT32",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val height: Int = 0,
  /**
   * The organization of the pixels in the image buffer is from left to
   * right and bottom up.
   */
  @field:WireField(
    tag = 4,
    adapter = "com.squareup.wire.ProtoAdapter#BYTES",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val image: ByteString = ByteString.EMPTY,
  /**
   * [Output Only] Monotonically increasing sequence number in a stream of
   * screenshots. The first screenshot will have a sequence of 0. A single
   * screenshot will always have a sequence number of 0. The sequence is not
   * necessarily contiguous, and can be used to detect how many frames were
   * dropped. An example sequence could be: [0, 3, 5, 7, 9, 11].
   */
  @field:WireField(
    tag = 5,
    adapter = "com.squareup.wire.ProtoAdapter#UINT32",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val seq: Int = 0,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<Image, Image.Builder>(ADAPTER, unknownFields) {
  override fun newBuilder(): Builder {
    val builder = Builder()
    builder.format = format
    builder.width = width
    builder.height = height
    builder.image = image
    builder.seq = seq
    builder.addUnknownFields(unknownFields)
    return builder
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is Image) return false
    if (unknownFields != other.unknownFields) return false
    if (format != other.format) return false
    if (width != other.width) return false
    if (height != other.height) return false
    if (image != other.image) return false
    if (seq != other.seq) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + format.hashCode()
      result = result * 37 + width.hashCode()
      result = result * 37 + height.hashCode()
      result = result * 37 + image.hashCode()
      result = result * 37 + seq.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    if (format != null) result += """format=$format"""
    result += """width=$width"""
    result += """height=$height"""
    result += """image=$image"""
    result += """seq=$seq"""
    return result.joinToString(prefix = "Image{", separator = ", ", postfix = "}")
  }

  fun copy(
    format: ImageFormat? = this.format,
    width: Int = this.width,
    height: Int = this.height,
    image: ByteString = this.image,
    seq: Int = this.seq,
    unknownFields: ByteString = this.unknownFields
  ): Image = Image(format, width, height, image, seq, unknownFields)

  class Builder : Message.Builder<Image, Builder>() {
    @JvmField
    var format: ImageFormat? = null

    @JvmField
    var width: Int = 0

    @JvmField
    var height: Int = 0

    @JvmField
    var image: ByteString = ByteString.EMPTY

    @JvmField
    var seq: Int = 0

    fun format(format: ImageFormat?): Builder {
      this.format = format
      return this
    }

    /**
     * width is contained in format.
     */
    @Deprecated(message = "width is deprecated")
    fun width(width: Int): Builder {
      this.width = width
      return this
    }

    /**
     * height is contained in format.
     */
    @Deprecated(message = "height is deprecated")
    fun height(height: Int): Builder {
      this.height = height
      return this
    }

    /**
     * The organization of the pixels in the image buffer is from left to
     * right and bottom up.
     */
    fun image(image: ByteString): Builder {
      this.image = image
      return this
    }

    /**
     * [Output Only] Monotonically increasing sequence number in a stream of
     * screenshots. The first screenshot will have a sequence of 0. A single
     * screenshot will always have a sequence number of 0. The sequence is not
     * necessarily contiguous, and can be used to detect how many frames were
     * dropped. An example sequence could be: [0, 3, 5, 7, 9, 11].
     */
    fun seq(seq: Int): Builder {
      this.seq = seq
      return this
    }

    override fun build(): Image = Image(
      format = format,
      width = width,
      height = height,
      image = image,
      seq = seq,
      unknownFields = buildUnknownFields()
    )
  }

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<Image> = object : ProtoAdapter<Image>(
      FieldEncoding.LENGTH_DELIMITED, 
      Image::class, 
      "type.googleapis.com/android.emulation.control.Image", 
      PROTO_3, 
      null
    ) {
      override fun encodedSize(value: Image): Int {
        var size = value.unknownFields.size
        if (value.format != null) size += ImageFormat.ADAPTER.encodedSizeWithTag(1, value.format)
        if (value.width != 0) size += ProtoAdapter.UINT32.encodedSizeWithTag(2, value.width)
        if (value.height != 0) size += ProtoAdapter.UINT32.encodedSizeWithTag(3, value.height)
        if (value.image != ByteString.EMPTY) size += ProtoAdapter.BYTES.encodedSizeWithTag(4,
            value.image)
        if (value.seq != 0) size += ProtoAdapter.UINT32.encodedSizeWithTag(5, value.seq)
        return size
      }

      override fun encode(writer: ProtoWriter, value: Image) {
        if (value.format != null) ImageFormat.ADAPTER.encodeWithTag(writer, 1, value.format)
        if (value.width != 0) ProtoAdapter.UINT32.encodeWithTag(writer, 2, value.width)
        if (value.height != 0) ProtoAdapter.UINT32.encodeWithTag(writer, 3, value.height)
        if (value.image != ByteString.EMPTY) ProtoAdapter.BYTES.encodeWithTag(writer, 4,
            value.image)
        if (value.seq != 0) ProtoAdapter.UINT32.encodeWithTag(writer, 5, value.seq)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): Image {
        var format: ImageFormat? = null
        var width: Int = 0
        var height: Int = 0
        var image: ByteString = ByteString.EMPTY
        var seq: Int = 0
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> format = ImageFormat.ADAPTER.decode(reader)
            2 -> width = ProtoAdapter.UINT32.decode(reader)
            3 -> height = ProtoAdapter.UINT32.decode(reader)
            4 -> image = ProtoAdapter.BYTES.decode(reader)
            5 -> seq = ProtoAdapter.UINT32.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return Image(
          format = format,
          width = width,
          height = height,
          image = image,
          seq = seq,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: Image): Image = value.copy(
        format = value.format?.let(ImageFormat.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }
}
