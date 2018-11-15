package com.bubble.bnlp.data.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.mongodb.reactivestreams.client.Success;
import org.bson.Document;
import org.junit.Test;

/**
 * 参考：https://github.com/mongodb/mongo-java-driver-reactivestreams/blob/master/examples/tour/src/main/tour/QuickTourAdmin.java
 *
 * @author wugang
 * date: 2018-11-13 11:47
 **/
public class MongoTest {

    @Test
    public void testFind() throws Throwable {
        MongoDatabase mongoDatabase = MongoFactory.getInstance().getMongoDatabase();
        String name = mongoDatabase.getName();
        System.out.println(name);

        // 获取collection的句柄
        MongoCollection<Document> collection = mongoDatabase.getCollection("hotelCity");
        SubscriberHelpers.ObservableSubscriber subscriber = new SubscriberHelpers.ObservableSubscriber<Success>();
        collection.find().first().subscribe(subscriber);
        subscriber.await();


    }

    @Test
    public void testMongoDB() {
        MongoClient mongoClient = MongoFactory.getInstance().getMongoClient();
        // getting a list of databases
        mongoClient.listDatabaseNames().subscribe(new SubscriberHelpers.PrintSubscriber<>("Database Names: %s"));
        mongoClient.close();
    }

}
