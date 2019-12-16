package com.gurgaczj;

import java.util.LinkedHashMap;

public class EstDecNode {

    private String item;
    private LinkedHashMap<String, EstDecNode> childrens;
    private Integer mrtid;
    private Double counter;
    private Double error;

    EstDecNode() {
        this.childrens = new LinkedHashMap<>();
    }

    EstDecNode(String item, Integer mrtid, double cMax) {
        this.item = item;
        this.mrtid = mrtid;

        this.counter = cMax;
        this.error = 0.0;

        this.childrens = new LinkedHashMap<>();
    }

    public EstDecNode(String item, Integer k, double cMax, double cMin) {
        this.item = item;
        this.mrtid = k;
        this.counter = cMax;

        this.error = cMax - cMin;

        this.childrens = new LinkedHashMap<>();
    }

    EstDecNode getChildNodeByItem(String item){
        if(childrens.isEmpty()){
            return null;
        }
        return childrens.get(item);
//        for(EstDecNode childNode : getChildrens()){
//            if(childNode.getItem().equals(item)){
//                return childNode;
//            }
//        }
//        return null;
//        return childrens.stream().filter(estDecNode -> estDecNode.getItem().equals(item)).findFirst().orElse(null);
    }


    LinkedHashMap<String, EstDecNode> getChildrens() {
        return childrens;
    }

    EstDecNode addChild(EstDecNode child){
        getChildrens().put(child.item, child);
        return child;
    }

    String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    void updateCount(Double d, Integer k) {
        counter = counter * Math.pow(d, k - mrtid) + 1;
        error = error * Math.pow(d, k - mrtid);
        mrtid = k;
    }

    void updateCountForSelectionPhase(Double d, Integer k){
        counter = counter * Math.pow(d, k - mrtid);
        error = error * Math.pow(d, k - mrtid);
        mrtid = k;
    }

    Double calculateSupport(Double Dk){
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
