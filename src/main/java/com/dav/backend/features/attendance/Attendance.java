package com.dav.backend.features.attendance;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
@Setter
public class Attendance {
    private String resultId;
    private String session;

    private int totalDays;
    private int presentDays;

    private double percentage;

    public Attendance() {}

    public Attendance(String resultId, String session, int totalDays, int presentDays) {
        this.resultId = resultId;
        this.session = session;
        setTotalDays(totalDays);
        setPresentDays(presentDays);
        // percentage calculated in setters
    }

    @JsonSetter("totalDays")
    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
        calculatePercentage();
    }

    @JsonSetter("presentDays")
    public void setPresentDays(int presentDays) {
        this.presentDays = presentDays;
        calculatePercentage();
    }

    @JsonIgnore
    private void calculatePercentage() {
        if (totalDays == 0) {
            this.percentage = 0;
        } else {
            double rawPercentage = ((double) presentDays / totalDays) * 100;
            BigDecimal bd = BigDecimal.valueOf(rawPercentage);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            this.percentage = bd.doubleValue();
        }
    }
}


