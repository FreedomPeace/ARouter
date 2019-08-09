package com.example.arouter_annotation.bean;


import com.example.arouter_annotation.Route;

import javax.lang.model.element.TypeElement;

/**
 * route的实体类
 */
public class RouteMeta {
    private String path;//path of the router
    private String group;//group of the router
    private TypeElement elementType;
    private Class<?> destination;

    public RouteMeta(String path, String group, Class<?> destination) {
        this.path = path;
        this.group = group;
        this.destination = destination;
    }


    public static RouteMeta build(String path, String group, Class<?> destination) {
        return new RouteMeta(path,group,destination);
    }
    public RouteMeta(Route route, TypeElement elementType) {
        this.path = route.path();
        this.group = route.group();
        this.elementType = elementType;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public TypeElement getElementType() {
        return elementType;
    }

    public void setElementType(TypeElement elementType) {
        this.elementType = elementType;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
