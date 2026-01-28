# Arquitetura do Apache Kafka

## Visão Geral da Arquitetura

```
┌─────────────────────────────────────────────────────────┐
│                     Kafka Cluster                        │
│                                                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐              │
│  │ Broker 1 │  │ Broker 2 │  │ Broker 3 │              │
│  │          │  │          │  │          │              │
│  │ Topic A  │  │ Topic A  │  │ Topic B  │              │
│  │ Part 0   │  │ Part 1   │  │ Part 0   │              │
│  └──────────┘  └──────────┘  └──────────┘              │
│                                                           │
└─────────────────────────────────────────────────────────┘
        ▲                                      ▼
        │                                      │
   ┌─────────┐                          ┌──────────┐
   │Producer │                          │ Consumer │
   └─────────┘                          └──────────┘
```

## Componentes Detalhados

### 1. Broker

**Função:**
- Servidor que armazena e gerencia partições
- Processa requisições de producers e consumers
- Gerencia replicação de dados

**Características:**
- Cada broker tem um ID único no cluster
- Armazena partições de diferentes tópicos
- Comunica-se com outros brokers para replicação

**Configurações Importantes:**
```properties
broker.id=1
log.dirs=/var/kafka-logs
num.network.threads=8
num.io.threads=8
```

### 2. Topic e Partições

**Estrutura de um Tópico:**
```
Topic: "pedidos" (replication-factor=2)

Broker 1                Broker 2                Broker 3
├─ Partition 0 (L)     ├─ Partition 0 (F)     ├─ Partition 1 (F)
├─ Partition 2 (L)     ├─ Partition 1 (L)     └─ Partition 2 (F)

L = Leader
F = Follower
```

**Distribuição de Partições:**
- Cada partição tem um leader broker
- Followers replicam dados do leader
- Se o leader falha, um follower vira leader

### 3. Replicação

**Como Funciona:**

```
Producer
   ↓
Leader (Broker 1)
   ├─→ Follower 1 (Broker 2) ─ ISR (In-Sync Replica)
   └─→ Follower 2 (Broker 3) ─ ISR
```

**In-Sync Replicas (ISR):**
- Réplicas que estão sincronizadas com o leader
- Apenas ISRs podem se tornar leader
- Se uma réplica fica muito atrasada, sai do ISR

**Configuração:**
```properties
# Número mínimo de ISRs antes de aceitar write
min.insync.replicas=2

# Fator de replicação padrão
default.replication.factor=3
```

### 4. Producer em Detalhes

**Fluxo de Publicação:**

```
1. Producer cria Record
2. Serializa chave e valor
3. Partitioner determina partição
4. Record vai para batch buffer
5. Sender thread envia batch para broker
6. Broker responde com sucesso/erro
7. Producer recebe acknowledgment
```

**Estratégias de Particionamento:**

```java
// 1. Com chave - mesma partição
producer.send(new ProducerRecord<>("topic", "chave", "valor"));

// 2. Sem chave - round-robin
producer.send(new ProducerRecord<>("topic", "valor"));

// 3. Particionador customizado
producer.send(new ProducerRecord<>("topic", partition, "chave", "valor"));
```

**ACK Levels:**
- `acks=0`: Fire and forget (sem confirmação)
- `acks=1`: Leader confirma (rápido, menos durável)
- `acks=all`: Todos ISRs confirmam (lento, mais durável)

### 5. Consumer em Detalhes

**Fluxo de Consumo:**

```
1. Consumer se junta ao grupo
2. Coordinator atribui partições (rebalance)
3. Consumer busca offset inicial
4. Poll() busca mensagens
5. Processa mensagens
6. Commit offset
```

**Consumer Group:**

```
Consumer Group "app-service"
├─ Consumer 1 → Partition 0, 1
├─ Consumer 2 → Partition 2, 3
└─ Consumer 3 → Partition 4, 5

Se Consumer 2 cai:
├─ Consumer 1 → Partition 0, 1, 2
└─ Consumer 3 → Partition 3, 4, 5
```

**Rebalance:**
- Ocorre quando consumer entra/sai do grupo
- Ou quando número de partições muda
- Durante rebalance, consumers param de processar

### 6. Commit de Offset

**Tipos de Commit:**

```java
// 1. Auto commit (padrão)
props.put("enable.auto.commit", "true");
props.put("auto.commit.interval.ms", "5000");

// 2. Manual commit síncrono
consumer.commitSync();

// 3. Manual commit assíncrono
consumer.commitAsync();
```

**Estratégias:**
- **Auto commit**: Simples mas pode perder mensagens
- **Manual sync**: Garante commit mas é mais lento
- **Manual async**: Rápido mas pode duplicar mensagens

### 7. ZooKeeper / KRaft

**Função do ZooKeeper (modo legado):**
- Eleição de controller
- Gerenciamento de cluster
- Armazenamento de metadados
- Coordenação de brokers

**KRaft (novo modo):**
- Substitui ZooKeeper
- Usa Raft protocol
- Mais simples e performático
- Disponível desde Kafka 3.3+

```
# Modo tradicional
ZooKeeper ← Brokers
    ↓
Metadados

# Modo KRaft
Controller Broker (Raft)
    ↓
Brokers
```

## Fluxo Completo

### Publicação de Mensagem

```
1. Producer → Serializa dados
2. Producer → Determina partição
3. Producer → Envia para broker leader
4. Broker Leader → Escreve em log local
5. Broker Leader → Replica para followers
6. Broker Leader → Responde ao producer
```

### Consumo de Mensagem

```
1. Consumer → Poll() do broker
2. Broker → Retorna batch de mensagens
3. Consumer → Deserializa dados
4. Consumer → Processa mensagens
5. Consumer → Commit offset
6. Broker → Persiste novo offset
```

## Garantias e Trade-offs

### Durabilidade vs Performance

**Alta Durabilidade:**
```properties
acks=all
min.insync.replicas=2
```
- ✅ Dados seguros
- ❌ Menor throughput

**Alta Performance:**
```properties
acks=1
compression.type=lz4
```
- ✅ Maior throughput
- ❌ Possível perda de dados

### Ordem vs Paralelismo

**Garantir Ordem:**
- Use 1 partição
- Ou use mesma chave para mensagens relacionadas

**Maximizar Paralelismo:**
- Use múltiplas partições
- Aceite que ordem só é garantida por partição

## Monitoramento

**Métricas Importantes:**
- Throughput (mensagens/segundo)
- Latência (p99, p95)
- Under-replicated partitions
- Consumer lag
- Disk usage

**Ferramentas:**
- JMX metrics
- Kafka Manager
- Prometheus + Grafana
- Confluent Control Center

## Padrões de Arquitetura

### Event Sourcing
```
Eventos → Kafka → Consumers → Estado
```

### CQRS (Command Query Responsibility Segregation)
```
Commands → Kafka → Write Model
                 ↓
               Read Model
```

### Log Aggregation
```
Apps → Logs → Kafka → Analytics/Storage
```

## Próximos Passos

Explore os exemplos práticos em `/examples` para ver o Kafka em ação!
