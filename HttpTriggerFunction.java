package org.example.functions;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class HttpTriggerFunction {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("HttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
//        final String query = request.getQueryParameters().get("name");
//        final String name = request.getBody().orElse(query);

        boolean useSsl = true;
        String cacheHostname = "fei.redis.cache.windows.net"; //System.getenv("REDISCACHEHOSTNAME");
        String cachekey = "p99jDuQOYnasxCuiE0oQ1MRbXTWfxzgqVAzCaMNqGcI="; //System.getenv("REDISCACHEKEY");

        // Connect to the Azure Cache for Redis over the TLS/SSL port using the key.
        Jedis jedis = new Jedis(cacheHostname, 6380, DefaultJedisClientConfig.builder()
                .password(cachekey)
                .ssl(useSsl)
                .build());

        // Perform cache operations using the cache connection object...

        // Simple PING command
//        System.out.println( "\nCache Command  : Ping" );
//        System.out.println( "Cache Response : " + jedis.ping());

//        jedis.set("P101", "{\"id\":101,\"firstname\":\"fei\",\"lastname\":\"wihardjo\"}");

        String jsonString = jedis.get("P101");
        // 10 gets
        long start10 = System.currentTimeMillis();
        int times;
        for (times = 1; times <= 10; times++) {
             jsonString = jedis.get("P101");
        }
        long end10 = System.currentTimeMillis();

        // 100 gets
        long start100 = System.currentTimeMillis();
        for (; times <= 100; times++) {
            jedis.get("P101");
        }
        long end100 = System.currentTimeMillis();

        // 1000 gets
        long start1000 = System.currentTimeMillis();
        for (times = 1; times <= 1000; times++) {
            jedis.get("P101");
        }
        long end1000 = System.currentTimeMillis();

//        if (name == null) {
//            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
//        } else {
            return request.createResponseBuilder(HttpStatus.OK)
                    .body(jsonString + "\n"
                            + "10 gets, total time = " + (end10-start10) + " ms\n"
                            + "100 gets, total time = " + (end100-start100) + " ms\n"
                            + "1000 gets, total time = " + (end1000-start1000) + " ms")
                    .build();
//        }
    }
}
