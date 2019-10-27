package com.gurgaczj;

import java.util.HashSet;

public class EstDecNode {

    private String item;
    private HashSet<EstDecNode> childrens;
    private Integer tid;
    private Double counter;
    private Double error;

    EstDecNode() {
        this.childrens = new HashSet<>();
    }

    EstDecNode(String item, Integer tid) {
        this.item = item;
        this.tid = tid;

        counter = 0.0;
        error = 0.0;

        this.childrens = new HashSet<>();
    }

    public EstDecNode(String item, Integer k, double count) {
        this.item = item;
        this.tid = k;
        this.counter = count;

        //TODO: calculate error
        this.error = 0.0;

        this.childrens = new HashSet<>();
    }

    EstDecNode getChildNodeByItem(String item){
        if(childrens.isEmpty()){
            return null;
        }

        return childrens.stream().filter(estDecNode -> estDecNode.getItem().equals(item)).findFirst().orElse(null);
    }


    HashSet<EstDecNode> getChildrens() {
        return childrens;
    }

    EstDecNode addChild(EstDecNode child){
        getChildrens().add(child);
        return child;
    }

    String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    void updateCount(Double d, Integer k) {
        counter = counter * toThePowerOf(d, k - tid) + 1;
        error = error * toThePowerOf(d, k - tid);
        tid = k;
    }

    Double calculateSupport(Double Dk){
        return counter / Dk;
    }

    private Double toThePowerOf(Double base, Integer exponent){
        return Math.pow(base, exponent);
    }

    public Double getCounter() {
        return counter;
    }

    public Double getError() {
        return error;
    }
}
