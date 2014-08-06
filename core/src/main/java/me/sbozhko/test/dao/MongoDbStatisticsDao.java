package me.sbozhko.test.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import me.sbozhko.test.entity.StatisticsEntity;

import java.net.UnknownHostException;

public class MongoDbStatisticsDao implements StatisticsDao {
    private static final String MAX_VALUE = "max_value";
    private static final String MIN_VALUE = "min_value";
    private static final String AVG_VALUE = "avg_value";
    private final MongoClient mongoClient;
    private final DB db;
    private final DBCollection coll;

    public MongoDbStatisticsDao(String mongoUri) throws UnknownHostException {
        MongoClientURI uri = new MongoClientURI(mongoUri);
        mongoClient = new MongoClient(uri);
        db = mongoClient.getDB(uri.getDatabase());
        coll = db.getCollection(uri.getCollection());
    }

    @Override
    public void save(StatisticsEntity statisticsEntity) {
        BasicDBObject doc = new BasicDBObject(MAX_VALUE, statisticsEntity.getMaxValue())
                .append(MIN_VALUE, statisticsEntity.getMinValue())
                .append(AVG_VALUE, statisticsEntity.getAvgValue());
        coll.insert(doc);
    }
}
