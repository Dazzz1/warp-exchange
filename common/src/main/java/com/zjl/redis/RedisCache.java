package com.zjl.redis;

public interface RedisCache {
    public interface Topic{
        String API_RESULT_MESSAGE = "api_result_message";

    }
    public interface Key{
        String TRADING_INFO = "trading_info";
        String API_UNIQUE_REQUEST_ID_PREFIX = "api_request_";
        String USER_SECRET_PREFIX = "user_secret_";
    }
}
