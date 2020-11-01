package com.octalgroup.mobilegurushop.Model

class ReviewModel {


    var id:Int=0
    var productid:Int=0
    var rating: Double?=null
    var review: String?=null
    var username: String?=null

    constructor():this (0,0,0.1,"",""){}

    constructor(id:Int, productid: Int, rating: Double?, review: String?, username: String?) {
        this.id = id
        this.productid = productid
        this.rating = rating
        this.review=review
        this.username=username

    }


}