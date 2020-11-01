package com.octalgroup.mobilegurushop.Model


class Banner {

    var productid:Int=0

    var bannerurl: String?=null


    constructor():this (0,""){}

    constructor(productid:Int, bannerurl: String?) {
        this.bannerurl = bannerurl
        this.productid=productid
    }


}