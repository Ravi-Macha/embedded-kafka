/*
 * Copyright 2016 Tuplejump
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

package com.tuplejump.embedded.kafka

import java.util.Properties

import scala.collection.JavaConverters._

/**
  * TODO remove hard coded, defaults, add config.
  * * Replication configurations *
  * "num.replica.fetchers" -> "4",
  * "replica.fetch.max.bytes=1048576
  * "replica.fetch.wait.max.ms=500
  * "replica.high.watermark.checkpoint.interval.ms=5000
  * "replica.socket.timeout.ms=30000
  * "replica.socket.receive.buffer.bytes=65536
  * "replica.lag.time.max.ms=10000

  * "controller.socket.timeout.ms=30000
  * "controller.message.queue.size=10

  * * Log configuration *
  * "num.partitions" -> "8",
  * "message.max.bytes" -> "1000000",
  * "auto.create.topics.enable" -> "true",
  * "log.index.interval.bytes" -> "4096",
  * "log.index.size.max.bytes" -> "10485760",
  * "log.retention.hours" -> "168",
  * "log.flush.interval.ms" -> "10000",
  * "log.flush.interval.messages" -> "20000",
  * "log.flush.scheduler.interval.ms" -> "2000",
  * "log.roll.hours" -> "168",
  * "log.retention.check.interval.ms" -> "300000",
  * "log.segment.bytes" -> "1073741824",

  * * ZK configuration *
  * "zookeeper.connection.timeout.ms" -> "6000",
  * "zookeeper.sync.time.ms" -> "2000",

  * * Socket server configuration *
  * "num.io.threads" -> "8",
  * "num.network.threads" -> "8",
  * "socket.request.max.bytes" -> "104857600",
  * "socket.receive.buffer.bytes" -> "1048576",
  * "socket.send.buffer.bytes" -> "1048576",
  * "queued.max.requests" -> "16",
  * "fetch.purgatory.purge.interval.requests" -> "100",
  * "producer.purgatory.purge.interval.requests" -> "100"
  */
trait Settings {

  def brokerConfig(kafkaConnect: String, zkConnect: String, logDir: String): Map[String, String] = {
    val seed = kafkaConnect.split(",").head.split(":").head //TODO cleanup
    Map(
      //"broker.id" -> "0",
      "host.name" -> kafkaConnect.split(",").head.split(":").head, //TODO cleanup,
      "metadata.broker.list" -> kafkaConnect,
      "advertised.host.name" -> seed,
      "advertised.port" -> "9092",
      "log.dir" -> logDir,
      "log.dirs" -> logDir,
      "zookeeper.connect" -> zkConnect,
      "replica.high.watermark.checkpoint.interval.ms" -> "5000",
      "log.flush.interval.messages" -> "1",
      "replica.socket.timeout.ms" -> "1500",
      "controlled.shutdown.enable" -> "true"
    )
  }

  def producerConfig(kafkaConnect: String, kSerializer: Class[_], vSerializer: Class[_]): Map[String, String] =
    Map(
      "metadata.broker.list" -> kafkaConnect,//bug? fails if not set, warns if is
      "bootstrap.servers" -> kafkaConnect,
      "key.serializer" -> kSerializer.getName,
      "value.serializer" -> vSerializer.getName
    )

  /** offsetPolicy = latest,earliest,none */
  def consumerConfig(group: String,
                     kafkaConnect: String,
                     zkConnect: String,
                     offsetPolicy: String,
                     autoCommitEnabled: Boolean,
                     kDeserializer: Class[_],
                     vDeserializer: Class[_]): Map[String, String] =
    Map(
      "bootstrap.servers" -> kafkaConnect,
      "group.id" -> group,
      "auto.offset.reset" -> offsetPolicy,
      "enable.auto.commit" -> autoCommitEnabled.toString,
      "key.deserializer" -> kDeserializer.getName,
      "value.deserializer" -> vDeserializer.getName,
      "zookeeper.connect" -> zkConnect)


  def kafkaParams(group: String,
                  kafkaConnect: String,
                  zkConnect: String,
                  offsetPolicy: String,
                  autoCommitEnabled: Boolean,
                  kDeserializer: Class[_],
                  vDeserializer: Class[_]): Map[String, String] =
    consumerConfig(group, kafkaConnect, zkConnect, offsetPolicy, autoCommitEnabled, kDeserializer, vDeserializer)

  def mapToProps(values: Map[String, String]): Properties = {
    val props = new Properties()
    props.putAll(values.asJava)
    props
  }
}
