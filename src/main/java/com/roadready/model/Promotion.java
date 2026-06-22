package com.roadready.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "promotions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Integer promotionId;

    @Column(name = "promo_code", nullable = false, unique = true)
    private String promoCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "valid_till", nullable = false)
    private Instant validTill;

    @Column(name = "discount_percentage", nullable = false)
    private Integer discountPercentage;

    @Column(name = "is_banner_active")
    private Boolean isBannerActive = false;
}
