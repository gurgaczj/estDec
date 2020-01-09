package com.gurgaczj.algorithm;

import com.google.common.collect.Maps;

import java.util.LinkedHashMap;
import java.util.Map;

public class EstDecNode {

    private Map<String, EstDecNode> childrens;
    private int mrtid;
    private double counter;
    private double error;

    EstDecNode() {
        this.childrens = Maps.newConcurrentMap();
    }

    EstDecNode(int mrtid, double cMax) {
        this.mrtid = mrtid;

        this.counter = cMax;
        this.error = 0.0;

        this.childrens = Maps.newConcurrentMap();
    }

    public EstDecNode(int k, double cMax, double cMin) {
        this.mrtid = k;
        this.counter = cMax;

        this.error = cMax - cMin;

        this.childrens = Maps.newConcurrentMap();
    }

    EstDecNode getChildNodeByItem(String item) {
        return childrens.get(item);
    }


    public Map<String, EstDecNode> getChildrens() {
        return childrens;
    }

    EstDecNode addChild(String item, EstDecNode child) {
        getChildrens().put(item, child);
        return child;
    }

    void updateCount(Double d, Integer k) {
        counter = counter * Math.pow(d, k - mrtid) + 1;
        error = error * Math.pow(d, k - mrtid);
        mrtid = k;
    }

    void updateCountForSelectionPhase(Double d, Integer k) {
        counter = counter * Math.pow(d, k - mrtid);
        error = error * Math.pow(d, k - mrtid);
        mrtid = k;
    }

    Double calculateSupport(Double Dk) {
        return counter / Dk;
    }

    public Double getCounter() {
        return counter;
    }

    public Double getError() {
        return error;
    }

    public void setChildrens(LinkedHashMap<String, EstDecNode> childrens) {
        this.childrens = childrens;
    }

    public Integer getMrtid() {
        return mrtid;
    }

    public void setMrtid(Integer mrtid) {
        this.mrtid = mrtid;
    }

    public void setCounter(Double counter) {
        this.counter = counter;
    }

    public void setError(Double error) {
        this.error = error;
    }
}
