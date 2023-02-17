package azure.blobstorage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;

/**
 * This class connects to a Az-Storage account
 * - Creates a cloud blob container.
 * - Lists files present in a container
 * - Upload files in a cloud container.
 */
public class BlobStorageContainerClient {
    private final String blobConnectionString = "<AZ_StorageAccount_Connection_String>";

    /**
     * Main method to test..
     * @param a
     */
    public static void main(String[] a){
        BlobStorageContainerClient blobStorageContainerClient = new BlobStorageContainerClient();
        blobStorageContainerClient.connectAzureBlobStorage();
    }

    /**
     * Method to Upload Blobs in cloud.
     */
    private void connectAzureBlobStorage(){
        // Create a BlobServiceClient object using a connection string
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(blobConnectionString)
                .buildClient();

        BlobContainerClient blobContainerClient = blobServiceClient
                                                    .getBlobContainerClient("firstcontainer");

        // Create a unique name for the container
        //String containerName = "demoBlobContainers" + java.util.UUID.randomUUID();
        // Create the container and return a container client object
        //BlobContainerClient blobContainerClient = blobServiceClient.createBlobContainer(containerName);

        System.out.println("\nListing blobs...");

        // List the blob(s) in the container.
        for (BlobItem blobItem : blobContainerClient.listBlobs()) {
            System.out.println("\t" + blobItem.getName());
        }

        // Upload the blob
        String fileName = "/Users/ctsuser1/Desktop/Hiring_Manager_Certificate_02062023.pdf";
        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        System.out.println("\nUploading to Blob storage as blob:\n\t" + blobClient.getBlobUrl());
        blobClient.uploadFromFile(fileName);
        System.out.println("Uploaded file into Container successfully..");
    }
}