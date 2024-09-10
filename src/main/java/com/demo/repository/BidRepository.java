package com.demo.repository;

import com.demo.domain.Bid;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BidRepository extends ReactiveMongoRepository<Bid, String>, BidRepositoryCustom {

    Flux<Bid> findByItemId(Long itemId);
}
