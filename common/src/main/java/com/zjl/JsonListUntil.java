package com.zjl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonListUntil {
    private static ObjectMapper mapper = new ObjectMapper();
    public static List listMap2listObj(List<Map> list) throws ClassNotFoundException, JsonProcessingException {
        ArrayList res = new ArrayList();
        for (Map o : list) {
            System.out.println(o);
            res.add(mapper.readValue(mapper.writeValueAsString(o),Class.forName((String)o.get("type"))));
        }
        return res;
    }
}
