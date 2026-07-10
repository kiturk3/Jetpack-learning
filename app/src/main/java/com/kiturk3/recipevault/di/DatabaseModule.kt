package com.kiturk3.recipevault.di

import android.content.Context
import androidx.room.Room
import com.kiturk3.recipevault.data.local.RecipeDatabase
import com.kiturk3.recipevault.data.local.dao.FavoriteDao
import com.kiturk3.recipevault.data.local.dao.RecipeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRecipeDatabase(
        @ApplicationContext context:  Context
    ): RecipeDatabase {
        return Room.databaseBuilder(context = context,
            RecipeDatabase::class.java,
            "recipe_vault_db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: RecipeDatabase): FavoriteDao{
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: RecipeDatabase): RecipeDao{
        return database.recipeDao()
    }
}