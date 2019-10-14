package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//Requires REGION and COUNTRY and COUNTRYREGION values to contain only legal values

public class IllegalGeoValues extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {

        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE

        //create list of all the countries in the map
        Map<String, List<String>> cols = FixedLists.Regions;
        List<String> keys = new ArrayList<>(cols.keySet());
        List<String> allcountries = new ArrayList<>();
        Map<String, String> errors = new HashMap<>();
        for (String k : keys){
            allcountries.addAll(cols.get(k));
        }

        //create list of all the regions in the map
        List<String> allregions = new ArrayList<>();
        allregions = keys;

        //create list of all the countryregions in the map
        Map<String, List<String>> cols_cr = FixedLists.CountryRegions;
        List<String> keys_cr = new ArrayList<>(cols_cr.keySet());
        List<String> allcountryregions = new ArrayList<>();
        for (String k : keys_cr){
            allcountryregions.addAll(cols_cr.get(k));
        }


        try {
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);

            //Go thru all columns in rs and UpdateRow
            String region = rs.getString("Region");
            String country = rs.getString("Country");
            String countryregion = rs.getString("Country_Region");
            if (region == null) { region = "";}
            if (country == null) { country = "";}
            if (countryregion == null) { countryregion = "";}

            List<String> regions = Arrays.asList(region.split(";"));
            List<String> countries = Arrays.asList(country.split(";")); //list of actual countries in the row
            List<String> countryregions = Arrays.asList(countryregion.split(";")); //list of actual countryregions in the row

            String region2 = UpdateRow.get("Region");
            String country2 = UpdateRow.get("Country");
            String countryregion2 = UpdateRow.get("Country_Region");

            if (region2 == "") { region2 = region; }
            if (country2 == "") { country2 = country; }
            if (countryregion2 == "") { countryregion2 = countryregion; }

            List<String> regions2 = Arrays.asList(region2.trim().split(";"));
            List<String> countries2 = Arrays.asList(country2.trim().split(";"));
            List<String> countryregions2 = Arrays.asList(countryregion2.trim().split(";"));

            ////Operate on rs value for Region
            //Check region for legal values
            if (!(region.equals(""))) {
                List<String> errs = new ArrayList();
                List<String> keeps = new ArrayList();
                //Check if each region is a legal value
                for (String reg : regions) {
                    if (!allregions.contains(reg)) {
                        errs.add(reg);
                    }
                    else {
                        keeps.add(reg);
                    }
                    if (errs.size()>0) {
                        errors.put("Region", "SUGGESTION: REMOVE '" + String.join(";", errs) + "'(Illegal value); ");
                    }
                }
            }
            //Check country for legal values
            if (!(country.equals(""))) {
                List<String> errs = new ArrayList();
                List<String> keeps = new ArrayList();
                for (String cou : countries) {
                    if (!allcountries.contains(cou)) {
                        errs.add(cou);
                    }
                    else {
                        keeps.add(cou);
                    }
                    if (errs.size()>0) {
                        errors.put("Country", "SUGGESTION: REMOVE '" + String.join(";", errs) + "'(Illegal value); ");
                    }
                }
            }
            //Check countryregion for legal values
            if (!(countryregion.equals(""))) {
                List<String> errs = new ArrayList();
                List<String> keeps = new ArrayList();
                for (String coureg : countryregions) {
                    if (!allcountryregions.contains(coureg)) {
                        errs.add(coureg);
                    }
                    else {
                        keeps.add(coureg);
                    }
                    if (errs.size()>0) {
                        errors.put("Country_Region", "SUGGESTION: REMOVE '" + String.join(";", errs) + "'(Illegal value); ");
                    }
                }
            }


            ////Operate on UpdateRow value
            if (Updater) {
                //Check region for legal values
                if (!(region2.equals(""))) {
                    List<String> errs = new ArrayList();
                    List<String> keeps = new ArrayList();
                    //Check if each region is a legal value
                    for (String reg : regions2) {
                        if (!allregions.contains(reg)) {
                            errs.add(reg);
                        }
                        else {
                            keeps.add(reg);
                        }
                        if (errs.size()>0) {
                            UpdateRow.put("Region", String.join(";", keeps));
                        }
                    }
                }
                //Check country for legal values
                if (!(country2.equals(""))) {
                    List<String> errs = new ArrayList();
                    List<String> keeps = new ArrayList();
                    for (String cou : countries2) {
                        if (!allcountries.contains(cou)) {
                            errs.add(cou);
                        }
                        else {
                            keeps.add(cou);
                        }
                        if (errs.size()>0) {
                            UpdateRow.put("Country", String.join(";", keeps));
                        }
                    }
                }
                //Check countryregion for legal values
                if (!(countryregion2.equals(""))) {
                    List<String> errs = new ArrayList();
                    List<String> keeps = new ArrayList();
                    for (String coureg : countryregions2) {
                        if (!allcountryregions.contains(coureg)) {
                            errs.add(coureg);
                        }
                        else {
                            keeps.add(coureg);
                        }
                        if (errs.size()>0) {
                            UpdateRow.put("Country_Region", String.join(";", keeps));
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