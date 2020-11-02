package com.octalgroup.mobilegurushop.Model


class ProductModel {

    var id:Int=0
    var name: String?=null
    var price: String?=null
    var image: String?=null
    var subcategory: String?=null
    var description: String?=null
    var category: String?=null

    constructor():this (0,"","","","","",""){}

    constructor(id:Int, name: String?, price: String?,  image: String?, subcategory: String?,description: String?, category:String?) {
        this.id = id
        this.name = name
        this.price = price
        this.image=image
        this.subcategory=subcategory
        this.description=description
        this.category=category
    }


}