package com.ozan.berk.util

object Message {
  def generateMessage(id: String): String = {
    val update = JsonUtil.readFile("testData.json").replace("{{id}}", id)
    update
  }
}