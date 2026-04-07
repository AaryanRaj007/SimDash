package com.aaryan.simdash.di

import android.content.Context
import androidx.room.Room
import com.aaryan.simdash.data.db.AlertThresholdDao
import com.aaryan.simdash.data.db.PendingAlertDao
import com.aaryan.simdash.data.db.SimDashDatabase
import com.aaryan.simdash.data.db.SimProfileDao
import com.aaryan.simdash.data.db.UsageSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSimDashDatabase(@ApplicationContext context: Context): SimDashDatabase {
        return Room.databaseBuilder(
            context,
            SimDashDatabase::class.java,
            "simdash_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideSimProfileDao(database: SimDashDatabase): SimProfileDao =
        database.simProfileDao()

    @Provides
    @Singleton
    fun provideAlertThresholdDao(database: SimDashDatabase): AlertThresholdDao =
        database.alertThresholdDao()

    @Provides
    @Singleton
    fun provideUsageSessionDao(database: SimDashDatabase): UsageSessionDao =
        database.usageSessionDao()

    @Provides
    @Singleton
    fun providePendingAlertDao(database: SimDashDatabase): PendingAlertDao =
        database.pendingAlertDao()
}
