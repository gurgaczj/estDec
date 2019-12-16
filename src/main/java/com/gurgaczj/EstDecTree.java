package com.gurgaczj;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.print.attribute.SetOfIntegerSyntax;
import java.util.*;
import java.util.stream.Collectors;

public class EstDecTree {

    private static final Logger logger = LogManager.getLogger("EstDecTree");

    private double d; // decay rate
    private double Dk; // |D|k
    private Integer k; // actual transaction id
    private final double smin; // minimum Support
    private final double sins; // Sins
    private final double sprn;

    private EstDecNode root;

    EstDecTree() {
        this.smin = 0.0;
        this.sins = 0.0;
        this.sprn = 0.0;

        Dk = 0.0;
        k = 0;

        root = new EstDecNode();
    }

    public EstDecTree(Double smin, Double sprn, Double sins) {
        this.smin = smin;
        this.sins = sins;
        this.sprn = sprn;

        Dk = 0.0;
        k = 0;

        root = new EstDecNode();
    }

    /**
     * Sets decay rate.
     *
     * @param b
     * @param h
     */
    void setDecayRate(Double b, Double h) {
        d = Math.pow(b, -1 / h);
        System.out.println("d=" + d);
    }

    Hashtable<Double, String[]> createFrequentItemSets(EstDecNode parentNode, String[] itemSet, Hashtable<Double, String[]> frequentItemSets) {
        String[] tempItems = new String[itemSet.length + 1];
        System.arraycopy(itemSet, 0, tempItems, 0, itemSet.length);
        for (EstDecNode childNode : parentNode.getChildrens()) {
            tempItems[itemSet.length] = childNode.getItem();
            childNode.updateCount(d, k);
            double nodeSupport = childNode.calculateSupport(Dk);
            if (nodeSupport >= smin) {
                frequentItemSets.put(nodeSupport, tempItems);
                // should continue?
                continue;
            }
            frequentItemSets.putAll(createFrequentItemSets(childNode, tempItems, frequentItemSets));
        }
        return frequentItemSets;
    }

    void updateParam() {
        // parameter updating phase
        this.Dk = Dk * d + 1;
        this.k++;
    }

    /**
     * @param itemSet
     */
    void updateCount(String[] itemSet) {
        //count updating phase
        EstDecNode currentNode = getRoot();
        int i = 1;
        for (String item : itemSet) {
            EstDecNode tempNode = currentNode.getChildNodeByItem(item);
            if (tempNode == null) {
                //itemSet do not exist in ML, skipping
                return;
            }
            if (tempNode.getItem().equals(itemSet[itemSet.length - 1])) {
                tempNode.updateCount(d, k);
            }
            //tempNode.updateCount(this.d, this.k);
            // pruning

            if (tempNode.calculateSupport(Dk) < sprn && itemSet.length > 1) {
                System.out.println("Pruning dla " + tempNode.getItem());
                currentNode.getChildrens().remove(tempNode);
                return;
            }
            currentNode = tempNode;
            i++;
        }
    }

    /**
     * Inserts ItemSet to monitoring lattice (ML)
     *
     * @param node
     * @param itemSet
     * @param index
     */
    public void insertItemSet(EstDecNode node, String[] itemSet, int index) {
        if (index == itemSet.length) {
            return;
        }
        EstDecNode childNode = node.getChildNodeByItem(itemSet[index]);
        EstDecNode newNode;
        if (childNode == null) {
            if (itemSet.length == 1) {
                newNode = node.addChild(new EstDecNode(itemSet[index], k, 1));
                insertItemSet(newNode, itemSet, ++index);
            } else { // itemSet.length != 1
//                System.out.print("[");
//                for(int i = 0; i < itemSet.length; i++){
//                    if(i == itemSet.length - 1){
//                        System.out.print(itemSet[i] + "]\n");
//                    }
//                    System.out.print(itemSet[i] + ", ");
//                }
                double cMax = estimateCMax(itemSet, 0);
                if (calculateSupport(cMax) >= sins) {
                    double cMin = estimateCMin(itemSet);
//                    System.out.println(Sets.newLinkedHashSet(Arrays.asList(itemSet)) + ", cmin = " + cMin
//                            + ", cmax = " + cMax + ", cmax - cmin = " + (cMax - cMin));
                    newNode = node.addChild(new EstDecNode(itemSet[index], k, cMax, cMin));
                    insertItemSet(newNode, itemSet, ++index);
                }
            }
        } else {
            insertItemSet(childNode, itemSet, ++index);
        }
    }

    private double estimateCMin(String[] itemSet) {
        List<Set<String>> powerSet = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList(itemSet))).stream()
                .filter(strings -> strings.size() != 0 && strings.size() == itemSet.length - 1)
                .collect(Collectors.toList());
        Map<Set<String>, Set<String>> distinctPairs = getDistinctPairs(powerSet);

        double cMin = 0.0;

        for (Map.Entry<Set<String>, Set<String>> entry : distinctPairs.entrySet()) {
            double countOfUnionItemset = calculateCountOfUnionItemset(entry);
            if (countOfUnionItemset > cMin) {
                cMin = countOfUnionItemset;
            }
        }
        return cMin;
    }

    private double calculateCountOfUnionItemset(Map.Entry<Set<String>, Set<String>> entry) {
        Sets.SetView<String> intersection = Sets.intersection(entry.getKey(), entry.getValue());
        double e1Count = getCountOfItemset(entry.getKey().toArray(new String[0]));
        double e2Count = getCountOfItemset(entry.getValue().toArray(new String[0]));
        double count;
        if (intersection.isEmpty()) {
            count = e1Count + e2Count - getK();
        } else {
            double interSectionCount = getCountOfItemset(intersection.toArray(new String[0]));
            count = e1Count + e2Count - interSectionCount;
        }
        return Math.max(count, 0.0);
    }

    private double getCountOfItemset(String[] itemset) {
        EstDecNode node = getRoot();
        for (int i = 0; i < itemset.length; i++) {
            node = node.getChildNodeByItem(itemset[i]);
            if (node == null) {
                return 0.0;
            }
            if (i == itemset.length - 1) {
                return node.getCounter();
            }
        }
        return 0.0;
    }

    private Map<Set<String>, Set<String>> getDistinctPairs(List<Set<String>> powerSet) {
        Map<Set<String>, Set<String>> distinctPairs = new LinkedHashMap<>();
        for (int i = 0; i < powerSet.size(); i++) {
            for (int j = i + 1; j < powerSet.size(); j++) {
                distinctPairs.put(powerSet.get(i), powerSet.get(j));
            }
        }
        return distinctPairs;
    }

    private double calculateSupport(double count) {
        return count / this.Dk;
    }

    /**
     * Estimates itemSet count
     *
     * @param itemSet
     * @param index
     * @return
     */
    private double estimateCMax(String[] itemSet, int index) {
        double cMax = Double.MAX_VALUE;
        for (int i = 0; i < itemSet.length; i++) {
            double count = getItemSetCountWithoutItemAtIndex(itemSet, i);
            if (count < cMax) {
                cMax = count;
            }
            if (cMax == 0) {
                // should already return 0?
                break;
            }
        }
        double cUpper = calculateCountForSubsets(itemSet.length) + calculateMaxCountBeforeSubsets(itemSet.length);
        if (cMax > cUpper) {
            cMax = cUpper;
        }
        return cMax;
    }

    private double calculateDk(int length) {
        return (1 - Math.pow(d, k - length)) / (1 - d);
    }

    private double calculateMaxCountBeforeSubsets(int length) {
        return sins * calculateDk(length - 1) * Math.pow(d, length - 1);
    }

    private double calculateCountForSubsets(int length) {
        return (1 - Math.pow(d, length - 1)) / (1 - d);
    }

    /**
     * Returns count of itemSet without item at given position
     *
     * @param itemSet
     * @param index
     * @return
     */
    private double getItemSetCountWithoutItemAtIndex(String[] itemSet, int index) {
        EstDecNode currentNode = getRoot();
        for (int i = 0; i < itemSet.length; i++) {
            if (i != index) {
                EstDecNode childNode = currentNode.getChildNodeByItem(itemSet[i]);
                if (childNode == null) {
                    //return currentNode.getCounter();
                    return 0;
                }
                currentNode = childNode;
            }
        }
        try {
            return currentNode.getCounter();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Return true if given item exists in given node. False otherwise.
     *
     * @param item
     * @param node
     * @return
     */
    public boolean itemExistsInNode(String item, EstDecNode node) {
        return node.getChildNodeByItem(item) != null;
    }

    public EstDecNode getNodeFromRootByItem(String item) {
        return getRoot().getChildNodeByItem(item);
    }

    public synchronized EstDecNode getRoot() {
        return root;
    }

    public double getD() {
        return d;
    }

    public void setD(Double d) {
        this.d = d;
    }

    public double getDk() {
        return Dk;
    }

    public void setDk(Double dk) {
        Dk = dk;
    }

    public Integer getK() {
        return k;
    }

    public void setK(Integer k) {
        this.k = k;
    }

    public double getMinSupport() {
        return smin;
    }

    public double getDelayedInsertionThreshold() {
        return sins;
    }

    public double getPruningThreshold() {
        return sprn;
    }

    public void setRoot(EstDecNode root) {
        this.root = root;
    }

    public double getSmin() {
        return smin;
    }

    public double getSins() {
        return sins;
    }

    public double getSprn() {
        return sprn;
    }
}
