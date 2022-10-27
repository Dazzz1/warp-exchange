package com.zjl.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zjl.JsonListUntil;
import com.zjl.message.event.AbstractEvent;
import com.zjl.service.SequencerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class EventListener {
    @Autowired
    SequencerService sequencerService;
    private Logger log = LoggerFactory.getLogger(this.getClass());
    @RabbitListener(queues = "to_sequence")
    private void listenerEvent(List<Map> messages) throws JsonProcessingException, ClassNotFoundException {
        List<AbstractEvent> events = JsonListUntil.listMap2listObj(messages);
        log.info("listened messages: "+events);
        sequencerService.processEvents(events);
    }

}
