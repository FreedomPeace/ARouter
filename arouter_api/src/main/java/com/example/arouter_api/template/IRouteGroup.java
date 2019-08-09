package com.example.arouter_api.template;


import com.example.arouter_annotation.bean.RouteMeta;

import java.util.Map;

public interface IRouteGroup {
    /**
     * <p>往 altas 中存入route</p>
     * Map的key是route的path ，value是route的实体包装类
     * @param altas input
     */
    void loadInfo(Map<String, RouteMeta> altas);
}
