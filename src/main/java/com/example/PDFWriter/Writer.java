package com.example.PDFWriter;

import com.example.dataFunctions.SQLFunctions;
import com.example.dataFunctions.SubjectMetaData;
import com.example.dataFunctions.Utility;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;


//TODO: Set report name to bold

public class Writer {

    static Document document;
    static String filename;

    //Different fonts for different parts of the pdf
    static Font boldTestFont = FontFactory.getFont("Calibri", 15, Font.BOLD);
    static Font boldFont = FontFactory.getFont("Calibri", 12, Font.BOLD);
    static Font cellFont = FontFactory.getFont("Calibri", 11);
    static BaseColor color = new BaseColor(248,203,173,255);//Color for subject+teacher name

    public Writer(String studentName){
        filename = studentName;
    }

    public static void main(String[] args){
        Writer obj = new Writer("A");
        obj.createReport();
        obj.closeDocument();
    }

    public void createReport(){
        initializeDocument();
        closeDocument();
    }

    private void initializeDocument(){
       document = new Document();

        String exactFilename = filename + ".pdf"; //As filename is also the name of the student

        try {
            String[] testDetails = SubjectMetaData.getTestDetail();

            PdfWriter.getInstance(document, new FileOutputStream(exactFilename));
            document.open();

            document.addAuthor("TheTool");
            document.addCreationDate();
            document.addSubject("Report card generated for "+filename);

            //Scaling the image
            Image header = Image.getInstance(ClassLoader.getSystemResource("fiitjee_header.png"));
            header.scalePercent(83);

            //Adding image as a cell in a table that spans 2 columns
            PdfPCell imageCell = new PdfPCell(header, false);
            imageCell.setColspan(2);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            imageCell.setBorder(Rectangle.NO_BORDER);

            //Creating report name cell
            Object reportName = Array.get(testDetails, 0);
            PdfPCell reportNameCell = new PdfPCell(new Phrase(reportName.toString().toUpperCase(Locale.ROOT),
                    boldTestFont));
            reportNameCell.setBackgroundColor(BaseColor.YELLOW);
            reportNameCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            reportNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            reportNameCell.setColspan(2);
            reportNameCell.setBorderWidthTop(1f); reportNameCell.setBorderWidthBottom(0f);

            //Adding opening table
            PdfPTable table = new PdfPTable(2); // 3 columns.
            table.setWidthPercentage(85); //Width 85%
            table.setSpacingAfter(20f); //Space after table

            PdfPCell nameStudent = new PdfPCell(new Paragraph("NAME OF THE STUDENT", cellFont));
            PdfPCell student = new PdfPCell(new Paragraph(filename, cellFont));
            student.setHorizontalAlignment(Element.ALIGN_CENTER);
            PdfPCell gradeSec = new PdfPCell(new Paragraph("GRADE & SEC",cellFont));
            PdfPCell gradeSecDetail = new PdfPCell(new Paragraph(Array.get(testDetails, 1).toString(), cellFont));
            gradeSecDetail.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(imageCell);
            table.addCell(reportNameCell);
            table.addCell(nameStudent);
            table.addCell(student);
            table.addCell(gradeSec);
            table.addCell(gradeSecDetail);
            document.add(table);

            JSONArray subjectList = SubjectMetaData.getSubjects();
            for (Object i : subjectList){
                createSubjectTable(i.toString());
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeDocument(){
        document.close();
    }

    private static void createSubjectTable(String Subject){

        PdfPTable reportTable = new PdfPTable(5);
        reportTable.setWidthPercentage(95);
        //reportTable.setSpacingAfter(15f);

        try {
            float[] columnWidths = {5f, 2f, 2f, 2f, 2f};
            reportTable.setWidths(columnWidths);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        String subjectTeacherText = Subject + " - " + SubjectMetaData.getTeacher(Subject);
        Phrase boldText = new Phrase(subjectTeacherText, boldFont);
        PdfPCell subjectTeacherCell = new PdfPCell(new Paragraph(boldText));
        subjectTeacherCell.setBackgroundColor(color);
        subjectTeacherCell.setColspan(5);
        subjectTeacherCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        reportTable.addCell(subjectTeacherCell);

        Stream.of("TEST NAME / DATE", "MARKS OBTAINED", "MAXIMUM MARKS", "CLASS AVERAGE", "HIGHEST SCORE")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setPhrase(new Phrase(columnTitle, FontFactory.getFont("Calibri", 11,
                            Font.BOLD)));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    header.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    header.setBorderWidthTop(0f);
                    //To stop cells with having their borders overlap with one another.
                    if (!columnTitle.equals("HIGHEST SCORE")){
                        header.setBorderWidthRight(0f);
                    }
                    reportTable.addCell(header);
                });



        List<String> subjectTests = SubjectMetaData.subjectTests(Subject);
        HashMap<String, String> studentMarks = SQLFunctions.getStudentMarks(filename, Subject);

        if (studentMarks.size() == 0){ //Avoiding creating the table by checking if the student has an empty report
            return;
        }

        for (String test : subjectTests){
            JSONObject testData = SubjectMetaData.getSubjectTestMetaData(Subject, test);

            PdfPCell testName = new PdfPCell(new Phrase(test +" / "+ testData.get("ConductedDate").toString(),
                    cellFont));
            PdfPCell marksOb = new PdfPCell(new Phrase(Utility.parseTrailingZero(studentMarks.get(test)), cellFont));
            PdfPCell maxM = new PdfPCell(new Phrase(testData.get("OutOf").toString(), cellFont));
            PdfPCell classAvg = new PdfPCell(new Phrase(Utility.parseTrailingZero(testData.get("Average").toString()),
                    cellFont));
            PdfPCell highScore = new PdfPCell(new Phrase(Utility.parseTrailingZero(testData.get("MaxM").toString()),
                    cellFont));

            testName.setHorizontalAlignment(Element.ALIGN_CENTER);
            testName.setBorderWidthRight(0f);
            marksOb.setHorizontalAlignment(Element.ALIGN_CENTER);
            marksOb.setBorderWidthRight(0f);
            maxM.setHorizontalAlignment(Element.ALIGN_CENTER);
            maxM.setBorderWidthRight(0f);
            classAvg.setHorizontalAlignment(Element.ALIGN_CENTER);
            classAvg.setBorderWidthRight(0f);
            highScore.setHorizontalAlignment(Element.ALIGN_CENTER);

            reportTable.addCell(testName);
            reportTable.addCell(marksOb);
            reportTable.addCell(maxM);
            reportTable.addCell(classAvg);
            reportTable.addCell(highScore);
        }

        try {document.add(reportTable);} catch (DocumentException e) {e.printStackTrace();}
    }



}
