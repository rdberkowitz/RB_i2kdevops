package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//For specific MappedValues in specific columns, replace value A with a mapped value B from list (A,B)

public class MappedValuesRule extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        Map<String, List<Pair<String, String>>> cols = FixedLists.MappedValues;
        // Add Lists as values in the map
        List<String> keys = new ArrayList<>(cols.keySet());
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
            for (String c : keys) {
                List<Pair<String, String>> values = cols.get(c); //this is a list of Pairs
                String item = rs.getString(c);
                if (item == null) {
                    item = "";
                }
                String item2 = UpdateRow.get(c);
                if (item2 == "") {
                    item2 = item;
                }

                //Operate on rs value
                if (!(item.equals(""))) {
                    //Manage items that include lists of values separated by semicolons
                    List<String> input = Arrays.asList(item.split(";"));
                    List<String> errs = new ArrayList();
                    List<String> keeps = input;
                    for (int j = 0; j < input.size(); j++) {
                        String in = input.get(j);
                        //Check if any replacer value matches
                        for (int i = 0; i < values.size(); i++) {
                            String checker = values.get(i).getL();
                            String replacer = values.get(i).getR();
                            //if the first item in the pair is in the real string,
                            //replace it with the 2nd item in the pair
                            if (in.toLowerCase().equals(checker.toLowerCase())) {
                                //see if 'checker' is already in the list
                                keeps.set(j, replacer);
                                errs.add(checker);
                            }

                        }
                    }
                    //Remove duplicates that might have been intro'd to list during replacements
                    Set<String> newKeeps = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                    newKeeps.addAll(keeps);

                    if (errs.size() > 0) {
                        String suggestion = String.join(";", newKeeps);
                        errors.put(c, "SUGGESTION: " + suggestion + " ('" + String.join(";", errs) + "' has mapped value for " + c + "); ");
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!(item2.equals(""))) {
                        //Manage items that include lists of values separated by semicolons
                        List<String> input = Arrays.asList(item.split(";"));
                        List<String> errs = new ArrayList();
                        List<String> keeps = input;
                        for (int j = 0; j < input.size(); j++) {
                            String in = input.get(j);
                            //Check if any replacer value matches
                            for (int i = 0; i < values.size(); i++) {
                                String checker = values.get(i).getL();
                                String replacer = values.get(i).getR();
                                //if the first item in the pair is in the real string,
                                //replace it with the 2nd item in the pair
                                if (in.toLowerCase().equals(checker.toLowerCase())) {
                                    keeps.set(j, replacer);
                                    errs.add(checker);
                                }
                            }
                        }
                        //Remove duplicates that might have been intro'd to list during replacements
                        Set<String> newKeeps = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                        newKeeps.addAll(keeps);

                        if (errs.size() > 0) {
                            String suggestion = String.join(";", newKeeps);
                            //Change the key,value pair in UpdateRow
                            UpdateRow.put(c, suggestion);
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