package com.example.projectF;

public class SubjectInfo {

    private String subjectName;
    private String teacherName;

    public SubjectInfo(){
        this.subjectName = "";
        this.teacherName = "";
    }

    public SubjectInfo(String subjectName, String teacherName){
        this.subjectName = subjectName;
        this.teacherName = teacherName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
}
