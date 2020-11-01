package com.octalgroup.mobilegurushop.Model

class TrainCartModel {


    var productid:Int=0
    var productimage: String?=null
    var productname: String?=null
    var productprice: String?=null
    var productquantity: Int=0
    var productsize: String?=null

    var producttype: String?=null



    constructor():this (0,"","",0,"","",""){}

    constructor(productid:Int, productname: String?, productprice: String?, productquantity: Int, productsize: String?, productimage: String?, producttype: String?) {
        this.productid = productid
        this.productname = productname
        this.productprice = productprice
        this.productquantity=productquantity
        this.productsize=productsize
        this.productimage=productimage
        this.producttype=producttype

    }


}