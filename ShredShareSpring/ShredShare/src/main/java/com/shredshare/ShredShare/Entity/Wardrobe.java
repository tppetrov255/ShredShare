package com.shredshare.ShredShare.Entity;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "wardrobes")
public class Wardrobe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wardrobe_id")
    private Integer wardrobeId;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "resort_id")
    private Resort resort;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EnumWardrobeStatus status;
}
