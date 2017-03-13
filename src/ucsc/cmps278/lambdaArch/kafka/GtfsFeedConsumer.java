package ucsc.cmps278.lambdaArch.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class GtfsFeedConsumer {

	String topic;
	Consumer<String, String> consumer;

	public GtfsFeedConsumer(String topic, Properties prop) {
		this.topic = topic;
		this.consumer = new KafkaConsumer<String, String>(prop);
	}

	public ArrayList<String> consume() throws Exception {
		consumer.subscribe(Arrays.asList(topic));
		
		ConsumerRecords<String, String> records = consumer.poll(100);
		System.out.println("Number of records: " + records.count());

		ArrayList<String> values = new ArrayList<>();
		
		for (ConsumerRecord<String, String> record : records) {
			System.out.println("offset = " + record.offset() + " key = " + record.key() + " value = " + record.value());
			values.add(record.value());
		}
		
		return values;
	}
}