package com.octalgroup.mobilegurushop.Model

class TypesModel {

    var id:Int=0
    var tname: String?=null
    var img: String?=null


    constructor():this (0,"",""){}

    constructor(id:Int, tname: String?, img: String?) {
        this.id = id
        this.tname = tname
        this.img = img
    }
}