package com.gurgaczj.algorithm;

import com.google.common.collect.LinkedHashMultimap;

import java.util.*;

public class EstDec {

    private EstDecTree estDecTree;

    public EstDec() {
        estDecTree = new EstDecTree();
    }


    public EstDec(double smin, double sins, double sprn) {
        estDecTree = new EstDecTree(smin, sprn, sins);
    }

    public void setDecayRate(double b, double h) {
        estDecTree.setDecayRate(b, h);
    }

    public void processTransaction(List<String[]> transaction) {
        estDecTree.updateParam();
        transaction.forEach(itemSet -> {
        estDecTree.updateCount(itemSet);
        estDecTree.insertItemSet(itemSet, 0);
        });
    }

    public Set<FrequentItemset> buildFrequentItemSets() {
        Set<FrequentItemset> frequentItemsets = Collections.synchronizedSet(new HashSet<>());

        for (Map.Entry<String, EstDecNode> estDecNode : getRootNode().getChildrens().entrySet()) {
            frequentItemsets.addAll(estDecTree.recursiveItemSetBuilding(estDecNode.getValue(), new String[0], frequentItemsets));
        }

        //this.rootNode.getChildrens().clear();

        return frequentItemsets;
    }

    private Set<FrequentItemset> recursiveItemSetBuilding(EstDecNode estDecNode, String[] items, Set<FrequentItemset> itemsSet) {
        estDecNode.updateCountForSelectionPhase(estDecTree.getD(), estDecTree.getK());
        double support = estDecNode.calculateSupport(estDecTree.getDk());
        if(support < estDecTree.getSmin()){
            return itemsSet;
        }
        int itemsetSize = items.length;
        String[] tempItems = new String[itemsetSize + 1];
        System.arraycopy(items, 0, tempItems, 0, itemsetSize);
        tempItems[itemsetSize] = estDecNode.getItem();
        itemsSet.add(new FrequentItemset(support, tempItems, estDecNode.getError()));
        if (estDecNode.getChildrens().isEmpty()) {
            //itemsSet.add(tempItems);
            return itemsSet;
        }
        for (Map.Entry<String, EstDecNode> childNode : estDecNode.getChildrens().entrySet()) {
            itemsSet.addAll(recursiveItemSetBuilding(childNode.getValue(), tempItems, itemsSet));
        }
        return itemsSet;
    }

//    private Set<String[]> getAllItemSetsFromNode(EstDecNode node, HashSet<String[]> itemSets) {
//        String[] itemArray = new String[1];
//        HashSet<EstDecNode> nodeChilds = node.getChildrens();
//        if (!nodeChilds.isEmpty()) {
//            for (EstDecNode estDecNode : nodeChilds) {
//                estDecNode.getItem();
//            }
//        }
//        return null;
//    }


    // for testing purposes
    public EstDecNode getRootNode() {
        return estDecTree.getRoot();
    }

    public Integer getK() { return estDecTree.getK(); }

    public Double getD() { return estDecTree.getD(); }

    public Double getDk() { return estDecTree.getDk(); }
}
