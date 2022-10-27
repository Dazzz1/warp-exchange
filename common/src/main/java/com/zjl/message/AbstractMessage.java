package com.zjl.message;

import java.io.Serializable;
import java.time.ZoneId;

public abstract class AbstractMessage  implements Serializable {
    public String refId = null;
    public long createAt;
}
