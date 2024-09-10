package com.demo.repository.impl;

import com.demo.domain.Bid;
import com.demo.repository.BidRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BidRepositoryImpl implements BidRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Bid> findMaxAmountByItemId(Long itemId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("itemId").is(itemId));
        query.with(Sort.by(Sort.Order.desc("amount")));
        query.limit(1);
        return mongoTemplate.findOne(query, Bid.class);
    }
}
