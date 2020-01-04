package com.gurgaczj.algorithm;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

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

    public EstDecTree(double smin, double sprn, double sins) {
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

    void updateParam() {
        // parameter updating phase
        this.Dk = Dk * d + 1;
        this.k++;
    }

    void updateCount(Set<String> itemSet) {
        //count updating phase
        EstDecNode currentNode = getRoot();
        int itemsetSize = itemSet.size();
        Iterator<String> itemSetIterator = itemSet.iterator();
        while(itemSetIterator.hasNext()){
            String item = itemSetIterator.next();
            EstDecNode tempNode = currentNode.getChildNodeByItem(item);
            if (tempNode == null) {
                //itemSet do not exist in ML, skipping
                return;
            }
            if (!itemSetIterator.hasNext()) {
                tempNode.updateCount(d, k);
            }

            if (tempNode.calculateSupport(Dk) < sprn && itemsetSize > 1) {
                //System.out.println("Pruning dla " + tempNode.getItem());
                currentNode.getChildrens().remove(tempNode);
                return;
            }
            currentNode = tempNode;
        }
    }

    public void insertItemSet(Set<String> itemSet) {
        int itemSetLength = itemSet.size();
        EstDecNode currentNode = getRoot();
        for (String item : itemSet) {
            EstDecNode childNode = currentNode.getChildNodeByItem(item);
            if (childNode == null) {
                if (itemSetLength == 1) {
                    currentNode.addChild(item, new EstDecNode(k, 1));
                    break;
                } else {
                    double cMax = estimateCMax(itemSet);
                    if (calculateSupport(cMax) >= sins) {
                        double cMin = estimateCMin(itemSet);
                        currentNode = currentNode.addChild(item, new EstDecNode(k, cMax, cMin));
                    }
                }
            } else {
                currentNode = childNode;
            }
        }
    }

    private double estimateCMin(Set<String> itemSet) {

        List<Set<String>> mSubsets = new ArrayList<>();

        String[] array = itemSet.toArray(new String[0]);
        for(int i = 0; i < array.length; i++) {
            mSubsets.add(getMSubset(array, i));
        }

        Map<Set<String>, Set<String>> distinctPairs = getDistinctPairs(mSubsets);

        double cMin = 0.0;

        for (Map.Entry<Set<String>, Set<String>> entry : distinctPairs.entrySet()) {
            double countOfUnionItemset = calculateCountOfUnionItemset(entry);
            if (countOfUnionItemset > cMin) {
                cMin = countOfUnionItemset;
            }
        }
        return cMin;
    }

    private Set<String> getMSubset(String[] array, int index) {
        Set<String> result = new LinkedHashSet<>();
        int arrayLength = array.length;
        for(int i = 0; i < arrayLength; i++){
            if(i == index)
                continue;

            result.add(array[i]);
        }
        return result;
    }

    private double calculateCountOfUnionItemset(Set<String> subset1, Set<String> subset2) {
        Sets.SetView<String> intersection = Sets.intersection(subset1, subset2);
        double e1Count = getCountOfItemset(subset1);
        double e2Count = getCountOfItemset(subset2);
        double count;
        if (intersection.isEmpty()) {
            count = e1Count + e2Count - getK();
        } else {
            double interSectionCount = getCountOfItemset(intersection);
            count = e1Count + e2Count - interSectionCount;
        }
        return Math.max(count, 0.0);
    }

    private double calculateCountOfUnionItemset(Map.Entry<Set<String>, Set<String>> entry) {
        Sets.SetView<String> intersection = Sets.intersection(entry.getKey(), entry.getValue());
        double e1Count = getCountOfItemset(entry.getKey());
        double e2Count = getCountOfItemset(entry.getValue());
        double count;
        if (intersection.isEmpty()) {
            count = e1Count + e2Count - getK();
        } else {
            double interSectionCount = getCountOfItemset(intersection);
            count = e1Count + e2Count - interSectionCount;
        }
        return Math.max(count, 0.0);
    }

    private double getCountOfItemset(String[] itemset) {
        EstDecNode node = getRoot();
        int itemsetSize = itemset.length;
        for (int i = 0; i < itemsetSize; i++) {
            node = node.getChildNodeByItem(itemset[i]);
            if (i == itemsetSize - 1) {
                return node.getCounter();
            }
        }
        return 0;
    }

    private double getCountOfItemset(Set<String> itemSet){
        EstDecNode node = getRoot();
        Iterator<String> iterator = itemSet.iterator();
        String item;
        while(iterator.hasNext()){
            item = iterator.next();
            node = node.getChildNodeByItem(item);
            if(node == null)
                return 0;

            if(!iterator.hasNext())
                return node.getCounter();
        }
        return 0;
    }

    private Map<Set<String>, Set<String>> getDistinctPairs(List<Set<String>> mSubsets) {
        Map<Set<String>, Set<String>> distinctPairs = new LinkedHashMap<>();
        for (int i = 0; i < mSubsets.size(); i++) {
            for (int j = i + 1; j < mSubsets.size(); j++) {
                distinctPairs.put(mSubsets.get(i), mSubsets.get(j));
            }
        }
        return distinctPairs;
    }

    public Set<FrequentItemset> recursiveItemsetBuilding(String item, EstDecNode estDecNode, String[] items, Set<FrequentItemset> itemsSet) {
        estDecNode.updateCountForSelectionPhase(getD(), getK());
        double support = estDecNode.calculateSupport(getDk());
        int itemsetSize = items.length;
        String[] tempItems = new String[itemsetSize + 1];
        if (support >= getSmin()) {
            System.arraycopy(items, 0, tempItems, 0, itemsetSize);
            tempItems[itemsetSize] = item;
            itemsSet.add(new FrequentItemset(support, tempItems, (estDecNode.getError()/getDk())));
        }

        for (Map.Entry<String, EstDecNode> childNode : estDecNode.getChildrens().entrySet()) {
            itemsSet = recursiveItemsetBuilding(childNode.getKey(), childNode.getValue(), tempItems, itemsSet);
        }

        return itemsSet;
    }

    private double calculateSupport(double count) {
        return count / this.Dk;
    }

    /**
     * Estimates itemSet count
     *
     * @param itemSet
     * @return
     */
    private double estimateCMax(Set<String> itemSet) {
        double cMax = Double.MAX_VALUE;
        int itemsetLength = itemSet.size();
        for (int i = 0; i < itemsetLength; i++) {
            double count = getItemSetCountWithoutItemAtIndex(itemSet, i);
            if (count < cMax) {
                cMax = count;
            }
        }
        double cUpper = calculateCountForSubsets(itemsetLength) + calculateMaxCountBeforeSubsets(itemsetLength);
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
    private double getItemSetCountWithoutItemAtIndex(Set<String> itemSet, int index) {
        EstDecNode currentNode = getRoot();
        int itemSetSize = itemSet.size();
        int i = 0;
        for (String item : itemSet) {
            if (i != index) {
                EstDecNode childNode = currentNode.getChildNodeByItem(item);
                if (childNode == null) {
                    return 0;
                }
                currentNode = childNode;
            }
            i++;
        }
        try {
            return currentNode.getCounter();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    public void forcePruning(EstDecNode node) {
        for(Map.Entry<String, EstDecNode> entry: node.getChildrens().entrySet()){
            double support = entry.getValue().calculateSupport(getDk());
            if(support < getSprn()){
                node.getChildrens().remove(entry.getKey());
            } else {
                forcePruning(entry.getValue());
            }
        }
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
