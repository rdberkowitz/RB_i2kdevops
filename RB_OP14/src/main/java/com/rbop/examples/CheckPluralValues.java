package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//If a column contains the plural of a legal values (value + ‘s’)
//replace the plural with the singular (e.g. Icelands --> Iceland)

public class CheckPluralValues extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {

        Map<String, List<String>> cols0 = FixedLists.LegalValues;
        List<String> keys0 = new ArrayList<>(cols0.keySet());

        //Need to make copy of fixedlist in order to add stuff
        Map<String, List<String>> cols = new HashMap<>();
        for (String k:keys0){
            cols.put(k,cols0.get(k));
        }

        Map<String, List<String>> cols1 = FixedLists.CountryRegions;
        List<String> countrykeys = new ArrayList<>(cols1.keySet());
        Map<String, List<String>> cols2 = FixedLists.Regions;
        List<String> regionkeys = new ArrayList<>(cols2.keySet());

        //Add countries and regions and countryregions to copied LegalValues
        List<String> allcountries = new ArrayList<>();
        for (String k : regionkeys){ allcountries.addAll(cols2.get(k)); }
        List<String> allcountryregions = new ArrayList<>();
        for (String k : countrykeys){ allcountryregions.addAll(cols1.get(k)); }
        List<String> allregions = regionkeys;
        cols.put("Region",allregions);
        cols.put("Country",allcountries);
        cols.put("Country_Region",allcountryregions);

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
                    List<String> keeps = input;
                    //System.out.println("column = "+c+" + has values+ "+input);
                    for (int j = 0; j < input.size(); j++) {
                        String in = input.get(j);
                        //Check if any input item contains the plural version of legal value
                        for (String v : values) {
                            //this is the plural value
                            String vplural = String.join("", v, "s");
                            //check if the actual value = the plural of a legal value
                            if (in.toLowerCase().equals(vplural.toLowerCase())) {
                                //replace the plural with the singular
                                keeps.set(j, v);
                                errs.add(vplural);
                            }
                        }
                    }
                    //Remove duplicates that might have been intro'd to list during replacements
                    Set<String> newKeeps = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                    newKeeps.addAll(keeps);

                    if (errs.size() > 0) {
                        String suggestion = String.join(";", newKeeps);
                        errors.put(c, "SUGGESTION: " + suggestion + " ('" + String.join(";", errs) + "' had plural value for " + c + "); ");
                    }
                }

                //Operate on UpdateRow value
                if (Updater) {
                    if (!(item2.equals(""))) {
                        //Manage items that include lists of values separated by semicolons
                        List<String> input = Arrays.asList(item2.split(";"));
                        List<String> errs = new ArrayList();
                        List<String> keeps = input;
                        //System.out.println("column = "+c+" + has values+ "+input);
                        for (int j = 0; j < input.size(); j++) {
                            String in = input.get(j);
                            //Check if any input item contains the plural version of legal value
                            for (String v : values) {
                                String vplural = String.join("", v, "s");
                                if (in.toLowerCase().equals(vplural.toLowerCase())) {
                                    keeps.set(j, v);
                                    errs.add(vplural);
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