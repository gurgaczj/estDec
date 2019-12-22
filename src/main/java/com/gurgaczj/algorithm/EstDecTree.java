package com.gurgaczj.algorithm;

import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    Hashtable<Double, String[]> createFrequentItemSets(EstDecNode parentNode, String[] itemSet, Hashtable<Double, String[]> frequentItemSets) {
        String[] tempItems = new String[itemSet.length + 1];
        System.arraycopy(itemSet, 0, tempItems, 0, itemSet.length);
        for (Map.Entry<String, EstDecNode> childNode : parentNode.getChildrens().entrySet()) {
            tempItems[itemSet.length] = childNode.getValue().getItem();
            childNode.getValue().updateCount(d, k);
            double nodeSupport = childNode.getValue().calculateSupport(Dk);
            if (nodeSupport >= smin) {
                frequentItemSets.put(nodeSupport, tempItems);
                // should continue?
                continue;
            }
            frequentItemSets.putAll(createFrequentItemSets(childNode.getValue(), tempItems, frequentItemSets));
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
                //System.out.println("Pruning dla " + tempNode.getItem());
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
     * @param itemSet
     * @param index
     */
    public void insertItemSet(String[] itemSet, int index) {
        int itemSetLength = itemSet.length;
        EstDecNode currentNode = getRoot();
        for (String s : itemSet) {
            EstDecNode childNode = currentNode.getChildNodeByItem(s);
            if (childNode == null) {
                if (itemSetLength == 1) {
                    currentNode.addChild(new EstDecNode(s, k, 1));
                    break;
                } else {
                    double cMax = estimateCMax(itemSet);
                    if (calculateSupport(cMax) >= sins) {
                        double cMin = estimateCMin(itemSet);
                        currentNode = currentNode.addChild(new EstDecNode(s, k, cMax, cMin));
                        index++;
                    }
                }
            } else {
                index++;
                currentNode = childNode;
            }
        }
    }

    private double estimateCMin(String[] itemSet) {
        int mSubsetLength = itemSet.length - 1;

        List<Set<String>> mSubsets = Collections.synchronizedList(new ArrayList<>());
        Set<Set<String>> powerSet = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList(itemSet)));
        Iterator<Set<String>> iterator = powerSet.iterator();
        Set<String> subSet;
        while (iterator.hasNext()){
            subSet = iterator.next();
            if(subSet.size() == mSubsetLength){
                mSubsets.add(subSet);
            }
        }
//        for(Set<String> set : powerSet){
//            if(set.size() == mSubsetLength){
//                mSubsets.add(set);
//            }
//        }
//                .stream()
//                .filter(strings -> strings.size() == itemsetLength - 1)
//                .collect(Collectors.toList());

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

    private double calculateSupport(double count) {
        return count / this.Dk;
    }

    /**
     * Estimates itemSet count
     *
     * @param itemSet
     * @return
     */
    private double estimateCMax(String[] itemSet) {
        double cMax = Double.MAX_VALUE;
        int itemsetLength = itemSet.length;
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
    private double getItemSetCountWithoutItemAtIndex(String[] itemSet, int index) {
        EstDecNode currentNode = getRoot();
        int itemSetSize = itemSet.length;
        for (int i = 0; i < itemSetSize; i++) {
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
