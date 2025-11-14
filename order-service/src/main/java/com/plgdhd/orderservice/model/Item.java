package com.plgdhd.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items", indexes = {
        @Index(name = "idx_item_name", columnList = "name"),
        @Index(name = "idx_item_price", columnList = "price")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "price", nullable = false)
    private Long price;

}
