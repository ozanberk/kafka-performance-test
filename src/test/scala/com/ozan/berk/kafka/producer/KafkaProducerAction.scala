package com.ozan.berk.kafka.producer

import java.util.Properties

import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.util.{Clock, DefaultClock}
import io.gatling.core.action.{Action, ExitableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine
import io.gatling.core.structure.ScenarioContext
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

class KafkaProducerAction[K, V](val ctx: ScenarioContext,
                                val throttled: Boolean,
                                val next: Action) extends ExitableAction {
  override def statsEngine: StatsEngine = ctx.coreComponents.statsEngine

  override def clock: Clock = new DefaultClock

  override def name: String = "Kafka Message Producer"

  val topicName: String = "topicName"

  def getProducerProperties: Properties = {
    val properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "bootstrap.servers")
    properties.setProperty(ProducerConfig.ACKS_CONFIG, "1")
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
    properties
  }

  override def execute(session: Session): Unit = {
    val producer = new KafkaProducer[String, String](getProducerProperties)
    val record = new ProducerRecord[String, String](topicName, session("id").as[String], session("event").as[String])

    producer.send(record, new Callback {
      val requestStartDate: Long = clock.nowMillis

      override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
        val requestEndDate: Long = clock.nowMillis
        statsEngine.logResponse(
          session,
          "kafka-performance-test-producer",
          startTimestamp = requestStartDate,
          endTimestamp = requestEndDate,
          if (e == null) OK else KO,
          None,
          if (e == null) None else Some(e.getMessage)
        )
        if (throttled) {
          ctx.coreComponents.throttler.throttle(session.scenario, () => next ! session)
        } else {
          next ! session
        }
      }
    })
  }
}
