// Code generated by Wire protocol buffer compiler, do not edit.
// Source: android.emulation.control.PhysicalModelValue in emulator_controller.proto
package com.android.emulator.control

import com.squareup.wire.EnumAdapter
import com.squareup.wire.FieldEncoding
import com.squareup.wire.Message
import com.squareup.wire.ProtoAdapter
import com.squareup.wire.ProtoReader
import com.squareup.wire.ProtoWriter
import com.squareup.wire.Syntax
import com.squareup.wire.Syntax.PROTO_3
import com.squareup.wire.WireEnum
import com.squareup.wire.WireField
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.hashCode
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic
import okio.ByteString

class PhysicalModelValue(
  @field:WireField(
    tag = 1,
    adapter = "com.android.emulator.control.PhysicalModelValue${'$'}PhysicalType#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val target: PhysicalType = PhysicalType.POSITION,
  /**
   * [Output Only]
   */
  @field:WireField(
    tag = 2,
    adapter = "com.android.emulator.control.PhysicalModelValue${'$'}State#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val status: State = State.OK,
  /**
   * Value interpretation depends on sensor, will contain at most 3 values.
   */
  @field:WireField(
    tag = 3,
    adapter = "com.android.emulator.control.ParameterValue#ADAPTER",
    label = WireField.Label.OMIT_IDENTITY
  )
  @JvmField
  val value: ParameterValue? = null,
  unknownFields: ByteString = ByteString.EMPTY
) : Message<PhysicalModelValue, PhysicalModelValue.Builder>(ADAPTER, unknownFields) {
  override fun newBuilder(): Builder {
    val builder = Builder()
    builder.target = target
    builder.status = status
    builder.value = value
    builder.addUnknownFields(unknownFields)
    return builder
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) return true
    if (other !is PhysicalModelValue) return false
    if (unknownFields != other.unknownFields) return false
    if (target != other.target) return false
    if (status != other.status) return false
    if (value != other.value) return false
    return true
  }

  override fun hashCode(): Int {
    var result = super.hashCode
    if (result == 0) {
      result = unknownFields.hashCode()
      result = result * 37 + target.hashCode()
      result = result * 37 + status.hashCode()
      result = result * 37 + value.hashCode()
      super.hashCode = result
    }
    return result
  }

  override fun toString(): String {
    val result = mutableListOf<String>()
    result += """target=$target"""
    result += """status=$status"""
    if (value != null) result += """value=$value"""
    return result.joinToString(prefix = "PhysicalModelValue{", separator = ", ", postfix = "}")
  }

  fun copy(
    target: PhysicalType = this.target,
    status: State = this.status,
    value: ParameterValue? = this.value,
    unknownFields: ByteString = this.unknownFields
  ): PhysicalModelValue = PhysicalModelValue(target, status, value, unknownFields)

  class Builder : Message.Builder<PhysicalModelValue, Builder>() {
    @JvmField
    var target: PhysicalType = PhysicalType.POSITION

    @JvmField
    var status: State = State.OK

    @JvmField
    var value: ParameterValue? = null

    fun target(target: PhysicalType): Builder {
      this.target = target
      return this
    }

    /**
     * [Output Only]
     */
    fun status(status: State): Builder {
      this.status = status
      return this
    }

    /**
     * Value interpretation depends on sensor, will contain at most 3 values.
     */
    fun value(value: ParameterValue?): Builder {
      this.value = value
      return this
    }

    override fun build(): PhysicalModelValue = PhysicalModelValue(
      target = target,
      status = status,
      value = value,
      unknownFields = buildUnknownFields()
    )
  }

  companion object {
    @JvmField
    val ADAPTER: ProtoAdapter<PhysicalModelValue> = object : ProtoAdapter<PhysicalModelValue>(
      FieldEncoding.LENGTH_DELIMITED, 
      PhysicalModelValue::class, 
      "type.googleapis.com/android.emulation.control.PhysicalModelValue", 
      PROTO_3, 
      null
    ) {
      override fun encodedSize(value: PhysicalModelValue): Int {
        var size = value.unknownFields.size
        if (value.target != PhysicalType.POSITION) size +=
            PhysicalType.ADAPTER.encodedSizeWithTag(1, value.target)
        if (value.status != State.OK) size += State.ADAPTER.encodedSizeWithTag(2, value.status)
        if (value.value != null) size += ParameterValue.ADAPTER.encodedSizeWithTag(3, value.value)
        return size
      }

      override fun encode(writer: ProtoWriter, value: PhysicalModelValue) {
        if (value.target != PhysicalType.POSITION) PhysicalType.ADAPTER.encodeWithTag(writer, 1,
            value.target)
        if (value.status != State.OK) State.ADAPTER.encodeWithTag(writer, 2, value.status)
        if (value.value != null) ParameterValue.ADAPTER.encodeWithTag(writer, 3, value.value)
        writer.writeBytes(value.unknownFields)
      }

      override fun decode(reader: ProtoReader): PhysicalModelValue {
        var target: PhysicalType = PhysicalType.POSITION
        var status: State = State.OK
        var value: ParameterValue? = null
        val unknownFields = reader.forEachTag { tag ->
          when (tag) {
            1 -> try {
              target = PhysicalType.ADAPTER.decode(reader)
            } catch (e: ProtoAdapter.EnumConstantNotFoundException) {
              reader.addUnknownField(tag, FieldEncoding.VARINT, e.value.toLong())
            }
            2 -> try {
              status = State.ADAPTER.decode(reader)
            } catch (e: ProtoAdapter.EnumConstantNotFoundException) {
              reader.addUnknownField(tag, FieldEncoding.VARINT, e.value.toLong())
            }
            3 -> value = ParameterValue.ADAPTER.decode(reader)
            else -> reader.readUnknownField(tag)
          }
        }
        return PhysicalModelValue(
          target = target,
          status = status,
          value = value,
          unknownFields = unknownFields
        )
      }

      override fun redact(value: PhysicalModelValue): PhysicalModelValue = value.copy(
        value = value.value?.let(ParameterValue.ADAPTER::redact),
        unknownFields = ByteString.EMPTY
      )
    }

    private const val serialVersionUID: Long = 0L
  }

  enum class State(
    override val value: Int
  ) : WireEnum {
    OK(0),

    /**
     * qemud service is not available/initiated.
     */
    NO_SERVICE(-3),

    /**
     * Sensor is disabled.
     */
    DISABLED(-2),

    /**
     * Unknown sensor (should not happen)
     */
    UNKNOWN(-1);

    companion object {
      @JvmField
      val ADAPTER: ProtoAdapter<State> = object : EnumAdapter<State>(
        State::class, 
        PROTO_3, 
        State.OK
      ) {
        override fun fromValue(value: Int): State? = State.fromValue(value)
      }

      @JvmStatic
      fun fromValue(value: Int): State? = when (value) {
        0 -> OK
        -3 -> NO_SERVICE
        -2 -> DISABLED
        -1 -> UNKNOWN
        else -> null
      }
    }
  }

  /**
   * Details on the sensors documentation can be found here:
   * https://developer.android.com/reference/android/hardware/Sensor.html#TYPE_
   */
  enum class PhysicalType(
    override val value: Int
  ) : WireEnum {
    POSITION(0),

    /**
     * All values are angles in degrees.
     * values = [x,y,z]
     */
    ROTATION(1),

    MAGNETIC_FIELD(2),

    /**
     * Temperature in °C
     */
    TEMPERATURE(3),

    /**
     * Proximity sensor distance measured in centimeters
     */
    PROXIMITY(4),

    /**
     * Ambient light level in SI lux units
     */
    LIGHT(5),

    /**
     * Atmospheric pressure in hPa (millibar)
     */
    PRESSURE(6),

    /**
     * Relative ambient air humidity in percent
     */
    HUMIDITY(7),

    VELOCITY(8),

    AMBIENT_MOTION(9);

    companion object {
      @JvmField
      val ADAPTER: ProtoAdapter<PhysicalType> = object : EnumAdapter<PhysicalType>(
        PhysicalType::class, 
        PROTO_3, 
        PhysicalType.POSITION
      ) {
        override fun fromValue(value: Int): PhysicalType? = PhysicalType.fromValue(value)
      }

      @JvmStatic
      fun fromValue(value: Int): PhysicalType? = when (value) {
        0 -> POSITION
        1 -> ROTATION
        2 -> MAGNETIC_FIELD
        3 -> TEMPERATURE
        4 -> PROXIMITY
        5 -> LIGHT
        6 -> PRESSURE
        7 -> HUMIDITY
        8 -> VELOCITY
        9 -> AMBIENT_MOTION
        else -> null
      }
    }
  }
}
