package com.gurgaczj.algorithm;

import com.google.common.collect.Sets;
import com.gurgaczj.model.FrequentItemset;

import java.util.*;
import java.util.stream.Collectors;

public class EstDec {

    private EstDecTree estDecTree;
    
    public EstDec() {
        this.estDecTree = new EstDecTree();
    }

    public EstDec(double smin, double sins, double sprn) {
        this.estDecTree = new EstDecTree(smin, sprn, sins);
    }

    public void setDecayRate(double b, double h) {
        estDecTree.setDecayRate(b, h);
    }

    public void processTransaction(Collection<String> transaction) {
        Set<Set<String>> itemsetPowerSet = Sets.powerSet(new LinkedHashSet<>(transaction))
                .stream()
                .filter(subSet -> subSet.size() != 0)
                .collect(Collectors.toSet());

        estDecTree.updateParam();

        for (Set<String> subSet : itemsetPowerSet) {
            estDecTree.updateCount(subSet);
            estDecTree.insertItemSet(subSet);
        }

        if (getK() % 10000 == 0) {
            estDecTree.forcePruning(getRootNode());
        }
    }

    public Set<FrequentItemset> buildFrequentItemSets() {
        Set<FrequentItemset> frequentItemsets = Collections.synchronizedSet(new HashSet<>());

        for (Map.Entry<String, EstDecNode> estDecNode : getRootNode().getChildrens().entrySet()) {
            frequentItemsets.addAll(estDecTree.recursiveItemsetBuilding(estDecNode.getKey(), estDecNode.getValue(), new String[0], frequentItemsets));
        }

        return frequentItemsets;
    }

    public EstDecNode getRootNode() {
        return estDecTree.getRoot();
    }

    public Integer getK() {
        return estDecTree.getK();
    }

    public Double getD() {
        return estDecTree.getD();
    }

    public Double getDk() {
        return estDecTree.getDk();
    }

    public EstDecTree getEstDecTree() {
        return estDecTree;
    }

    public void setEstDecTree(EstDecTree estDecTree) {
        this.estDecTree = estDecTree;
    }
}
