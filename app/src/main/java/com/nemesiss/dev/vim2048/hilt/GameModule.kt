package com.nemesiss.dev.vim2048.hilt

import android.content.Context
import com.nemesiss.dev.vim2048.model.ElementColorTable
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GameModule {

    @Provides
    @Singleton
    fun colorTable(@ApplicationContext context: Context) = ElementColorTable.getInstance(context)

}