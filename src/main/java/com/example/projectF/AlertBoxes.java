package com.example.projectF;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class AlertBoxes {

    public static Alert ErrorBox(String msg){
        return new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
    }

    public static Alert ConfirmBox(String msg){
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
    }

    public static Alert InfoBox(String msg){
        return new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
    }
}
