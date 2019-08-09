package com.example.arouter_api.template;

import java.util.Map;

public interface IRouterRoot {
    /**
     * <p>往 routes 中存入IRouteGroup的实现类的class对象</p>
     *   routes（M）ap :key是group，
     *          ：value是IRouteGroup接口的实体类的class
     * @param routes input
     */
    void loadInfo(Map<String,Class<? extends IRouteGroup>> routes);
}
