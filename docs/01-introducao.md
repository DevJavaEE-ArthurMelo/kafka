# Introdução ao Apache Kafka

## O que é Apache Kafka?

Apache Kafka é uma plataforma distribuída de streaming de eventos open-source, originalmente desenvolvida pelo LinkedIn e posteriormente doada à Apache Software Foundation.

## Para que serve?

Kafka é usado para:
- **Mensageria**: Sistema de mensagens pub/sub de alto desempenho
- **Stream Processing**: Processamento de dados em tempo real
- **Integração de Sistemas**: Conectar diferentes aplicações e serviços
- **Armazenamento de Logs**: Armazenar e processar logs de aplicações
- **Event Sourcing**: Implementar arquiteturas baseadas em eventos

## Principais Características

### 1. Alto Throughput
- Capaz de processar milhões de mensagens por segundo
- Latência extremamente baixa (milissegundos)

### 2. Escalabilidade
- Escala horizontalmente através de partições
- Suporta clusters com múltiplos brokers

### 3. Durabilidade
- Persistência de mensagens em disco
- Replicação de dados entre brokers
- Tolerância a falhas

### 4. Distribuído
- Arquitetura distribuída por natureza
- Alta disponibilidade

## Casos de Uso Comuns

1. **Sistemas de Mensageria**: Substituição de message brokers tradicionais (RabbitMQ, ActiveMQ)
2. **Analytics em Tempo Real**: Processamento de métricas e KPIs em tempo real
3. **Log Aggregation**: Centralização de logs de múltiplos serviços
4. **Event Sourcing**: Manter histórico completo de mudanças de estado
5. **Microserviços**: Comunicação assíncrona entre serviços

## Comparação com Outras Tecnologias

### Kafka vs RabbitMQ
- **Kafka**: Foco em alto throughput e persistência
- **RabbitMQ**: Foco em roteamento complexo e garantias de entrega

### Kafka vs Database
- **Kafka**: Projetado para streaming e eventos temporais
- **Database**: Projetado para consultas complexas e estado atual

## Próximos Passos

Continue para [02-conceitos-basicos.md](./02-conceitos-basicos.md) para entender os conceitos fundamentais do Kafka.
