package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.TetrisApp;

public class SettingsView {

    // Final buttons
    private final Slider brightnessSlider;
    private final Slider saturationSlider;
    private final Slider contrastSlider;
    private final ComboBox<String> backGroundColorBox;

    // Settings Adjustment Variables
    public static ColorAdjust visualSettings = new ColorAdjust();
    public static double brightness = 0;
    public static double saturation = 0;
    public static double contrast = 0;
    public static String backgroundColor = "#121212;";

    // Reference to TetrisView variables
    private BorderPane borderPane;

    /**
     * Constructor
     */
    public SettingsView() {
        borderPane = TetrisApp.view.borderPane;
        Stage stage = TetrisApp.view.stage;

        // Back Button
        // Buttons and Stuff
        Button backButton = new Button("Back");
        backButton.setId("Settings");
        backButton.setPrefSize(150, 50);
        backButton.setFont(new Font(12));
        backButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        // Brightness slider
        brightnessSlider = new Slider(0, 1, 0.5);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setValue(brightness + 0.5);
        brightnessSlider.setStyle("-fx-control-inner-background: palegreen;");

        // Brightness label
        Label brightnessLabel = new Label("Brightness");
        brightnessLabel.setFont(new Font(20));
        brightnessLabel.setTextFill(Color.WHITE);

        // Saturation slider
        saturationSlider = new Slider(0, 1, 0.5);
        saturationSlider.setShowTickLabels(true);
        saturationSlider.setValue(saturation + 0.5);
        saturationSlider.setStyle("-fx-control-inner-background: palegreen;");

        // Saturation label
        Label saturationLabel = new Label("Saturation");
        saturationLabel.setFont(new Font(20));
        saturationLabel.setTextFill(Color.WHITE);

        // Contrast slider
        contrastSlider = new Slider(0, 1, 0.5);
        contrastSlider.setShowTickLabels(true);
        contrastSlider.setValue(contrast + 0.5);
        contrastSlider.setStyle("-fx-control-inner-background: palegreen;");

        // Contrast label
        Label contrastLabel = new Label("Contrast");
        contrastLabel.setFont(new Font(20));
        contrastLabel.setTextFill(Color.WHITE);

        // Volume slider
        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setStyle("-fx-control-inner-background: palegreen;");

        // Volume label
        Label volumeLabel = new Label("Volume");
        volumeLabel.setFont(new Font(20));
        volumeLabel.setTextFill(Color.WHITE);

        // Background color selector
        backGroundColorBox = new ComboBox<>();
        backGroundColorBox.getItems().addAll("Default","Red", "Blue", "Green", "Yellow", "Black", "White");
        if (backgroundColor == "#121212;") {
            backGroundColorBox.setValue("Default");
        }else {
            backGroundColorBox.setValue(backgroundColor);
        }

        // Background color label
        Label backGroundColorLabel = new Label("Background Color");
        backGroundColorLabel.setFont(new Font(20));
        backGroundColorLabel.setTextFill(Color.WHITE);

        // hbox containing background color changing
        HBox colorChange = new HBox(20, backGroundColorLabel, backGroundColorBox);
        colorChange.setPadding(new Insets(20, 20, 20, 20));
        colorChange.setAlignment(Pos.CENTER);

        // vbox containing all visual settings
        VBox visualSettings = new VBox(20, backButton, brightnessLabel,
                brightnessSlider, saturationLabel, saturationSlider, contrastLabel,
                contrastSlider, volumeLabel, volumeSlider, colorChange);
        visualSettings.setPadding(new Insets(20, 20, 20, 20));
        visualSettings.setAlignment(Pos.CENTER);

        // Control settings label
        Label controlSettingsLabel = new Label("Controls");
        controlSettingsLabel.setFont(new Font(20));
        controlSettingsLabel.setTextFill(Color.WHITE);

        Button testButton = new Button("Test");
        testButton.setId("Test");
        testButton.setPrefSize(150, 50);
        testButton.setFont(new Font(12));
        testButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        //vbox containing all control settings
        VBox controlSettings = new VBox(20, controlSettingsLabel, testButton);
        controlSettings.setPadding(new Insets(20, 20, 20, 20));
        controlSettings.setAlignment(Pos.CENTER);

        //hbox containing all settings
        HBox settings = new HBox(50);
        settings.setPadding(new Insets(20, 20, 20, 20));
        settings.setAlignment(Pos.CENTER);
        settings.getChildren().addAll(visualSettings, controlSettings);

        // Implementing the controls
        backButton.setOnAction(e -> {
            TetrisApp.view.initUI();
        });

        brightnessSlider.setOnMouseReleased(e -> {
            adjustBrightness(brightnessSlider.getValue() - 0.5);
            updateSettings(borderPane);
        });

        saturationSlider.setOnMouseReleased(e -> {
            adjustSaturation(saturationSlider.getValue() - 0.5);
            updateSettings(borderPane);
        });

        contrastSlider.setOnMouseReleased(e -> {
            adjustContrast(contrastSlider.getValue() - 0.5);
            updateSettings(borderPane);
        });

        volumeLabel.setOnMouseReleased(e -> {
        });

        backGroundColorBox.setOnAction(e -> {
            adjustBackgroundColor(backGroundColorBox.getValue());
            updateSettings(borderPane);
        });

        borderPane = new BorderPane();
        borderPane.setCenter(settings);
        borderPane.setStyle("-fx-background-color: " + backgroundColor);
        updateSettings(borderPane);

        var scene = new Scene(borderPane, 800, 800);
        stage.setScene(scene);
        stage.show();
    }

    /*
     * Updates settings according to the settings variables
     */
    public static void updateSettings(BorderPane borderPane){
        visualSettings.setBrightness(brightness);
        visualSettings.setSaturation(saturation);
        visualSettings.setContrast(contrast);
        borderPane.setStyle("-fx-background-color: " + backgroundColor);
        borderPane.setEffect(visualSettings);
    }

    private void adjustBrightness(double newBrightness) {
        brightness = newBrightness;
    }

    private void adjustSaturation(double newSaturation) {
        saturation = newSaturation;
    }

    private void adjustContrast(double newContrast) {
        contrast = newContrast;
    }

    private void adjustBackgroundColor(String newBackgroundColor) {
        backgroundColor = newBackgroundColor;
        if (backgroundColor == "Default") {
            backgroundColor = "#121212;";
        }
    }

    private void adjustVolume(double newVolume) {
        // TODO
    }

}


