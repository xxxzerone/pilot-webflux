package com.demo.controller;

import com.demo.domain.Bid;
import com.demo.domain.model.BidModel;
import com.demo.repository.BidRepository;
import com.demo.service.AuctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/bids")
public class BidController {

    private final BidRepository bidRepository;
    private final Sinks.Many<Bid> sink;

    public BidController(BidRepository bidRepository) {
        this.bidRepository = bidRepository;
        sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @PostMapping("/bid")
    public Mono<Bid> placeBid(@RequestBody BidModel bidModel) {
        log.info("bidModel: {}", bidModel);
        return bidRepository.save(Bid.builder()
                .itemId(bidModel.itemId())
                .bidder(bidModel.bidder())
                .amount(bidModel.amount())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping(value = "/{itemId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Bid>> getBids(@PathVariable("itemId") Long itemId) {
        Flux<Bid> initialMaxBidFlux = bidRepository.findMaxAmountByItemId(itemId)
                .flatMapMany(Flux::just)
                .publishOn(Schedulers.boundedElastic());

        Flux<Bid> maxBidUpdatesFlux = sink.asFlux()
                .filter(bid -> bid.getItemId().equals(itemId))
                .flatMap(bid -> bidRepository.findMaxAmountByItemId(itemId))
                .distinctUntilChanged() // Only emit when the max bid changes
                .publishOn(Schedulers.boundedElastic());

        return Flux.merge(initialMaxBidFlux, maxBidUpdatesFlux)
                .map(bid -> ServerSentEvent.builder(bid).build())
                .log()
                .publishOn(Schedulers.boundedElastic())
                .doOnCancel(() -> sink.asFlux().blockLast());
    }

    @PostMapping
    public Mono<Bid> save(@RequestBody Bid bid) {
        return bidRepository.save(bid)
                .doOnNext(sink::tryEmitNext)
                .log();
    }
}
