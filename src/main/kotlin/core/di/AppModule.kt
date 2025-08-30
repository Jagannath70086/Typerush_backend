package com.typer.core.di

import com.typer.auth.domain.repository.UserRepository
import com.typer.auth.infrastructure.repository.UserRepositoryImpl
import com.typer.practice.domain.repository.PracticeRepository
import com.typer.practice.infrastructure.repository.PracticeRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::UserRepositoryImpl) bind(UserRepository::class)
    singleOf(::PracticeRepositoryImpl) bind(PracticeRepository::class)
}