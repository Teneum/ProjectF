package com.example.dataFunctions;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;


public class Excel2SQL {

    //Creating class constructor
    static String filepath;
    static String subject;
    static final String url = "jdbc:sqlite:workbook.db";

    public Excel2SQL(String file, String sub){
        //Params: file -> Specify the Excel sheet from which data has to be pulled
        //        sub -> Specify the subject according to which table is created
        filepath = file;
        subject = sub;
    }


    public void build(String teacherName){
        try {Class.forName("org.sqlite.JDBC");} catch (ClassNotFoundException e) {e.printStackTrace();}
        try{
            buildTable(); //Building the table in DB file
            SubjectMetaData.initialise(); //Creating metadata json file

            //Establishing Connection to DB
            Connection conn = DriverManager.getConnection(url);

            //Creating iterator for Excel spreadsheet
            FileInputStream inputStream = new FileInputStream(filepath);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0); //Setting to first sheet in Excel file
            Iterator<Row> rowIterator = firstSheet.iterator();

            //Creating SQL for insertion of values
            ArrayList<String> columnNames = GetColumns();

            //Ugly bit of code. Will make it better in future updates.....maybe
            StringBuilder baseSQL = new StringBuilder("INSERT INTO " + subject.replaceAll("[^A-Za-z0-9]", "") + " (");
            StringBuilder suffix = new StringBuilder("(");
            for (int i = 0; i < columnNames.size(); i++){
                String val = columnNames.get(i);
                val = val.replaceAll("[^A-Za-z0-9]","");
                if (i == columnNames.size() - 1){
                    baseSQL.append(val).append(") VALUES ");
                    suffix.append("?)");
                }
                else{
                    baseSQL.append(val).append(", ");
                    suffix.append("?, ");
                }
            }
            String sql = baseSQL + suffix.toString();
            conn.setAutoCommit(false);
            PreparedStatement statement = conn.prepareStatement(sql);

            rowIterator.next(); //Skipping the header row

            while (rowIterator.hasNext()){
                Row nextRow = rowIterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                int i = 0;
                while (cellIterator.hasNext()){
                    i++;
                    Cell cell = cellIterator.next();
                    statement.setString(i, cell.toString().trim());
                    if (i == 1){
                        SubjectMetaData.addToStudentList(cell.toString());
                    }
                }
                statement.addBatch();
            }
            statement.executeBatch();
            conn.commit();
            conn.close();
            workbook.close();

            SubjectMetaData.createSubject(subject, columnNames, teacherName);
        }
        catch (SQLException | IOException e){
            e.printStackTrace();
        }
    }

    public static ArrayList<String> GetColumns() throws IOException {
        //Iterating through first row of spreadsheet to get column names
        FileInputStream inputStream = new FileInputStream(filepath);
        Workbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = (XSSFSheet) workbook.getSheetAt(0);

        //Iterating through first row of spreadsheet to get column names
        Row row = sheet.getRow(0);
        Iterator<Cell> iterator = row.cellIterator();
        ArrayList<String> columnNames = new ArrayList<>();
        while (iterator.hasNext()){
            Cell cell = iterator.next();
            columnNames.add(cell.toString());
        }
        workbook.close();
        columnNames.set(0, "name"); //Setting first column as name

        return columnNames;
    }

    private static void buildTable(){
        //Copying the Excel columns into DB
        try {Class.forName("org.sqlite.JDBC");} catch (ClassNotFoundException e) {e.printStackTrace();}

        try{
            Connection conn = DriverManager.getConnection(url);
            Statement cursor = conn.createStatement();

            ArrayList<String> columnNames = GetColumns();
            columnNames.set(0, "name"); //Setting first column as name

            //Forming SQL statement from ArrayList of column names
            StringBuilder baseSQL = new StringBuilder("CREATE TABLE IF NOT EXISTS " + subject.replaceAll("[^A-Za-z0-9]", "") + " (");
            for (int i = 0; i < columnNames.size(); i++) {
                String val = columnNames.get(i);
                //Cleaning the column names for SQL
                val = val.replaceAll("[^A-Za-z0-9]", "");
                if (i == columnNames.size() - 1) {
                    baseSQL.append(val).append(" TEXT);");
                } else {
                    baseSQL.append(val).append(" TEXT,");
                }
            }
            cursor.executeUpdate(baseSQL.toString());
            cursor.close();
            conn.close();

        }
        catch (SQLException | IOException e){
            e.printStackTrace();
        }
    }

    private static boolean tableExists(){
        //Checking if table for subject exists
        try {Class.forName("org.sqlite.JDBC");} catch (ClassNotFoundException e) {e.printStackTrace();}

        try {
        Connection conn = DriverManager.getConnection(url);
            DatabaseMetaData md = conn.getMetaData();
            ResultSet rs = md.getTables(null, null, subject, null);
            rs.last();
            conn.close();
            return rs.getRow() > 0;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
