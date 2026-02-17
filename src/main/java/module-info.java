module com.chess {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.chess to javafx.fxml;
    exports com.chess;
    exports com.chess.model;
    exports com.chess.model.pieces;
    exports com.chess.view;
    exports com.chess.controller;
}