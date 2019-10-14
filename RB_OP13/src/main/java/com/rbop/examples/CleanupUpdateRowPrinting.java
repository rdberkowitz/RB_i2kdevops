package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//This is the last rule to run.
//Add all "correct" values to UpdateRow, for printing purposes

public class CleanupUpdateRowPrinting extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {

        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE
        Map<String, String> errors = new HashMap<>();

        try {
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);


            ////Operate on UpdateRow value
            if (Updater) {
                for (String h : headers) {
                    String item = rs.getString(h);
                    String updateitem = UpdateRow.get(h);
                    if (updateitem == "") { updateitem = item;}
                    if (updateitem == null) { updateitem = "";}
                    UpdateRow.put(h,updateitem);
                }
            }

            /*if (errors.size() > 0) {
                return new RuleResult(true, errors, UpdateRow);
            }
            */
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RuleResult(false, errors, UpdateRow);
    }
}