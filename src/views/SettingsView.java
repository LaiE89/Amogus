package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.TetrisApp;
import javafx.scene.input.KeyCode;
import audio.AudioManager;

public class SettingsView {

    // Final buttons
    private final Slider brightnessSlider;
    private final Slider saturationSlider;
    private final Slider contrastSlider;
    private final Slider volumeSlider;
    private final ComboBox<String> backGroundColorBox;

    // Settings Adjustment Variables
    public static ColorAdjust visualSettings = new ColorAdjust();
    public static double brightness = 0;
    public static double saturation = 0;
    public static double contrast = 0;
    public static String backgroundColor = "#121212;";

    // Reference to TetrisView variables
    private BorderPane borderPane;
    private Button rotateControl;
    private Button leftControl;
    private Button rightControl;
    private Button downControl;
    private Button dropControl;
    private Button holdControl;
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
        backButton.setFocusTraversable(false);

        // Brightness slider
        brightnessSlider = new Slider(0, 1, 0.5);
        brightnessSlider.setShowTickLabels(true);
        brightnessSlider.setStyle("-fx-control-inner-background: palegreen;");

        // Brightness label
        Label brightnessLabel = new Label("Brightness");
        brightnessLabel.setFont(new Font(20));
        brightnessLabel.setTextFill(Color.WHITE);

        // Saturation slider
        saturationSlider = new Slider(0, 1, 0.5);
        saturationSlider.setShowTickLabels(true);
        saturationSlider.setStyle("-fx-control-inner-background: palegreen;");

        // Saturation label
        Label saturationLabel = new Label("Saturation");
        saturationLabel.setFont(new Font(20));
        saturationLabel.setTextFill(Color.WHITE);

        // Contrast slider
        contrastSlider = new Slider(0, 1, 0.5);
        contrastSlider.setShowTickLabels(true);
        contrastSlider.setStyle("-fx-control-inner-background: palegreen;");

        // Contrast label
        Label contrastLabel = new Label("Contrast");
        contrastLabel.setFont(new Font(20));
        contrastLabel.setTextFill(Color.WHITE);

        // Volume slider
        volumeSlider = new Slider(0, 1, 0.5);
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

        Label dropLabel = new Label("DROP");
        dropLabel.setFont(new Font(20));
        dropLabel.setTextFill(Color.WHITE);

        dropControl = new Button("w");
        HBox dropControlBox = new HBox(20, dropLabel, dropControl);
        dropControlBox.setAlignment(Pos.CENTER_RIGHT);
        dropControl.setFocusTraversable(false);

        Label rotateLabel = new Label("ROTATE");
        rotateLabel.setFont(new Font(20));
        rotateLabel.setTextFill(Color.WHITE);

        rotateControl = new Button("space");
        HBox rotateControlBox = new HBox(20, rotateLabel, rotateControl);
        rotateControlBox.setAlignment(Pos.CENTER_RIGHT);
        rotateControl.setFocusTraversable(false);

        Label leftLabel = new Label("LEFT");
        leftLabel.setFont(new Font(20));
        leftLabel.setTextFill(Color.WHITE);

        leftControl = new Button("a");
        HBox leftControlBox = new HBox(20, leftLabel, leftControl);
        leftControlBox.setAlignment(Pos.CENTER_RIGHT);
        leftControl.setFocusTraversable(false);

        Label rightLabel = new Label("RIGHT");
        rightLabel.setFont(new Font(20));
        rightLabel.setTextFill(Color.WHITE);

        rightControl = new Button("d");
        HBox rightControlBox = new HBox(20, rightLabel, rightControl);
        rightControlBox.setAlignment(Pos.CENTER_RIGHT);
        rightControl.setFocusTraversable(false);

        Label downLabel = new Label("DOWN");
        downLabel.setFont(new Font(20));
        downLabel.setTextFill(Color.WHITE);

        downControl = new Button("s");
        HBox downControlBox = new HBox(20, downLabel, downControl);
        downControlBox.setAlignment(Pos.CENTER_RIGHT);
        downControl.setFocusTraversable(false);

        Label holdLabel = new Label("HOLD");
        holdLabel.setFont(new Font(20));
        holdLabel.setTextFill(Color.WHITE);

        holdControl = new Button("E");
        HBox holdControlBox = new HBox(20, holdLabel, holdControl);
        holdControlBox.setAlignment(Pos.CENTER_RIGHT);
        holdControl.setFocusTraversable(false);

        //vbox containing all control settings
        VBox controlSettings = new VBox(20, controlSettingsLabel, dropControlBox,rotateControlBox, leftControlBox, rightControlBox, downControlBox, holdControlBox);
        controlSettings.setPadding(new Insets(20, 20, 20, 20));
        controlSettings.setAlignment(Pos.CENTER);

        //hbox containing all settings
        HBox settings = new HBox(50);
        settings.setPadding(new Insets(20, 20, 20, 20));
        settings.setAlignment(Pos.CENTER);
        settings.getChildren().addAll(visualSettings, controlSettings);

        // Implementing the controls
        backButton.setOnAction(e -> {
            AudioManager.getInstance().playSound("menu.wav");
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

        volumeSlider.setOnMouseReleased(e -> {
            AudioManager.getInstance().setVolume(volumeSlider.getValue());
            AudioManager.getInstance().changeVolumes();
        });

        backGroundColorBox.setOnAction(e -> {
            adjustBackgroundColor(backGroundColorBox.getValue());
            updateSettings(borderPane);
        });

        dropControl.setOnAction(e -> {
            borderPane.setOnKeyReleased(c -> {
                adjustControls(dropControl, c.getCode(), 0);
                borderPane.setOnKeyReleased(null);
            });
        });

        leftControl.setOnAction(e -> {
            borderPane.setOnKeyReleased(c -> {
                adjustControls(leftControl, c.getCode(), 1);
                borderPane.setOnKeyReleased(null);
            });
        });
        rightControl.setOnAction(e -> {
            borderPane.setOnKeyReleased(c -> {
                adjustControls(rightControl, c.getCode(), 2);
                borderPane.setOnKeyReleased(null);
            });
        });
        downControl.setOnAction(e -> {
            borderPane.setOnKeyReleased(c -> {
                adjustControls(downControl, c.getCode(), 3);
                borderPane.setOnKeyReleased(null);
            });
        });
        rotateControl.setOnAction(e -> {
            borderPane.setOnKeyReleased(c -> {
                adjustControls(rotateControl, c.getCode(), 4);
                borderPane.setOnKeyReleased(null);
            });
        });
        holdControl.setOnAction(e -> {
            borderPane.setOnKeyReleased(c -> {
                adjustControls(holdControl, c.getCode(), 5);
                borderPane.setOnKeyReleased(null);
            });
        });
        borderPane = new BorderPane();
        borderPane.setCenter(settings);
        borderPane.setStyle("-fx-background-color: " + backgroundColor);
        updateSettings(borderPane);
        updateSettingsView();

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

    public void adjustControls(Button button, KeyCode newKey, int moveType){
        if(!TetrisApp.view.controlMap.containsValue(newKey)){
            if(newKey == KeyCode.SPACE){
                button.setText("SPACE");
            }else{
                button.setText(newKey.toString());
            }
            TetrisApp.view.controlMap.put(moveType, newKey);
        }
    }

    public void updateSettingsView(){
        brightnessSlider.setValue(brightness + 0.5);
        saturationSlider.setValue(saturation + 0.5);
        contrastSlider.setValue(contrast + 0.5);
        volumeSlider.setValue(AudioManager.getInstance().getVolume());
        dropControl.setText(TetrisApp.view.controlMap.get(0).toString());
        leftControl.setText(TetrisApp.view.controlMap.get(1).toString());
        rightControl.setText(TetrisApp.view.controlMap.get(2).toString());
        downControl.setText(TetrisApp.view.controlMap.get(3).toString());
        rotateControl.setText(TetrisApp.view.controlMap.get(4).toString());
        holdControl.setText(TetrisApp.view.controlMap.get(5).toString());
    }
}


