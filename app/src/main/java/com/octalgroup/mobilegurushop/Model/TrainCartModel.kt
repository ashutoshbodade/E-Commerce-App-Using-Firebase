package com.octalgroup.mobilegurushop.Model

class TrainCartModel {


    var productid:Int=0
    var productimage: String?=null
    var productname: String?=null
    var productprice: String?=null
    var productquantity: Int=0
    var productcategory: String?=null

    var productsubcategory: String?=null



    constructor():this (0,"","",0,"","",""){}

    constructor(productid:Int, productname: String?, productprice: String?, productquantity: Int, productcategory: String?, productimage: String?, productsubcategory: String?) {
        this.productid = productid
        this.productname = productname
        this.productprice = productprice
        this.productquantity=productquantity
        this.productcategory=productcategory
        this.productimage=productimage
        this.productsubcategory=productsubcategory

    }


}