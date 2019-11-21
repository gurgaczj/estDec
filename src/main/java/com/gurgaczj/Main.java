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
//        double b = 2;
//        double h = 10000;
//
//        double d = Math.pow(b, -(1/h));
//        System.out.println("d = " + d);
//
//        double DkBefore = 1.99993;
//
//        double Dk = DkBefore * d + 1;
//
//        System.out.println("|Dk| = " + Dk);
//
//        int e = 3;
//        double sMin = 0.001;
//        int percent = 10;
//        double sIns = sMin * (percent/100.0);
//        BigDecimal bigDecimal = new BigDecimal(sIns);
//        double sPrn = sIns;
//
//        System.out.println("sPrn = sIns = " + bigDecimal.toString());
//
//        int k = 2;
//        int eMinusOne = e - 1;
//
//        double cUpper = (sIns * calcDk(k - eMinusOne, d) * Math.pow(d, eMinusOne)) + ((1 - Math.pow(d, e - 1))/(1 - d));
//
//        System.out.println("Cupper = " + cUpper);
//
//        double cMax = 0.0;
//        double cMin = 0.0;
//
//        if(cMax > cUpper){
//            cMax = cUpper;
//        }
//
//        double calculatedSupport = calcSupp(cMax, Dk);
//        System.out.println("Obliczone wsparcie dla wstawiania = " + calculatedSupport);
//        if(calculatedSupport >= sIns){
//            System.out.println("Wstawiamy");
//        } else {
//            System.out.println("Nie wstawiamy");
//        }
//
//        Set<String> set = new HashSet<>();
//        set.addAll(Arrays.asList("1", "2"));
//        Set<Set<String>> powerSet = Sets.powerSet(set);
//        List<String[]> arrayStringSet = powerSet.stream()
//                .map(strings -> strings.toArray(new String[strings.size()]))
//                .filter(array -> array.length != 0)
//                .sorted(Main::compareLength).collect(Collectors.toList());
//
//
//        arrayStringSet.forEach(strings -> {
//            System.out.print("[ ");
//            for(int i = 0; i < strings.length; i++){
//                System.out.print(strings[i] + " ");
//            }
//            System.out.print("]");
//            System.out.println();
//        });
//
//        System.out.println(1* Math.pow(d, 3-1));

        //powerSet.forEach(System.out::println);

//        long beforeUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
//        //TODO: more transactions
//
        System.out.println(new BigDecimal(0.001 * (10/100d)));
        ArrayList<String[]> itemSets = new ArrayList<>();
                Arrays.asList(
                new String[]{"1", "2"},
                        new String[]{"2", "4"},
                        new String[]{"2", "4"}
        );
//        itemSets.add(new String[]{"2", "3"});
//        for(int i = 0; i < 10001; i++){
//            itemSets.add(new String[]{"1", "2", "3"});
//        }
//        itemSets.add(new String[]{"2", "3"});
//        itemSets.add(new String[]{"2", "3"});
//        itemSets.add(new String[]{"2", "3"});

        String[] words = new String[]{"amet", "consectetur", "adipiscing"};
        //"Lorem","ipsum","dolor","sit","elit.","Pellentesque","eget","quam","nec","ligula","faucibus","aliquam","ac","vitae","turpis.","Cras","nec","sodales","ligula,","ut","porta","diam"
        Random random = new Random();
        int wordsArrayLength = words.length;


        int percentageSins = 10;
        int percentageSprn = 10;

        double smin = 0.001;
        double sins = smin * (percentageSins / 100.0);
        double sprn = smin * (percentageSprn / 100.0);
        System.out.println("sins = " + new BigDecimal(sins).toString() + ", sprn = " + new BigDecimal(sprn).toString());


        EstDec algorithm = new EstDec(smin, sins, sprn);
        algorithm.setDecayRate(2, 10000);
        //itemSets.forEach(algorithm::processTransaction);
        AtomicInteger asd = new AtomicInteger(0);
        itemSets.forEach(strings -> {
            //System.out.println("Transakcja = "+ asd);
            //algorithm.processTransaction(strings);
            Set<Set<String>> powerSet = Sets.powerSet(Sets.newHashSet(strings));
            List<String[]> arrayStringSet = powerSet.stream()
                .map(strings1 -> strings1.toArray(new String[strings1.size()]))
                .filter(array -> array.length != 0)
                .sorted(Main::compareLength).collect(Collectors.toList());

            arrayStringSet.forEach(algorithm::processTransaction);
            //asd.incrementAndGet();
        });

        //long totalMem = Runtime.getRuntime().totalMemory();
        //System.out.println("Wolna pamięć przed generowaniem: " + (totalMem - Runtime.getRuntime().freeMemory()));
//        for (int i = 0; i < 100; i++) {
//            int arrayLength = random.nextInt(100) + 1;
//            if (arrayLength == 0) {
//                continue;
//            }
//            String[] wordsArray = new String[arrayLength];
//            for (int j = 0; j < wordsArray.length; j++) {
//                wordsArray[j] = words[random.nextInt(wordsArrayLength)];
//            }
//            algorithm.processTransaction(wordsArray);
//
//        }
        //System.out.println("Wolna pamięć po generowaniu: " + (totalMem - Runtime.getRuntime().freeMemory()));
        System.out.println("Start: " + LocalDateTime.now().toString() + "\n");
        // finding frequent itemsets from monitoring lattice
        LinkedHashMultimap<Double, String[]> frequentItemSets = algorithm.buildFrequentItemSets();
        if (frequentItemSets == null) {
            System.out.println("No frequent itemsets");
        } else {
            frequentItemSets.forEach((aDouble, strings) -> {
                StringBuilder frequentItemSet = new StringBuilder("Itemset support: ");
                frequentItemSet.append(aDouble);
                frequentItemSet.append(", items: ");
                for (int i = 0; i < strings.length; i++) {
                    frequentItemSet.append(strings[i]);
                    frequentItemSet.append(", ");
                }
                System.out.println(frequentItemSet.toString());
            });
            System.out.println("Size of set of frequent itemsets: " + frequentItemSets.size());
        }

        System.out.println("\nEnd: " + LocalDateTime.now().toString());
    }

    private static int compareLength(Object[] o1, Object[] o2){
        if(o1.length < o2.length){
            return -1;
        }
        if(o1.length == o2.length){
            return 0;
        }
        return 1;
    }

    private static double calcSupp(double cMax, double dk) {
        return cMax / dk;
    }

    private static double calcDk(int k, double d) {
        return (1 - Math.pow(d, k))/(1 - d);
    }
}
