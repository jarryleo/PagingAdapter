package cn.leo.paging_adapter.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.leo.paging_ktx.adapter.CheckedData


@Entity
data class RepoEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
) : CheckedData
