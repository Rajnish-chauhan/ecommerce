package com.project.ecommerce.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    // Resolves configured URI with chained fallbacks to avoid startup resolution errors
    @Value("${spring.data.mongodb.uri:${MONGO_ATLAS_PROD:${MONGO_LOCAL_TEST:mongodb://localhost:27017/ecommerce}}}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return "ecommerce";
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }
}