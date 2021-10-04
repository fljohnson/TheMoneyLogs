package com.fouracessoftware.themoneylogs.data.roomy

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(indices = arrayOf(Index(value = ["name"],unique = true))



)
data class Category(
    @ColumnInfo
    val name:String,
    val description:String? = null,
    @ColumnInfo(name = "is_open_ended")
    val openEnded:Boolean = false
) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var categoryId:Int = 0

    override fun toString(): String {
        if(description.isNullOrEmpty())
        {
            return name
        }
        return "$name ($description)"
    }
}
