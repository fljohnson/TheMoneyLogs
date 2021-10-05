package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.util.Calendar
import androidx.room.*

@Entity(tableName = "planned",indices = [Index(value = ["due_date","amount","payee"],unique=true),Index(value = ["category_id"])],
    foreignKeys = [ForeignKey(entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("category_id"))]



)
data class PlannedTxn(
    val amount:Float? =null,
    val payee:String,
    @ColumnInfo(name = "due_date")
    val dateDue:Calendar? = null,
    @ColumnInfo(name = "category_id")
    val categoryId:Int


) {


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "txn_id")
    var txnId:Long = 0
}
