package com.project.ecommerce.config; // Check kar lijiye aapka package name yahi hai

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    // fetch data from db
    @Value("${MONGO_LOCAL_TEST}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        // gave db name
        return "ecommerce";
    }

    @Override
    public MongoClient mongoClient() {
        // build connection
        return MongoClients.create(mongoUri);
    }
}