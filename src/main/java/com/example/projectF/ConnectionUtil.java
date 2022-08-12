package com.example.projectF;

import com.example.PDFWriter.Writer;
import com.example.dataFunctions.Excel2SQL;
import com.example.dataFunctions.LocationInit;
import com.example.dataFunctions.SubjectMetaData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.simple.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public class ConnectionUtil {

    static ObservableList<SubjectInfo> dataPoints = FXCollections.observableArrayList();

    //Getting
    public static ObservableList<SubjectInfo> getSubjectInfo(){
        return FXCollections.observableArrayList();
    }

    public static ObservableList<SubjectInfo> addNewSubjectInfo(String subjectName, String teacherName){
        dataPoints.add(new SubjectInfo(subjectName, teacherName));
        return dataPoints;
    }

    public static ObservableList<SubjectTestData> getTestHeaders(String Subject){
        JSONArray tests = SubjectMetaData.getSubjectTests(Subject);
        ObservableList<SubjectTestData> dataPoints = FXCollections.observableArrayList();
        for (Object i : tests){
            dataPoints.add(new SubjectTestData(i.toString()));
        }
        return dataPoints;
    }

    public static void initialiseSubject(String subject, String filePath, String teacherName){
        if (subject.equals("")){
            return;
        }
        subject = subject.trim();
        teacherName = teacherName.trim();

        Excel2SQL f = new Excel2SQL(filePath, subject);
        f.build(teacherName);
        SubjectMetaData.updateTeacher(subject, teacherName);
    }

    public static String getFilePath(Stage stage){
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        return selectedFile.getAbsolutePath();
    }


    public static void saveTestChanges(TableView<SubjectTestData> table, String subject){
        try{
            for (SubjectTestData row : table.getItems()){
                subject = subject.trim();
                String testName = row.getTestName().trim();
                String conductedDate = row.getTestDate().trim();
                String marks = row.getTestMarks().trim();
                float fMarks = Float.parseFloat(marks);
                SubjectMetaData.updateMMarks(subject, testName, fMarks);
                SubjectMetaData.updateConductedDate(subject, testName, conductedDate);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean checkTableMissingVal(TableView<SubjectTestData> table){
        try{
            for (SubjectTestData i : table.getItems()){
                if (i.getTestDate().equals("") || i.getTestMarks().equals("")){
                    return false;
                }
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean checkTextFieldFill(TextField... fields){
        for (TextField field : fields) {
            if (field.getText().equals("")) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkMarkIsFloat(TableView<SubjectTestData> table){
        try{
            for (SubjectTestData i : table.getItems()){
                Float.parseFloat(i.getTestMarks());
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public static void constrainUserFromExit(Stage window){
        window.setOnCloseRequest(event -> {
            event.consume();
            Alert infoBox = AlertBoxes.InfoBox("Please finish subject addition before quitting the window");
            infoBox.showAndWait();
        });
    }

    public static boolean checkTableEmpty(TableView<SubjectInfo> table){
        ObservableList<SubjectInfo> items = table.getItems();
        return items.isEmpty();
    }

    public static void generateReport(Stage window, String reportName, String gradeSec){
        SubjectMetaData.updateTestDetail(reportName.trim(), gradeSec.trim());

       //Getting dir location
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(window);
        String filePath = selectedDirectory.getAbsolutePath();

        Set<String> studentList = SubjectMetaData.getStudentList();
        for (String i : studentList){
            Writer w = new Writer(i, filePath);
            w.createReport();
        }
        Alert infoBox = AlertBoxes.InfoBox("Reports have been created successfully!");
        infoBox.showAndWait();
        if (infoBox.getResult() == ButtonType.OK){
            try {LocationInit.deletePath();} catch (IOException e) {e.printStackTrace();}
            window.close();
        }
    }
}
