package com.fouracessoftware.themoneylogs.data.roomy

import android.content.Context
import androidx.room.*


@Database(entities = [Category::class, PlannedTxn::class, PlanNote::class, ActualTxn::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
        abstract fun categoryDao(): CategoryDao
        abstract fun plannedTxnDao(): PlannedTxnDao
        abstract fun planNoteDao(): PlanNoteDao
        abstract fun actualTxnDao(): ActualTxnDao

        companion object {

                // For Singleton instantiation
                @Volatile private var instance: AppDatabase? = null

                fun getInstance(context: Context): AppDatabase {
                        return instance ?: synchronized(this) {
                                instance ?: buildDatabase(context).also { instance = it }
                        }
                }


                // Create and pre-populate the database. See this article for more details:
                // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
                private fun buildDatabase(context: Context): AppDatabase {
                        return Room.databaseBuilder(context, AppDatabase::class.java,"TheMoneyLog.db")
                        //return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                                /*
                                .addCallback(
                                        object : RoomDatabase.Callback() {
                                                override fun onCreate(db: SupportSQLiteDatabase) {
                                                        super.onCreate(db)
                                                        val request = OneTimeWorkRequestBuilder<SeedDatabaseWorker>()
                                                                .setInputData(workDataOf(KEY_FILENAME to PLANT_DATA_FILENAME))
                                                                .build()
                                                        WorkManager.getInstance(context).enqueue(request)
                                                }
                                        }
                                )*/
                                .build()
                }
        }
}