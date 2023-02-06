package azure.cosmosdbconnect;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DemoCosmosDBClient {
    public static final String cosmosDbConnectionString = "https://az204-cosmosdb-demo.documents.azure.com:443/";
    public static final String cosmosDbDatabaseKey = "bM6A50dkfCKEUeqsAXJvqQKuG3jHgqY5CxHgPyYmzcNsZ4exDvkVnDnmOe7KQwDxTzPsuRb347UUACDbaqTZNA==";
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static final String _CosmosDbName = "EmployeeManagement";
    public static final String _CosmosDbContainerName = "EmployeeDetails";

    public static void main(String[] a) throws Exception{
        DemoCosmosDBClient _DemoCosmosDBClient = new DemoCosmosDBClient();

        _DemoCosmosDBClient.createCosmosDB();
        _DemoCosmosDBClient.createCosmosDBContainer();
        _DemoCosmosDBClient.insertDataInCosmosContainer();
        //_CosmosDemoAsyncClient.close();
    }

    public void  createCosmosDB() {
        DemoCosmosDBClient _DemoCosmosDBClient = new DemoCosmosDBClient();
        CosmosAsyncClient _CosmosDemoAsyncClient = _DemoCosmosDBClient.getCosmosAsyncClient();
        _CosmosDemoAsyncClient.createDatabaseIfNotExists(_CosmosDbName)
        // TIP: Our APIs are Reactor Core based, so try to chain your calls
        .map(databaseResponse ->
                _CosmosDemoAsyncClient.getDatabase(databaseResponse.getProperties().getId()))
        .subscribe(database -> System.out.printf("Created database '%s'.%n", database.getId()));
    }

    public void createCosmosDBContainer(){
        DemoCosmosDBClient _DemoCosmosDBClient = new DemoCosmosDBClient();
        CosmosAsyncClient _CosmosDemoAsyncClient = _DemoCosmosDBClient.getCosmosAsyncClient();
        _CosmosDemoAsyncClient.createDatabaseIfNotExists(_CosmosDbName)
            // TIP: Our APIs are Reactor Core based, so try to chain your calls
            .flatMap(databaseResponse -> {
                String databaseId = databaseResponse.getProperties().getId();
                return _CosmosDemoAsyncClient.getDatabase(databaseId)
                        // Create Container
                        .createContainerIfNotExists(_CosmosDbContainerName, "/department")
                        .map(containerResponse -> _CosmosDemoAsyncClient.getDatabase(databaseId)
                                .getContainer(containerResponse.getProperties().getId()));
            })
            .subscribe(container -> System.out.printf("Created container '%s' in database '%s'.%n",
                    container.getId(), container.getDatabase().getId()));
    }

    public void insertDataInCosmosContainer(){
        DemoCosmosDBClient _DemoCosmosDBClient = new DemoCosmosDBClient();
        CosmosAsyncClient _CosmosDemoAsyncClient = _DemoCosmosDBClient.getCosmosAsyncClient();
        for(int counter = 0; counter<10; counter++){
            EmployeeBean _EmployeeBean = new EmployeeBean();
            _EmployeeBean.setEmployeeName("EmployeeName"+counter);
            _EmployeeBean.setEmployeeDepartment("IT");
            _EmployeeBean.setEmployeeAddress("Address"+counter);
            _EmployeeBean.setEmployeeZip("71210"+counter);
            _CosmosDemoAsyncClient.getDatabase(_CosmosDbName)
                    .getContainer(_CosmosDbContainerName)
                    .createItem(_EmployeeBean);
        }
    }

    public CosmosAsyncClient getCosmosAsyncClient(){
        // Create a new CosmosAsyncClient via the CosmosClientBuilder
        // It only requires endpoint and key, but other useful settings are available
        return new CosmosClientBuilder()
                .endpoint(cosmosDbConnectionString)
                .key(cosmosDbDatabaseKey)
                .buildAsyncClient();
    }

    public CosmosClient getCosmosClient(){
        // Create a new CosmosClient via the CosmosClientBuilder
        return new CosmosClientBuilder()
                .endpoint(cosmosDbConnectionString)
                .key(cosmosDbDatabaseKey)
                .buildClient();
    }

    public String getConsoleInput(){
        String inputString = "";
        try {
            inputString = reader.readLine();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        return inputString;
    }
}