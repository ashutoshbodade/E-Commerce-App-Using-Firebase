package com.octalgroup.mobilegurushop.Model

class OrderProductModel {


    var orderid:Int=0
    var productid:Int=0
    var productimage: String?=null
    var productname: String?=null
    var productprice: String?=null
    var productquantity: String?=null
    var productsize: String?=null
    var producttotal: String?=null
    var producttype: String?=null


    constructor():this (0,0,"","","","","","",""){}

    constructor(orderid:Int, productid: Int, productimage: String?, productname: String?, productprice: String?, productquantity: String?, productsize: String?,producttotal: String?
                ,producttype: String?) {
        this.orderid = orderid
        this.productid = productid
        this.productimage = productimage
        this.productname=productname
        this.productprice=productprice
        this.productquantity=productquantity
        this.productsize=productsize
        this.producttotal=producttotal
        this.producttype=producttype
    }


}