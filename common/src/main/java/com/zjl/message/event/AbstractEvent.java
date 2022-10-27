package com.zjl.message.event;


import com.zjl.message.AbstractMessage;
import org.springframework.lang.Nullable;

public abstract class AbstractEvent extends AbstractMessage {
    public long previousId;
    public long sequenceId;
    @Nullable
    public String uniqueId;
    public String type;
}
