# Exemplos de Consumer

Este diretório contém exemplos práticos de implementação de Consumers Kafka.

## Exemplos Disponíveis

### 1. Simple Consumer (Java)
`SimpleConsumer.java` - Consumer básico que lê mensagens

**Funcionalidades:**
- Configuração básica
- Leitura contínua de mensagens
- Commit automático
- Tratamento de erros

**Como usar:**
```bash
javac -cp ".:kafka-clients-*.jar" SimpleConsumer.java
java -cp ".:kafka-clients-*.jar" SimpleConsumer
```

### 2. Consumer com Commit Manual
`ManualCommitConsumer.java` - Consumer com controle manual de offset

**Funcionalidades:**
- Commit manual síncrono
- Commit manual assíncrono
- Maior controle sobre processamento

### 3. Consumer Group
`ConsumerGroupExample.java` - Exemplo de múltiplos consumers em grupo

**Funcionalidades:**
- Consumer groups
- Rebalancing
- Distribuição de partições

### 4. Consumer com Tratamento de Erros
`ResilientConsumer.java` - Consumer robusto com retry

**Funcionalidades:**
- Retry logic
- Dead letter queue
- Logging de erros

### 5. Consumer de Múltiplos Tópicos
`MultiTopicConsumer.java` - Consumer que lê vários tópicos

**Funcionalidades:**
- Subscribe em múltiplos tópicos
- Pattern matching de tópicos
- Processamento diferenciado por tópico

## Configurações Importantes

```java
// Básico
props.put("bootstrap.servers", "localhost:9092");
props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

// Consumer Group
props.put("group.id", "my-consumer-group");

// Auto commit
props.put("enable.auto.commit", "true"); // false para manual
props.put("auto.commit.interval.ms", "5000");

// Offset reset
props.put("auto.offset.reset", "earliest"); // earliest, latest, none

// Heartbeat e session
props.put("session.timeout.ms", "30000");
props.put("heartbeat.interval.ms", "3000");
props.put("max.poll.interval.ms", "300000");

// Fetch size
props.put("max.poll.records", "500");
props.put("fetch.min.bytes", "1");
props.put("fetch.max.wait.ms", "500");
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

1. **Sempre especifique um group.id**
   ```java
   props.put("group.id", "meu-servico-v1");
   ```

2. **Use shutdown hooks** para graceful shutdown
   ```java
   Runtime.getRuntime().addShutdownHook(new Thread(() -> {
       consumer.wakeup();
   }));
   ```

3. **Commit após processamento bem-sucedido**
   ```java
   ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
   for (ConsumerRecord<String, String> record : records) {
       processRecord(record);
       consumer.commitSync(Collections.singletonMap(
           new TopicPartition(record.topic(), record.partition()),
           new OffsetAndMetadata(record.offset() + 1)
       ));
   }
   ```

4. **Handle rebalance callback**
   ```java
   consumer.subscribe(Arrays.asList("topic"), new ConsumerRebalanceListener() {
       public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
           // Commit offsets antes de perder partições
       }
       public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
           // Inicializar estado para novas partições
       }
   });
   ```

## Offset Management

### Auto Commit (Simples)
```java
props.put("enable.auto.commit", "true");
props.put("auto.commit.interval.ms", "5000");
```

**Vantagens:**
- Simples de implementar
- Menos código

**Desvantagens:**
- Pode perder mensagens em crash
- Pode processar duplicadas

### Manual Commit Síncrono (Confiável)
```java
props.put("enable.auto.commit", "false");

while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        processRecord(record);
    }
    consumer.commitSync(); // Bloqueia até commit ser confirmado
}
```

**Vantagens:**
- Garante que offset só é commitado após processamento
- Mais confiável

**Desvantagens:**
- Mais lento (operação síncrona)

### Manual Commit Assíncrono (Performático)
```java
props.put("enable.auto.commit", "false");

while (true) {
    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
    for (ConsumerRecord<String, String> record : records) {
        processRecord(record);
    }
    consumer.commitAsync((offsets, exception) -> {
        if (exception != null) {
            log.error("Commit falhou", exception);
        }
    });
}
```

**Vantagens:**
- Não bloqueia
- Melhor performance

**Desvantagens:**
- Pode perder commits em falha
- Ordem de commits não garantida

## Consumer Lag

Consumer lag é o atraso entre producer e consumer.

**Monitorar:**
```bash
kafka-consumer-groups.sh --bootstrap-server localhost:9092 \
  --describe --group meu-consumer-group
```

**Resolver lag:**
1. Aumentar número de consumers no grupo
2. Aumentar `max.poll.records`
3. Otimizar processamento
4. Aumentar número de partições

## Troubleshooting

### Problema: "CommitFailedException"
- Consumer saiu do grupo durante processamento
- Aumente `max.poll.interval.ms`
- Reduza `max.poll.records`
- Otimize processamento

### Problema: Rebalance constante
- Processamento muito lento
- Session timeout muito baixo
- Aumente `session.timeout.ms`
- Aumente `max.poll.interval.ms`

### Problema: Mensagens duplicadas
- Use idempotência no processamento
- Ou use commit manual após processamento
- Considere transactions

### Problema: Mensagens puladas
- Auto commit commitou antes de processar
- Use commit manual
- Ou implemente retry logic
