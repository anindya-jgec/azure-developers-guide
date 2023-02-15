package azure.servicebusQueue;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderAsyncClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * https://learn.microsoft.com/en-us/samples/azure/azure-sdk-for-java/servicebus-samples/?source=recommendations
 * https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/servicebus/azure-messaging-servicebus/src/samples/java/com/azure/messaging/servicebus
 *
 * This class sends messages to ASB - Queue
 * - Maintain session
 * - Async mode
 */
public class ServiceBusQueueSessionAsyncSender {
    String connectionString = "Endpoint=sb://apaz204.servicebus.windows.net/;SharedAccessKeyName=sender-policy;SharedAccessKey=laUcIgIXsAcDGCcLBN14/y6fW8LFvJFZP+ASbNzMdOg=";
    String queueName = "apaz204-sb-queue";

    /**
     * Main method to invoke this demo on how to send and receive a {@link ServiceBusMessage} to and from a
     * session-enabled Azure Service Bus queue.
     *
     * @param args Unused arguments to the program.
     *
     * @throws InterruptedException If the program is unable to sleep while waiting for the operations to complete.
     */
    public static void main(String[] args) throws InterruptedException {
        ServiceBusQueueSessionAsyncSender sample = new ServiceBusQueueSessionAsyncSender();
        sample.run();
    }

    public void run() throws InterruptedException {
        AtomicBoolean sampleSuccessful = new AtomicBoolean(false);
        CountDownLatch countdownLatch = new CountDownLatch(1);

        String sessionId = "greetings-id";

        // Any clients built from the same ServiceBusClientBuilder share the same connection.
        ServiceBusClientBuilder builder = new ServiceBusClientBuilder()
                .connectionString(connectionString);

        // Instantiate a client that will be used to send messages.
        ServiceBusSenderAsyncClient sender = builder
                .sender()
                .queueName(queueName)
                .buildAsyncClient();

        // Setting the sessionId parameter ensures all messages end up
        // in the same session and are received in order.
        List<ServiceBusMessage> messages = Arrays.asList(
                new ServiceBusMessage(BinaryData.fromBytes("Hello".getBytes(UTF_8))).
                                    setSessionId(sessionId),
                new ServiceBusMessage(BinaryData.fromBytes("Bonjour".getBytes(UTF_8)))
                                    .setSessionId(sessionId)
        );

        // This sends all the messages in a single message batch.
        // This call returns a Mono<Void>, which we subscribe to.
        // It completes successfully when the event has been delivered
        // to the Service queue or topic. It completes with an error
        // if an exception occurred while sending the message.
        sender.sendMessages(messages).subscribe(unused -> System.out.println("Batch sent."),
                error -> System.err.println("Error occurred while publishing message batch: " + error),
                () -> {
                    System.out.println("Batch send complete.");
                    sampleSuccessful.set(true);
                });

        // subscribe() is not a blocking call. We wait here so the program does not end before the send is complete.
        countdownLatch.await(10, TimeUnit.SECONDS);

        sender.close();

        System.out.println("Connection closed and execution complete..");
    }
}