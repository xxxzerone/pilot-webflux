package com.demo.repository;

import com.demo.domain.Bid;
import reactor.core.publisher.Mono;

public interface BidRepositoryCustom {

    Mono<Bid> findMaxAmountByItemId(Long itemId);
}
