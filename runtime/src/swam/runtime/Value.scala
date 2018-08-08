/*
 * Copyright 2018 Lucas Satabin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package swam
package runtime

sealed abstract class Value(val tpe: ValType) {
  def asInt: Int = throw new ConversionException("Cannot be converted to Int")
  def asLong: Long = throw new ConversionException("Cannot be converted to Long")
  def asFloat: Float = throw new ConversionException("Cannot be converted to Float")
  def asDouble: Double = throw new ConversionException("Cannot be converted to Double")
}
object Value {
  case class Int32(i: Int) extends Value(ValType.I32) {
    override def asInt = i
  }
  case class Int64(l: Long) extends Value(ValType.I64) {
    override def asLong = l
  }
  case class Float32(f: Float) extends Value(ValType.F32) {
    override def asFloat = f
    override def equals(that: Any): Boolean = that match {
      case Float32(f1) => f.isNaN && f1.isNaN || f == f1
      case _ => false
    }
  }
  case class Float64(d: Double) extends Value(ValType.F64) {
    override def asDouble = d
    override def equals(that: Any): Boolean = that match {
      case Float64(d2) => d.isNaN && d2.isNaN || d == d2
      case _ => false
    }
  }

  def zero(tpe: ValType): Value = tpe match {
    case ValType.I32 => Int32(0)
    case ValType.I64 => Int64(0l)
    case ValType.F32 => Float32(0.0f)
    case ValType.F64 => Float64(0.0d)
  }
}
