package com.dav.backend.utils;

import com.dav.backend.features.student.Student;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.text.*;
import java.util.*;

@Slf4j
public class StudentExcelImporter {

    public static List<Student> importStudents(InputStream excelInput, List<Integer> errorRows) {
        List<Student> students = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(excelInput)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter dataFormatter = new DataFormatter();
            int rowNum = 0;
            for (Row row : sheet) {
                if (rowNum == 0) { rowNum++; continue; } // Skip header

                try {
                    Student student = new Student();
                    student.setAdmissionNo(dataFormatter.formatCellValue(row.getCell(1)).trim());
                    student.setStudentName(dataFormatter.formatCellValue(row.getCell(2)).trim());
                    student.setMobileNo(dataFormatter.formatCellValue(row.getCell(3)).trim());
                    student.setClassName(dataFormatter.formatCellValue(row.getCell(4)).trim());
                    student.setSection(dataFormatter.formatCellValue(row.getCell(5)).trim());
                    student.setFatherName(dataFormatter.formatCellValue(row.getCell(6)).trim());
                    student.setMotherName(dataFormatter.formatCellValue(row.getCell(7)).trim());

                    // Date conversion: "DD-MM-YYYY" to "yyyy-MM-dd"
                    String excelDate = dataFormatter.formatCellValue(row.getCell(8));
                    SimpleDateFormat inputFmt = new SimpleDateFormat("dd-MM-yyyy");

                    // Set timezone to UTC to avoid day-shift issues
                    inputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
                    Date birthDate = inputFmt.parse(excelDate);
                    student.setBirthDate(birthDate);


                    student.setAddress(dataFormatter.formatCellValue(row.getCell(9)).trim());
                    students.add(student);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    errorRows.add(rowNum);
                }
                rowNum++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }
}
