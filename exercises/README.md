# Exercícios Práticos - Apache Kafka

Este diretório contém exercícios práticos para consolidar o aprendizado sobre Kafka.

## Como Usar

1. Leia o enunciado do exercício
2. Tente resolver sozinho primeiro
3. Consulte as dicas se precisar
4. Compare com a solução sugerida
5. Execute e teste seu código

## Pré-requisitos

- Kafka instalado e rodando (veja `docs/03-instalacao-setup.md`)
- Java 8+ ou Python 3.7+
- Conhecimento básico de programação

---

## Exercício 1: Hello Kafka

**Nível:** Iniciante

**Objetivo:** Criar seu primeiro producer e consumer

**Tarefa:**
1. Crie um producer que envia 5 mensagens para um tópico chamado "hello-kafka"
2. Crie um consumer que lê essas mensagens e as imprime no console
3. As mensagens devem ter o formato: "Mensagem número X"

**Dicas:**
- Use `SimpleProducer.java` como referência
- Lembre-se de criar o tópico antes: `kafka-topics.sh --create --topic hello-kafka --partitions 1 --replication-factor 1`
- Configure `auto.offset.reset=earliest` no consumer

**O que você vai aprender:**
- Configuração básica de producer e consumer
- Envio e recebimento de mensagens
- Conceito de tópico

---

## Exercício 2: Particionamento

**Nível:** Intermediário

**Objetivo:** Entender como funcionam partições

**Tarefa:**
1. Crie um tópico com 3 partições: "users-topic"
2. Implemente um producer que envia mensagens com chaves (user_id)
3. Envie 30 mensagens com user_ids de 1 a 10 (3 mensagens por usuário)
4. Crie um consumer e observe em quais partições as mensagens caíram
5. Verifique se mensagens do mesmo user_id foram para a mesma partição

**Dicas:**
- Use `ProducerRecord` com chave: `new ProducerRecord<>(topic, key, value)`
- Para ver as partições: `kafka-topics.sh --describe --topic users-topic`
- No consumer, imprima `record.partition()` junto com a chave

**O que você vai aprender:**
- Como Kafka particiona mensagens
- Importância das chaves para ordem
- Hash partitioning

---

## Exercício 3: Consumer Groups

**Nível:** Intermediário

**Objetivo:** Trabalhar com múltiplos consumers

**Tarefa:**
1. Crie um tópico "events" com 4 partições
2. Implemente um producer que envia 100 mensagens
3. Crie 2 consumers no mesmo grupo e observe a distribuição
4. Adicione um terceiro consumer e veja o rebalancing
5. Pare um consumer e observe a redistribuição

**Dicas:**
- Use o mesmo `group.id` para todos consumers do grupo
- Execute cada consumer em um terminal diferente
- Use `kafka-consumer-groups.sh --describe` para ver a distribuição

**O que você vai aprender:**
- Consumer groups
- Rebalancing
- Paralelização de consumo
- Alta disponibilidade

---

## Exercício 4: Commit Manual de Offset

**Nível:** Intermediário

**Objetivo:** Controlar quando offsets são commitados

**Tarefa:**
1. Crie um consumer com commit manual desabilitado
2. Processe mensagens e faça commit apenas após processamento bem-sucedido
3. Simule uma falha de processamento e veja que a mensagem não é perdida
4. Implemente retry logic para mensagens que falharam

**Dicas:**
- Configure `enable.auto.commit=false`
- Use `consumer.commitSync()` após processar com sucesso
- Para simular falha, lance exceção para mensagens específicas

**O que você vai aprender:**
- Controle de offset
- Garantias de processamento
- At-least-once semantics
- Retry logic

---

## Exercício 5: Producer com Callback

**Nível:** Intermediário

**Objetivo:** Usar callbacks para produção assíncrona

**Tarefa:**
1. Implemente um producer que usa callbacks
2. Envie 1000 mensagens rapidamente
3. No callback, conte sucessos e falhas
4. Implemente retry para mensagens que falharam
5. Compare o tempo com envio síncrono

**Dicas:**
- Use `producer.send(record, callback)`
- Callback recebe `RecordMetadata` e `Exception`
- Use `AtomicInteger` para contar sucessos/falhas (thread-safe)

**O que você vai aprender:**
- Produção assíncrona
- Callbacks
- Performance tuning
- Error handling

---

## Exercício 6: Dead Letter Queue

**Nível:** Avançado

**Objetivo:** Implementar pattern de DLQ

**Tarefa:**
1. Crie um consumer que processa mensagens
2. Se processamento falhar após 3 tentativas, envie para DLQ
3. Implemente um consumer separado para o DLQ
4. Grave logs das mensagens que foram para DLQ

**Dicas:**
- Use um tópico separado como DLQ (ex: "main-topic-dlq")
- Adicione headers com informações de erro
- Considere usar `ProducerRecord` com headers

**O que você vai aprender:**
- Error handling avançado
- Pattern DLQ
- Resiliência
- Observabilidade

---

## Exercício 7: Serialização Customizada

**Nível:** Avançado

**Objetivo:** Trabalhar com objetos Java complexos

**Tarefa:**
1. Crie uma classe `Order` com campos: id, userId, amount, items
2. Implemente um custom serializer/deserializer para Order
3. Crie producer e consumer que trabalham com objetos Order
4. Considere usar JSON ou Avro

**Dicas:**
- Implemente `Serializer<Order>` e `Deserializer<Order>`
- Ou use bibliotecas: Jackson para JSON, Confluent Schema Registry para Avro
- Configure `value.serializer` e `value.deserializer` com suas classes

**O que você vai aprender:**
- Serialização customizada
- Schema evolution
- Integração com JSON/Avro
- Type safety

---

## Exercício 8: Monitoramento de Consumer Lag

**Nível:** Avançado

**Objetivo:** Monitorar e gerenciar lag

**Tarefa:**
1. Crie um producer que envia mensagens continuamente
2. Crie um consumer "lento" que demora para processar
3. Use ferramentas para monitorar o lag
4. Implemente alertas quando lag ultrapassar threshold
5. Adicione mais consumers para reduzir lag

**Dicas:**
- Use `kafka-consumer-groups.sh --describe` para ver lag
- Ou use JMX metrics
- Simule processamento lento com `Thread.sleep()`

**O que você vai aprender:**
- Consumer lag
- Monitoramento
- Escalabilidade
- Operações

---

## Exercício Bônus: Mini Projeto

**Nível:** Avançado

**Objetivo:** Integrar tudo que aprendeu

**Tarefa:**
Implemente um sistema de processamento de pedidos:

1. **Order Producer Service:**
   - Recebe pedidos via REST API
   - Publica no tópico "orders"
   - Usa chave = user_id

2. **Payment Processor Service:**
   - Consome de "orders"
   - Processa pagamento (simule)
   - Publica resultado em "payment-results"
   - Usa DLQ para falhas

3. **Notification Service:**
   - Consome de "payment-results"
   - Envia notificação (simule)
   - Usa consumer group para escalabilidade

4. **Monitoring Dashboard:**
   - Monitora lag de todos consumers
   - Conta mensagens processadas
   - Rastreia erros

**O que você vai aprender:**
- Arquitetura event-driven
- Microserviços
- Integração de sistemas
- Práticas do mundo real

---

## Recursos Adicionais

- Consulte `/examples` para código de referência
- Leia `/docs` para conceitos teóricos
- Use `/resources/links-uteis.md` para materiais extras

## Dúvidas?

- Revise a documentação em `/docs`
- Consulte os exemplos em `/examples`
- Pratique, pratique, pratique!

Boa sorte! 🚀
