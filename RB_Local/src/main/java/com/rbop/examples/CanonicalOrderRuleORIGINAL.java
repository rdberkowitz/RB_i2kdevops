package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Requires column label to be the correct position
//Block is first item in a name (Block X, not X Block) UNLESS name is all-letter string
//Well, Field, Basin are last item in a name (X Well, not Well X)

public class CanonicalOrderRuleORIGINAL extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }
    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        //if a method throws an exception, need to surround try/catch, ignore it...
        //Good practise: catch exception, report it, use that to debug
        List<String> cols = Arrays.asList(new String[]{"Field", "Well", "Block"});
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();

        //regular expression to use for Block
        Pattern p = Pattern.compile("([A-Za-z]{4,})",Pattern.CASE_INSENSITIVE);

        boolean Updater = true; //allow operation UpdateRow if TRUE

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
                    //This is to make sure that weird characters don't get counted
                    item = item.replaceAll(String.valueOf(Pattern.compile("[\\u200e]+")),"");
                    if (item.contains(c) || item.contains(c.toLowerCase()) || item.contains(c.toUpperCase())) {
                        //split on whitespace and nonbreaking whitespace characters
                        List<String> items = Arrays.asList(item.toUpperCase().split("[\\s\\u00A0]"));
                        String last = items.get(items.size() - 1).toUpperCase();
                        String first = items.get(0).toUpperCase();
                        String item_= item;
                        //if FIRST word is not a Canonical or Canonical+S
                        if (c == "Block" && !(first.equals(c.toUpperCase()) | first.equals(String.join("",c.toUpperCase(),"S")))) {
                            item_ = item.replaceAll(("(?i)"+c), "").trim();
                            //regex match for strings that allow Block at end of name items here
                            Matcher m = p.matcher(item_);
                            Boolean X = m.find();
                            //if regex DOES NOT match, then Block needs to go to beginning of name
                            if (!X){
                                String suggestion = c+" "+item_;
                                errors.put(c, "SUGGESTION: "+suggestion.replaceAll("\\s{2,}", " ").trim()+" (Move " + c + " to beginning of name); ");
                            }
                        }
                        //if LAST word is not a Canonical or Canonical+S
                        if (c != "Block" && !(last.equals(c.toUpperCase()) | last.equals(String.join("",c.toUpperCase(),"S")))) {
                            item_ = item.replaceAll("(?i)"+c, "");
                            String suggestion = item_+" "+c;
                            errors.put(c, "SUGGESTION: "+suggestion.replaceAll("\\s{2,}", " ").trim()+" (Move " + c + " to end of name); ");
                        }
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!item2.equals("")) {
                        //This is to make sure that weird characters don't get counted
                        item = item2.replaceAll(String.valueOf(Pattern.compile("[\\u200e]+")), "");
                        if (item.contains(c) || item.contains(c.toLowerCase()) || item.contains(c.toUpperCase())) {
                            List<String> items = Arrays.asList(item.toUpperCase().split(" "));
                            String last = items.get(items.size() - 1).toUpperCase();
                            String first = items.get(0).toUpperCase();
                            String item_ = item;
                            //if FIRST word is not a Canonical or Canonical+S
                            if (c == "Block" && !(first.equals(c.toUpperCase()) | first.equals(String.join("",c.toUpperCase(),"S")))) {
                                item_ = item.replaceAll("(?i)" + c, "");
                                //regex match for strings that allow Block at end of name items here
                                Matcher m = p.matcher(item_);
                                Boolean X = m.find();
                                //if regex DOES NOT match, then Block needs to go to beginning of name
                                if (!X){
                                    String suggestion = c+" "+item_;
                                    //Change the key,value pair in UpdateRow
                                    UpdateRow.put(c, suggestion);
                                }
                            }
                            //if LAST word is not a Canonical or Canonical+S
                            if (c != "Block" && !(last.equals(c.toUpperCase()) | last.equals(String.join("",c.toUpperCase(),"S")))) {
                                item_ = item.replaceAll("(?i)" + c, "");
                                String suggestion = item_ + " "+c;
                                //Change the key,value pair in UpdateRow
                                UpdateRow.put(c, suggestion.trim());
                            }
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