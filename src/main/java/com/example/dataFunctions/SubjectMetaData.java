package com.example.dataFunctions;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class SubjectMetaData {
    final static String file = LocationInit.getPath() + "\\metadata.json";


    @SuppressWarnings("unchecked")
    public static void createSubject(String subject, ArrayList<String> columnNames, String teacher){
        try {
            initialise();
            columnNames.remove(0);

            JSONObject metadata = getMetaData();

            //Creating a JSONObject to store test name as key and value as JSONObject of metadata
            JSONObject subjectHeader = new JSONObject();
            for (String i : columnNames){
                String j = i.replaceAll("[^A-Za-z0-9]", "");
                JSONObject data = new JSONObject();
                List<Number> marks = SQLFunctions.getTestMarks(subject, j); //Fetching marks of test

                //Calculating metadata
                float avg = Average(marks);
                Number max = Max(marks);

                data.put("Average", avg); //Average marks in the class
                data.put("MaxM", max); //Maximum marks obtained by a student
                data.put("OutOf", 0);//Maximum marks possible
                data.put("ConductedDate", "none");//Date on which test was held
                subjectHeader.put(i, data);
            }
            JSONArray tests = new JSONArray();
            tests.addAll(columnNames);

            subjectHeader.put("Tests", tests); //Adding JSONArray with elements being the name of all the tests
            subjectHeader.put("Teacher", teacher); //Adding teacher name to subject data

            metadata.put(subject, subjectHeader); //Writing Subject JSONObject

            JSONArray subjectList = (JSONArray) metadata.get("Subjects");
            subjectList.add(subject);
            write(metadata);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public static void initialise(){
        if (checkEmpty()){
            JSONObject metadata = new JSONObject();

            JSONArray subjectAvailable = new JSONArray(); //JSONArray to contain list of all subjects
            JSONObject students = new JSONObject(); //JSONObject to contain student's name and email id in key-value.

            metadata.put("TestName", "null");
            metadata.put("TestGrade", "null");
            metadata.put("StudentData", students);
            metadata.put("Subjects", subjectAvailable);
            write(metadata);
        }
    }

    private static void write(JSONObject data){
        try (FileWriter json = new FileWriter(file)){
            json.write(data.toJSONString());
            json.flush();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private static boolean checkEmpty(){
        File data = new File(file);
        return data.length() == 0;
    }

    private static float Average(List<Number> array){
        float sum = 0;
        for (Number i : array){
            sum = sum + i.floatValue();
        }
        //TODO: Round average to 2 decimal places
        return Math.round((sum / array.size())*100) / 100;
    }

    private static Number Max(List<Number> array){
        Number greatest = 0;
        for (Number i : array){
            if (i.floatValue() > greatest.floatValue()){
                greatest = i;
            }
        }
        return greatest;
    }

    @SuppressWarnings("unchecked")
    public static void updateMMarks(String subject, String test, float marks){
       //Function to update marks of a specfic test
        try {
           JSONObject metadata = getMetaData();
           JSONObject subjectData = (JSONObject) metadata.get(subject);
           JSONObject testData = (JSONObject) subjectData.get(test);
           testData.put("OutOf", marks);

           write(metadata);
       }
       catch (Exception e){
           e.printStackTrace();
       }
    }

    @SuppressWarnings("unchecked")
    public static void updateConductedDate(String subject, String test, String date){
        try {
            JSONObject metadata = getMetaData();
            JSONObject subjectData = (JSONObject) metadata.get(subject);
            JSONObject testData = (JSONObject) subjectData.get(test);
            testData.put("ConductedDate", date);

            write(metadata);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void updateStudent(String student, String email){
        try{
            JSONObject metadata = getMetaData();
            JSONObject studentData = (JSONObject) metadata.get("StudentData");
            studentData.put(student, email);
            write(metadata);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<String> subjectTests(String subject){
        try{
            JSONObject metadata = getMetaData();
            JSONObject subjectData = (JSONObject) metadata.get(subject);
            return (List<String>) subjectData.get("Tests");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    public static JSONArray getSubjects(){
        try{
            JSONObject metadata = getMetaData();
            return (JSONArray) metadata.get("Subjects");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new JSONArray();
    }

    private static JSONObject getMetaData(){
        try {
            //Reading the top most JSON object
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(file);
            Object obj = jsonParser.parse(reader);
            reader.close();
            return (JSONObject) obj;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new JSONObject();
    }

    @SuppressWarnings("unchecked")
    public static void updateTestDetail(String testName, String grade){
        JSONObject metadata = getMetaData();
        metadata.put("TestName", testName);
        metadata.put("TestGrade", grade);
        write(metadata);
    }

    public static String[] getTestDetail(){
        JSONObject metadata = getMetaData();
        return new String[]{metadata.get("TestName").toString(), metadata.get("TestGrade").toString()};
    }

    @SuppressWarnings("unchecked")
    public static void updateTeacher(String subject, String name){
        JSONObject metadata = getMetaData();
        JSONObject subjectData = (JSONObject) metadata.get(subject);
        subjectData.put("Teacher", name);
    }

    public static JSONArray getSubjectTests(String subject){
        JSONObject metadata = getMetaData();
        JSONObject subjectData = (JSONObject) metadata.get(subject);
        return (JSONArray) subjectData.get("Tests");
    }

    public static String getTeacher(String subject){
        JSONObject metadata = getMetaData();
        JSONObject subjectData = (JSONObject) metadata.get(subject);
        return subjectData.get("Teacher").toString();
    }

    public static JSONObject getSubjectTestMetaData(String subject, String test){
        JSONObject metadata = getMetaData();
        JSONObject data = (JSONObject) metadata.get(subject);
        return (JSONObject) data.get(test);
    }

    @SuppressWarnings("unchecked")
    public static void addToStudentList(String studentName, String studentID){
        JSONObject metadata = getMetaData();
        JSONObject studentData = (JSONObject) metadata.get("StudentData");
        studentData.put(studentName, studentID);
        write(metadata);
    }

    @SuppressWarnings("unchecked")
    public static void addToStudentList(String studentName){
        JSONObject metadata = getMetaData();
        JSONObject studentData = (JSONObject) metadata.get("StudentData");
        studentData.put(studentName, "none");
        write(metadata);
    }

    @SuppressWarnings("unchecked")
    public static Set<String> getStudentList(){
        JSONObject metadata = getMetaData();
        JSONObject studentData = (JSONObject) metadata.get("StudentData");
        return  studentData.keySet();
    }

    public static void delSubject(String subject){
        JSONObject metadata = getMetaData();
        JSONArray subjectList = (JSONArray) metadata.get("Subjects");
        subjectList.remove(subject);
        metadata.remove(subject);
        write(metadata);
    }
}
