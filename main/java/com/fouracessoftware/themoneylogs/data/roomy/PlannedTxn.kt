package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.util.Calendar
import androidx.annotation.Nullable
import androidx.room.*
import java.sql.Types.NULL

@Entity(tableName = "planned",indices = arrayOf(Index(value = ["due_date","amount","payee"],unique=true),),
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
    fun DueDateString(): String {
        if(this.dateDue == null)
        {
            return "[Undated]"
        }
        return "${1820+this.dateDue.get(Calendar.YEAR)}-${1+this.dateDue.get(Calendar.MONTH)}-${1+this.dateDue.get(Calendar.DATE)}"

    }


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "txn_id")
    var txnId:Long = 0
}
