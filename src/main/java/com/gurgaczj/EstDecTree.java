package com.gurgaczj;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Hashtable;

public class EstDecTree {

    private static final Logger logger = LogManager.getLogger("EstDecTree");

    private Double d; // decay rate
    private Double Dk; // |D|k
    private Integer k; // actual transaction id
    private final Double smin; // minimum Support
    private final Double sins; // Sins
    private final Double sprn;

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

    /**
     * @param itemSet
     */
    boolean updateParams(String[] itemSet) {
        // parameter updating phase
        Dk = Dk * d + 1;
        k++;

        EstDecNode currentNode = getRoot();

        for (String item : itemSet) {
            EstDecNode tempNode = currentNode.getChildNodeByItem(item);
            if (tempNode == null) {
                //itemSet do not exist in ML, skipping
                return true;
            }
            tempNode.updateCount(d, k);

            // pruning
            if (tempNode.calculateSupport(Dk) < sprn && itemSet.length > 1) {
                currentNode.getChildrens().remove(tempNode);
                return false;
            }
            currentNode = tempNode;
        }
        return false;
    }

    /**
     * Inserts ItemSet to monitoring lattice (ML)
     *
     * @param itemSet
     */
    public void insertItemSet(String[] itemSet) {

        EstDecNode currentNode = getRoot();
        //double count = estimateCount(itemSet, 0);
        //double calculatedSupport = calculateSupport(count);

        for (String item : itemSet) {
            EstDecNode tempNode = currentNode.getChildNodeByItem(item);
            if (tempNode != null) {
                currentNode = tempNode;
            } else { // tempNode == null
                if (itemSet.length == 1) {
                    currentNode = currentNode.addChild(new EstDecNode(item, k));
                } else { // itemSet.length != 1
                    double count = estimateCount(itemSet, 0);
                    if (calculateSupport(count) >= sins) {
                        currentNode = currentNode.addChild(new EstDecNode(item, k, count));
                    }
                }
            }
        }
    }

    public void insertItemSet(EstDecNode node, String[] itemSet, int index) {
        if (index == itemSet.length) {
            return;
        }
        EstDecNode childNode = node.getChildNodeByItem(itemSet[index]);
        EstDecNode newNode;
        if (childNode == null) {
            if (itemSet.length == 1) {
                newNode = node.addChild(new EstDecNode(itemSet[index], k, 0));
                insertItemSet(newNode, itemSet, ++index);
            } else { // itemSet.length != 1
                double count = estimateCount(itemSet, 0);
                if (calculateSupport(count) >= sins) {
                    newNode = node.addChild(new EstDecNode(itemSet[index], k, count));
                    insertItemSet(newNode, itemSet, ++index);
                }
            }
        } else {
            insertItemSet(childNode, itemSet, ++index);
        }
    }

    private double calculateSupport(double count) {
        return count / Dk;
    }

    /**
     * Estimates itemSet count
     *
     * @param itemSet
     * @param index
     * @return
     */
    private double estimateCount(String[] itemSet, int index) {
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
        return (1 - Math.pow(d, length)) / (1 - d);
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
     * Adds item to node
     *
     * @param item
     * @param k
     * @param node
     * @return
     */
    public EstDecNode addItemToNode(String item, int k, EstDecNode node) {
        return node.addChild(new EstDecNode(item, k));
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

    public Double getD() {
        return d;
    }

    public void setD(Double d) {
        this.d = d;
    }

    public Double getDk() {
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

    public Double getMinSupport() {
        return smin;
    }

    public Double getDelayedInsertionThreshold() {
        return sins;
    }

    public Double getPruningThreshold() {
        return sprn;
    }

    public void setRoot(EstDecNode root) {
        this.root = root;
    }

    public Double getSmin() {
        return smin;
    }

    public Double getSins() {
        return sins;
    }

    public Double getSprn() {
        return sprn;
    }
}
