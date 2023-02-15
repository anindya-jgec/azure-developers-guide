package azure.redisdbconnect;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;

/**
 * This connect to Azure Cloud Redis DB using Jedis Client.
 * More related documents on : https://learn.microsoft.com/en-us/azure/azure-cache-for-redis/cache-java-get-started
 */
public class DemoRedisClient {

    private static final String REDISCACHEHOSTNAME = "<REDIS_HOST>";
    private static final String REDISCACHEKEY = "<REDIS_KEY>";
    public static void main(String[] a){
        boolean useSsl = true;

        // Connect to the Azure Cache for Redis over the TLS/SSL port using the key.
        Jedis jedis = new Jedis(REDISCACHEHOSTNAME, 6380, DefaultJedisClientConfig.builder()
                .password(REDISCACHEKEY)
                .ssl(useSsl)
                .build());

        // Perform cache operations using the cache connection object...
        System.out.println( "\nCache Command  : Ping" );
        System.out.println( "Cache Response : " + jedis.ping());

        // Simple get and put of integral data types into the cache
        System.out.println( "\nCache Command  : GET Message" );
        System.out.println( "Cache Response : " + jedis.get("Message"));

        System.out.println( "\nCache Command  : SET Message" );
        System.out.println( "Cache Response : " + jedis.set("Message", "Hello! The cache is working from Java!"));

        // Demonstrate "SET Message" executed as expected...
        System.out.println( "\nCache Command  : GET Message" );
        System.out.println( "Cache Response : " + jedis.get("Message"));

        // Get the client list, useful to see if connection list is growing...
        System.out.println( "\nCache Command  : CLIENT LIST" );
        System.out.println( "Cache Response : " + jedis.clientList());

        jedis.close();
    }
}