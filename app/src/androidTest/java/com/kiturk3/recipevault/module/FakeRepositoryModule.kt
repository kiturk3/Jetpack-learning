package com.kiturk3.recipevault.module

import com.kiturk3.recipevault.domain.repository.RecipeRepository
import com.kiturk3.recipevault.repository.FakeAndroidTestRecipeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FakeRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        impl: FakeAndroidTestRecipeRepository
    ): RecipeRepository
}