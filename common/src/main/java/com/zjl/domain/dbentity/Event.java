package com.zjl.domain.dbentity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    private long sequenceId;
    private String type;
    private String data;
    private long createAt;
}
