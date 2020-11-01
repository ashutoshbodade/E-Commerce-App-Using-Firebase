package com.octalgroup.mobilegurushop.Database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity


@Entity(tableName = "cart", primaryKeys = ["uid","productId","productSize"])
class CartItem {

    @NonNull
    @ColumnInfo(name = "productId")
    var productId:String=""

    @ColumnInfo(name = "productName")
    var productName:String?=null

    @ColumnInfo(name = "productImage")
    var productImage:String?=null

    @ColumnInfo(name = "productPrice")
    var productPrice:Double=0.0


    @ColumnInfo(name = "productQuantity")
    var productQuantity:Int=0

    @NonNull
    @ColumnInfo(name = "productSize")
    var productSize:String?=null

    @ColumnInfo(name = "productType")
    var productType:String?=""

    @ColumnInfo(name = "userPhone")
    var userPhone:String?=""

    @NonNull
    @ColumnInfo(name = "uid")
    var uid:String?=""

}