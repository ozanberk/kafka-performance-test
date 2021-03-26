package com.ozan.berk.util

import scala.io.Source

object JsonUtil {
  def readFile(fileName: String): String = {
    val file = Source.fromFile(getClass.getClassLoader.getResource("data/" + fileName).toURI)
    file.mkString
  }
}