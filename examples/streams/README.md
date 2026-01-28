# Kafka Streams - Processamento de Streams

Este diretório contém exemplos de Kafka Streams para processamento em tempo real.

## O que é Kafka Streams?

Kafka Streams é uma biblioteca cliente para construir aplicações e microserviços de processamento de streams, onde os dados de entrada e saída são armazenados em clusters Kafka.

## Características

- **Simples**: API de alto nível para operações comuns
- **Stateful**: Suporta processamento com estado
- **Escalável**: Paralelização automática
- **Fault-tolerant**: Recuperação automática de falhas
- **Exatamente uma vez**: Garantias de processamento

## Conceitos Principais

### 1. KStream
Stream de registros (imutável)
```java
KStream<String, String> stream = builder.stream("input-topic");
```

### 2. KTable
Tabela de changelog (mutável, última versão)
```java
KTable<String, String> table = builder.table("users-topic");
```

### 3. GlobalKTable
Tabela replicada em todas instâncias
```java
GlobalKTable<String, String> global = builder.globalTable("reference-data");
```

## Operações Comuns

### Transformações

#### Filter
```java
stream.filter((key, value) -> value.contains("importante"));
```

#### Map
```java
stream.map((key, value) -> KeyValue.pair(key.toUpperCase(), value));
```

#### FlatMap
```java
stream.flatMapValues(value -> Arrays.asList(value.split(" ")));
```

### Agregações

#### Count
```java
stream.groupByKey()
      .count()
      .toStream();
```

#### Reduce
```java
stream.groupByKey()
      .reduce((aggValue, newValue) -> aggValue + newValue);
```

### Joins

#### Stream-Stream Join
```java
leftStream.join(rightStream,
    (leftValue, rightValue) -> leftValue + rightValue,
    JoinWindows.of(Duration.ofMinutes(5)));
```

#### Stream-Table Join
```java
stream.join(table,
    (streamValue, tableValue) -> streamValue + tableValue);
```

## Exemplo Completo: Word Count

```java
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import java.util.Arrays;

public class WordCountExample {
    public static void main(String[] args) {
        StreamsBuilder builder = new StreamsBuilder();
        
        // Input stream
        KStream<String, String> textLines = 
            builder.stream("text-input");
        
        // Processing
        textLines
            .flatMapValues(line -> Arrays.asList(line.toLowerCase().split("\\W+")))
            .groupBy((key, word) -> word)
            .count()
            .toStream()
            .to("word-count-output");
        
        // Start application
        KafkaStreams streams = new KafkaStreams(builder.build(), config);
        streams.start();
    }
}
```

## Casos de Uso

1. **Real-time Analytics**: Agregações em tempo real
2. **ETL**: Transformação de dados
3. **Alerting**: Detecção de padrões
4. **Enrichment**: Junção de streams
5. **Monitoring**: Métricas em tempo real

## Pré-requisitos

```xml
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-streams</artifactId>
    <version>3.6.0</version>
</dependency>
```

## Próximos Passos

- Explore os exemplos neste diretório
- Leia [Kafka Streams Documentation](https://kafka.apache.org/documentation/streams/)
- Pratique com os exercícios em `/exercises`
