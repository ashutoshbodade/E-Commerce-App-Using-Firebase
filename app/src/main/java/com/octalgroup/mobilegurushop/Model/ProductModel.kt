package com.octalgroup.mobilegurushop.Model


class ProductModel {


    var id:Int=0
    var name: String?=null
    var price: String?=null
    var qty: String?=null
    var unit: String?=null
    var image: String?=null
    var type: String?=null
    var description: String?=null
    var dtype: String?=null


    constructor():this (0,"","","","","","","",""){}

    constructor(id:Int, name: String?, price: String?, qty: String?, unit: String?, image: String?, type: String?,description: String?, dtype:String?) {
        this.id = id
        this.name = name
        this.price = price
        this.qty=qty
        this.unit=unit
        this.image=image
        this.type=type
        this.description=description
        this.dtype=dtype
    }


}