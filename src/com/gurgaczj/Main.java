package com.gurgaczj;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        //TODO: more transactions
        List<String[]> transacions = Arrays.asList(new String[]{"asd", "sdaf"},
                new String[]{"sdaf", "sdf"});

        Double minSupport = 0.1;
        Double thresholdForPruning = 0.01;

        EstDec algorithm = new EstDec(minSupport, thresholdForPruning);
        algorithm.setDecayRate(2, 10000);

        transacions.forEach(algorithm::processTransaction);
    }
}
