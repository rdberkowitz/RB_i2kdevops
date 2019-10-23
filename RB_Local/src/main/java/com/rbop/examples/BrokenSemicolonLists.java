package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*Check format of semicolon-separated lists.
Need to remove spaces and in some cases and item labels*/

public class BrokenSemicolonLists extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        List<String> cols = Arrays.asList(new String[]{"Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field",  "Formation", "Well","Rock_Type", "Geologic_Age", "Onshore_Offshore"});
        Pattern P1 = Pattern.compile(";\\s");
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();

        //regular expression to use for Block
        Pattern p = Pattern.compile("([A-Za-z]{4,})",Pattern.CASE_INSENSITIVE);

        boolean Updater = true; //allow operation UpdateRow if TRUE

        try{
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
                Matcher m1 = P1.matcher(item);
                Boolean match = m1.find();
                if (match) {
                    String item_ = item;
                    if (c=="Basin"||c=="Field"){
                        item_ = item.replaceAll("(?i)"+c, "");
                        item_ = item_.replaceAll(String.valueOf(P1)," "+c+";");
                        item_ = item_+c;
                    }
                    if (c=="Block"){
                        item_ = item.replaceAll(("(?i)"+c), "").trim();
                        //regex match for strings that allow Block at end of name items here
                        Matcher mblock = p.matcher(item_);
                        Boolean X = mblock.find();
                        //block name goes at beginning Abcde Block;Fghij BLock...
                        if (X){
                            item_ = item_.replaceAll(String.valueOf(P1)," "+c+";");
                            if (item_.endsWith(";")){item_ = item_.substring(0, item_.length() - 1); }
                            item_ = String.join("",item_," ",c);
                        }
                        //block name goes at end Block X;Block Y;Block Z
                        else{
                            item_ = item_.replaceAll(String.valueOf(P1),";"+c+" ");
                            item_=String.join("",c," ",item_);
                            if (item_.endsWith(";")){item_ = item_.substring(0, item_.length() - 1); }
                        }
                    }
                    else{
                        item_ = item_.replaceAll(String.valueOf(P1),";");
                    }
                    if(item_.endsWith(";")) {
                        item_ = item_.substring(0,item_.length() - 1);
                    }
                    errors.put(c,"SUGGESTION: "+item_+" (Fix semicolon list syntax); ");
                }

                //Operate on UpdateRow value
                if (Updater) {
                    Matcher m1a = P1.matcher(item2);
                    Boolean matcha = m1a.find();
                    if (matcha) {
                        String item_ = item2;
                        if (c == "Basin" || c == "Field") {
                            item_ = item2.replaceAll("(?i)" + c, "");
                            item_ = item_.replaceAll(String.valueOf(P1), " " + c + ";");
                            item_ = item_ + c;
                        }
                        if (c=="Block"){
                            item_ = item2.replaceAll(("(?i)"+c), "").trim();
                            //regex match for strings that allow Block at end of name items here
                            Matcher mblock = p.matcher(item_);
                            Boolean X = mblock.find();
                            //block name goes at beginning Abcde Block;Fghij BLock...
                            if (X){
                                item_ = item_.replaceAll(String.valueOf(P1)," "+c+";");
                                if (item_.endsWith(";")){item_ = item_.substring(0, item_.length() - 1); }
                                item_ = String.join("",item_," ",c);
                            }
                            //block name goes at end Block X;Block Y;Block Z
                            else{
                                item_ = item_.replaceAll(String.valueOf(P1),";"+c+" ");
                                item_=String.join("",c," ",item_);
                                if (item_.endsWith(";")){item_ = item_.substring(0, item_.length() - 1); }
                            }
                        }
                        else{
                            item_ = item_.replaceAll(String.valueOf(P1),";");
                        }
                        if(item_.endsWith(";")) {
                            item_ = item_.substring(0,item_.length() - 1);
                        }
                        //Change the key,value pair in UpdateRow
                        UpdateRow.put(c, item_);
                    }
                }


            }
            if (errors.size()>0) {
                return new RuleResult(true, errors, UpdateRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new RuleResult(false,  errors, UpdateRow);
    }
}