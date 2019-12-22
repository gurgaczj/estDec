package com.gurgaczj;

import com.google.common.collect.Sets;
import com.gurgaczj.algorithm.EstDec;
import com.gurgaczj.algorithm.FrequentItemset;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {

    final static String appTitle = "EstDec";

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui.fxml"));
        Scene scene = new Scene(root, 600, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle(appTitle);
        primaryStage.show();
    }
}
