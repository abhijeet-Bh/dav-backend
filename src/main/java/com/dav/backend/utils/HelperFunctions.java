package com.dav.backend.utils;

import com.dav.backend.features.results.SubjectResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class HelperFunctions {
    public static double getPercentage(List<SubjectResult> marks) {
        double sum = 0, totalMarks = 0;
        for (SubjectResult mark : marks) {
            sum += mark.getTotalMarks();
            totalMarks += mark.getMaxMarks();
        }
        if (totalMarks == 0) return 0;

        double rawPercentage = (sum / totalMarks) * 100;
        BigDecimal bd = BigDecimal.valueOf(rawPercentage);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
