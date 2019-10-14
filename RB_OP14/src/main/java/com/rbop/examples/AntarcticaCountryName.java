package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//Treats COUNTRY as 'Antarctica' for REGION == Antarctica

public class AntarcticaCountryName extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {

        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE
        Map<String, String> errors = new HashMap<>();

        try {
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);

            //Go thru all columns in rs and UpdateRow
            String region = rs.getString("Region");
            if (region == null) { region = "";}
            List<String> regions = Arrays.asList(region.split(";")); //list of actual countryregions in the row

            String region2 = UpdateRow.get("Region");
            if (region2 == "") { region2 = region; }
            List<String> regions2 = Arrays.asList(region2.trim().split(";"));

            //Check country for "Antarctica"
            List<String> newcountries = new ArrayList();
            if ((regions.contains("Antarctica"))) {
                String country = rs.getString("Country");
                if (country == null) { country = "";}
                List<String> countries = Arrays.asList(country.split(";"));
                if (country.equals("") | !countries.contains("Antarctica")) {
                    newcountries.add("Antarctica");
                    errors.put("Country", "SUGGESTION: add 'Antarctica' to COUNTRY? ");
                }
            }


            ////Operate on UpdateRow value
            if (Updater) {
                //Check country for "Antarctica"
                List<String> newcountries2 = new ArrayList();
                if ((regions2.contains("Antarctica"))) {
                    String country = rs.getString("Country");
                    if (country == null) {
                        country = "";
                    }
                    String country2 = UpdateRow.get("Country");
                    if (country2 == null) {
                        country2 = country;
                    }
                    List<String> countries2 = Arrays.asList(country2.split(";"));
                    if (country2.equals("") | !countries2.contains("Antarctica")) {
                        newcountries2.add("Antarctica");
                        UpdateRow.put("Country", String.join(";", newcountries2));
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