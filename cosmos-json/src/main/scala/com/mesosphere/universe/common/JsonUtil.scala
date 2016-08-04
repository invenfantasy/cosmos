package com.mesosphere.universe.common


import io.circe.Parser
import java.util.Base64
import java.nio.charset.StandardCharsets
import io.circe.Decoder
import io.circe.Error
import io.circe.Printer
import cats.data.Xor

object JsonUtil {

  def decode64[A: Decoder](str: String): Xor[Error,A] = {
    new Parser {}.decode[A](new String(Base64.getDecoder.decode(str),StandardCharsets.UTF_8))
  }
  val dropNullKeysPrinter: Printer = Printer.noSpaces.copy(dropNullKeys = true)
}
