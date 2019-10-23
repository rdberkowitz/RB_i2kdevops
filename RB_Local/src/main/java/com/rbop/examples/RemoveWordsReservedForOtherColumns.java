package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//If a cell contains a reserved word for a column,
// add the word to the correct column

public class RemoveWordsReservedForOtherColumns extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {

        //Things to compare
        List<String> comps = Arrays.asList(new String[]{"Field","Formation"});

        //Things that are words reserved for an individual column
        Map<String, List<String>> cols1 = FixedLists.LegalValues;
        List<String> keys1 = new ArrayList<>(cols1.keySet());
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE


        try {
            for (String h : headers) {
                String item = rs.getString(h);
                if (item == null) {
                    item = "<empty>";
                }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",", realVals);

            //Go thru all columns in rs and UpdateRow
            for (String h : comps) {
                //Here is the value in the column h
                String item = rs.getString(h);
                if (item == null) {
                    item = "";
                }
                String item2 = UpdateRow.get(h);
                if (item2 == "") {
                    item2 = item;
                }

                //Operate on rs value
                if (!(item.equals(""))) {
                    List<String> errs = new ArrayList();
                    //Look at all column names that have specific legal values associated with them
                    for (String k : keys1) {
                        List<String> values = cols1.get(k); //this is a list of strings
                        for (String v : values) {
                            String v1 = String.join("", "\\b", v, "\\b");
                            Pattern p = Pattern.compile(v1, Pattern.CASE_INSENSITIVE);
                            //Check column legal values for columns besides h
                            if (!k.equals(h)) {
                                Matcher m = p.matcher(item.toLowerCase());
                                //If a legal-value from elsewhere is found in the column
                                if (m.find()) {
                                    item = item.replaceAll("(?i)" + v1, "");
                                    item = item;
                                    errs.add(k);
                                }
                            }
                        }
                    }

                    if (errs.size() > 0) {
                        errors.put(h, "SUGGESTION: " + "remove strings that belong in " + String.join(" ", errs));
                    }
                }


                //Operate on UpdateRow value
                if (Updater) {

                    if (!(item2.equals(""))) {
                        List<String> errs = new ArrayList();
                        //Look at all column names that have specific legal values associated with them
                        for (String k : keys1) {
                            List<String> values = cols1.get(k); //this is a list of strings
                            for (String v : values) {
                                String v1 = String.join("", "\\b", v, "\\b");
                                Pattern p = Pattern.compile(v1, Pattern.CASE_INSENSITIVE);
                                //Check column legal values for columns besides h
                                if (!k.equals(h)) {
                                    Matcher m = p.matcher(item2.toLowerCase());
                                    //If a legal-value from elsewhere is found in the column
                                    if (m.find()) {
                                        item2 = item2.replaceAll("(?i)" + v1, "");
                                        item2 = item2;
                                        errs.add(k);
                                    }
                                }
                            }
                        }
                        if (errs.size() > 0) {
                            UpdateRow.put(h, item2);
                        }
                    }
                }

                //FINAL REPORTING
                if (errors.size() > 0) {
                    return new RuleResult(true, errors, UpdateRow);
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return new RuleResult(false, errors, UpdateRow);
    }
}