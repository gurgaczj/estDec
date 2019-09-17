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
        this.childrens = new HashSet<>();
    }

    EstDecNode getChildNodeByItem(String item){
        if(childrens.isEmpty()){
            return null;
        }

        return childrens.stream().filter(estDecNode -> estDecNode.getItem().equals(item)).findFirst().orElse(null);
    }


    public HashSet<EstDecNode> getChildrens() {
        return childrens;
    }

    String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void updateCount(Double d, Integer k) {
        counter = counter * toThePowerOf(d, k - tid) + 1;
        error = error * toThePowerOf(d, k - tid);
        tid = k;
    }

    Double calculateSupport(Double Dk){
        return counter / Dk;
    }

    Double toThePowerOf(Double d, Integer exponent){
        return Math.pow(d, exponent);
    }

    public Double getCounter() {
        return counter;
    }

    public Double getError() {
        return error;
    }
}
