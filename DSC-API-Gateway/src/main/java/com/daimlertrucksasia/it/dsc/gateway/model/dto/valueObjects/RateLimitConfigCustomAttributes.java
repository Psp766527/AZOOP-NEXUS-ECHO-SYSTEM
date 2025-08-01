package com.daimlertrucksasia.it.dsc.gateway.model.dto.valueObjects;

import lombok.Value;

import java.util.Map;

@Value
public class RateLimitConfigCustomAttributes {

    Map<String, String> attributes;

    public String get(String key) {
        return attributes.get(key);
    }

    public boolean containsKey(String key) {
        return attributes.containsKey(key);
    }
}
