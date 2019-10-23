package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Requires values in LegalCanonical list to be part of item's canonical name
//First replaces items from synonyms with actual canonicals

public class MissingCanonicalSynonymRule extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        Map<String, List<String>> cols = FixedLists.LegalCanonicals;
        Map<String, List<String>> cols1 = FixedLists.Synonyms;
        List<String> keys = new ArrayList<>(cols.keySet()); //This is all the items that have canonical legal values
        List<String> replacers = new ArrayList<>(cols1.keySet()); //This is all the items that have synonyms
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
            for (String c : keys){ //columns that have legal canonical values
                List<String> values = cols.get(c); //this is a list of legal canonical values
                String item = rs.getString(c);
                if (item == null) { item = ""; }
                String item2 = UpdateRow.get(c);
                if (item2 == "") { item2 = item; }
                boolean checkcompounds = false;

                //make regex 'or' string out of values
                String v = String.join("|",values);
                Pattern p = Pattern.compile(v,Pattern.CASE_INSENSITIVE);

                //Operate on rs value
                if (!(item.equals(""))){
                    //Manage items that include lists of values separated by semicolons
                    List<String> input = Arrays.asList(item.split(";"));
                    List<String> errs = new ArrayList();
                    List<String> keeps = new ArrayList();
                    //Check each item in the cell
                    for (String in : input) {
                        String orig = in;
                        //Manage items that include values separated by |
                        List<String> temp = Arrays.asList(in.split("\\|"));
                        if (temp.size()>input.size()) {
                            checkcompounds = true;
                        }
                        //First check if any synonym is in the item & is not a legal canonical & replace it with legal canonical
                        if (replacers.contains(c)){  //This is all the items that have synonyms
                            for (String r:cols1.get(c)) { //This is all the synonyms for the column
                                Pattern p1 = Pattern.compile(r, Pattern.CASE_INSENSITIVE + Pattern.LITERAL);
                                Matcher m1 = p1.matcher(in);
                                ////if the synonym is NOT a legal canonical, replace the synonym with the right canonical
                                if (m1.find() && !r.toLowerCase().equals(c.toLowerCase())) {
                                    if (!values.contains(r)) { //synonym r is not in legal canonicals
                                        in = in.substring(0, m1.start()) + in.substring(m1.end(), in.length());
                                    }
                                }
                                //Now check the compound part
                                if (checkcompounds){
                                    List<String> comps = Arrays.asList(in.split("\\|"));
                                    List<String> comps_temp = new ArrayList<>();
                                    for (String co:comps){
                                        Matcher mc = p1.matcher(co);
                                        if (mc.find() && !r.toLowerCase().equals(c.toLowerCase())) {
                                            if (!values.contains(r)) { //synonym r is not in legal canonicals
                                                co = co.substring(0, mc.start()) + co.substring(mc.end(), co.length());

                                            }
                                        }
                                        comps_temp.add(co);
                                    }
                                    in = String.join("|",comps_temp);
                                }
                            }
                        }
                        //look at each allowable canonical synonym
                        Matcher mc1 = p.matcher(in.toLowerCase());
                        //First do each compound component
                        if (checkcompounds){
                            List<String> comps = Arrays.asList(in.split("\\|"));
                            List<String> comps_temp = new ArrayList<>();
                            for (String co:comps){
                                if (!mc1.find()) {
                                    co = String.join(" ",co,c);
                                    errs.add(orig);
                                }
                                comps_temp.add(co);
                            }
                            in = String.join("|",comps_temp);
                        }
                        //Now look at each allowable canonical synonym
                        Matcher m = p.matcher(in.toLowerCase());
                        if (!m.find()) {
                            //if none of the allowables are in the name, call it an error
                            errs.add(in);
                            //tack on the column name to the end of the name
                            keeps.add(String.join(" ",in,c));
                        } else {
                            //if at least one allowable is in the name, keep the value
                            keeps.add(in);
                        }
                    }
                    //Remove duplicates in errs
                    Set<String> listToSet = new HashSet<String>(errs);
                    List<String> listOfErrs = new ArrayList<String>(listToSet);
                    if (listOfErrs.size()> 0) {
                        String suggestion = String.join(";", keeps);
                        errors.put(c,"SUGGESTION: "+suggestion+" ('"+String.join(";",listOfErrs)+"' is missing a canonical value for "+c+"); ");
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!(item2.equals(""))) {
                        //Manage items that include lists of values separated by semicolons
                        List<String> input = Arrays.asList(item2.split(";"));
                        List<String> errs = new ArrayList();
                        List<String> keeps = new ArrayList();
                        //Check each item in the cell
                        for (String in : input) {
                            String orig = in;
                            List<String> temp = Arrays.asList(in.split("\\|"));
                            if (temp.size()>input.size()) {
                                checkcompounds = true;
                            }
                            //First check if any synonym is in the item & replace it with legal canonical
                            if (replacers.contains(c)){
                                for (String r:cols1.get(c)){
                                    Pattern p1 = Pattern.compile(r,Pattern.CASE_INSENSITIVE+Pattern.LITERAL);
                                    Matcher m1 = p1.matcher(in);
                                    ////if the synonym is NOT a legal canonical, replace the synonym with the right canonical
                                    if (m1.find() && !r.toLowerCase().equals(c.toLowerCase())) {
                                        if (!values.contains(r)) { //synonym r is not in legal canonicals
                                            in = in.substring(0, m1.start()) + in.substring(m1.end(), in.length());

                                        }
                                    }
                                    //Now check the compound part
                                    if (checkcompounds){
                                        List<String> comps = Arrays.asList(in.split("\\|"));
                                        List<String> comps_temp = new ArrayList<>();
                                        for (String co:comps){
                                            Matcher mc = p1.matcher(co);
                                            if (mc.find() && !r.toLowerCase().equals(c.toLowerCase())) {
                                                if (!values.contains(r)) { //synonym r is not in legal canonicals
                                                    co = co.substring(0, mc.start()) + co.substring(mc.end(), co.length());

                                                }
                                            }
                                            comps_temp.add(co);
                                        }
                                        in = String.join("|",comps_temp);
                                    }

                                }
                            }

                            //look at each allowable canonical synonym
                            Matcher mc1 = p.matcher(in.toLowerCase());
                            //First do each compound component
                            if (checkcompounds){
                                List<String> comps = Arrays.asList(in.split("\\|"));
                                List<String> comps_temp = new ArrayList<>();
                                for (String co:comps){
                                    if (!mc1.find()) {
                                        co = String.join(" ",co,c);
                                        errs.add(orig);
                                    }
                                    comps_temp.add(co);
                                }
                                in = String.join("|",comps_temp);
                            }
                            //Now look at re-built compounds
                            Matcher m = p.matcher(in.toLowerCase());
                            if (!m.find()) {
                                //if none of the allowables are in the name, call it an error
                                errs.add(in);
                                //tack on the column name to the end of the name
                                keeps.add(String.join(" ",in,c));
                            } else {
                                //if at least one allowable is in the name, keep the value
                                keeps.add(in);
                            }
                        }
                        //Remove duplicates in errs
                        Set<String> listToSet = new HashSet<String>(errs);
                        List<String> listOfErrs = new ArrayList<String>(listToSet);
                        if (listOfErrs.size()> 0) {
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