package com.zjl.dao;

import com.zjl.domain.MatchRecord;
import com.zjl.domain.dbentity.DBMatchRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatchRecordMapper {
    int insertList(@Param("list") List<MatchRecord> dbMatchRecords);
}
