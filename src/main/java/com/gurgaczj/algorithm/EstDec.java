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
        EstDecNode root = getRootNode();

        if (root.getChildrens().isEmpty()) {
            return null;
        }

        //Hashtable<Double, String[]> frequentItemSets = estDecTree.createFrequentItemSets(estDecTree.getRoot(), new String[0], new Hashtable<Double, String[]>());

        LinkedHashMultimap<Double , String[]> itemSetsSet = LinkedHashMultimap.create();
        Set<FrequentItemset> frequentItemsets2 = new HashSet<>();
        ArrayDeque<FrequentItemset> frequentItemsets = new ArrayDeque<>();
        for (Map.Entry<String, EstDecNode> estDecNode : root.getChildrens().entrySet()) {
            frequentItemsets.addAll(recursiveItemSetBuilding(estDecNode.getValue(), new String[0], frequentItemsets2));
        }

        return frequentItemsets2;
    }



    private Set<FrequentItemset> recursiveItemSetBuilding(EstDecNode estDecNode, String[] items, Set<FrequentItemset> itemsSet) {
        estDecNode.updateCountForSelectionPhase(estDecTree.getD(), estDecTree.getK());
        if(estDecNode.getItem().equals("12")){
            System.out.println("12");
        }
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
}
