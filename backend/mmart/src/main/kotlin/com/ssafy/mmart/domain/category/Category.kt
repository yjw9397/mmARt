package com.ssafy.mmart.domain.category

import com.ssafy.mmart.domain.Base
import javax.persistence.*

@Entity
data class Category (
    @Id
    @Column(name = "categoryIdx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var categoryIdx : Int? = null,

    @Column(name = "categoryName")
    var categoryName : String,

    @Column(name = "placeInfo")
    var placeInfo : String,
): Base()