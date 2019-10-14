package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

//Requires column label to be part of item's canonical name

public class MissingCanonicalRule extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        //if a method throws an exception, need to surround try/catch, ignore it...
        //Good practise: catch exception, report it, use that to debug
        List<String> cols = Arrays.asList(new String[]{"Basin", "Field", "Well", "Block"});
        List<String> replacers = FixedLists.Replacers;
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE
        try{

            //First create the real values row
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
                if (!item.equals("") && !(item.toLowerCase().contains(c.toLowerCase()))){
                    //Get rid of any wrong potential canonical name in the item
                    for (String r:replacers){
                        Pattern p = Pattern.compile(r,Pattern.CASE_INSENSITIVE+Pattern.LITERAL);
                        Matcher m = p.matcher(item);
                        //get rid of that word and replace it with the right canonical
                        if (m.find()) {
                            item = item.substring(0, m.start()) + item.substring(m.end(), item.length());
                        }
                    }
                    errors.put(c,"SUGGESTION: "+item.replaceAll("\\s{2,}", " ").trim()+" "+c+" (Canonical needs to include '"+c.toUpperCase()+"'); ");
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!item2.equals("") && !(item2.toLowerCase().contains(c.toLowerCase()))) {
                        //Get rid of any wrong potential canonical name in the item
                        for (String r : replacers) {
                            Pattern p = Pattern.compile(r, Pattern.CASE_INSENSITIVE + Pattern.LITERAL);
                            Matcher m = p.matcher(item2);
                            //get rid of that word and replace it with the right canonical
                            if (m.find()) {
                                item2 = item2.substring(0, m.start()) + item2.substring(m.end(), item2.length());
                            }
                        }
                        UpdateRow.put(c, item2.replaceAll("\\s{2,}", " ").trim() + " " + c);
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