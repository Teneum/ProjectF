package com.example.dataFunctions;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

public class Utility {

    public static HashMap<String, String> map(List<String> keys, List<String> values)
    {
        int keysSize = keys.size();
        int valuesSize = values.size();

        // if the size of both arrays is not equal, throw an
        // IllegalArgumentsException
        if (keysSize != valuesSize) {
            throw new IllegalArgumentException(
                    "The number of keys doesn't match the number of values.");
        }


        if (keysSize == 0) {
            return new HashMap<>();
        }

        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < keysSize; i++) {
            map.put(keys.get(i), values.get(i));
        }
        return map;
    }

    public static String parseTrailingZero(String expression){
        try{
            float f = Float.parseFloat(expression);
            return NumberFormat.getInstance().format(f);
        }
        catch (Exception e) {
            return expression;
        }
    }

    //Method to create a sample Excel file for end user
    public static XSSFWorkbook createSampleSpreadsheet(){
        XSSFWorkbook workbook = new XSSFWorkbook();
        try {
            String filename = "sample.xlsx";

            XSSFSheet sheet = workbook.createSheet();
            XSSFRow rowHead = sheet.createRow((short)0);

            rowHead.createCell(0).setCellValue("Student");
            rowHead.createCell(1).setCellValue("Test 1");
            rowHead.createCell(2).setCellValue("Test 2");
            rowHead.createCell(3).setCellValue("Test 3");

            return workbook;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return workbook;
    }
}
