package com.bubble.bnlp.data.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MongoDB Reactive Streams 响应式流单例类
 *
 * @author wugang
 * date: 2018-11-13 09:55
 **/
public class MongoFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoFactory.class);
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoFactory() {
        LOGGER.info("MongoFactory start create ...");
    }

    public static MongoFactory getInstance() {
        return MongoFactoryHolder.instance;
    }

    private static class MongoFactoryHolder {
        private static MongoFactory instance = new MongoFactory();

        static {
            initClient();
        }

        private static void initClient() {
//            List<ServerAddress> serverAddressList = Lists.newArrayListWithCapacity(3);
//            serverAddressList.add(new ServerAddress("localhost", 27017));
//            ClusterSettings clusterSettings = ClusterSettings.builder()
//                    .hosts(serverAddressList).build();
            MongoCredential credential = MongoCredential.createCredential("search", "flight", "xxx".toCharArray());

            MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                    .credential(credential).build();
            MongoClient mongoClient = MongoClients.create(mongoClientSettings);
            instance.setMongoClient(mongoClient);
            instance.setMongoDatabase(mongoClient.getDatabase("flight"));
        }

    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    public void setMongoDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }
}
