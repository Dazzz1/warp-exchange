package com.zjl.service;

import com.zjl.message.event.AbstractEvent;
import com.zjl.message.event.CreateOrderEvent;

public interface EventService {
        void sendEvent(AbstractEvent event);
}
