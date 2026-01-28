# Conceitos Básicos do Kafka

## Arquitetura Geral

O Kafka é baseado em alguns componentes principais que trabalham juntos:

```
Producer → Kafka Broker → Consumer
              ↓
           ZooKeeper
```

## Componentes Principais

### 1. Producer (Produtor)
- Aplicação que **publica** (envia) mensagens para tópicos Kafka
- Decide em qual partição a mensagem será escrita
- Pode usar chaves para garantir ordem de mensagens

**Exemplo conceitual:**
```
Producer → escolhe tópico → envia mensagem → Kafka armazena
```

### 2. Consumer (Consumidor)
- Aplicação que **consome** (lê) mensagens de tópicos Kafka
- Pode fazer parte de um grupo de consumidores (Consumer Group)
- Mantém controle do offset (posição) de leitura

**Exemplo conceitual:**
```
Kafka → Consumer lê mensagens → processa dados
```

### 3. Broker
- Servidor Kafka que armazena e gerencia mensagens
- Um cluster Kafka pode ter múltiplos brokers
- Responsável por replicação e distribuição de dados

### 4. Topic (Tópico)
- **Categoria** ou **feed** de mensagens
- Similar a uma tabela em banco de dados ou pasta
- Mensagens são organizadas por tópicos

**Exemplo:**
- Tópico "pedidos": mensagens sobre pedidos de clientes
- Tópico "usuarios": mensagens sobre cadastro de usuários

### 5. Partition (Partição)
- Cada tópico é dividido em uma ou mais partições
- Permite paralelismo e escalabilidade
- Mensagens dentro de uma partição são ordenadas

```
Tópico: pedidos
├── Partição 0: [msg1, msg2, msg3]
├── Partição 1: [msg4, msg5, msg6]
└── Partição 2: [msg7, msg8, msg9]
```

### 6. Offset
- Identificador único e sequencial de cada mensagem dentro de uma partição
- Usado pelo consumer para rastrear quais mensagens já foram lidas
- É persistente e nunca muda para uma mensagem

```
Partição 0: [0:msg1, 1:msg2, 2:msg3, 3:msg4]
               ↑      ↑      ↑      ↑
            offsets
```

### 7. Consumer Group
- Grupo de consumidores que trabalham juntos para consumir um tópico
- Cada partição é consumida por apenas um consumidor do grupo
- Permite escalabilidade horizontal

```
Consumer Group "app-processors"
├── Consumer 1 → lê Partição 0
├── Consumer 2 → lê Partição 1
└── Consumer 3 → lê Partição 2
```

### 8. ZooKeeper (ou KRaft)
- Gerencia e coordena o cluster Kafka
- Mantém metadados sobre brokers, tópicos e partições
- **Nota**: Versões mais recentes usam KRaft ao invés de ZooKeeper

## Fluxo de Dados

### Publicação (Producer → Kafka)
1. Producer cria uma mensagem
2. Escolhe um tópico
3. Opcionalmente define uma chave
4. Kafka determina a partição (baseado na chave ou round-robin)
5. Mensagem é escrita na partição

### Consumo (Kafka → Consumer)
1. Consumer se inscreve em um ou mais tópicos
2. Kafka atribui partições ao consumer
3. Consumer lê mensagens a partir do offset
4. Após processar, consumer commita o novo offset

## Garantias do Kafka

1. **Ordem**: Mensagens em uma partição são estritamente ordenadas
2. **Durabilidade**: Mensagens são persistidas em disco
3. **Replicação**: Dados são replicados entre brokers
4. **At-least-once**: Por padrão, cada mensagem é processada pelo menos uma vez

## Conceitos Importantes

### Retenção
- Kafka retém mensagens por um período configurável
- Padrão: 7 dias
- Mensagens podem ser relidas dentro do período de retenção

### Replicação
- Cada partição pode ter múltiplas réplicas
- Uma réplica é o **leader**, outras são **followers**
- Se o leader falha, um follower se torna o novo leader

### Compactação
- Log compaction mantém apenas a mensagem mais recente de cada chave
- Útil para manter estado atualizado

## Próximos Passos

Continue para [03-instalacao-setup.md](./03-instalacao-setup.md) para aprender como instalar e configurar o Kafka.
