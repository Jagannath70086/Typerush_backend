package com.typer.core.di

import com.typer.auth.domain.repository.UserRepository
import com.typer.auth.infrastructure.repository.UserRepositoryImpl
import com.typer.compete.domain.repository.CompeteRepository
import com.typer.compete.handlers.CompeteHandler
import com.typer.compete.infrastructure.repository.CompeteRepositoryImpl
import com.typer.create_contest.domain.repository.CreateContestRepository
import com.typer.create_contest.handlers.CreateContestHandler
import com.typer.create_contest.infrastructure.repository.CreateContestRepositoryImpl
import com.typer.practice.domain.repository.PracticeRepository
import com.typer.practice.infrastructure.repository.PracticeRepositoryImpl
import com.typer.waiting.domain.repository.WaitingRepository
import com.typer.waiting.handlers.WaitingHandler
import com.typer.waiting.infrastructure.repository.WaitingRepositoryImpl
import com.typer.websocket.WebSocketHandler
import com.typer.websocket.WebsocketManager
import com.typer.websocket.inter_data_exchange.EventBus
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::UserRepositoryImpl) bind(UserRepository::class)
    singleOf(::PracticeRepositoryImpl) bind(PracticeRepository::class)
    singleOf(::CompeteRepositoryImpl) bind(CompeteRepository::class)
    singleOf(::CreateContestRepositoryImpl) bind(CreateContestRepository::class)
    singleOf(::WaitingRepositoryImpl) bind(WaitingRepository::class)

    singleOf(::EventBus)

    singleOf(::CompeteHandler) bind(WebSocketHandler::class)
    singleOf(::CreateContestHandler) bind(WebSocketHandler::class)
    singleOf(::WaitingHandler) bind(WebSocketHandler::class)

    single { WebsocketManager(getAll(), get()) }
}