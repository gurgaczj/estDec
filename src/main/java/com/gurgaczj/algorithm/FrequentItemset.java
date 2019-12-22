package com.gurgaczj.algorithm;

import java.math.BigDecimal;

public class FrequentItemset {

    private Double support;
    private String[] items;
    private String itemsetString;
    private BigDecimal error;

    public FrequentItemset(double support, String[] items, double error) {
        this.support = support;
        this.items = items;
        this.error = new BigDecimal(error);
        this.itemsetString = toString();
    }

    public Double getSupport() {
        return support;
    }

    public void setSupport(Double support) {
        this.support = support;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }

    public BigDecimal getError() {
        return error;
    }

    public void setError(BigDecimal error) {
        this.error = error;
    }

    public String getItemsetString() {
        return itemsetString;
    }

    public void setItemsetString(String itemsetString) {
        this.itemsetString = itemsetString;
    }

    public String toString(){
        StringBuilder frequentItemsetInfo = new StringBuilder("[");
        for(int i = 0; i < getItems().length; i++){
            frequentItemsetInfo.append(getItems()[i]);
            if(i == getItems().length - 1){
                break;
            }
            frequentItemsetInfo.append(", ");
        }
        frequentItemsetInfo.append("]");
        return frequentItemsetInfo.toString();
    }


}
