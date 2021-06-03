package com.nemesiss.dev.crossingcontainermovement.hilt

import android.content.Context
import com.nemesiss.dev.crossingcontainermovement.model.ElementColorTable
import com.tencent.mmkv.MMKV
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

    @Provides
    @Singleton
    fun mmkv() = MMKV.defaultMMKV()!!
}