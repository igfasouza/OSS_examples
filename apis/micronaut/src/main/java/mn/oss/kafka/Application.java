package mn.oss.kafka;

import java.util.UUID;

import io.micronaut.context.ApplicationContext;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;

public class Application {

    Producer producer;

    public Application(Producer producer) {
        this.producer = producer;
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = Micronaut.run(Application.class);
    }

    @EventListener
    @Async
    public void onStartup(StartupEvent event) {
        for(int i=0; i<10; i++) {
            String key = UUID.randomUUID().toString();
            String val = "Message " + i + " from Micronaut!";
            producer.sendMessage(key, val);
            System.out.println("Sent message #" + i + " to topic.");
        }
        System.out.println("All messages sent to consumer");

    }
}