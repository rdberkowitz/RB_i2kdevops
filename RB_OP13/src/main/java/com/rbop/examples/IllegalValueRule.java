package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Only allows certain values for Rock_Type, Geologic_Age, Type (oil/gas/etc), Onshore_Offshore
//legal value has to be at the end of the name (so, 'Lower Eocene' = ok)

public class IllegalValueRule extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        Map<String, List<String>> cols = FixedLists.LegalValues;
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
            for (String c : keys){
                List<String> values = cols.get(c); //this is a list of strings
                String item = rs.getString(c);
                if (item == null) { item = ""; }
                String item2 = UpdateRow.get(c);
                if (item2 == "") { item2 = item; }

                //Operate on rs value
                if (!(item.equals(""))){
                    //Manage items that include lists of values separated by semicolons
                    List<String> input = Arrays.asList(item.split(";"));
                    List<String> errs = new ArrayList();
                    List<String> keeps = new ArrayList();
                    String v = "(\\b"+String.join("\\b$)|(\\b",values)+"\\b$)";
                    Pattern p = Pattern.compile(v,Pattern.CASE_INSENSITIVE);
                    //Check column legal values for columns besides h
                    for (String in : input){
                        Matcher m = p.matcher(item.toLowerCase());
                        if(m.find()){
                            keeps.add(in);
                        }
                        else{
                            errs.add(in);
                        }
                    }
                    if (errs.size()>0 && keeps.size()>0){
                        String suggestion = String.join(";", keeps);
                        errors.put(c,"SUGGESTION: "+suggestion+" ('"+String.join(";",errs)+"' is illegal value for "+c+"); ");
                    }
                    if (errs.size()>0 && keeps.size()==0){
                        String suggestion = "??";
                        errors.put(c,"SUGGESTION: "+suggestion+" ('"+String.join(";",errs)+"' is illegal value for "+c+"); ");
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!(item2.equals(""))) {
                        //Manage items that include lists of values separated by semicolons
                        List<String> input = Arrays.asList(item2.split(";"));
                        List<String> errs = new ArrayList();
                        List<String> keeps = new ArrayList();
                        String v = "(\\b"+String.join("\\b$)|(\\b",values)+"\\b$)";
                        Pattern p = Pattern.compile(v,Pattern.CASE_INSENSITIVE);
                        for (String in : input){
                            Matcher m = p.matcher(item.toLowerCase());
                            if(m.find()){
                                keeps.add(in);
                            }
                            else{
                                errs.add(in);
                            }
                        }
                        if (errs.size() > 0 && keeps.size() > 0) {
                            String suggestion = String.join(";", keeps);
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