`BOOTSTRAP_SERVERS_CONFIG` needs to be updated

`topicName` needs to be updated to produce message to different topic 

`testData.json` needs to be updated with the message which is expected by the topic

check out `message` method in `Message` object in case you want to override different variable inside the json (current version overwrites `id` only)

`don't forget to check feed and exec methods to overwrite session`

and check `execute` method in `KafkaProducerAction` class in case you want to get the value from session that you set during the `Simulation`