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
import java.time.LocalDateTime;
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
    @FXML
    private Button mineFIButton;
    @FXML
    private TextArea logArea;
    @FXML
    private Button clearML;

    private EstDec algorithm;
    private double sMin;

    @FXML
    public void initialize() {
        fiColumn.setCellValueFactory(new PropertyValueFactory<>("itemsetString"));
        fiSupportColumn.setCellValueFactory(new PropertyValueFactory<>("support"));
        fiErrorColumn.setCellValueFactory(new PropertyValueFactory<>("error"));

        sminProperty.setText("0.05");
        bProperty.setText("2");
        hProperty.setText("10000");
        percentage.setText("10");

        sMin = Double.parseDouble(sminProperty.getText());
        double sins = sMin * (Integer.parseInt(percentage.getText()) / 100.0);
        double sprn = sMin * (Integer.parseInt(percentage.getText()) / 100.0);

        this.algorithm = new EstDec(sMin, sins, sprn);

        this.algorithm.setDecayRate(Double.parseDouble(bProperty.getText()), Double.parseDouble(hProperty.getText()));

        selectFileButton.setOnAction(event -> insertFiFromFile());

        mineFIButton.setOnAction(event -> mineFis());

        clearML.setOnAction(event -> algorithm.getRootNode().getChildrens().clear());
    }

    private void insertFiFromFile() {
//        //TODO: file chooser
//
//        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV File (*.csv)", "*.csv");
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Wybierz plik .csv");
//        fileChooser.getExtensionFilters().add(extensionFilter);
//
//        File csv = fileChooser.showOpenDialog(rootPane.getScene().getWindow());
//
//
//        String regex;
//        TextInputDialog textInputDialog = new TextInputDialog();
//        textInputDialog.setTitle("Podaj delimiter");
//        textInputDialog.setHeaderText("Podaj delimiter");
//        textInputDialog.setContentText("Podaj znak dzielenia danych w pliku");
//        Optional<String> s = textInputDialog.showAndWait();
//        if (s.isPresent()) {
//            regex = s.get();
//            System.out.println("regex = #" + regex + "#");
//        } else {
//            appendToLogArea("Nie podano znaku podziału danych");
//            return;
//        }
//
//        new Thread(() -> {
//            try {
//                FileReader fileReader = new FileReader(csv);
//                BufferedReader bufferedReader = new BufferedReader(fileReader);
//                String line;
//                int i = 1;
//                while ((line = bufferedReader.readLine()) != null) {
//
//                    String[] transaction = line.split(regex);
//                    Set<Set<String>> powerSet = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList(transaction)));
//                    List<String[]> filteredPowerSet = powerSet.stream()
//                            .filter(set -> set.size() != 0)
//                            .map(strings1 -> strings1.toArray(new String[strings1.size()]))
//                            .collect(Collectors.toList());
//                    algorithm.processTransaction(filteredPowerSet);
//
//                    i++;
//                    if (i % 10000 == 0) {
//                        System.out.println(i);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//                appendToLogArea("Błąd podczas odczytywania pliku");
//            }
//        }).start();

        Set<String[]> transactions = new LinkedHashSet<>();
        transactions.add(new String[]{"1", "2"});
        transactions.add(new String[]{"2", "4"});
        transactions.add(new String[]{"2", "4"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"2", "4"});

        boolean selected = countErrorBox.isSelected();

        System.out.println(selected);

        for (String[] transaction :
                transactions) {
            Set<Set<String>> powerSet = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList(transaction)));
            List<String[]> filteredPowerSet = powerSet.stream()
                    .filter(set -> set.size() != 0)
                    .map(strings1 -> strings1.toArray(new String[0]))
                    .collect(Collectors.toList());
            algorithm.processTransaction(filteredPowerSet);
        }
    }

    @FXML
    private void mineFis() {
        fiTable.getItems().clear();
        new Thread(() -> {
            System.out.println(algorithm.getD() + " --- " + algorithm.getK() + " --- " + algorithm.getDk() + " --- " + sMin);
            Set<FrequentItemset> frequentItemSets = algorithm.buildFrequentItemSets();

            if (frequentItemSets.isEmpty() || frequentItemSets == null) {
                //TODO: log error
                appendToLogArea("Brak zbiorów częstych");
                return;
            }
            List<FrequentItemset> sorted = frequentItemSets.stream().sorted((fi1, fi2) -> fi2.getSupport().compareTo(fi1.getSupport())).collect(Collectors.toList());
            frequentItemSets.clear();
            frequentItemSets = null;
            fiTable.getItems().addAll(sorted);
            sorted.clear();
            sorted = null;
        }).start();
    }

    @FXML
    private void clearLogs(){
        logArea.clear();
    }

    public void appendToLogArea(String msg) {
        logArea.appendText(LocalDateTime.now().toString().replace("T", " ").substring(0, 19)
                + " " + msg + "\n");
    }
}
