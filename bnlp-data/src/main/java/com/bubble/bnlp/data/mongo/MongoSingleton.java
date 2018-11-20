package com.bubble.bnlp.data.mongo;

import com.bubble.bnlp.common.ToolKits;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ClusterConnectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MongoDB连接类
 *
 * @author wugang
 * date: 2018-11-20 11:42
 **/
public class MongoSingleton {
    private static final Logger LOGGER = LoggerFactory.getLogger(MongoSingleton.class);

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    private MongoSingleton() {
    }

    public static MongoSingleton getInstance() {
        return MongoHolder.instance;
    }

    private static class MongoHolder {
        private static MongoSingleton instance = new MongoSingleton();

        static {
            init();
        }

        private static void init() {
            Instant begin = Instant.now();
            Properties properties = ToolKits.getProperties();
            String hosts = (String) properties.getOrDefault("mongo.serverAndPort", "localhost:27017");
            String database = properties.getProperty("mongo.database");
            String user = properties.getProperty("mongo.user");
            String password = properties.getProperty("mongo.password");

            List<ServerAddress> serverAddressList = Arrays.stream(hosts.split(","))
                    .map(hostAndPort -> {
                        String[] hp = hostAndPort.split(":");
                        return new ServerAddress(hp[0], Integer.parseInt(hp[1]));
                    }).collect(Collectors.toList());
            MongoCredential credential = MongoCredential.createCredential(user, database, password.toCharArray());
            MongoClient mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                            builder.hosts(serverAddressList)
//                                            .requiredReplicaSetName("") // 副本集名称
                                                    .mode(serverAddressList.size() == 1 ? ClusterConnectionMode.SINGLE : ClusterConnectionMode.MULTIPLE) // 连接到集群中的多个或单个服务器
                                                    .serverSelectionTimeout(30000, TimeUnit.MILLISECONDS) // 选择服务器的超时时间，默认值是30秒。
                                                    .maxWaitQueueSize(10 * 7) // maxPoolSize * waitQueueMultiple 等待服务器可用的并发操作的最大数量,默认500
                                                    .localThreshold(30, TimeUnit.MILLISECONDS) // 可以接受的延迟差异
                            )
                            .applyToConnectionPoolSettings(builder ->
                                            builder.minSize(5) // 默认0；连接的最小数量。当空闲时，这些连接将保存在池中；
                                                    .maxSize(10) //默认100；允许的最大连接数。当空闲时，这些连接将保存在池中。一旦池耗尽，任何需要连接的操作都将阻塞等待可用连接。
                                                    .maxWaitQueueSize(10 * 7) // 默认500；这是连接从池中可用的最大等待者数量。
                                                    .maxWaitTime(1500, TimeUnit.MILLISECONDS) //默认2min，0表示它不会等待。一个负数意味着它将无限期地等待；线程等待连接可用的最长时间
                                                    .maxConnectionLifeTime(1000, TimeUnit.MILLISECONDS) // 池连接可以存活的最长时间。零值表示生命周期没有限制。超过其生命周期的池连接将被关闭，并在必要时替换为新的连接。
                                                    .maxConnectionIdleTime(1000, TimeUnit.MILLISECONDS) // 池连接的最大空闲时间。零值表示空闲时间没有限制。超过空闲时间的池连接将被关闭，并在必要时用新连接替换。
//                                            .maintenanceFrequency() // 维护工作运行之间的时间间隔。
//                                            .maintenanceInitialDelay() // 在连接池上运行第一个维护作业之前等待的时间。
                            )
                            .applyToServerSettings(builder ->
                                    builder.heartbeatFrequency(20000, TimeUnit.MILLISECONDS) // 设置集群监视器到达每个服务器的频率。默认值是10秒。
                                            .minHeartbeatFrequency(500, TimeUnit.MILLISECONDS) // 设置最小心跳频率。如果驱动程序必须频繁地重新检查服务器的可用性，它将至少比上一次检查等待这么长时间，以避免浪费精力。默认值是500毫秒。
                            )
                            .applyToSocketSettings(builder ->
                                            builder.connectTimeout(2500, TimeUnit.MILLISECONDS) // socket连接超时时间
                                                    .readTimeout(5500, TimeUnit.MILLISECONDS) // socket read超时时间
//                                            .receiveBufferSize() // 设置receive缓冲区大小。
//                                            .sendBufferSize() // 设置send缓冲区大小。
                            )
                            .applyToSslSettings(builder ->
                                    builder.enabled(false) // 是否开启SSL
                                            .invalidHostNameAllowed(false) // 是否应该允许无效的主机名。默认值为false
                            )
                            .credential(credential) // 连接凭证设置
//                            .readConcern(ReadConcern.MAJORITY) // 只能读取到：成功写入到大多数节点的数据
//                            .writeConcern(WriteConcern.MAJORITY.withWTimeout(2500, TimeUnit.MILLISECONDS))
                            .readPreference(ReadPreference.secondary()) // 优先读取主节点外的其他节点；
                            .retryWrites(true) // 设置如果由于网络错误导致写操作失败，是否应该重新尝试写操作。
                            .compressorList(Collections.singletonList(MongoCompressor.createZlibCompressor().withProperty(MongoCompressor.LEVEL, 5))) // 设置用于将消息压缩到服务器的压缩器。驱动程序将使用列表中配置为服务器支持的第一个压缩器。
                            .applicationName("BubbleNLP") // 设置应用程序的逻辑名称。客户机可以使用应用程序名称向服务器标识应用程序，用于服务器日志、慢速查询日志和概要收集。
                            .build());

            instance.setMongoClient(mongoClient);
            instance.setMongoDatabase(mongoClient.getDatabase(database));
            if (database.equals(instance.getMongoDatabase().getName())) {
                LOGGER.info("MongoDB connected successfully, sever is {} database is [{}], costs {} ms", serverAddressList.toString(), database, Duration.between(begin, Instant.now()).toMillis());
            }
        }

    }

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    private void setMongoClient(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

    private void setMongoDatabase(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public MongoDatabase addMongoDatabase(String databaseName) {
        return getMongoClient().getDatabase(databaseName);
    }

    public void close() {
        this.mongoClient.close();
    }

}
