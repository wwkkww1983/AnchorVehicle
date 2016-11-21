package com.anke.vehicle.entity;

/**
 * 实体功能：作为自定义的ListVIew的适配器的适配类型
 */
public class PoiSearchList {
    private String firstSearchLocationResult;
    private String secondSearchLocationResult;

    public PoiSearchList(String firstSearchLocationResult, String secondSearchLocationResult) {
        this.firstSearchLocationResult = firstSearchLocationResult;
        this.secondSearchLocationResult = secondSearchLocationResult;
    }

    public String getFirstSearchLocationResult() {
        return firstSearchLocationResult;
    }

    public void setFirstSearchLocationResult(String firstSearchLocationResult) {
        this.firstSearchLocationResult = firstSearchLocationResult;
    }

    public String getSecondSearchLocationResult() {
        return secondSearchLocationResult;
    }

    public void setSecondSearchLocationResult(String secondSearchLocationResult) {
        this.secondSearchLocationResult = secondSearchLocationResult;
    }
}
