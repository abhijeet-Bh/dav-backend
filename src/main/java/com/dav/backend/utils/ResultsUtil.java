package com.dav.backend.utils;

import com.dav.backend.features.attendance.Attendance;
import com.dav.backend.features.results.StudentResultsDTO;
import com.dav.backend.features.results.SubjectResult;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

@Service
public class ResultsUtil {
    public byte[] fillResultPdf(StudentResultsDTO studentResultsDTO) throws Exception {
        if(studentResultsDTO.getMarks().size() > 7){
            throw new RuntimeException("This Result Template supports only 7 subjects");
        }

        InputStream templateInput = getClass().getResourceAsStream("/templates/student-results-v3.pdf");  // Your fillable template

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(templateInput), new PdfWriter(baos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        Map<String, PdfFormField> fields = form.getFormFields();

        // Fill basic metadata
        fields.get("session").setValue(studentResultsDTO.getSession());
        fields.get("examType").setValue(studentResultsDTO.getExamType());
        fields.get("studentName").setValue(studentResultsDTO.getStudent().getStudentName());
        fields.get("admissionNo").setValue(studentResultsDTO.getStudent().getAdmissionNo());
        fields.get("classSection").setValue(studentResultsDTO.getStudent().getClassName() + " - " + studentResultsDTO.getStudent().getSection());
        fields.get("rollNo").setValue(studentResultsDTO.getRollNo());
        fields.get("fatherName").setValue(studentResultsDTO.getStudent().getFatherName());
        fields.get("motherName").setValue(studentResultsDTO.getStudent().getMotherName());
        fields.get("phone").setValue(studentResultsDTO.getStudent().getMobileNo());
        fields.get("dateOfBirth").setValue(String.valueOf(studentResultsDTO.getStudent().getBirthDate()));
        fields.get("Address").setValue(studentResultsDTO.getStudent().getAddress());

        int i=1;
        double totalMarks = 0, totalMarksObtained = 0;
        for(SubjectResult sub: studentResultsDTO.getMarks()){
            fields.get("sub"+i).setValue(sub.getSubName());
            fields.get("max"+i).setValue(String.valueOf(sub.getMaxMarks()));
            fields.get("pass"+i).setValue(String.valueOf(sub.getPassMarks()));
            fields.get("th"+i).setValue(String.valueOf(sub.getTheoryMarks()));
            fields.get("pr"+i).setValue(String.valueOf(sub.getPracticalMarks()));
            fields.get("tot"+i).setValue(String.valueOf(sub.getTotalMarks()));
            fields.get("grade"+i).setValue(String.valueOf(sub.getGrade()));
            totalMarksObtained += sub.getTotalMarks();
            totalMarks += sub.getMaxMarks();
            i++;
        }

        double percentage = ((double) totalMarksObtained / totalMarks) * 100;
        fields.get("percentage").setValue(String.format("%.2f%%", percentage));

        fields.get("result").setValue(studentResultsDTO.getResult());

        Attendance attendance = studentResultsDTO.getAttendance();

        fields.get("totalWorkingDays").setValue(String.valueOf(attendance.getTotalDays()));
        fields.get("attendance").setValue(String.valueOf(attendance.getPresentDays()));
        fields.get("attendanceDuration").setValue(attendance.getSession());
        double attPerc = attendance.getPercentage();
        fields.get("attPercentage").setValue(String.format("%.2f%%", attPerc));


        fields.get("remark").setValue(studentResultsDTO.getRemark());
        fields.get("place").setValue(studentResultsDTO.getPlace());
        fields.get("date").setValue(studentResultsDTO.getDate());

        form.flattenFields();

        pdfDoc.close();
        templateInput.close();

        return baos.toByteArray();
    }
}
