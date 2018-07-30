package com.nowcoder.lyy.model;

import java.util.HashMap;
import java.util.Map;

public class ViewObject {       //方便把所有的数据打包到一起
    private Map<String, Object> objs = new HashMap<String, Object>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
