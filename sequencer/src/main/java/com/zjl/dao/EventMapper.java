package com.zjl.dao;

import com.zjl.domain.dbentity.Event;
import com.zjl.message.event.AbstractEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EventMapper {
    int insertList(@Param("events") List<Event> events);

    Long selectMaxSequenceId();
}
