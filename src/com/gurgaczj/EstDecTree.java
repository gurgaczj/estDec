package com.gurgaczj;

public class EstDecTree {

    private static Double d; // decay rate
    private Double Dk; // |D|k - number of transactions
    private Integer k; // actual transaction id
    private final Double minSupport;
    private final Double thresholdForPruning;

    private EstDecNode root;

    EstDecTree() {
        this.minSupport = 0.0;
        this.thresholdForPruning = 0.0;

        Dk = 0.0;
        k = 0;

        root = new EstDecNode();
    }

    public EstDecTree(Double minSupport, Double thresholdForPruning) {
        this.minSupport = minSupport;
        this.thresholdForPruning = thresholdForPruning;

        Dk = 0.0;
        k = 0;

        root = new EstDecNode();
    }

    void setDecayRate(Double b, Double h){
        d = Math.pow(b, -1/h);
    }

    void updateParams(String[] transaction){
        // parameter updating phase
        Dk = Dk * d + 1;
        k++;

        // count updating phase
        for (String item :
                transaction) {
            EstDecNode itemNode = getRoot().getChildNodeByItem(item);
            if(itemNode == null){
                continue;
            }

            itemNode.updateCount(d, k);

            // pruning
            if(itemNode.calculateSupport(Dk) < thresholdForPruning && transaction.length > 1){
                root.getChildrens().remove(itemNode);
            }
        }
    }

    public EstDecNode getRoot() {
        return root;
    }

    public static Double getD() {
        return d;
    }

    public static void setD(Double d) {
        EstDecTree.d = d;
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
        return minSupport;
    }

    public Double getThresholdForPruning() {
        return thresholdForPruning;
    }

    public void setRoot(EstDecNode root) {
        this.root = root;
    }
}
