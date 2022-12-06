package model;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import views.ConnectView;
import views.TetrisView;

import javafx.application.Application;
import javafx.stage.Stage;

/** 
 * A Tetris Application, in JavaFX
 * 
 * Based on the Tetris assignment in the Nifty Assignments Database, authored by Nick Parlante
 */
public class TetrisApp extends Application {
    TetrisModel model;
    public static TetrisView view;

    /** 
     * Main method
     * 
     * @param args agument, if any
     */
    public static void main(String[] args) {
        launch(args);
    }

    /** 
     * Start method.  Control of application flow is dictated by JavaFX framework
     * 
     * @param primaryStage stage upon which to load GUI elements
     */
    @Override
    public void start(Stage primaryStage) {
        this.model = new TetrisModel(); // create a model
        this.view = TetrisView.getInstance(model, primaryStage);

        // Check if the user exited the application. On exit, closes any opened server or client connections.
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (ConnectView.clientHostedServer != null) {
                    ConnectView.clientHostedServer.closeServer();
                }
                if (ConnectView.client != null) {
                    ConnectView.client.closeClientConnection();
                }
                Platform.exit();
            }
        });
    }
}

