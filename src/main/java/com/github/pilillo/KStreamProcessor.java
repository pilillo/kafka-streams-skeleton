package com.github.pilillo;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;

import java.util.Properties;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

public class KStreamProcessor {
	private Properties props;
	private KStreamBuilder builder;
	private KafkaStreams streams;
	
	public KStreamProcessor(String appName, String bootstrap, String zookeper, String schemaRegistry, String offsetStartPolicy){
		props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appName);
        
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(StreamsConfig.ZOOKEEPER_CONNECT_CONFIG, zookeper);
        if(schemaRegistry != null && !schemaRegistry.isEmpty())
        	props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, schemaRegistry);
        
        
        
        
        props.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG,
        									GenericAvroSerde.class
        									//Serdes.String().getClass().getName()
        );
        
        // https://cwiki.apache.org/confluence/display/KAFKA/Kafka+Streams+Application+Reset+Tool
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetStartPolicy);

        builder = new KStreamBuilder();
	}
	
	public KStream getStream(Serde keySerde, Serde valSerde, String ... topics){
		
		for(String t : topics){
			System.out.println("Connecting to topic "+t);
		}
		
		return builder.stream(keySerde, valSerde, topics);
	}
	
	public KStream getStream(String ... topics){
		return builder.stream(topics);
	}
	
	public void start(){
		streams = new KafkaStreams(builder, props);
		streams.start();
	}
	
	public void close(){
		streams.close();
	}

	public static void main(String[] args) {
		
		ArgumentParser parser = ArgumentParsers.newArgumentParser("StreamProcessor")
				.defaultHelp(true)
				.description("An example stream processor using Kafka Streams");

		parser.addArgument("--name").type(String.class).help("name of the streaming application being started");
		parser.addArgument("--zookeeper").type(String.class).help("path to zookeeper");
		parser.addArgument("--broker").type(String.class).help("path to the Kafka broker");
		parser.addArgument("--schema-registry").type(String.class).help("path to the schema registry");
		parser.addArgument("--from-beginning").action(Arguments.storeTrue()).help("whether to start from the beginning of the topic (i.e., retention time)");
		parser.addArgument("--topic").type(String.class).help("whether to start from the beginning of the topic (i.e., retention time)");
		
		/*
		Namespace ns = parser.parseArgsOrFail(args);
		
		String appId = ns.getString("name");
		String zookeeper = ns.getString("zookeeper");
		String broker = ns.getString("broker");
		String schemaRegistry = ns.getString("schema_registry");
		boolean fromBeginning = ns.getBoolean("from_beginning");
		*/
		boolean okargs = false;
		String appId = "";
		String zookeeper = "";
		String broker= "";
		String schemaRegistry= "";
		boolean fromBeginning = false;
		String topic = "";
		try {
            Namespace ns = parser.parseArgs(args);
            appId = ns.getString("name");
    		zookeeper = ns.getString("zookeeper");
    		broker = ns.getString("broker");
    		schemaRegistry = ns.getString("schema_registry");
    		fromBeginning = ns.getBoolean("from_beginning");
    		topic = ns.getString("topic");
    		okargs = true;
        } catch (ArgumentParserException e) {
            parser.handleError(e);
        }

		if(okargs){
			KStreamProcessor ksp = new KStreamProcessor(appId, broker, zookeeper, schemaRegistry, fromBeginning ? "earliest" : "latest");
			
			// connect to the input Kafka topics
			KStream<String, GenericRecord> stream = ksp.getStream(topic);
			
			stream
			
				.foreach(new ForeachAction<String, GenericRecord>() {
				@Override
				public void apply(String key, GenericRecord value) {
					//System.out.println(key + ": " + value.toString());
					//System.out.println(value.get("avro_field1")+", "+value.get("avro_field2"));
				}
			 });
			
			/*
			stream.foreach(new ForeachAction<String, String>() {
			    public void apply(String key, String value) {
			        System.out.println(key + ": " + value);
			    }
			 });
			*/
			
			
			// start the processing
			ksp.start();
			  
			// close the stream as soon as we terminate it with a SIGNAL
			Runtime.getRuntime().addShutdownHook(new Thread(ksp::close));
		}
		
	}

}
