package com.octalgroup.mobilegurushop.Model

class AddressModel {

    var addressline: String?=null
    var city: String?=null

    var district: String?=null
    var housenumber: String?=null

    var postalcode: String?=null
    var state: String?=null

    constructor():this ("","","","","",""){}

    constructor(addressline: String?, city: String?, district: String?, housenumber: String?,postalcode: String?, state: String?) {
        this.addressline = addressline
        this.city = city

        this.district = district
        this.housenumber = housenumber

        this.postalcode = postalcode
        this.state = state
    }

}