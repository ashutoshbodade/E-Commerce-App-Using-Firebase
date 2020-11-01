package com.octalgroup.mobilegurushop.Model

class OrderModel {


    var orderid:Int=0
    var deliveryaddress: String?=null
    var deliverytype: String?=null
    var orderdate: String?=null
    var orderstatus: String?=null
    var ordertime: String?=null
    var paymentstatus: String?=null
    var totalprice: String?=null
    var confirmdate: String?=null
    var confirmtime: String?=null
    var intransitdate: String?=null
    var intransittime: String?=null
    var deliverydate: String?=null
    var deliverytime: String?=null
    var cancleddate: String?=null
    var cancledtime: String?=null
    var cancledreason: String?=null
    var cancledby: String?=null
    var docid: String?=null
    var paymentmode: String?=null

    var paymentid: String?=null
    var userid: String?=null

    var userphone: String?=null
    var supportnumber: String?=null

    var did: String?=null
    var dname: String?=null
    var dimage: String?=null
    var dnumber: String?=null

    var instructions: String?=null
    var trainno: String?=null
    var trainname: String?=null

    constructor():this (0,"","","","","","","","","","","",","
    ,"","","","","","","","","","","","","","","",""
    ,"",""){}

    constructor(orderid:Int, deliveryaddress: String?,deliverytype:String?, orderdate: String?, orderstatus: String?, ordertime: String?, paymentid: String?, paymentmode: String?,
                paymentstatus: String?,supportnumber: String?,totalprice: String?,userid: String?,userphone: String?,confirmdate: String?,confirmtime: String?,
                intransitdate: String?,intransittime: String?,deliverydate: String?,deliverytime: String?,cancleddate: String?,cancledtime: String?,cancledreason: String?,cancledby:String?,
                docid:String?, did:String?, dname:String?, dimage:String?, dnumber:String?, instructions:String?, trainno:String?, trainname:String?
                ) {
        this.orderid = orderid
        this.deliveryaddress = deliveryaddress
        this.deliverytype=deliverytype
        this.orderdate = orderdate
        this.orderstatus=orderstatus
        this.ordertime=ordertime
        this.paymentid=paymentid
        this.paymentmode=paymentmode
        this.paymentstatus=paymentstatus
        this.supportnumber=supportnumber
        this.totalprice=totalprice
        this.userid=userid
        this.userphone=userphone
        this.confirmdate=confirmdate
        this.confirmtime=confirmtime
        this.intransitdate=intransitdate
        this.intransittime=intransittime
        this.deliverydate=deliverydate
        this.deliverytime=deliverytime
        this.cancleddate=cancleddate
        this.cancledtime=cancledtime
        this.cancledreason=cancledreason
        this.cancledby=cancledby
        this.docid=docid

        this.did=did
        this.dname=dname
        this.dimage=dimage
        this.dnumber=dnumber

        this.instructions=instructions
        this.trainno=trainno
        this.trainname=trainname

    }


}