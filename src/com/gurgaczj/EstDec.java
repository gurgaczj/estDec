package com.gurgaczj;

public class EstDec {

    private EstDecTree estDecTree;

    public EstDec(){
        estDecTree = new EstDecTree();
    }

    public EstDec(Double minSupport, Double thresholdForPruning) {
        estDecTree = new EstDecTree(minSupport, thresholdForPruning);
    }

    void setDecayRate(double b, double h){
        estDecTree.setDecayRate(b, h);
    }

    public void processTransaction(String... transaction) {
        estDecTree.updateParams(transaction);

    }
}
