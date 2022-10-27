package com.zjl.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MatchResult {
    private Order tackerOrder;
    private List<MatchRecord> records = new ArrayList<>();

}
