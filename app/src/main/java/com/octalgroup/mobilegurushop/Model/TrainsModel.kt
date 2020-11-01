package com.octalgroup.mobilegurushop.Model

class TrainsModel {

    var name: String?=null
    var number: String?=null


    constructor():this ("",""){}

    constructor(name: String?, number: String?) {
        this.name = name
        this.number = number
    }

}