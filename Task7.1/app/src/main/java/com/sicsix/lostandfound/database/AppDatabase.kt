package com.sicsix.lostandfound.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sicsix.lostandfound.model.LostAndFound

@Database(entities = [LostAndFound::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Provides the DAOs that offer methods interact with the database.
     */
    abstract fun lostAndFoundDao(): LostAndFoundDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton instance of the database.
         *
         * @param context The context used to get the application context to create or retrieve the database.
         * @return The singleton instance of [AppDatabase].
         */
        fun getDatabase(context: Context): AppDatabase {
            // If the INSTANCE is not null, then return it, if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lostandfound_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface LostAndFoundDao {
    /**
     * Saves an advert to the database.
     *
     * @param advert The advert to save.
     * @return The unique ID of the advert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(advert: LostAndFound): Long

    /**
     * Removes an advert from the database.
     *
     * @param advert The advert to remove.
     */
    @Delete
    suspend fun delete(advert: LostAndFound)

    /**
     * Retrieves an advert by its ID.
     *
     * @param id The unique ID of the advert.
     * @return The advert with the specified ID, or null if no advert with such an ID exists.
     */
    @Query("SELECT * FROM lostandfound WHERE id = :id")
    suspend fun getById(id: Int): LostAndFound?

    /**
     * Retrieves all adverts from the database.
     *
     * @return A list of all adverts in the database.
     */
    @Query("SELECT * FROM lostandfound")
    suspend fun getAll(): List<LostAndFound>
}
