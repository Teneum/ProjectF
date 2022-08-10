package com.example.dataFunctions;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SQLFunctions {
    final static String url = "jdbc:sqlite:workbook.db";

    public static void main(String[] args){
        HashMap<String, String> l = getStudentMarks("A", "maths");
        System.out.println(l);
    }

    public static List<Number> getTestMarks(String subject, String test){

        try {Class.forName("org.sqlite.JDBC");} catch (ClassNotFoundException e) {e.printStackTrace();}
        List<Number> finalMarks = new ArrayList<>();
        try{
            Connection conn = DriverManager.getConnection(url);
            Statement cursor = conn.createStatement();

            //SQL Query being assigned to a temporary ArrayList
            List<String> marks = new ArrayList<>();
            String baseSQL = "SELECT " + test + " FROM " + subject;
            try (ResultSet rs = cursor.executeQuery(baseSQL)){
                while (rs.next()){
                    marks.add(rs.getString(1));
                }
            }

            //Cleaning through list replacing any non-float entry to 0
            for (String i : marks){
                try {
                    finalMarks.add(Float.parseFloat(i));
                }
                catch (Exception ignored){}
            }

            cursor.close();
            conn.close();
            return finalMarks;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return finalMarks;
    }


    public static HashMap<String, String> getStudentMarks(String name, String subject){
        try {Class.forName("org.sqlite.JDBC");} catch (ClassNotFoundException e) {e.printStackTrace();}

        try{
            Connection conn = DriverManager.getConnection(url);
            List<String> marks = new ArrayList<>();
            List<String> testNames = SubjectMetaData.subjectTests(subject);

            String baseSQL = "SELECT * FROM " + subject + " WHERE name = ?";
            try(PreparedStatement stmt = conn.prepareStatement(baseSQL)){
                stmt.setString(1, name);

                try (ResultSet rs = stmt.executeQuery()){
                    for (String testName : testNames) {
                        marks.add(rs.getString(testName.replaceAll("[^A-Za-z0-9]", "")));
                    }
                }
            }

            return Utility.map(testNames, marks);

        } catch (SQLException e) {
            e.printStackTrace(); //Log here
        }
        return new HashMap<>();

    }

    public static void dropSubject(String subject){
        try {Class.forName("org.sqlite.JDBC");} catch (ClassNotFoundException e) {e.printStackTrace();}

        try {
            Connection conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
            String baseSQL = "DROP TABLE " + subject;
            try (PreparedStatement stmt = conn.prepareStatement(baseSQL)){
                stmt.executeUpdate();
                conn.commit();
                conn.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
