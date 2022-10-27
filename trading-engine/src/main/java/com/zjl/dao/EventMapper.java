package com.zjl.dao;

import com.zjl.domain.dbentity.Event;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventMapper {
    Event selectBySequenceId(long sequenceId);
}
