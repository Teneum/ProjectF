package com.example.projectF;

import com.example.dataFunctions.LocationInit;
import com.example.dataFunctions.SQLFunctions;
import com.example.dataFunctions.SubjectMetaData;
import com.example.dataFunctions.Utility;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    TableView<SubjectInfo> table;
    Stage window;
    Button generateReport = new Button("Generate Report");

    @Override
    public void start(Stage primaryStage) {
        //Initialising save location for DB files
        LocationInit.initialise();

        window = primaryStage;
        window.setTitle("Report Builder - Version 0.1");
        generateReport.setDisable(true);

        VBox container = new VBox();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(8);
        container.getChildren().add(grid);

        //Opening Labels
        // -> Name of the report
        Label reportNameLabel = new Label("Name of Report: ");
        GridPane.setConstraints(reportNameLabel, 0, 0);

        TextField reportNameInput = new TextField("");
        reportNameInput.setPromptText("Eg: Phase Test 2022");
        GridPane.setConstraints(reportNameInput, 1, 0);

        // -> Grade Section
        Label gradeSecLabel = new Label("Grade & Section: ");
        GridPane.setConstraints(gradeSecLabel, 0, 1);

        TextField gradeSecInput = new TextField("");
        gradeSecInput.setPromptText("Ex: 12-B");
        GridPane.setConstraints(gradeSecInput, 1, 1);
        grid.getChildren().addAll(reportNameLabel, reportNameInput, gradeSecLabel, gradeSecInput);

        //Adding Buttons
        Button addSubjectButton = new Button("Add Subject");
        Button getSampleExcel = new Button("Get Sample Spreadsheet");
        Button deleteSubjectButton = new Button("Delete Subject");

        //Setting action for add & delete button
        addSubjectButton.setOnAction(e -> AddSubjectWindow.display(table, generateReport));

        deleteSubjectButton.setOnAction(e -> {
            SubjectInfo selectedItem = table.getSelectionModel().getSelectedItem();
            String subject = selectedItem.getSubjectName();

            Alert alertBox = AlertBoxes.ConfirmBox("Are you sure you want to remove " + subject + " from the subject list?");
            alertBox.showAndWait();

            if (alertBox.getResult() == ButtonType.YES){
                SQLFunctions.dropSubject(subject);
                SubjectMetaData.delSubject(subject);
                table.getItems().remove(selectedItem);
                if (ConnectionUtil.checkTableEmpty(table)) {generateReport.setDisable(true);}
            }
        });

        //Setting action for sample button
        getSampleExcel.setOnAction(e -> createSample());

        GridPane.setConstraints(addSubjectButton, 0, 3);
        GridPane.setConstraints(deleteSubjectButton, 1, 3);
        GridPane.setConstraints(getSampleExcel, 0, 2);

        grid.getChildren().addAll(addSubjectButton, getSampleExcel, deleteSubjectButton);

        //Creating TableView
        TableColumn<SubjectInfo, String> subjectNameColumn = new TableColumn<>("Subject");
        subjectNameColumn.setMinWidth(125);
        subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));

        TableColumn<SubjectInfo, String> teacherNameColumn = new TableColumn<>("Teacher");
        teacherNameColumn.setMinWidth(125);
        teacherNameColumn.setCellValueFactory(new PropertyValueFactory<>("teacherName"));

        //Customizing table
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPadding(new Insets(15, 10, 15, 10));
        table.setFixedCellSize(25);
        table.prefHeightProperty().bind(Bindings.size(table.getItems()).multiply(table.getFixedCellSize()).add(140));

        table.setItems(ConnectionUtil.getSubjectInfo());
        table.getColumns().addAll(subjectNameColumn, teacherNameColumn);
        container.getChildren().addAll(table);

        //Adding functionality to generateReport button
        generateReport.setOnAction(e ->{
            if (!ConnectionUtil.checkTextFieldFill(reportNameInput, gradeSecInput)){
                Alert errorBox = AlertBoxes.ErrorBox("Please fill in all the fields");
                errorBox.show();
            }
            else{
                Alert infoBox = AlertBoxes.InfoBox("Please choose a folder to save the generated reports to");
                infoBox.showAndWait();
                if (infoBox.getResult() == ButtonType.OK){
                    ConnectionUtil.generateReport(window, reportNameInput.getText().trim(), gradeSecInput.getText().trim());
                }
            }
        });

        //Place Report Button
        StackPane pane = new StackPane();
        pane.setPadding(new Insets(40));
        pane.getChildren().add(generateReport);
        pane.setAlignment(Pos.BOTTOM_CENTER);
        container.getChildren().add(pane);

        window.setOnCloseRequest(event -> {
            event.consume();
            Alert confirmBox = AlertBoxes.ConfirmBox("Are you sure you want to quit? All progress will be lost");
            confirmBox.showAndWait();
            if (confirmBox.getResult() == ButtonType.YES){
                try {LocationInit.deletePath();} catch (IOException e) {e.printStackTrace();}
                window.close();
            }
        });

        Scene scene = new Scene(container, 500, 400);
        window.setScene(scene);
        window.show();

    }

    private void createSample(){
        FileChooser fileChooser = new FileChooser();

        //Set extension filter to .xlsx files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show file save dialog
        File file = fileChooser.showSaveDialog(window);

        //If file is not null, write to file using output stream.
        if (file != null) {
            try (FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath())) {
                XSSFWorkbook workbook = Utility.createSampleSpreadsheet();
                workbook.write(outputStream);
                workbook.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
