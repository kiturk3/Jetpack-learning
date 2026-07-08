package com.kiturk3.recipevault.di

import android.content.Context
import androidx.room.Room
import com.kiturk3.recipevault.data.local.RecipeDatabase
import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideRecipeDatabase(
        @ApplicationContext context:  Context
    ): RecipeDatabase {
        return Room.databaseBuilder(context = context,
            RecipeDatabase::class.java,
            "recipe_vault_db").build()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: RecipeDatabase): FavoriteDao{
        return database.favoriteDao()
    }
}