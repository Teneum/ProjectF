package com.example.projectF;

import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;

public class SubjectTestData {

    private String testName;
    private String testDate;
    private String testMarks;

    public SubjectTestData(){
        this.testName = "";
        this.testDate = "";
        this.testMarks = "";
    }

    public SubjectTestData(String testName){
        this.testName = testName;
        this.testDate = "";
        this.testMarks = "";
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestDate() {
        return this.testDate;
    }

    public void setTestDate(String testDate) {this.testDate = testDate;}

    public String getTestMarks() {
        return this.testMarks;
    }

    public void setTestMarks(String testMarks) {
        this.testMarks = testMarks;
    }
}