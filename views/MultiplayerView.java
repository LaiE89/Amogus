package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import model.TetrisApp;
import model.TetrisModel;

import java.util.HashMap;

public class MultiplayerView extends GameView{
    VBox opponentsBoardsLeft;
    VBox opponentsBoardsRight;
    public MultiplayerView() {
        super();
        TetrisApp.view.opBoard1 = new Group();
        TetrisApp.view.opBoard2 = new Group();
        TetrisApp.view.opBoard3 = new Group();
        TetrisApp.view.opBoard4 = new Group();

        opponentsBoardsLeft = new VBox(20, TetrisApp.view.opBoard1, TetrisApp.view.opBoard3);
        opponentsBoardsRight = new VBox(20, TetrisApp.view.opBoard2, TetrisApp.view.opBoard4);
        Button chatButton = new Button("Chat");
        chatButton.setId("Chat");
        chatButton.setFocusTraversable(false);
        chatButton.setPrefSize(150, 50);
        chatButton.setFont(new Font(12));
        chatButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        HBox controls = new HBox(20, chatButton);
        controls.setPadding(new Insets(20, 20, 20, 20));
        controls.setAlignment(Pos.CENTER);

        chatButton.setOnAction(e -> {
            //TO DO!
            this.createChatView();
            this.borderPane.requestFocus();
        });

        borderPane.setTop(controls);
        borderPane.setCenter(canvas);
        borderPane.setLeft(opponentsBoardsLeft);
        borderPane.setRight(opponentsBoardsRight);
        SettingsView.updateSettings(borderPane);

        var scene = new Scene(borderPane, 800, 800);
        this.stage.setScene(scene);
        this.stage.show();
    }

    /**
     * Create the view to chat with players in the lobby
     */
    private void createChatView(){
        // TODO: Change LoadView class into a UI for chatting
    }
}
