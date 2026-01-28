# Instalação e Setup do Kafka

## Pré-requisitos

- Java 8+ instalado e configurado
- Docker (opcional, mas recomendado para desenvolvimento)

## Verificando Java

```bash
java -version
```

Deve mostrar Java 8 ou superior.

## Opção 1: Instalação com Docker (Recomendado)

### Vantagens
- Fácil de configurar
- Ambiente isolado
- Fácil de limpar/resetar

### Docker Compose

Crie um arquivo `docker-compose.yml`:

```yaml
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

### Iniciar o Kafka

```bash
docker-compose up -d
```

### Verificar Status

```bash
docker-compose ps
```

### Parar o Kafka

```bash
docker-compose down
```

## Opção 2: Instalação Manual

### Baixar Kafka

```bash
# Baixar a versão mais recente
wget https://archive.apache.org/dist/kafka/3.6.0/kafka_2.13-3.6.0.tgz

# Extrair
tar -xzf kafka_2.13-3.6.0.tgz
cd kafka_2.13-3.6.0
```

### Iniciar ZooKeeper

Em um terminal:

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

### Iniciar Kafka Broker

Em outro terminal:

```bash
bin/kafka-server-start.sh config/server.properties
```

## Primeiros Comandos

### Criar um Tópico

```bash
# Com Docker
docker exec -it <kafka-container-id> kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic test-topic

# Instalação Manual
bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic test-topic
```

### Listar Tópicos

```bash
# Com Docker
docker exec -it <kafka-container-id> kafka-topics --list \
  --bootstrap-server localhost:9092

# Instalação Manual
bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```

### Descrever um Tópico

```bash
# Com Docker
docker exec -it <kafka-container-id> kafka-topics --describe \
  --bootstrap-server localhost:9092 \
  --topic test-topic

# Instalação Manual
bin/kafka-topics.sh --describe \
  --bootstrap-server localhost:9092 \
  --topic test-topic
```

### Producer Console

```bash
# Com Docker
docker exec -it <kafka-container-id> kafka-console-producer \
  --bootstrap-server localhost:9092 \
  --topic test-topic

# Instalação Manual
bin/kafka-console-producer.sh \
  --bootstrap-server localhost:9092 \
  --topic test-topic
```

Digite mensagens e pressione Enter.

### Consumer Console

```bash
# Com Docker (em outro terminal)
docker exec -it <kafka-container-id> kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic test-topic \
  --from-beginning

# Instalação Manual
bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic test-topic \
  --from-beginning
```

## Configurações Importantes

### server.properties (Broker)

```properties
# ID único do broker
broker.id=1

# Porta de escuta
listeners=PLAINTEXT://localhost:9092

# Diretório de logs
log.dirs=/tmp/kafka-logs

# Número de partições padrão
num.partitions=3

# Retenção de mensagens (7 dias)
log.retention.hours=168
```

## Ferramentas de Gerenciamento

### Kafka Tool / Offset Explorer
- Interface gráfica para gerenciar Kafka
- Download: https://www.kafkatool.com/

### Kafdrop
- Interface web para visualizar tópicos
- Docker: `docker run -d -p 9000:9000 -e KAFKA_BROKERCONNECT=localhost:9092 obsidiandynamics/kafdrop`

### Conduktor (Pago/Trial)
- IDE completa para Kafka
- https://www.conduktor.io/

## Troubleshooting

### Problema: Não consegue conectar ao Kafka

**Solução:**
- Verifique se Kafka e ZooKeeper estão rodando
- Verifique as portas: 2181 (ZooKeeper) e 9092 (Kafka)
- Confirme o `advertised.listeners` no server.properties

### Problema: "Topic already exists"

**Solução:**
```bash
# Deletar tópico
kafka-topics.sh --delete --bootstrap-server localhost:9092 --topic nome-topico
```

### Problema: Kafka muito lento

**Solução:**
- Aumente a memória JVM
- Configure `log.flush.interval.messages` e `log.flush.interval.ms`
- Use SSDs para `log.dirs`

## Próximos Passos

Continue para [04-arquitetura.md](./04-arquitetura.md) para entender a arquitetura detalhada do Kafka.
