package com.plgdhd.paymentservice.migration;

import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "init-payment-collection", order = "001", author = "plgdhd")
public class PaymentMigration {

    private final MongoTemplate mongoTemplate;

    public PaymentMigration(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Execution
    public void changeSet() {
        if (!mongoTemplate.collectionExists("payments")) {
            mongoTemplate.createCollection("payments");
        }

        mongoTemplate.getCollection("payments").createIndex(
                Indexes.compoundIndex(
                        Indexes.ascending("user_id"),
                        Indexes.descending("timestamp")
                ),
                new IndexOptions().name("user_timestamp_idx")
        );
    }

    @RollbackExecution
    public void rollback() {
        if (mongoTemplate.collectionExists("payments")) {
            mongoTemplate.dropCollection("payments");
        }
    }
}