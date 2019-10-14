package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.commons.collections.list.FixedSizeList;
import org.apache.commons.lang3.StringUtils;

//Check for title case capitalisation

public class CheckCaps extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }
    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        List<String> smallwords = FixedLists.Smallwords;
        List<String> cols = Arrays.asList(new String[]{"Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field",  "Formation", "Well","Rock_Type", "Geologic_Age", "Onshore_Offshore"});
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE

        //First create the real values row
        try {
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
                if (!item.equals("")) {
                 //   List<String> allitems = Arrays.asList(item.trim().split("[;|/]"));
                    List<String> allitems = Arrays.asList(item.trim().split("[;/]"));
                    boolean err = false;
                    List<String> allnewitem = new ArrayList();
                    for (String allit:allitems){
                        List<String> item_ = Arrays.asList(allit.trim().split("(?<=\\s)|(?<=-)|(?<=\\|)"));
                        List<String> newitem = new ArrayList();
                        for (String it : item_) {
                            String I = it.substring(0, 1).toUpperCase() + it.substring(1);
                            String trimmed = it.trim().replaceAll("([-\\|])","");
                            if (!smallwords.contains(trimmed.toLowerCase()) && !it.equals(I)) {
                                newitem.add(I);
                                err = true;
                            }
                            else {
                                newitem.add(it);
                            }
                        }
                        String newitem_ = StringUtils.join(newitem,"");
                        allnewitem.add(newitem_);
                    }
                    if (err){
                        String suggestion = StringUtils.join(allnewitem,";");
                        errors.put(c,"SUGGESTION: "+suggestion+" (All words need to be Title Case); ");
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!(item2.equals(""))) {
                        List<String> allitems = Arrays.asList(item2.trim().split("[;]"));
                        boolean err = false;
                        List<String> allnewitem = new ArrayList();
                        for (String allit : allitems) {
                            List<String> item_ = Arrays.asList(allit.trim().split("(?<=\\s)|(?<=-)|(?<=\\|)"));
                            List<String> newitem = new ArrayList();
                            for (String it : item_) {
                                String trimmed = it.trim().replaceAll("([-\\|])","");
                                String I = it.substring(0, 1).toUpperCase() + it.substring(1);
                                if (!smallwords.contains(trimmed.toLowerCase()) && !it.equals(I)) {
                                    newitem.add(I);
                                    err = true;
                                } else {
                                    newitem.add(it);
                                }
                            }
                            String newitem_ = StringUtils.join(newitem, "");
                            allnewitem.add(newitem_);
                        }
                        if (err) {
                            String suggestion = StringUtils.join(allnewitem, ";");
                            //Change the key,value pair in UpdateRow
                            UpdateRow.put(c, suggestion);
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