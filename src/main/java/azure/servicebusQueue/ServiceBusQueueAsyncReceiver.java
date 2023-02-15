package azure.servicebusQueue;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusReceiverAsyncClient;
import com.azure.messaging.servicebus.ServiceBusReceiverClient;
import reactor.core.Disposable;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * https://learn.microsoft.com/en-us/java/api/overview/azure/service-bus?view=azure-java-stable
 * https://learn.microsoft.com/en-us/samples/azure/azure-sdk-for-java/servicebus-samples/?source=recommendations
 */
public class ServiceBusQueueAsyncReceiver {
    String connectionString = "Endpoint=sb://apaz204.servicebus.windows.net/;SharedAccessKeyName=receiver-policy;SharedAccessKey=mrmRs3+QxoeqGnFMyHq5w73LvUoq+UKvi+ASbGJb+34=";
    String queueName = "apaz204-sb-queue";

    /**
     * Main method to test readMessageAsync
     *
     * @param a
     */
    public static void main(String[] a){
        ServiceBusQueueAsyncReceiver serviceBusQueueAsyncReceiver = new ServiceBusQueueAsyncReceiver();
        try{
            serviceBusQueueAsyncReceiver.readMessageAsync();
        }catch (Exception ex){
            ex.getMessage();
        }
    }

    /**
     * Method to read message from Service Bus Queue.
     *
     * @throws InterruptedException
     */
    public void readMessageAsync() throws InterruptedException{
        AtomicBoolean sampleSuccessful = new AtomicBoolean(true);
        CountDownLatch countdownLatch = new CountDownLatch(1);

        ServiceBusReceiverAsyncClient receiver = new ServiceBusClientBuilder()
                                            .connectionString(connectionString)
                                            .receiver()
                                            .queueName(queueName)
                                            .buildAsyncClient();


        Disposable subscription = receiver.receiveMessages().subscribe(message -> {
            System.out.printf("Sequence # %s :: Content :: %s%n", message.getSequenceNumber(), message.getBody());
                },
                error -> {
                    System.err.println("Error occurred while receiving message: " + error);
                    sampleSuccessful.set(false);
                },
                () -> System.out.println("Receive complete...")
            );

        // Receiving messages from the queue for a duration of 20 seconds.
        // Subscribe is not a blocking call so we wait here so the program does not end.
        countdownLatch.await(20, TimeUnit.SECONDS);

        // Disposing of the subscription will cancel the receive() operation.
        subscription.dispose();

        // Close the receiver.
        receiver.close();

        System.out.println("Connection closed and execution complete..");
    }
}