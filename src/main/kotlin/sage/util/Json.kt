package sage.util

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import java.io.IOException

object Json {
  private val om = ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

  @JvmStatic fun json(`object`: Any): String {
    try {
      return om.writeValueAsString(`object`)
    } catch (e: JsonProcessingException) {
      throw IllegalArgumentException(e)
    }

  }

  fun <T> `object`(json: String, type: Class<T>): T {
    try {
      return om.readValue(json, type)
    } catch (e: IOException) {
      throw IllegalArgumentException(e)
    }

  }

  fun <T> `object`(json: String, jt: JavaType): T {
    try {
      return om.readValue<T>(json, jt)
    } catch (e: IOException) {
      throw IllegalArgumentException(e)
    }

  }

  fun typeFactory(): TypeFactory {
    return om.typeFactory
  }
}
