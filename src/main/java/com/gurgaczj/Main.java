package com.gurgaczj;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Sets;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        System.out.println("Start: " + LocalDateTime.now().toString() + "\n");
        Set<String[]> itemSets = new LinkedHashSet<>();
        itemSets.add(new String[]{"1", "2"});
        itemSets.add(new String[]{"2", "4"});
        itemSets.add(new String[]{"2", "4"});
        itemSets.add(new String[]{"12", "24", "36"});
        itemSets.add(new String[]{"12", "24", "36"});
        itemSets.add(new String[]{"12", "24", "36"});
        itemSets.add(new String[]{"12", "24", "36"});
        itemSets.add(new String[]{"2", "4"});


        String[] words = new String[]{"amet", "consectetur", "adipiscing"};
        //"Lorem","ipsum","dolor","sit","elit.","Pellentesque","eget","quam","nec","ligula","faucibus","aliquam","ac","vitae","turpis.","Cras","nec","sodales","ligula,","ut","porta","diam"
        Random random = new Random();
        int wordsArrayLength = words.length;


        int percentageSins = 10;
        int percentageSprn = 10;

        double smin = 0.1;
        double sins = smin * (percentageSins / 100.0);
        double sprn = smin * (percentageSprn / 100.0);
        System.out.println("sins = " + new BigDecimal(sins).toString() + ", sprn = " + new BigDecimal(sprn).toString());

        EstDec algorithm = new EstDec(smin, sins, sprn);
        algorithm.setDecayRate(2, 10000);

        itemSets.forEach(strings -> {
            Set<Set<String>> powerSet = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList(strings)));
            List<String[]> transaction = powerSet.stream()
                    .filter(set -> set.size() != 0)
                    .map(strings1 -> strings1.toArray(new String[0]))
                    .collect(Collectors.toList());
            algorithm.processTransaction(transaction);
        });



        // finding frequent itemsets from monitoring lattice
        Set<FrequentItemset> frequentItemSets = algorithm.buildFrequentItemSets();
        List<FrequentItemset> sorted = frequentItemSets.stream().sorted((fi1, fi2) -> fi2.getCount().compareTo(fi1.getCount())).collect(Collectors.toList());
        if (sorted == null) {
            System.out.println("No frequent itemsets");
        } else {
            sorted.forEach(System.out::println);
        }

        System.out.println("\nKoniec: " + LocalDateTime.now().toString());
    }

    private static int compareLength(Object[] o1, Object[] o2) {
        if (o1.length < o2.length) {
            return -1;
        }
        if (o1.length == o2.length) {
            return 0;
        }
        return 1;
    }
}
