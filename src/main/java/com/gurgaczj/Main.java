package com.gurgaczj;

import com.google.common.collect.LinkedHashMultimap;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        //TODO: more transactions
        List<String[]> itemSets = Arrays.asList(new String[]{"asd", "sdaf"},
                new String[]{"asd", "fas"},
                new String[]{"sdaf", "sdf"},
                new String[]{"wegerg"},
                new String[]{"asd", "fas", "sd"},
                new String[]{"asd", "sdaf"},
                new String[]{"asd", "sdaf"},
                new String[]{"asd", "sdaf"},
                new String[]{"asd", "sdaf"});

        String[] words = new String[]{"amet","consectetur","adipiscing"};
        //"Lorem","ipsum","dolor","sit","elit.","Pellentesque","eget","quam","nec","ligula","faucibus","aliquam","ac","vitae","turpis.","Cras","nec","sodales","ligula,","ut","porta","diam"
        Random random = new Random();
        int wordsArrayLength = words.length;


        Integer percentageSins = 30;
        Integer percentageSprn = 30;

        Double smin = 0.1;
        Double sins = smin * (percentageSins/100);
        Double sprn = smin * (percentageSprn/100);

        EstDec algorithm = new EstDec(smin, sins, sprn);
        algorithm.setDecayRate(2, 10000);

        //itemSets.forEach(algorithm::processTransaction);

        for(int i = 0; i < 30; i++){
            int arrayLength = random.nextInt(5)+1;
            if(arrayLength == 0){
                continue;
            }
            String[] wordsArray = new String[arrayLength];
            for (int j = 0; j < wordsArray.length; j++){
                wordsArray[j] = words[random.nextInt(wordsArrayLength)];
            }
            algorithm.processTransaction(wordsArray);

        }


        System.out.println("Rozpoczęto: " + LocalDateTime.now().toString() + "\n");
        // displaying items in tree
        LinkedHashMultimap<Double, String[]> s = algorithm.buildFrequentItemSets();
//        List<String> itemsets = s.stream().map(strings -> {
//            String items = "";
//            for(int i = 0; i < strings.length; i++){
//                items += strings[i] + " ";
//            }
//            return items;
//        }).sorted().collect(Collectors.toList());
        s.forEach((aDouble, strings) -> {
            StringBuilder frequentItemSet = new StringBuilder("Wsparcie: ");
            frequentItemSet.append(aDouble);
            frequentItemSet.append(", itemSet: ");
            for(int i = 0; i < strings.length; i++){
                frequentItemSet.append(strings[i]);
                frequentItemSet.append(", ");
            }
            System.out.println(frequentItemSet.toString());
        });
//        s.forEach(strings -> {
//            String items = "";
//            for(int i = 0; i < strings.length; i++){
//                items += strings[i] + " ";
//            }
//            System.out.println(items);
//        });
//        for(Map.Entry<Double, String[]> entry : s.entrySet()){
//            StringBuilder stringBuilder = new StringBuilder(entry.getKey().toString() + " {");
//            System.out.println("value size: " + entry.getValue().length);
//            for(String item: entry.getValue()){
//                stringBuilder.append(item + ", ");
//            }
//            System.out.println(stringBuilder.toString());
//        }
        System.out.println("Rozmiar: " + s.size());
        System.out.println("\nZakończono: " + LocalDateTime.now().toString());
    }
}
