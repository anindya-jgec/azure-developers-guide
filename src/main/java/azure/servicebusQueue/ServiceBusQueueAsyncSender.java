package azure.servicebusQueue;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusMessageBatch;
import com.azure.messaging.servicebus.ServiceBusSenderAsyncClient;
import com.azure.messaging.servicebus.ServiceBusSenderClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

/**
 * https://learn.microsoft.com/en-us/java/api/overview/azure/service-bus?view=azure-java-stable
 * https://learn.microsoft.com/en-us/samples/azure/azure-sdk-for-java/servicebus-samples/?source=recommendations
 *
 * https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/servicebus/azure-messaging-servicebus/src/samples/java/com/azure/messaging
 *
 * This class connects and sends message to ASB - Queue.
 */
public class ServiceBusQueueAsyncSender {
    String connectionString = "Endpoint=sb://apaz204.servicebus.windows.net/;SharedAccessKeyName=sender-policy;SharedAccessKey=lkhDuSLLdFOoZPlInTWnP/gC1puFD07p0+ASbK7WxRk=";
    String queueName = "apaz204-sb-queue";

    public static void main(String[] a){
        ServiceBusQueueAsyncSender serviceBusQueueAsyncSender = new ServiceBusQueueAsyncSender();
        try{
            serviceBusQueueAsyncSender.sendMessageAsync();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    private void sendMessageAsync() throws InterruptedException{
        AtomicBoolean sampleSuccessful = new AtomicBoolean(false);
        CountDownLatch countdownLatch = new CountDownLatch(1);

        ServiceBusSenderClient sender = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .queueName(queueName)
                .buildClient();

        // Create a message to send.
        final ServiceBusMessageBatch messageBatch = sender.createMessageBatch();

        IntStream.range(0, 10)
                .mapToObj(index -> new ServiceBusMessage(BinaryData.fromString("Message Id - " + index)))
                .forEach(message -> messageBatch.tryAddMessage(message));

        // Send that message. It completes successfully when the event has been delivered to the Service queue or topic.
        // It completes with an error if an exception occurred while sending the message.
        sender.sendMessages(messageBatch);

        System.out.println("All messages sent successfully...");

        // Close the sender.
        sender.close();

        System.out.println("Connection closed and execution complete..");
    }
}