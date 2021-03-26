package com.ozan.berk.kafka

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolComponents, ProtocolKey}
import io.gatling.core.session.Session

object KafkaDsl {
  val KafkaProtocolKey: ProtocolKey[KafkaProtocol, KafkaComponents] = new ProtocolKey[KafkaProtocol, KafkaComponents] {
    override def protocolClass: Class[Protocol] = classOf[KafkaProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): KafkaProtocol = KafkaProtocol()

    override def newComponents(coreComponents: CoreComponents): KafkaProtocol => KafkaComponents = {
      kafkaProtocol => {
        val kafkaComponents = KafkaComponents(kafkaProtocol)
        kafkaComponents
      }
    }
  }
}

case class KafkaProtocol() extends Protocol {
  type Components = KafkaComponents

  def apply(): KafkaProtocol = KafkaProtocol()
}

case class KafkaComponents(kafkaProtocol: KafkaProtocol) extends ProtocolComponents {
  override def onStart: Session => Session = ProtocolComponents.NoopOnStart

  override def onExit: Session => Unit = ProtocolComponents.NoopOnExit
}