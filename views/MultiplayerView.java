package views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MultiplayerView extends GameView{

    public MultiplayerView() {
        super();

        Button chatButton = new Button("Chat");
        chatButton.setId("Chat");
        chatButton.setPrefSize(150, 50);
        chatButton.setFont(new Font(12));
        chatButton.setStyle("-fx-background-color: #17871b; -fx-text-fill: white;");

        HBox controls = new HBox(20, chatButton);
        controls.setPadding(new Insets(20, 20, 20, 20));
        controls.setAlignment(Pos.CENTER);

        Label holdPieceLabel = new Label("Hold");
        holdPieceLabel.setFont(new Font(20));
        holdPieceLabel.setTextFill(Color.WHITE);

        VBox holdPieceBox = new VBox(20, holdPieceLabel, holdPieceCanvas);
        holdPieceBox.setPadding(new Insets(40, 20, 20, 20));
        holdPieceBox.setAlignment(Pos.TOP_CENTER);

        chatButton.setOnAction(e -> {
            //TO DO!
            this.createChatView();
            this.borderPane.requestFocus();
        });

        borderPane.setBottom(controls);
        borderPane.setTop(holdPieceBox);
        borderPane.setCenter(canvas);
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
