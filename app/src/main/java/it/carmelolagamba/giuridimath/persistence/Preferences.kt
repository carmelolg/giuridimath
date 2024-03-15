package it.carmelolagamba.giuridimath.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author carmelolg
 * @since version 0.1
 */
@Entity(tableName = "preferences")
data class Preferences(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int?,
    @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") var value: String
    )
