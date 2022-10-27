package com.zjl.dao;

import com.zjl.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    int insertList(@Param("orders") List<Order> orders);
}
