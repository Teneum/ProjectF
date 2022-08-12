package com.example.projectF;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddSubjectWindow {

    static boolean subjectNameFill = false;
    
    public static void display(TableView<SubjectInfo> subjectTable, Button btn){
        Stage window = new Stage();

        //Setting some elements up beforehand.
        TableView<SubjectTestData> table = new TableView<>();
        subjectNameFill = false;
        Button saveButton = new Button("Save");
        saveButton.setDisable(true);

        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Add Subject - Report Builder");

        VBox container = new VBox();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(8);
        container.getChildren().add(grid);

        //Opening Labels
        // -> Name of Subject
        Label subjectNameLabel = new Label("Name of Subject: ");
        GridPane.setConstraints(subjectNameLabel, 0, 0);

        TextField subjectNameInput = new TextField("");
        subjectNameInput.setPromptText("Eg: Physics");
        GridPane.setConstraints(subjectNameInput, 1, 0);

        // -> Teacher Name
        Label teacherNameLabel = new Label("Teacher Name: ");
        GridPane.setConstraints(teacherNameLabel, 0, 1);

        TextField teacherNameInput = new TextField("");
        teacherNameInput.setPromptText("Ex: Mr. John Doe");
        GridPane.setConstraints(teacherNameInput, 1, 1);
        grid.getChildren().addAll(subjectNameLabel, subjectNameInput, teacherNameLabel, teacherNameInput);

        //User Input for file path
        Button addPathButton = new Button("Add Path");
        Label addPathLabel = new Label();
        GridPane.setConstraints(addPathButton, 0, 2);
        GridPane.setConstraints(addPathLabel, 1, 2);

        //Setting action for addPathButton
        addPathButton.setOnAction(e -> {
            String filePath = ConnectionUtil.getFilePath(window);
            addPathLabel.setText(filePath);

            //Creating a confirm button to confirm and save changes
            Button confirmPath = new Button("Confirm");
            GridPane.setConstraints(confirmPath, 0, 3);
            grid.getChildren().add(confirmPath);

            confirmPath.setOnAction(ex -> {
                if (ConnectionUtil.checkTextFieldFill(subjectNameInput, teacherNameInput)){
                    subjectNameFill = true;
                }
                if (subjectNameFill){
                    subjectNameInput.setDisable(true);
                    teacherNameInput.setDisable(true);
                    addPathButton.setDisable(true);
                    saveButton.setDisable(false);
                    ConnectionUtil.initialiseSubject(subjectNameInput.getText(), filePath, teacherNameInput.getText());
                    table.setItems(ConnectionUtil.getTestHeaders(subjectNameInput.getText()));
                    ConnectionUtil.constrainUserFromExit(window);
                    confirmPath.setDisable(true);
                }
                else{
                    Alert errorBox = AlertBoxes.ErrorBox("Please fill in name of the subject and teacher.");
                    errorBox.showAndWait();
                }
            });
        });
        grid.getChildren().addAll(addPathButton, addPathLabel);

        //Creating TableView
        TableColumn<SubjectTestData, String> testNameColumn = new TableColumn<>("Test");
        testNameColumn.setMinWidth(125);
        testNameColumn.setCellValueFactory(new PropertyValueFactory<>("testName"));

        TableColumn<SubjectTestData, String> testDateColumn = new TableColumn<>("Date");
        testDateColumn.setMinWidth(125);
        testDateColumn.setCellValueFactory(new PropertyValueFactory<>("testDate"));
        testDateColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        testDateColumn.setOnEditCommit(event -> {
            SubjectTestData obj = event.getRowValue();
            obj.setTestDate(event.getNewValue());
        });

        TableColumn<SubjectTestData, String> testMarksColumn = new TableColumn<>("Maximum Marks");
        testMarksColumn.setMinWidth(125);
        testMarksColumn.setCellValueFactory(new PropertyValueFactory<>("testMarks"));
        testMarksColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        testMarksColumn.setOnEditCommit(event -> {
            SubjectTestData obj = event.getRowValue();
            obj.setTestMarks(event.getNewValue());
        });


        table.getItems().clear();//Clearing contents for if a new subject is added.
        table.setEditable(true);
        table.setColumnResizePolicy(javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPadding(new Insets(15, 10, 15, 10));
        table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(180));

        table.getColumns().addAll(testNameColumn, testDateColumn, testMarksColumn);
        container.getChildren().addAll(table);


        saveButton.setOnAction(e -> {

            boolean fillCond = ConnectionUtil.checkTableMissingVal(table);
            if (!fillCond){
                Alert missVal = AlertBoxes.ErrorBox("Table fields cannot be left empty. Please try again");
                missVal.showAndWait();
            }
            else if (!ConnectionUtil.checkMarkIsFloat(table)){
                Alert errorBox = AlertBoxes.ErrorBox("Maximum marks can only be numbers. Please try again");
                errorBox.showAndWait();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Save Subject?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    ConnectionUtil.saveTestChanges(table, subjectNameInput.getText());
                    subjectTable.setItems(ConnectionUtil.addNewSubjectInfo(subjectNameInput.getText(), teacherNameInput.getText()));
                    btn.setDisable(false);
                    window.close();
                }
            }
        });

        StackPane pane = new StackPane();
        pane.setPadding(new Insets(40));
        pane.getChildren().add(saveButton);
        pane.setAlignment(Pos.BOTTOM_CENTER);
        container.getChildren().add(pane);

        Scene scene = new Scene(container, 500, 400);
        window.setScene(scene);
        window.show();
    }


}
