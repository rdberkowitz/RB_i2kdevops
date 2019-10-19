package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

//Check for leading and trailing white space

public class CheckTrailingSpaces extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        List<String> cols = Arrays.asList(new String[]{"Region", "Country", "Country Region", "Basin", "Leasing Area", "Block", "Field",  "Formation", "Well"});
       // List<String> cols = Arrays.asList(new String[]{"Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field",  "Formation", "Well","Rock_Type", "Geologic_Age", "Onshore_Offshore"});
        Pattern LEADING = Pattern.compile("^\\s+");
        Pattern TRAILING = Pattern.compile("\\s+$");
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
                //Manage items that include lists of values separated by semicolons
                List<String> input = Arrays.asList(item.split(";"));
                List<String> errs = new ArrayList();
                List<String> update = new ArrayList();
                for (String in:input) {
                    Matcher m1 = LEADING.matcher(in);
                    Matcher m2 = TRAILING.matcher(in);
                    if (m1.find() || m2.find()) {
                        errs.add(in.trim());
                        update.add(in.trim());
                    } else {
                        update.add(in);
                    }
                }
                if(errs.size()>0) {
                    String suggestion = String.join(";",update);
                    errors.put(c, "SUGGESTION: " + suggestion + "(Remove trailing spaces); ");
                }

                //Operate on UpdateRow value
                if (Updater) {
                    List<String> input2 = Arrays.asList(item2.split(";"));
                    List<String> errs2 = new ArrayList();
                    List<String> update2 = new ArrayList();
                    for (String in:input2) {
                        Matcher m1 = LEADING.matcher(in);
                        Matcher m2 = TRAILING.matcher(in);
                        if (m1.find() || m2.find()) {
                            errs2.add(in.trim());
                            update2.add(in.trim());
                        } else {
                            update2.add(in);
                        }
                    }

                    if(errs2.size()>0) {
                        String suggestion = String.join(";",update2);
                        //Change the key,value pair in UpdateRow
                        UpdateRow.put(c,suggestion);
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