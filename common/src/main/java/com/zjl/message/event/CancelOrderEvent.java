package com.zjl.message.event;

import lombok.Data;

@Data
public class CancelOrderEvent extends AbstractEvent {
    public long userId;
    public long orderId;
}
