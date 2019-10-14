package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.*;

//https://www.i18nqa.com/debug/utf8-debug.html
//https://regexr.com/

public class IllegalCharactersRule extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs , Map<String, String> UpdateRow) {
        Map<String, String> cols = FixedLists.WeirdChars;
        List<String> keys = Arrays.asList(new String[]{"Mapped_Term", "Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field", "Formation", "Well", "Rock_Type", "Geologic_Age", "Onshore_Offshore"});
        List<String> chars = new ArrayList<>(cols.keySet());
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
                    List<String> item_index = new ArrayList<>();
                    String item_ = item;
                    //Check if any string in the item contains a weird character
                    for (String c : chars) {
                        //Turn map string into regex
                        String v = cols.get(c);
                        Pattern vp = Pattern.compile(v);
                        Matcher m = vp.matcher(item_);
                        //Add to count for each bad character
                        int count = 0;
                        while (m.find()){ count ++; }
                        if (count > 0) {
                            item_ = item_.replaceAll(String.valueOf(vp),c);
                            item_index.add(String.join("", String.valueOf(count), "x", v));
                        }
                    }
                    if (item_index.size() > 0) {
                        //Check if any input item contains a legal value
                        errors.put(k, "SUGGESTION: "+item_+" (Illegal chars " + item_index.toString()+"); ");
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!(item2.equals(""))) {
                        List<String> item_index = new ArrayList<>();
                        String item_ = item2;
                        //Check if any string in the item contains a weird character
                        for (String c : chars) {
                            //Turn map string into regex
                            String v = cols.get(c);
                            Pattern vp = Pattern.compile(v);
                            Matcher m = vp.matcher(item_);
                            //Add to count for each bad character
                            int count = 0;
                            while (m.find()) {
                                count++;
                            }
                            if (count > 0) {
                                item_ = item_.replaceAll(String.valueOf(vp), c);
                                item_index.add(String.join("", String.valueOf(count), "x", v));
                            }
                        }
                        if (item_index.size() > 0) {
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
