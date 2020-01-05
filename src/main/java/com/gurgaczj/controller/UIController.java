package com.gurgaczj.controller;

import com.google.common.collect.LinkedHashMultiset;
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

        sminProperty.setText("0.5");
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

    private List<String[]> generateData(int n) {
        String words = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum blandit turpis nisi, ac " +
                "suscipit est vestibulum id. Ut nec placerat ante. Aenean congue odio quis lorem suscipit venenatis. " +
                "Sed sed dui eros. Mauris venenatis lobortis ante, egestas congue dui elementum a. Fusce vulputate " +
                "mauris sapien, a viverra metus dignissim in. Phasellus eget lacinia purus. Praesent at pretium arcu, " +
                "nec luctus orci. Praesent non rhoncus tellus. Aliquam sed eros a augue scelerisque blandit.";

        String[] separated = words.split(" ");

        List<String[]> result = new ArrayList<>(n);
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            int arrayLength = random.nextInt(separated.length) + 1;
            if (arrayLength > 30) {
                arrayLength = 30;
            }
            String[] transaction = new String[arrayLength];
            for (int j = 0; j < arrayLength; j++) {
                int index = random.nextInt(separated.length);
                transaction[j] = separated[index];
            }
            result.add(transaction);
        }
        appendToLogArea("Ilość wygenerowanych danych = " + result.size());
        return result;
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
            System.out.println("regex = #" + regex + "#");
        } else {
            appendToLogArea("Nie podano znaku podziału danych");
            return;
        }

        new Thread(() -> {
            try {
                FileReader fileReader = new FileReader(csv);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                List<List<String>> fileContent = new ArrayList<>();
                appendToLogArea("Rozpoczęto czytanie pliku");
                while ((line = bufferedReader.readLine()) != null) {

                    String[] transaction = line.split(regex);
                    fileContent.add(Arrays.asList(transaction));
                    //algorithm.processTransaction(Arrays.asList(transaction));
//                    Set<Set<String>> powerSet = Sets.powerSet(Sets.newLinkedHashSet(Arrays.asList(transaction)));
//                    List<Set<String>> filteredPowerSet = powerSet.stream()
//                            .filter(set -> set.size() != 0)
//                            .collect(Collectors.toList());
//                    filteredPowerSet.forEach(algorithm::processTransaction);
//                    algorithm.processTransaction(filteredPowerSet);
                }
                bufferedReader.close();
                fileReader.close();
                appendToLogArea("Rozpoczęto analize");
                fileContent.forEach(algorithm::processTransaction);
                appendToLogArea("Zakonczono analize");
            } catch (IOException e) {
                e.printStackTrace();
                appendToLogArea("Błąd podczas odczytywania pliku");
            }
        }).start();


//               List<Collection<String>> transactions = new ArrayList<>();
//        transactions.add(Arrays.asList("1", "2"));
//        transactions.add(Arrays.asList("2", "4"));
//        transactions.add(Arrays.asList("2", "4"));
//        LinkedHashMultiset<Set<String>> transactionsSet = new LinkedHashSet<>();
//        transactionsSet.add(new LinkedHashSet<>(Arrays.asList("1", "2")));
//        transactionsSet.add(new LinkedHashSet<>(Arrays.asList("2", "4")));
//        transactionsSet.add(new LinkedHashSet<>(Arrays.asList("2", "4")));
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"12", "24", "36"});
//        transactions.add(new String[]{"2", "4"});

//        boolean selected = countErrorBox.isSelected();
//
//        System.out.println(selected);
//
        // List<String[]> transactions = generateData(15000);
//        appendToLogArea("Rozpoczęto analizę");
//
//        new Thread(() -> {
//            for (Collection<String> transaction :
//                    transactions) {
//                algorithm.processTransaction(transaction);
//            }
//            appendToLogArea("Zakończono analizę");
//        }).start();

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
            fiTable.getItems().addAll(sorted);
            frequentItemSets.clear();
            sorted.clear();
        }).start();
    }

    @FXML
    private void clearLogs() {
        logArea.clear();
    }

    public void appendToLogArea(String msg) {
        logArea.appendText(LocalDateTime.now().toString().replace("T", " ").substring(0, 19)
                + " " + msg + "\n");
    }
}
