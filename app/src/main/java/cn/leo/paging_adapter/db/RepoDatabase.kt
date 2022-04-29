package cn.leo.paging_adapter.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cn.leo.paging_adapter.App

@Database(entities = [RepoEntity::class], version = 1, exportSchema = false)
abstract class RepoDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao

    companion object {
        val instance = AppDatabaseHolder.db
    }

    private object AppDatabaseHolder {
        val db: RepoDatabase = Room
            .databaseBuilder(
                App.context!!,
                RepoDatabase::class.java,
                "PagingDemoDataBase.db"
            )
            .fallbackToDestructiveMigration() //数据库升级时清空数据
            .allowMainThreadQueries() //允许在主线程中查询
            .build()
    }
}