package com.gurgaczj.controller;

import com.gurgaczj.algorithm.EstDec;
import com.gurgaczj.model.FrequentItemset;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UIController {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<FrequentItemset> fiTable;
    @FXML
    private TableColumn<FrequentItemset, Set<String>> fiColumn;
    @FXML
    private TableColumn<FrequentItemset, String> fiSupportColumn;
    @FXML
    private TableColumn<FrequentItemset, String> fiErrorColumn;
    @FXML
    private TextField bProperty;
    @FXML
    private TextField hProperty;
    @FXML
    private TextField sMinProperty;
    @FXML
    private TextField sInsProperty;
    @FXML
    private TextField sPrnProperty;
    @FXML
    private Button selectFileButton;
    @FXML
    private Button mineFIButton;
    @FXML
    private TextArea logArea;

    private EstDec algorithm;
    boolean isMining = false;

    @FXML
    public void initialize() {
        logArea.setEditable(false);
        fiColumn.setCellValueFactory(new PropertyValueFactory<>("itemset"));
        fiSupportColumn.setCellValueFactory(new PropertyValueFactory<>("support"));
        fiErrorColumn.setCellValueFactory(new PropertyValueFactory<>("error"));

        selectFileButton.setOnAction(event -> insertFiFromFile());

        mineFIButton.setOnAction(event -> mineFis());
    }

    private void insertFiFromFile() {
        if (isMining){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Proszę czekać");
            alert.setHeaderText("Proszę czekać");
            alert.setContentText("Proszę czekać, trwa analizowanie");
            alert.showAndWait();
            return;
        }
        try {
            initEstDec();
            appendToLogArea("d = " + algorithm.getD());
        } catch (IllegalArgumentException e) {
            appendToLogArea(e.getMessage());
            return;
        }

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV File (*.csv)", "*.csv");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik .csv");
        fileChooser.getExtensionFilters().add(extensionFilter);

        File csv = fileChooser.showOpenDialog(rootPane.getScene().getWindow());

        if(csv == null){
            appendToLogArea("Nie wybrano pliku");
            return;
        }
        
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
        isMining = true;
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
                }
                bufferedReader.close();
                fileReader.close();
                appendToLogArea("Rozpoczęto analize");
                fileContent.forEach(strings -> {
                    this.algorithm.processTransaction(strings);
                });
                isMining = false;
                mineFis();
                appendToLogArea("Zakonczono analize");
            } catch (Exception e) {
                e.printStackTrace();
                isMining = false;
                appendToLogArea("Błąd podczas odczytywania pliku");
                return;
            }
        }).start();
    }

    private void initEstDec() throws IllegalArgumentException {
        double bPropertyVal;
        try {
            bPropertyVal = Double.parseDouble(bProperty.getText());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Podaj wartość dla parametru b");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Zły format dla parametru b");
        }

        if(bPropertyVal <= 1){
            throw new IllegalArgumentException("Wartość parametru b jest mniejsza lub równa 1");
        }

        double hPropertyVal;
        try {
            hPropertyVal = Double.parseDouble(hProperty.getText());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Podaj wartość dla parametru h");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Zły format dla parametru h");
        }

        if(hPropertyVal < 1){
            throw new IllegalArgumentException("Wartość parametru h jest mniejsza od 1");
        }

        double d = Math.pow(bPropertyVal, -1 / hPropertyVal);

        if(!(Math.pow(bPropertyVal, -1) <= d) && !(d < 1)){
            throw new IllegalArgumentException("Wartości parametrów b i h nie są zgodne dla (b^-1) <= d < 1");
        }

        double sMin;
        try {
            sMin = Double.parseDouble(sMinProperty.getText());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Podaj wartość dla sMin");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Zły format dla sMin");
        }

        double sIns;
        try {
            sIns = Double.parseDouble(sInsProperty.getText());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Podaj wartość dla sIns");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Zły format dla sIns");
        }
        
        if(sIns >= sMin){
            throw new IllegalArgumentException("Sins powinno być mniejsze od Smin");
        }

        double sPrn;
        try {
            sPrn = Double.parseDouble(sPrnProperty.getText());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("Podaj wartość dla sPrn");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Zły format dla sPrn");
        }
        
        if(sPrn >= sMin){
            throw new IllegalArgumentException("Sprn powinno być mniejsze od Smin");
        }      

        this.algorithm = new EstDec(sMin, sIns, sPrn);

        this.algorithm.setDecayRate(bPropertyVal, hPropertyVal);
    }

    @FXML
    private void mineFis() {
        fiTable.getItems().clear();
        new Thread(() -> {
            Set<FrequentItemset> frequentItemSets = algorithm.buildFrequentItemSets();

            if (frequentItemSets.isEmpty() || frequentItemSets == null) {
                appendToLogArea("Brak zbiorów częstych");
                return;
            }
            List<FrequentItemset> sorted = frequentItemSets.stream().sorted((fi1, fi2) -> fi2.getSupport().compareTo(fi1.getSupport())).collect(Collectors.toList());
            appendToLogArea("Liczba zbiorów częstych wynosi: " + sorted.size());
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
