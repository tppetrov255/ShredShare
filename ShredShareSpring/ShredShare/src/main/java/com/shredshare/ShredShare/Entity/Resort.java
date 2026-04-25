package com.shredshare.ShredShare.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "resorts")
@Data
public class Resort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resort_id")
    private Integer resortId;

    @Column(nullable = false)
    private String name;

    private String country;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    private String season;

    private Integer beginnerSlopes;

    private Integer intermediateSlopes;

    private Integer difficultSlopes;

    private Integer totalSlopes;

    private String childFriendly;

    private String snowparks;

    private String gondola;

    private Integer highestPoint;
}
