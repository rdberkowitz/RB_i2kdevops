package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//If a cell contains a reserved word for a column,
// suggest moving the term to the correct column

public class SuggestDifferentColumn extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {

        //Things to compare
        List<String> comps = Arrays.asList(new String[]{"Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field", "Formation", "Well"});
      //  List<String> comps = Arrays.asList(new String[]{"Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field", "Formation", "Well", "Rock_Type", "Geologic_Age", "Type", "County", "Size_Class", "Fully_Resolved", "Onshore_Offshore"});

        //Things that are part of a canonical name
        Map<String, List<String>> cols = FixedLists.Synonyms;
        List<String> keys = new ArrayList<>(cols.keySet());
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
            //Here is an empty table to store revmoved column values
            Map<String, List<String>> removers = new HashMap<>();


            //Go thru all columns in rs and UpdateRow
            for (String h : comps) {

                //Here is the value in the column h
                String item = rs.getString(h);
                if (item == null) { item = ""; }
                String item2 = UpdateRow.get(h);
                if (item2 == "") { item2 = item; }

                //Operate on rs value
                if (!(item.equals(""))) {
                    List<String> input = Arrays.asList(item.split(";"));
                    //Look at all column names that have specific legal canonical values
                    for (String k : keys) {
                        List<String> values = cols.get(k); //this is a list of strings
                        String v1 = String.join("|", values); //make regex 'or' string out of values
                        String v = String.join("","\\b",v1,"\\b");
                        Pattern p = Pattern.compile(v, Pattern.CASE_INSENSITIVE);
                        //Check column legal synonyms for columns besides h
                        if (!k.equals(h)) {
                            for (String in : input) {
                                String orig_in = in;
                                //look at each allowable canonical synonym
                                Matcher m = p.matcher(in.toLowerCase());
                                //If a canonical synonym is found in the column
                                if (m.find()) {
                                    in=in.replace(h,"").trim(); //take out the original column name h
                                    //First add stuff to Adders table
                                    if (adders.keySet().contains(k)) { //Column k has already been reported in adders:
                                        List<String> temp = adders.get(k);
                                        temp.add(in);
                                        adders.put(k, temp);
                                    } else { //Column k has not yet been reported in adders
                                        List<String> temp = new ArrayList();
                                        temp.add(in);
                                        adders.put(k, temp);
                                    }
                                    //Then add stuff to Removers table
                                    if (removers.keySet().contains(h)) { //Column h has already been reported in removers:
                                        List<String> temp = removers.get(h);
                                        temp.add(in);
                                        removers.put(h, temp);
                                    } else { //Column h has not yet been reported in removers:
                                        List<String> temp = new ArrayList();
                                        temp.add(in);
                                        removers.put(h, temp);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //Here's where we make suggestions based on original value, adders, and removers
            if (removers.size() > 0|adders.size()>0) {
                //List of all the columns we care about
                List<String> update_keys = new ArrayList<>();
                update_keys.addAll(adders.keySet());
                update_keys.addAll(removers.keySet());
                Set<String> set = new LinkedHashSet<>();
                set.addAll(update_keys);
                update_keys.clear();
                update_keys.addAll(set);
                for (String k : update_keys) {
                    //Here is an empty table to record add/remove keys for each column
                    List<String> add_keys = new ArrayList<>();
                    List<String> remove_keys = new ArrayList<>();
                    String orig = rs.getString(k);
                    if (orig == null) { orig = ""; }
                    List<String> orig_ = Arrays.asList(orig.split(";"));
                    List<String> update = new ArrayList<>();
                    update.addAll(orig_);
                    //add stuff from adders
                    if (adders.keySet().contains(k)) {
                        for (String a:adders.get(k)) {
                            for(String r:removers.keySet()){
                                if(removers.get(r).equals(adders.get(k))) {
                                    add_keys.add(r);
                                }
                            }
                            //String suggestion = ("need to add " + adders.get(k) + " to " + update);
                            if (!update.contains(a)){update.add(a);}; //don't duplicate values
                        }
                    }
                    //remove stuff from removers
                    if (removers.keySet().contains(k)) {
                        for (String r:removers.get(k)) {
                            for(String a:adders.keySet()){
                                if(adders.get(a).equals(removers.get(k))) {
                                    remove_keys.add(a);
                                }
                            }
                            update.remove(r);
                        }
                    }
                    //remove empty values from update list
                    for (String u:update){ if (u==""){update.remove(u);} }
                    //build suggestion explanation
                    String added = "";
                    String removed = "";
                    if (add_keys.size()>0){
                        added="add items from "+String.join(";",add_keys);
                    }
                    if (remove_keys.size()>0){
                        removed="move items to "+String.join(";",remove_keys);
                    }

                    String suggestion = String.join(";", update);
                    errors.put(k, "SUGGESTION: " + suggestion+" ("+added+";"+removed+")");
                }

            }


            //Operate on UpdateRow value
            if (Updater) {

                //Here is an empty table to store added column values
                Map<String, List<String>> adders2 = new HashMap<>();
                //Here is an empty table to store revmoved column values
                Map<String, List<String>> removers2 = new HashMap<>();

                //Go thru all columns in UpdateRow
                for (String h : comps) {

                    //Here is the value in the column h
                    String item = rs.getString(h);
                    if (item == null) { item = ""; }
                    String item2 = UpdateRow.get(h);
                    if (item2 == "") { item2 = item; }
                    //Operate on rs value
                    if (!(item2.equals(""))) {
                        List<String> input = Arrays.asList(item2.split(";"));
                        //Look at all column names that have specific legal canonical values
                        for (String k : keys) {
                            List<String> values = cols.get(k); //this is a list of strings
                            String v = String.join("|", values); //make regex 'or' string out of values
                            Pattern p = Pattern.compile(v, Pattern.CASE_INSENSITIVE);
                            //Check column legal synonyms for columns besides h
                            if (!k.equals(h)) {
                                for (String in : input) {
                                    String orig_in = in;
                                    //look at each allowable canonical synonym
                                    Matcher m = p.matcher(in.toLowerCase());
                                    //If a canonical synonym is found in the column
                                    if (m.find()) {
                                        in=in.replace(h,"").trim(); //take out the original column name h
                                        //First add stuff to Adders table
                                        if (adders2.keySet().contains(k)) { //Column k has already been reported in adders:
                                            List<String> temp = adders.get(k);
                                           // temp.add(in);
                                            temp.add(orig_in);
                                            adders2.put(k, temp);
                                        } else { //Column k has not yet been reported in adders
                                            List<String> temp = new ArrayList();
                                            // temp.add(in);
                                            temp.add(orig_in);
                                            adders2.put(k, temp);
                                        }
                                        //Then add stuff to Removers table
                                        if (removers2.keySet().contains(h)) { //Column h has already been reported in removers:
                                            List<String> temp = removers2.get(h);
                                            // temp.add(in);
                                            temp.add(orig_in);
                                            removers2.put(h, temp);
                                        } else { //Column h has not yet been reported in removers:
                                            List<String> temp = new ArrayList();
                                            // temp.add(in);
                                            temp.add(orig_in);
                                            removers2.put(h, temp);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                //Here's where we make suggestions based on original value, adders, and removers
                if (removers2.size() > 0|adders2.size()>0) {
                    //List of all the columns we care about
                    List<String> update_keys = new ArrayList<>();
                    update_keys.addAll(adders2.keySet());
                    update_keys.addAll(removers2.keySet());
                    Set<String> set = new LinkedHashSet<>();
                    set.addAll(update_keys);
                    update_keys.clear();
                    update_keys.addAll(set);
                    System.out.println(update_keys);
                    for (String k : update_keys) {
                        //Here is an empty table to record add/remove keys for each column
                        List<String> add_keys = new ArrayList<>();
                        List<String> remove_keys = new ArrayList<>();
                        String orig = rs.getString(k);
                        if (orig == null) { orig = ""; }
                        List<String> orig_ = Arrays.asList(orig.split(";"));
                        List<String> update = new ArrayList<>();
                        update.addAll(orig_);
                        //add stuff from adders
                        if (adders2.keySet().contains(k)) {
                            for (String a:adders2.get(k)) {
                                for(String r:removers2.keySet()){
                                    if(removers2.get(r).equals(adders2.get(k))) {
                                        add_keys.add(r);
                                    }
                                }
                                //String suggestion = ("need to add " + adders.get(k) + " to " + update);
                                if (!update.contains(a)){update.add(a);System.out.println("new thing for "+k+" is "+a);}; //don't duplicate values
                            }
                        }
                        //remove stuff from removers
                        if (removers2.keySet().contains(k)) {
                            for (String r:removers2.get(k)) {
                                for(String a:adders2.keySet()){
                                    if(adders2.get(a).equals(removers2.get(k))) {
                                        remove_keys.add(a);
                                    }
                                }
                                update.remove(r);
                            }
                        }
                        //remove empty values from update list
                        for (String u:update){ if (u==""){update.remove(u);} }

                        //remove original column synonym values from each item
                        List<String> finalupdate = new ArrayList<>();
                        for (String uk:update_keys){
                            if(!(k.equals(uk))) {
                                for (String up:update){
                                    String test = up;
                                    if (keys.contains(uk)) {
                                        String re = String.join("|(?i)", cols.get(uk));
                                        test = test.replaceAll("(?i)" + re, "").trim();
                                    }
                                    finalupdate.add(test);
                                }
                            }
                        }

                        String suggestion = String.join(";", finalupdate);
                        UpdateRow.put(k, suggestion);
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