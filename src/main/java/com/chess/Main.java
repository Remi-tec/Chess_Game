package com.chess;

import com.chess.view.BoardView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.chess.model.Board;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Board board = new Board();
        BoardView boardView = new BoardView(board);

        Scene scene = new Scene(boardView,600,600);

        primaryStage.setTitle("Chess Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
