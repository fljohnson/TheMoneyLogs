package com.fouracessoftware.themoneylogs.data.roomy

import androidx.room.*

@Entity(tableName = "plan_note",
    indices = [Index(value = ["xid"])]
)
data class PlanNote(
    @ColumnInfo
    val xid: Long,
    val content: String?=null
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var noteId:Long = 0
}
