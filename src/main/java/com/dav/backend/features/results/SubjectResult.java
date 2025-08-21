package com.dav.backend.features.results;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectResult {
    private String subName;
    private double maxMarks;
    private double passMarks;
    private double theoryMarks;
    private double practicalMarks;

    private double totalMarks;
    private String grade;

    public SubjectResult() {}

    public SubjectResult(String subName, double maxMarks, double passMarks, double theoryMarks, double practicalMarks) {
        this.subName = subName;
        this.maxMarks = maxMarks;
        this.passMarks = passMarks;
        setTheoryMarks(theoryMarks);
        setPracticalMarks(practicalMarks);
        computeTotalAndGrade();
    }

    @JsonSetter("theoryMarks")
    public void setTheoryMarks(double theoryMarks) {
        this.theoryMarks = theoryMarks;
        computeTotalAndGrade();
    }

    @JsonSetter("practicalMarks")
    public void setPracticalMarks(double practicalMarks) {
        this.practicalMarks = practicalMarks;
        computeTotalAndGrade();
    }

    private void computeTotalAndGrade() {
        this.totalMarks = this.theoryMarks + this.practicalMarks;
        this.grade = calculateGrade(totalMarks);
    }

    @JsonIgnore
    private String calculateGrade(double totalMarks) {
        if (totalMarks >= 91) return "A1";
        else if (totalMarks >= 81) return "A2";
        else if (totalMarks >= 71) return "B1";
        else if (totalMarks >= 61) return "B2";
        else if (totalMarks >= 51) return "C1";
        else if (totalMarks >= 41) return "C2";
        else if (totalMarks >= 33) return "D";
        else return "E";
    }
}

