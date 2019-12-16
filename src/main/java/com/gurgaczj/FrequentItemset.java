package com.gurgaczj;

import java.math.BigDecimal;

public class FrequentItemset {

    private Double count;
    private String[] items;
    private BigDecimal error;

    public FrequentItemset(double count, String[] items, double error) {
        this.count = count;
        this.items = items;
        this.error = new BigDecimal(error);
    }

    public Double getCount() {
        return count;
    }

    public void setCount(Double count) {
        this.count = count;
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

    public String toString(){
        StringBuilder frequentItemsetInfo = new StringBuilder("Frequent itemset support = ");
        frequentItemsetInfo.append(getCount());
        frequentItemsetInfo.append(", item set = [");
        for(int i = 0; i < getItems().length; i++){
            frequentItemsetInfo.append(getItems()[i]);
            if(i == getItems().length - 1){
                break;
            }
            frequentItemsetInfo.append(", ");
        }
        frequentItemsetInfo.append("], error = ");
        frequentItemsetInfo.append(getError().toString());
        return frequentItemsetInfo.toString();
    }


}
