package com.vivek.currencyconverter.di // ktlint-disable package-name

import com.vivek.currencyconverter.data.repository.ExchangeRepository
import com.vivek.currencyconverter.data.repository.ExchangeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindRepository(impl: ExchangeRepositoryImpl): ExchangeRepository


}
