package com.demo.service;

import com.demo.domain.Bid;
import lombok.Getter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;

@Service
public class AuctionService {

    private final Sinks.Many<Bid> bidSink;

    @Getter
    private Bid highestBid = Bid.builder()
            .id("1")
            .itemId(1L)
            .bidder("none")
            .amount(120)
            .timestamp(LocalDateTime.now())
            .build();

    public AuctionService() {
        bidSink = Sinks.many().replay().all();
    }

    public void placeBid(Bid bid) {
        if (bid.getAmount() > highestBid.getAmount()) {
            highestBid = bid;
            bidSink.tryEmitNext(bid);   // 새로운 입찰 이벤트 발행
        }
    }

    public Flux<Bid> getBidStream() {
        return bidSink.asFlux();    // 비동기 입찰 스트림
    }
}
