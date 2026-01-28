import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * SimpleConsumer - Exemplo básico de Consumer Kafka
 * 
 * Este exemplo demonstra:
 * - Configuração básica de um consumer
 * - Subscribe em um tópico
 * - Leitura contínua de mensagens
 * - Processamento de registros
 * - Graceful shutdown
 */
public class SimpleConsumer {
    
    // Flag para controlar o loop de consumo
    private static volatile boolean running = true;
    
    public static void main(String[] args) {
        // Configuração do Consumer
        Properties props = new Properties();
        
        // Endereço dos brokers Kafka
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Identificador do grupo de consumidores
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "simple-consumer-group");
        
        // Deserializers para chave e valor
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        // Onde começar a ler quando não há offset commitado
        // earliest = do início, latest = apenas novas mensagens
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        
        // Auto commit de offsets (commit automático a cada 5 segundos)
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
        
        // Número máximo de registros retornados em um poll()
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "100");
        
        // Criar o consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        
        // Shutdown hook para graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nRecebido sinal de shutdown...");
            running = false;
            consumer.wakeup(); // Interrompe poll() se estiver bloqueado
        }));
        
        try {
            // Subscribe no tópico
            String topic = "test-topic";
            consumer.subscribe(Arrays.asList(topic));
            System.out.println("Consumer inscrito no tópico: " + topic);
            System.out.println("Aguardando mensagens...\n");
            
            // Loop principal de consumo
            while (running) {
                // Poll busca mensagens do Kafka
                // Duration define timeout máximo de espera
                ConsumerRecords<String, String> records = 
                    consumer.poll(Duration.ofMillis(1000));
                
                // Iterar sobre os registros recebidos
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf(
                        "Recebido: tópico=%s, partição=%d, offset=%d, " +
                        "timestamp=%d, chave=%s, valor=%s%n",
                        record.topic(),
                        record.partition(),
                        record.offset(),
                        record.timestamp(),
                        record.key(),
                        record.value()
                    );
                    
                    // Processar a mensagem
                    processMessage(record.key(), record.value());
                }
                
                // Se não recebeu mensagens, mostra indicador
                if (records.isEmpty()) {
                    System.out.print(".");
                }
            }
            
        } catch (org.apache.kafka.common.errors.WakeupException e) {
            // Esperado durante shutdown
            System.out.println("Consumer foi interrompido");
            
        } catch (Exception e) {
            System.err.println("Erro no consumer: " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            // Sempre fechar o consumer para liberar recursos
            System.out.println("Fechando consumer...");
            consumer.close();
            System.out.println("Consumer fechado com sucesso");
        }
    }
    
    /**
     * Processa uma mensagem recebida
     * Aqui você implementaria a lógica de negócio
     */
    private static void processMessage(String key, String value) {
        // Simulação de processamento
        try {
            // Sua lógica de negócio aqui
            // Por exemplo: salvar em banco, chamar API, etc.
            
            // Simulando algum processamento
            Thread.sleep(50);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Processamento interrompido");
        } catch (Exception e) {
            System.err.println("Erro ao processar mensagem: " + e.getMessage());
            // Aqui você pode implementar retry logic ou dead letter queue
        }
    }
}
