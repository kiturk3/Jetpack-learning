package com.kiturk3.recipevault.di

import com.kiturk3.recipevault.data.repository.RecipeRepositoryImpl
import com.kiturk3.recipevault.domain.repository.RecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        impl: RecipeRepositoryImpl
    ): RecipeRepository
}