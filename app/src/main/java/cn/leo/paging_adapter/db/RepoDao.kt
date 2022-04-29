package cn.leo.paging_adapter.db

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface RepoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(pokemonList: List<RepoEntity>)

    @Query("SELECT * FROM RepoEntity")
    fun get(): PagingSource<Int, RepoEntity>

    @Query("DELETE FROM RepoEntity")
    suspend fun clear()

    @Delete
    fun delete(repo: RepoEntity)

    @Update
    fun update(repo: RepoEntity)
}