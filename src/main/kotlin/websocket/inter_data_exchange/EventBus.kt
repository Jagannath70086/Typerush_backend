package com.typer.websocket.inter_data_exchange

import com.typer.websocket.WebsocketEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class EventBus {
    private val _events = MutableSharedFlow<WebsocketEvent>(extraBufferCapacity = 100)
    val events: SharedFlow<WebsocketEvent> = _events

    suspend fun publish(event: WebsocketEvent) {
        _events.emit(event)
    }
}
