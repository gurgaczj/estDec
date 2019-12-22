package com.gurgaczj.controller;

import com.google.common.collect.Sets;
import com.gurgaczj.algorithm.EstDec;
import com.gurgaczj.algorithm.FrequentItemset;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class UIController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<FrequentItemset> fiTable;
    @FXML
    private TableColumn<FrequentItemset, String[]> fiColumn;
    @FXML
    private TableColumn<FrequentItemset, Double> fiSupportColumn;
    @FXML
    private TableColumn<FrequentItemset, Double> fiErrorColumn;
    @FXML
    private TextField bProperty;
    @FXML
    private TextField hProperty;
    @FXML
    private TextField sminProperty;
    @FXML
    private TextField percentage;
    @FXML
    private Button selectFileButton;
    @FXML
    private CheckBox countErrorBox;

    private EstDec algorithm;

    @FXML
    public void initialize() {
        fiColumn.setCellValueFactory(new PropertyValueFactory<>("itemsetString"));
        fiSupportColumn.setCellValueFactory(new PropertyValueFactory<>("support"));
        fiErrorColumn.setCellValueFactory(new PropertyValueFactory<>("error"));

        sminProperty.setText("0.05");
        bProperty.setText("2");
        hProperty.setText("10000");
        percentage.setText("10");

        double smin = Double.parseDouble(sminProperty.getText());
        double sins = smin * (Integer.parseInt(percentage.getText()) / 100.0);
        double sprn = smin * (Integer.parseInt(percentage.getText()) / 100.0);

        this.algorithm = new EstDec(smin, sins, sprn);

        this.algorithm.setDecayRate(Double.parseDouble(bProperty.getText()), Double.parseDouble(hProperty.getText()));

        selectFileButton.setOnAction(event -> insertFiFromFile());
    }

    public void initEstDecAlgorithm() {
        double smin = Double.parseDouble(sminProperty.getText());
        double sins = smin * (Integer.parseInt(percentage.getText()) / 100.0);
        double sprn = smin * (Integer.parseInt(percentage.getText()) / 100.0);

        this.algorithm = new EstDec(smin, sins, sprn);

        this.algorithm.setDecayRate(Double.parseDouble(bProperty.getText()), Double.parseDouble(hProperty.getText()));
    }

    private void insertFiFromFile() {
        //TODO: file chooser

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV File (*.csv)", "*.csv");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik .csv");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File csv = fileChooser.showOpenDialog(rootPane.getScene().getWindow());


        String regex;
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Podaj delimiter");
        textInputDialog.setHeaderText("Podaj delimiter");
        textInputDialog.setContentText("Podaj znak dzielenia danych w pliku");
        Optional<String> s = textInputDialog.showAndWait();
        if (s.isPresent()) {
            regex = s.get();
        } else {
            //TODO: log error
            regex = ";";
        }


        try {
            FileReader fileReader = new FileReader(csv);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            int i = 1;
            while ((line = bufferedReader.readLine()) != null) {

                String[] transaction = line.split(regex);
                Set<Set<String>> powerSet = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList(transaction)));
                List<String[]> filteredPowerSet = powerSet.stream()
                        .filter(set -> set.size() != 0)
                        .map(strings1 -> strings1.toArray(new String[0]))
                        .collect(Collectors.toList());
                algorithm.processTransaction(filteredPowerSet);

                if (i % 10 == 0) {
                    Set<Set<String>> powerSe2t = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList("12", "24", "36")));
                    powerSe2t.forEach(System.out::println);
                    List<String[]> filteredPowerSet2 = powerSe2t.stream()
                            .filter(set -> set.size() != 0)
                            .map(strings1 -> strings1.toArray(new String[0]))
                            .collect(Collectors.toList());
                    algorithm.processTransaction(filteredPowerSet2);
                }

                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //TODO: log error
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: log error
        }

        System.gc();

//        Set<String[]> transactions = new LinkedHashSet<>();
//        transactions.add(new String[]{"1", "2"});
//        transactions.add(new String[]{"2", "4"});
//        transactions.add(new String[]{"2", "4"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"2", "4"});
//
//        boolean selected = countErrorBox.isSelected();
//
//        System.out.println(selected);
//
//        for (String[] transaction :
//                transactions) {
//            Set<Set<String>> powerSet = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList(transaction)));
//            List<String[]> filteredPowerSet = powerSet.stream()
//                    .filter(set -> set.size() != 0)
//                    .map(strings1 -> strings1.toArray(new String[0]))
//                    .collect(Collectors.toList());
//            algorithm.processTransaction(filteredPowerSet);
//        }

        mineFis();
    }

    private void mineFis() {
        new Thread(() -> {
            fiTable.getItems().clear();
            Set<FrequentItemset> frequentItemSets = algorithm.buildFrequentItemSets();
            List<FrequentItemset> sorted = frequentItemSets.stream().sorted((fi1, fi2) -> fi2.getSupport().compareTo(fi1.getSupport())).collect(Collectors.toList());
            frequentItemSets.clear();
            fiTable.getItems().addAll(sorted);
            initEstDecAlgorithm();
        }).start();
    }
}
