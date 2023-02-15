package azure.servicebusTopic;

import com.azure.messaging.servicebus.*;

import java.util.ArrayList;
import java.util.List;

/**
 * https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-topics-subscriptions
 * https://learn.microsoft.com/en-us/azure/service-bus-messaging/service-bus-java-how-to-use-topics-subscriptions?source=recommendations
 *
 * https://github.com/Azure/azure-sdk-for-java/tree/main/sdk/servicebus/azure-messaging-servicebus/src/samples/java/com/azure/messaging
 *
 * This class connects ASB and sends message to a topic.
 * Subscribers read messages from topic.
 */
public class ServiceBusSender {
    static String connectionString = "<SAP-Sender-Policy-URL>";
    static String topicName = "apaz204-sb-topic";
    static String subName = "<SUBSCRIPTION NAME>";

    /**
     * Message to create SB Messages.
     * @return
     */
    private List<ServiceBusMessage> createMessages()
    {
        List<ServiceBusMessage> serviceBusMessages = new ArrayList<>();
        // create a list of messages and return it to the caller
        for(int count = 1; count<=10; count++) {

            ServiceBusMessage message = new ServiceBusMessage("Message No - "+count+" with random value -" + Math.random());
            serviceBusMessages.add(message);
        }
        return serviceBusMessages;
    }

    /**
     * Method to send messages as Batch
     * in ASB - Topic.
     */
    private void sendMessageBatch()
    {
        // create a Service Bus Sender client for the topic
        ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(topicName)
                .buildClient();

        // Creates an ServiceBusMessageBatch where the ServiceBus.
        ServiceBusMessageBatch messageBatch = senderClient.createMessageBatch();

        // create a list of messages
        List<ServiceBusMessage> listOfMessages = createMessages();

        // We try to add as many messages as a batch can fit based on the maximum size and send to Service Bus when
        // the batch can hold no more messages. Create a new batch for next set of messages and repeat until all
        // messages are sent.
        for (ServiceBusMessage message : listOfMessages) {
            if (messageBatch.tryAddMessage(message)) {
                continue;
            }

            // The batch is full, so we create a new batch and send the batch.
            senderClient.sendMessages(messageBatch);
            System.out.println("Sent a batch of messages to the topic: " + topicName);

            // create a new batch
            messageBatch = senderClient.createMessageBatch();

            // Add that message that we couldn't before.
            if (!messageBatch.tryAddMessage(message)) {
                System.err.printf("Message is too large for an empty batch. Skipping. Max size: %s.", messageBatch.getMaxSizeInBytes());
            }
        }

        if (messageBatch.getCount() > 0) {
            senderClient.sendMessages(messageBatch);
            System.out.println("Sent a batch of messages to the topic: " + topicName);
        }

        //close the client
        senderClient.close();

        System.out.println("Connection closed and execution complete..");
    }

    public static void main(String[] a){
        ServiceBusSender serviceBusSender = new ServiceBusSender();
        serviceBusSender.sendMessageBatch();
    }

    /**
     * Message to send single message
     * ASB - Topic
     */
    private void sendMessage()
    {
        // create a Service Bus Sender client for the queue
        ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(topicName)
                .buildClient();

        // send one message to the topic
        senderClient.sendMessage(new ServiceBusMessage("Hello, World!"));
        System.out.println("Sent a single message to the topic: " + topicName);
    }
}