package com.zjl.clearing;

import com.zjl.domain.MatchResult;
import com.zjl.domain.Order;

public interface ClearService {
    void clearMatchResult(MatchResult result);
    void clearCancelOrder(Order order);
}
