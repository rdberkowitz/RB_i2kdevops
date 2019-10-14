package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.UnaryOperator;

//Requires COUNTRY and COUNTRYREGION values to align according to lists

public class CountryHasLegalCountryRegionORIGINAL extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        Map<String, List<String>> cols = FixedLists.CountryRegions;
        List<String> keys = new ArrayList<>(cols.keySet());
        Map<String, String> errors = new HashMap<>();
        boolean Updater = true; //allow operation UpdateRow if TRUE

        //create list of all the countryregions in the map
        List<String> allcountryregions = new ArrayList<>();
        List<String> realVals = new ArrayList();
        for (String k : keys){
            allcountryregions.addAll(cols.get(k));
        }
        try {
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);

            //Go thru all columns in rs and UpdateRow
            String country = rs.getString("Country");
            String countryregion = rs.getString("Country_Region");
            if (country == null) { country = ""; }
            if (countryregion == null) { countryregion = ""; }
            List<String> countries = Arrays.asList(country.trim().split(";"));
            List<String> countryregions = Arrays.asList(countryregion.trim().split(";"));

            String country2 = UpdateRow.get("Country");
            String countryregion2 = UpdateRow.get("Country_Region");
            if (country2 == "") { country2 = country; }
            if (countryregion2 == "") { countryregion2 = countryregion; }
            List<String> countries2 = Arrays.asList(country2.trim().split(";"));
            List<String> countryregions2 = Arrays.asList(countryregion2.trim().split(";"));


            ////Operate on rs value for Country
            //Get relevant country dictionary (skip empty strings)
            if (!(country.equals(""))) {
                //Manage items that include lists of values separated by semicolons
                //First check if anything in countries is an illegal value
                List<String> errs = new ArrayList();
                List<String> keeps = new ArrayList();
                //Grab the relevant country dictionary
                for (String c : countries) {
                    if (!keys.contains(c)) {
                        errors.put("Country", "SUGGESTION: REMOVE '"+c+"'(Illegal value for COUNTRY); ");
                    }
                    else {
                        List<String> values = cols.get(c); //this is a list of acceptable countries
                        if (!(countryregion.equals(""))) {
                            for (String cr : countryregions) {
                                if (!values.contains(cr)) {
                                    String c_list = String.join(";",values);
                                    errs.add(c_list);
                                }
                                else {
                                    keeps.add(cr);
                                }
                            }
                        }
                        if (keeps.size()>0){
                            List<String> removers = new ArrayList();
                            for (String cr:countryregions){
                                if (!keeps.contains(cr)){
                                    removers.add(cr);
                                    String suggestion = String.join(";", removers);
                                    errs = new ArrayList();
                                    errors.put("Country_Region", "SUGGESTION: REMOVE '"+suggestion+"'(Country implies countryregion); ");
                                }
                            }
                        }
                        //Don't want to suggest new values for CountryRegion.
                        /*if (errs.size() > 0) {
                            String suggestion = String.join(";", errs);
                            errors.put("Country_Region", "SUGGESTION: " + suggestion + " (Country implies countryregion); ");
                        }*/
                    }
                }
            }

            ////Operate on rs value for CountryRegion
            //Get relevant countryregion dictionary (skip empty strings)
            if (!(countryregion.equals(""))) {
                List<String> errs = new ArrayList();
                List<String> keeps = new ArrayList();
                //Manage items that include lists of values separated by semicolons
                //First check if anything in countryregions is an illegal value
                List<String> allcountryregionsCAPS = allcountryregions;
                UnaryOperator<String> uo = (x) -> x.toUpperCase();
                allcountryregionsCAPS.replaceAll(uo);
                for (String cr : countryregions) {
                    if (!allcountryregionsCAPS.contains(cr.toUpperCase())) {
                        errors.put("Country_Region", "SUGGESTION: REMOVE '"+cr+"' (Illegal value for COUNTRYREGION); ");
                    }
                    //Next find country that goes with each countryregion
                    else {
                        //List<String> vals = new ArrayList();
                        if (!(country.equals(""))) {
                            for (String k : keys) {
                                List<String> vals = cols.get(k); //this is a list of acceptable regions
                                if (vals.contains(cr)) {
                                    if (countries.contains(k)) {
                                        keeps.add(k);
                                    } else {
                                        if (!keeps.contains(k) && countryregions.size()>1) {
                                            errs.add(k);
                                        }
                                    }
                                }
                            }
                            if (errs.size() > 0) {
                                List<String> adders = new ArrayList();
                                adders.addAll(errs);
                                adders.addAll(keeps);
                                //String suggestion = String.join(";", errs);
                                String suggestion = String.join(";", adders);
                                errors.put("Country", "SUGGESTION: " + suggestion + " (Countryregion implies country); ");
                            }
                        }
                    }
                }
            }


            ////Operate on UpdateRow value
            if (Updater) {
                // ...for Country
                if (!(country2.equals(""))) {
                    //Manage items that include lists of values separated by semicolons
                    //First check if anything in countries is an illegal value
                    List<String> errs = new ArrayList();
                    List<String> keeps = new ArrayList();
                    //Grab the relevant country dictionary
                    for (String c : countries2) {
                        if (!keys.contains(c)) {
                            errors.put("Country", "SUGGESTION: REMOVE '" + c + "'(Illegal value for COUNTRY); ");
                        } else {
                            List<String> values = cols.get(c); //this is a list of acceptable countries
                            if (!(countryregion2.equals(""))) {
                                for (String cr : countryregions2) {
                                    if (!values.contains(cr)) {
                                        String c_list = String.join(";", values);
                                        errs.add(c_list);
                                    } else {
                                        keeps.add(cr);
                                    }
                                }
                            }
                            if (keeps.size() > 0) {
                                List<String> removers = new ArrayList();
                                for (String cr : countryregions2) {
                                    if (!keeps.contains(cr)) {
                                        removers.add(cr);
                                        errs = new ArrayList();
                                        //Change the key,value pair in UpdateRow
                                        UpdateRow.put("Country_Region", String.join(";", keeps));
                                    }
                                }
                            }
                            //Don't want to suggest new values for CountryRegion.
                            /*if (errs.size() > 0) {
                                String suggestion = String.join(";", errs);
                                //Change the key,value pair in UpdateRow
                                UpdateRow.put("Country_Region", suggestion);
                            }*/
                        }
                    }
                }

                ////...for CountryRegion
                if (!(countryregion2.equals(""))) {
                    List<String> errs = new ArrayList();
                    List<String> keeps = new ArrayList();
                    //Manage items that include lists of values separated by semicolons
                    //First check if anything in countryregions is an illegal value
                    List<String> allcountryregionsCAPS = allcountryregions;
                    UnaryOperator<String> uo = (x) -> x.toUpperCase();
                    allcountryregionsCAPS.replaceAll(uo);
                    for (String cr : countryregions) {
                        if (!allcountryregionsCAPS.contains(cr.toUpperCase())) {
                            errors.put("Country_Region", "SUGGESTION: REMOVE '" + cr + "' (Illegal value for COUNTRYREGION); ");
                        }
                        //Next find country that goes with each countryregion
                        else {
                            //List<String> vals = new ArrayList();
                            if (!(country2.equals(""))) {
                                for (String k : keys) {
                                    List<String> vals = cols.get(k); //this is a list of acceptable regions
                                    if (vals.contains(cr)) {
                                        if (countries2.contains(k)) {
                                            keeps.add(k);
                                        } else {
                                            if (!keeps.contains(k) && countryregions2.size() > 1) {
                                                errs.add(k);
                                            }
                                        }
                                    }
                                }
                                if (errs.size() > 0) {
                                    List<String> adders = new ArrayList();
                                    adders.addAll(errs);
                                    adders.addAll(keeps);
                                    String suggestion = String.join(";", adders);
                                    //Change the key,value pair in UpdateRow
                                    UpdateRow.put("Country", suggestion);
                                }
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