package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//Requires REGION and COUNTRY and COUNTRYREGION values to contain only legal values

public class CountryRegionHasAnd extends SingleRowRule {
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
            String countryregion = rs.getString("Country Region");
            //String countryregion = rs.getString("Country_Region");
            if (countryregion == null) { countryregion = "";}
            List<String> countryregions = Arrays.asList(countryregion.split(";")); //list of actual countryregions in the row

            String countryregion2 = UpdateRow.get("Country Region");
          //  String countryregion2 = UpdateRow.get("Country_Region");
            if (countryregion2 == "") { countryregion2 = countryregion; }
            List<String> countryregions2 = Arrays.asList(countryregion2.trim().split(";"));


            //Check countryregions for 'and' values
            if (!(countryregion.equals(""))) {
                List<String> errs = new ArrayList();
                List<String> keeps = new ArrayList();
                for (String coureg : countryregions) {
                    if (coureg.contains(" and ")) {
                        String newcoureg = coureg.replaceAll(" and ",";");
                        errs.add(coureg);
                        keeps.add(newcoureg);
                    }
                    else {
                        keeps.add(coureg);
                    }
                    if (errs.size()>0) {
                        errors.put("Country Region", "SUGGESTION: Split '" + String.join(";", errs) + "'into two values; ");
                       // errors.put("Country_Region", "SUGGESTION: Split '" + String.join(";", errs) + "'into two values; ");
                    }
                }
            }


            ////Operate on UpdateRow value
            if (Updater) {


                //Check countryregion for legal values
                if (!(countryregion2.equals(""))) {
                    List<String> errs = new ArrayList();
                    List<String> keeps = new ArrayList();
                    for (String coureg : countryregions2) {
                        if (coureg.contains(" and ")) {
                            String newcoureg = coureg.replaceAll(" and ",";");
                            errs.add(coureg);
                            keeps.add(newcoureg);
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