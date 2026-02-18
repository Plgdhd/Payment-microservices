package com.plgdhd.paymentservice.repository;

import com.plgdhd.paymentservice.model.Payment;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByUserId(Long userId);
    List<Payment> findByStatusIn(List<String> statuses);

    List<Payment> findByTimestampBetween(Instant from, Instant to);
    @Aggregation(pipeline = {
        "{ '$match': { 'timestamp': { '$gte': ?0, '$lte': ?1 } } }",
        "{ '$group': { _id: null, total: { $sum: '$paymentAmount' } } }"
    })
    
    List<TotalSum> getTotalSumForPeriod(Instant from, Instant to);
    interface TotalSum { BigDecimal getTotal(); }
}
