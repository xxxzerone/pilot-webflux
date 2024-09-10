package com.demo.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bids")
public class Bid {

    @Id
    private String id;
    private Long itemId;
    private String bidder;
    private double amount;
    private LocalDateTime timestamp;
}

