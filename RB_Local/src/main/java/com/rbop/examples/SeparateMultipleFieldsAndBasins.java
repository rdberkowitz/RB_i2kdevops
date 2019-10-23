package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//https://www.i18nqa.com/debug/utf8-debug.html
//https://regexr.com/

public class SeparateMultipleFieldsAndBasins extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs , Map<String, String> UpdateRow) {
        List<String> keys = Arrays.asList(new String[]{ "Basin", "Field"});
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE

        try {

            //First create the real values row
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);

            //Go thru all columns in rs and UpdateRow
            for (String k : keys) {
                String item = rs.getString(k);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
                String item2 = UpdateRow.get(k);
                if (item == null) {
                    item = "";
                }
                if (item2 == "") {
                    item2 = item;
                }

                //Operate on rs value
                if (!(item.equals(""))) {
                    //Replace dash's and slash's with a semicolon
                    String item_ = item.replaceAll("[/](?!\\d)",";");
                    item_ = item_.replaceAll(" - ","|");
                    if (!item.equals(item_)) {
                        //Check if any chars where replaced
                        errors.put(k, "SUGGESTION: "+item_+" (separate multiple names)");
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!(item2.equals(""))) {
                        //Replace dash's and slash's with a semicolon
                        String item_ = item2.replaceAll("[/](?!\\d)",";");
                        item_ = item_.replaceAll(" - ","|");
                        if (!item.equals(item_)) {
                            //Change the key,value pair in UpdateRow
                            UpdateRow.put(k, item_);
                        }
                    }
                }

            }
            if (errors.size() > 0) {
                return new RuleResult(true, errors, UpdateRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RuleResult(false, errors, UpdateRow);
    }
}
