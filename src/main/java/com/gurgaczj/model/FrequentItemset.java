package com.gurgaczj.model;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

public class FrequentItemset {

    private String support;
    private Set<String> itemset;
    private String error;

    public FrequentItemset(double support, String[] items, double error) {
        this.support = new BigDecimal(support).toString();
        this.itemset = toSet(items);
        this.error = new BigDecimal(error).toString();
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Set<String> getItemset() {
        return itemset;
    }

    public void setItemset(Set<String> itemset) {
        this.itemset = itemset;
    }

    public Set<String> toSet(String[] itemSet) {
        int itemsetLength = itemSet.length;
        Set<String> result = new LinkedHashSet<>(itemsetLength);
        for (int i = 0; i < itemsetLength; i++) {
            result.add(itemSet[i]);
        }
        return result;
    }


}