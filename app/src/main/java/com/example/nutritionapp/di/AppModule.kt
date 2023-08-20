package com.example.nutritionapp.di

import android.content.Context
import android.content.SharedPreferences
import com.example.nutritionapp.data.repository.NutritionRepository
import com.example.nutritionapp.data.repository.NutritionRepositoryImpl
import com.example.nutritionapp.util.SharedPrefConstants
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            SharedPrefConstants.LOCAL_SHARED_PREF,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    fun provideNutritionRepository(repositoryImpl: NutritionRepositoryImpl): NutritionRepository {
        return repositoryImpl
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

}