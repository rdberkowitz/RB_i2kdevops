package com.rbop.examples;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public abstract class SingleRowRule {
    public abstract String getRealValues();
   // public List<String> headers = Arrays.asList(new String[]{"Mapped_Term","Tag","Oilfield_Places","Source","Comments","Date","Editor","Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field", "Formation", "Well", "Rock_Type", "Geologic_Age", "Type","County","Size_Class","Fully_Resolved","Companies","Operator","Onshore_Offshore"});
    public List<String> headers = Arrays.asList(new String[]{"Id","Term","Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field", "Formation", "Well"});

    public RuleResult evaluateRule(ResultSet rs, int row, Map<String, String> UpdateRow) {
        RuleResult result = evaluate(rs, UpdateRow);
        if (result.isBad()) {
            report(result, row, rs);
        }
        return result;
    }
    abstract RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow);
    //method that will return a RuleResult
    //https://syntaxdb.com/ref/java/abstract
    //each rule evaluates differently, but each rule must evaluate (that's why its abstract)
    //anything that extends SingleRowRule has to evaluate.


    //This should just print ou the error for each single-row-rule
    void report(RuleResult result, int row, ResultSet rs) {
        Map<String, String> err = result.getErrs();
        Map<String, String> newrow = result.getUpdateRow();

        System.out.println(String.format("Row %d: %s %s", row, result.getErrs(), result.getUpdateRow()));
        System.out.println(err.keySet());
    }
}