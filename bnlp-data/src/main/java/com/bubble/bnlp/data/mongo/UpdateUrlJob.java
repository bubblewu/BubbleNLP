package com.bubble.bnlp.data.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bubble.bnlp.common.http.OKHttpUtil;
import com.google.common.collect.Lists;
import com.mongodb.Block;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOneModel;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

/**
 * 修改历史推荐酒店的预定URL，添加轨迹参数
 *
 * @author wugang
 * date: 2018-11-19 11:12
 **/
public class UpdateUrlJob {
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUrlJob.class);

    public static void main(String[] args) {
        Instant begin = Instant.now();
//        MongoClient mongoClient = mongo();
//        MongoDatabase database = mongoClient.getDatabase("flight");
        MongoDatabase database = MongoSingleton.getInstance().getMongoDatabase();
        MongoCollection<Document> collection = database.getCollection("tipsUserLink");
        Bson bson = and(eq("type", 5), lt("updateTime", 1542618000000L));
        List<UpdateOneModel<Document>> batchList = Lists.newArrayList();
        long batch = 0;
        long totalCount = collection.countDocuments(bson);
        LOGGER.info("total count is {}", totalCount);

        MongoCursor<Document> cursor = collection.find(bson).iterator();
        while (cursor.hasNext()) {
            Document document = cursor.next();
            String replaceFields = document.getString("replaceFields");
            JSONObject json = parse(document.getString("phoneId"), replaceFields);
            document.put("replaceFields", json.toString());
            batch++;
            batchList.add(new UpdateOneModel<>(eq("_id", document.getString("_id")),
                            combine(set("replaceFields", json.toString()), set("updateTime", "1542618000000"))
                    )
            );
            if (batch % 1000 == 0) {
                collection.bulkWrite(batchList);
                batchList = Lists.newArrayList();
                System.out.println("has parse and save count: " + batch);
            }
            if (totalCount == batch) {
                collection.bulkWrite(batchList);
                System.out.println("has parse and save count: " + batch);
            }

        }
//        mongoClient.close();
        MongoSingleton.getInstance().close();
        LOGGER.info("update hotel book url done, costs {} ms", Duration.between(begin, Instant.now()).toMillis());
    }


    private static void testBulkUpdate(MongoCollection<Document> collection) {
        List<Document> documentList = Lists.newArrayList();
        collection.find(eq("type", 5)).forEach((Block<? super Document>) documentList::add);
        LOGGER.info("total count is {}", documentList.size());
        List<UpdateOneModel<Document>> batchList = Lists.newArrayList();
        int totalCount = documentList.size();
        int batch = 0;
        for (int i = 0; i < totalCount; i++) {
            Document document = documentList.get(i);
            String replaceFields = document.getString("replaceFields");
            JSONObject json = parse(document.getString("phoneId"), replaceFields);
            document.put("replaceFields", json.toString());
            batch++;
            batchList.add(new UpdateOneModel<>(new Document("_id", document.getString("_id")),
                    new Document("$set", new Document("replaceFields", json.toString()))
            ));

            if (batch % 1000 == 0) {
                collection.bulkWrite(batchList);
                batchList = Lists.newArrayList();
                System.out.println("has parse and save count: " + batch);
            }
            if (totalCount == batch) {
                collection.bulkWrite(batchList);
                System.out.println("has parse and save count: " + batch);
            }
        }
    }

    private static MongoClient mongo() {
        MongoCredential credential = MongoCredential.createCredential("search", "flight", "search@123".toCharArray());
        MongoClient mongoClient = MongoClients.create(
                MongoClientSettings.builder()
                        .applyToClusterSettings(builder ->
                                builder.hosts(Arrays.asList(new ServerAddress("localhost", 27017))))
                        .credential(credential)
                        .build());
        return mongoClient;
    }


    // 预定添加轨迹参数`analyseSourceEntry`:
//        - 来源：homeRec
//        - 用户类型：old / new
//        - 产品类型：0 猜你喜欢；1 曾经预定；
    private static JSONObject parse(String phoneId, String replaceFields) {
        JSONObject json = JSON.parseObject(replaceFields);
        String pre = "homeRec_";
        String userType;
        if (isNewUser(phoneId)) {
            userType = "new_";
        } else {
            userType = "old_";
        }
        int recType = 0;
        if (json != null) {
            if ("曾经预定".equals(json.getJSONObject("tag").getString("text"))) {
                recType = 1;
            }
            String analyseSourceEntry = pre + userType + recType;
            String oldUrl = json.getString("url");
            json.put("url", oldUrl + "&analyseSourceEntry=" + analyseSourceEntry);

        }
        return json;
    }

    private static boolean isNewUser(String userId) {
        List<String> userIdList = new ArrayList<>(1);
        userIdList.add(userId);
        String paramId = userIdList.toString();
        String url = "http://hotel.rsscc.cn/hljd_core_new/rest/newhotel/ifNewUser";
        String result = OKHttpUtil.doPost(url, paramId);
        JSONObject json = (JSONObject) JSON.parse(result);
        if (json == null) {
            System.out.println("query new user error. result is null.");
            return false;
        }
        if (json.getInteger("code") == 0) {
            JSONArray results = json.getJSONArray("data");
            if (results != null) {
                return results.stream().anyMatch(r -> {
                    JSONObject obj = (JSONObject) r;
                    return obj.getInteger(userId) == -1;
                });
            }
        }
        return false;
    }


}
