package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//If any column value has {“Gas Plant”, “Gas Refinery”, “Oil Refinery”, “Pipeline”,”Platform”} then it should go in “Facility Type” column for its canonical name

public class CellHasFacilityType extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        Map<String, List<String>> cols = FixedLists.Facilities;
        List<String> facilities = new ArrayList<>(cols.get("Facility Type"));
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE

        try {
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);

            //Go thru all columns in rs and UpdateRow
            for (String c : cols.get("Columns")){
                String item = rs.getString(c);
                if (item == null) { item = ""; }
                String item2 = UpdateRow.get(c);
                if (item2 == "") { item2 = item; }

                //Operate on rs value
                if (!(item.equals(""))){
                    //Manage items that include lists of values separated by semicolons
                    List<String> input = Arrays.asList(item.split(";"));
                    for (String in : input){
                        //Check if any input item contains a facility value
                        for (String f: facilities){
                            boolean add = in.toUpperCase().contains(f.toUpperCase());
                            //Save list of keys that might have an error
                            if (add==true ){
                                errors.put(c,"SUGGESTION: <move to new Facility Type column>; ");
                            }
                        }
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!(item2.equals(""))) {
                        //Manage items that include lists of values separated by semicolons
                        List<String> input = Arrays.asList(item2.split(";"));
                        for (String in : input) {
                            //Check if any input item contains a facility value
                            for (String f : facilities) {
                                boolean add = in.toUpperCase().contains(f.toUpperCase());
                                //Save list of keys that might have an error
                                if (add == true) {
                                    //Change the key,value pair in UpdateRow
                                    UpdateRow.put(c, "<move to Facility Type column>");
                                }
                            }
                        }
                    }
                }

            }
            if (errors.size()>0){
                return new RuleResult(true, errors, UpdateRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RuleResult(false, errors, UpdateRow);
    }
}