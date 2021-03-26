package com.ozan.berk.kafka.consumer

import java.time.Duration
import java.util.{Collections, Properties}

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.{Clock, DefaultClock}
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.gatling.core.structure.ScenarioContext
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecords, KafkaConsumer}
import org.apache.kafka.common.serialization.StringDeserializer

class KafkaConsumerAction[K, V](val ctx: ScenarioContext,
                                val throttled: Boolean,
                                val next: Action) extends ExitableAction {
  override def statsEngine: StatsEngine = ctx.coreComponents.statsEngine

  override def clock: Clock = new DefaultClock

  override def name: String = "Kafka Props Consumer"

  val topicName: String = "topicName"

  def getConsumerProperties: Properties = {
    val properties = new Properties();
    properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "bootstrap.servers")
    properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "scala-consumer")
    properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
    properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
    properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    properties
  }

  override def execute(session: Session): Unit = {
    val consumer = new KafkaConsumer[String, String](getConsumerProperties)
    consumer.subscribe(Collections.singletonList(topicName))

    val requestStartDate = clock.nowMillis

    while (requestStartDate < requestStartDate + 10000) {
      val pollMessages: ConsumerRecords[String, String] = consumer.poll(Duration.ofSeconds(5))

      val requestEndDate = clock.nowMillis

      pollMessages.forEach { message =>
        println(message.key())
        statsEngine.logResponse(session,
          "kafka-performance-test-consumer",
          startTimestamp = requestStartDate,
          endTimestamp = requestEndDate,
          if (message.value() != null) OK else KO,
          None,
          if (message.value() == null) None else Some(message.key())
        )
      }
      consumer.close()
    }
  }
}
