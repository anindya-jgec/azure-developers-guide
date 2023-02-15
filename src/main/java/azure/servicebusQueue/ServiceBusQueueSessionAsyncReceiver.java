package azure.servicebusQueue;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusException;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

import java.util.concurrent.TimeUnit;

/**
 * https://learn.microsoft.com/en-us/samples/azure/azure-sdk-for-java/servicebus-samples/?source=recommendations
 * https://github.com/Azure/azure-sdk-for-java/blob/main/sdk/servicebus/azure-messaging-servicebus/src/samples/java/com/azure/messaging/servicebus
 *
 * This class receives messages from ASB - Queue
 * - Maintain session
 * - Async mode
 */
public class ServiceBusQueueSessionAsyncReceiver {
    String connectionString = "Endpoint=sb://apaz204.servicebus.windows.net/;SharedAccessKeyName=receiver-policy;SharedAccessKey=k11nDvINbimL6m9cpEKGOhrtjqjWtTc5x+ASbEFEx1g=";
    String queueName = "apaz204-sb-queue";

    /**
     * Main method to start the sample application.
     * @param args Ignored args.
     * @throws InterruptedException If the application is interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        ServiceBusQueueSessionAsyncReceiver sample = new ServiceBusQueueSessionAsyncReceiver();
        sample.run();
    }

    public void run() throws InterruptedException {
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sessionProcessor()
                .queueName(queueName)
                .maxConcurrentSessions(2)
                .processMessage(ServiceBusQueueSessionAsyncReceiver::processMessage)
                .processError(ServiceBusQueueSessionAsyncReceiver::processError)
                .buildProcessorClient();

        System.out.println("Starting the processor");
        processorClient.start();

        TimeUnit.SECONDS.sleep(10);
        //System.out.println("Stopping the processor");
        //processorClient.stop();

        //TimeUnit.SECONDS.sleep(10);
        //System.out.println("Resuming the processor");
        //processorClient.start();

        //TimeUnit.SECONDS.sleep(10);
        System.out.println("Closing the processor");
        processorClient.close();

        System.out.println("Connection closed and execution complete..");
    }

    /**
     * Processes each message from the Service Bus entity.
     *
     * @param context Received message context.
     */
    private static void processMessage(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage message = context.getMessage();
        System.out.printf("Processing message. Session: %s, Sequence #: %s. Contents: %s%n", message.getMessageId(),
                message.getSequenceNumber(), message.getBody());

        // When this message function completes, the message is automatically completed.
        // If an exception is thrown in here, the message is abandoned.
        // To disable this behaviour, toggle ServiceBusSessionProcessorClientBuilder.disableAutoComplete()
        // when building the session receiver.
    }

    /**
     * Processes an exception that occurred in the Service Bus Processor.
     *
     * @param context Context around the exception that occurred.
     */
    private static void processError(ServiceBusErrorContext context) {
        System.out.printf("Error when receiving messages from namespace: '%s'. Entity: '%s'%n",
                context.getFullyQualifiedNamespace(), context.getEntityPath());

        if (!(context.getException() instanceof ServiceBusException)) {
            System.out.printf("Non-ServiceBusException occurred: %s%n", context.getException());
            return;
        }

        ServiceBusException exception = (ServiceBusException) context.getException();
        System.out.printf("ServiceBusException source: %s. Reason: %s. Is transient? %s%n", context.getErrorSource(),
                exception.getReason(), exception.isTransient());
    }
}