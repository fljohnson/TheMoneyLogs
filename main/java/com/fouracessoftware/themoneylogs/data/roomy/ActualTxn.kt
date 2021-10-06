package com.fouracessoftware.themoneylogs.data.roomy

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.room.*
import java.util.*

@Entity(tableName = "actual",
    indices = [Index(value = ["xid"])]
)
class ActualTxn(
    val xid: Long,
    val datePaid: Calendar,
    val amount: Float)
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var actualId:Long = 0

    fun datePaid(): String {
        return dateFormat.format(datePaid)

    }
    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    }
}
