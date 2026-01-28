# Exemplos de Producer

Este diretório contém exemplos práticos de implementação de Producers Kafka.

## Exemplos Disponíveis

### 1. Simple Producer (Java)
`SimpleProducer.java` - Producer básico que envia mensagens de texto

**Funcionalidades:**
- Configuração básica
- Envio síncrono
- Tratamento de erros

**Como usar:**
```bash
javac -cp ".:kafka-clients-*.jar" SimpleProducer.java
java -cp ".:kafka-clients-*.jar" SimpleProducer
```

### 2. Producer com Chave
`ProducerWithKey.java` - Producer que usa chaves para garantir ordem

**Funcionalidades:**
- Mensagens com chave
- Particionamento por chave
- Garantia de ordem para mesma chave

### 3. Producer Assíncrono
`AsyncProducer.java` - Producer com callbacks assíncronos

**Funcionalidades:**
- Envio assíncrono
- Callbacks de sucesso/erro
- Melhor performance

### 4. Producer com Compressão
`CompressedProducer.java` - Producer usando compressão

**Funcionalidades:**
- Compressão de mensagens (gzip, snappy, lz4)
- Economia de banda
- Trade-off latência vs throughput

### 5. Producer Transacional
`TransactionalProducer.java` - Producer com transações

**Funcionalidades:**
- Transações ACID
- Exatamente uma vez (exactly-once)
- Atomicidade de múltiplas mensagens

## Configurações Importantes

```java
// Básico
props.put("bootstrap.servers", "localhost:9092");
props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

// Acknowledgments
props.put("acks", "all"); // 0, 1, ou all

// Retries
props.put("retries", 3);
props.put("retry.backoff.ms", 1000);

// Batching
props.put("batch.size", 16384);
props.put("linger.ms", 10);

// Compressão
props.put("compression.type", "lz4"); // gzip, snappy, lz4, zstd
```

## Pré-requisitos

```xml
<!-- Maven dependency -->
<dependency>
    <groupId>org.apache.kafka</groupId>
    <artifactId>kafka-clients</artifactId>
    <version>3.6.0</version>
</dependency>
```

## Padrões e Boas Práticas

1. **Sempre feche o producer** quando terminar
   ```java
   try (Producer<String, String> producer = new KafkaProducer<>(props)) {
       // usar producer
   }
   ```

2. **Use callbacks** para produção assíncrona
   ```java
   producer.send(record, (metadata, exception) -> {
       if (exception != null) {
           log.error("Erro ao enviar", exception);
       }
   });
   ```

3. **Configure timeouts adequadamente**
   ```java
   props.put("max.block.ms", 60000);
   props.put("request.timeout.ms", 30000);
   ```

4. **Trate erros retriáveis vs não retriáveis**
   - Retriável: NetworkException, TimeoutException
   - Não retriável: SerializationException, RecordTooLargeException

## Troubleshooting

### Problema: "TimeoutException"
- Aumente `max.block.ms` e `request.timeout.ms`
- Verifique conectividade com brokers

### Problema: "BufferExhaustedException"
- Aumente `buffer.memory`
- Diminua taxa de produção
- Aumente número de brokers

### Problema: Mensagens fora de ordem
- Configure `max.in.flight.requests.per.connection=1`
- Use `enable.idempotence=true`
