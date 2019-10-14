package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Check for leading and trailing white space

public class CheckIntermediateSpaces extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        List<String> cols = Arrays.asList(new String[]{"Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field",  "Formation", "Well","Rock_Type", "Geologic_Age", "Onshore_Offshore"});
        Pattern SPACES = Pattern.compile("\\s{2,}");
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE

        try{
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);

            //Go thru all columns in rs and UpdateRow
            for (String c : cols) {
                String item = rs.getString(c);
                if (item == null) { item = ""; }
                String item2 = UpdateRow.get(c);
                if (item2 == "") { item2 = item; }

                //Operate on rs value
                Matcher m1 = SPACES.matcher(item);
                if (m1.find()) {
                    errors.put(c,"SUGGESTION: "+item.replaceAll(" {2,}", " ")+"(Replace multiple spaces); ");
                }

                //Operate on UpdateRow value
                if (Updater) {
                    Matcher m1a = SPACES.matcher(item2);
                    if (m1a.find()) {
                        //Change the key,value pair in UpdateRow
                        UpdateRow.put(c, item2.replaceAll(" {2,}", " "));
                    }
                }

            }
            if (errors.size()>0) {
                return new RuleResult(true, errors, UpdateRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RuleResult(false, errors, UpdateRow);
    }
}