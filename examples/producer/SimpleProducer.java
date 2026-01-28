import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * SimpleProducer - Exemplo básico de Producer Kafka
 * 
 * Este exemplo demonstra:
 * - Configuração básica de um producer
 * - Envio síncrono de mensagens
 * - Tratamento de exceções
 * - Fechamento adequado de recursos
 */
public class SimpleProducer {
    
    public static void main(String[] args) {
        // Configuração do Producer
        Properties props = new Properties();
        
        // Endereço dos brokers Kafka
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // Serializers para chave e valor
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // Configuração de acknowledgment (all = aguarda todas réplicas)
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        
        // Número de retentativas em caso de falha (3 retries = 4 tentativas totais)
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        
        // Try-with-resources garante fechamento do producer
        try (Producer<String, String> producer = new KafkaProducer<>(props)) {
            
            String topic = "test-topic";
            
            // Enviar 10 mensagens
            for (int i = 0; i < 10; i++) {
                String key = "key-" + i;
                String value = "Mensagem número " + i;
                
                // Criar o registro (record) para envio
                ProducerRecord<String, String> record = 
                    new ProducerRecord<>(topic, key, value);
                
                try {
                    // Envio síncrono - aguarda confirmação
                    var metadata = producer.send(record).get();
                    
                    System.out.printf(
                        "Mensagem enviada com sucesso: " +
                        "tópico=%s, partição=%d, offset=%d, timestamp=%d%n",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp()
                    );
                    
                } catch (Exception e) {
                    System.err.println("Erro ao enviar mensagem: " + e.getMessage());
                    e.printStackTrace();
                }
                
                // Pequeno delay entre mensagens (apenas para exemplo)
                Thread.sleep(100);
            }
            
            // Flush garante que todas mensagens pendentes sejam enviadas
            producer.flush();
            System.out.println("\nTodas as mensagens foram enviadas!");
            
        } catch (Exception e) {
            System.err.println("Erro fatal no producer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
