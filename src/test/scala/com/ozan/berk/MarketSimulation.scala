package com.ozan.berk

import com.ozan.berk.kafka.KafkaProtocol
import com.ozan.berk.kafka.consumer.KafkaConsumerAction
import com.ozan.berk.kafka.producer.KafkaProducerAction
import com.ozan.berk.util.Message
import io.gatling.core.Predef._
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.{ScenarioBuilder, ScenarioContext}

import scala.concurrent.duration._

class MarketSimulation extends Simulation {

  private val produceMarkets: ActionBuilder = (ctx: ScenarioContext, next: Action) => {
    new KafkaProducerAction(ctx, ctx.throttled, next)
  }

  private val consumeMarkets: ActionBuilder = (ctx: ScenarioContext, next: Action) => {
    new KafkaConsumerAction(ctx, ctx.throttled, next)
  }

  val kafkaProtocol: KafkaProtocol = KafkaProtocol()

  val marketCreation: ScenarioBuilder = scenario("Message Production to the Kafka")
    .feed(Iterator.continually(Map("id" -> java.util.UUID.randomUUID.toString)))
    .exec(session => session.set("event", Message.generateMessage(session("id").as[String])))
    .exec(produceMarkets)

  val marketConsuming: ScenarioBuilder = scenario("Message Consuming from the Kafka")
    .exec(consumeMarkets)

  setUp(
    marketCreation.inject(constantUsersPerSec(10) during (10 seconds))
      .protocols(kafkaProtocol),
    marketConsuming.inject(constantConcurrentUsers(1) during (1 minutes))
      .protocols(kafkaProtocol)
  ).assertions(
    global.responseTime.max.lt(5000),
    global.successfulRequests.percent.gt(95),
    forAll.failedRequests.percent.lt(1))
}