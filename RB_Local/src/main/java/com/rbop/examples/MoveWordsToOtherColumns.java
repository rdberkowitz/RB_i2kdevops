package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//If a cell contains a reserved word for a column,
// add the word to the correct column

public class MoveWordsToOtherColumns extends SingleRowRule {
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

            //Here is an empty table to store added column values
            Map<String, List<String>> adders = new HashMap<>();
            //Here is an empty table to store columns from which values were moved
            Map<String, List<String>> movers = new HashMap<>();

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
                                    //Add stuff to Adders table; note where it came from in Movers table
                                    if (adders.keySet().contains(k)) { //Column k has already been reported in adders:
                                        List<String> temp = adders.get(k);
                                        List<String> temp1 = movers.get(k);
                                        temp.add(v);
                                        temp1.add(h);
                                        adders.put(k, temp);
                                        movers.put(k,temp1);
                                    } else { //Column k has not yet been reported in adders
                                        List<String> temp = new ArrayList();
                                        List<String> temp1 = new ArrayList();
                                        temp.add(v);
                                        temp1.add(h);
                                        adders.put(k, temp);
                                        movers.put(k,temp1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //Here's where we make suggestions based on original value, adders, and removers
            if (adders.size() > 0) {
                //First make adders into a set
                //Here is an empty table to store added column values
                //First check if added items are already in proposed column
                for (String a : adders.keySet()) {
                    List<String> current = adders.get(a);
                    String orig = rs.getString(a);
                    if (orig == null) {
                        orig = "";
                    }
                    List<String> orig_ = Arrays.asList(orig.split(";"));
                    List<String> newadders = new ArrayList<>(); //this is the items that will be updated
                    //Make a set from original + added values
                    for (String o : orig_) {
                        if (!o.equals("")) {
                            newadders.add(o);
                        }
                    }
                    newadders.addAll(current);
                    Set<String> caseInsensitiveSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                    caseInsensitiveSet.addAll(newadders);
                    int orig_size = orig_.size();
                    if (orig == "") {
                        orig_size = 0;
                    }
                    if (current.size() > orig_size) {
                        String added = " (move words from " + String.join(";", movers.get(a)) + ")";
                        String suggestion = String.join(";", caseInsensitiveSet);
                        errors.put(a, "SUGGESTION: " + suggestion + added);
                    }
                }
            }



            //Operate on UpdateRow value
            if (Updater){

                //Here is an empty table to store added column values
                Map<String, List<String>> adders2 = new HashMap<>();
                //Here is an empty table to store columns from which values were moved
                Map<String, List<String>> movers2 = new HashMap<>();

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
                    if (!(item2.equals(""))) {
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
                                        //Add stuff to Adders table; note where it came from in Movers table
                                        if (adders2.keySet().contains(k)) { //Column k has already been reported in adders:
                                            List<String> temp = adders2.get(k);
                                            List<String> temp1 = movers2.get(k);
                                            temp.add(v);
                                            temp1.add(h);
                                            adders2.put(k, temp);
                                            movers2.put(k,temp1);
                                        } else { //Column k has not yet been reported in adders
                                            List<String> temp = new ArrayList();
                                            List<String> temp1 = new ArrayList();
                                            temp.add(v);
                                            temp1.add(h);
                                            adders2.put(k, temp);
                                            movers2.put(k,temp1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                //Here's where we make suggestions based on original value, adders, and removers
                if (adders2.size() > 0) {
                    //First make adders into a set
                    //Here is an empty table to store added column values
                    //First check if added items are already in proposed column
                    for (String a : adders2.keySet()) {
                        List<String> current = adders2.get(a);
                        String orig = rs.getString(a);
                        if (orig == null) {
                            orig = "";
                        }
                        String orig2 = UpdateRow.get(a);
                        if (orig2 == "") {
                            orig2 = orig;
                        }
                        List<String> orig_ = Arrays.asList(orig2.split(";"));
                        List<String> newadders = new ArrayList<>(); //this is the items that will be updated
                        //Make a set from original + added values
                        for (String o : orig_) {
                            if (!o.equals("")) {
                                newadders.add(o);
                            }
                        }
                        newadders.addAll(current);
                        Set<String> caseInsensitiveSet = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                        caseInsensitiveSet.addAll(newadders);
                        List<String> capsSet = new ArrayList<>();
                        for (String c:caseInsensitiveSet){
                            c=c.toLowerCase();
                            String I = c.substring(0, 1).toUpperCase() + c.substring(1);
                            capsSet.add(I);
                        }
                        int orig_size = orig_.size();
                        if (orig == "") {
                            orig_size = 0;
                        }
                        if (current.size() > orig_size) {
                            String suggestion = String.join(";", capsSet);
                            UpdateRow.put(a, suggestion);
                        }
                    }
                }


            }



                //FINAL REPORTING
            if (errors.size() > 0) {
                return new RuleResult(true, errors, UpdateRow);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return new RuleResult(false, errors, UpdateRow);
    }
}