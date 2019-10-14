package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//Requires REGION and COUNTRY and COUNTRYREGION values to align according to lists

public class GeoValuesMatch extends SingleRowRule {
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
        for (String k : keys) {
            allcountries.addAll(cols.get(k));
        }

        //create list of all the regions in the map
        List<String> allregions = new ArrayList<>();
        allregions = keys;

        //create list of all the countryregions in the map
        Map<String, List<String>> cols_cr = FixedLists.CountryRegions;
        List<String> keys_cr = new ArrayList<>(cols_cr.keySet());
        List<String> allcountryregions = new ArrayList<>();
        for (String k : keys_cr) {
            allcountryregions.addAll(cols_cr.get(k));
        }


        try {
            for (String h : headers) {
                String item = rs.getString(h);
                if (item == null) {
                    item = "<empty>";
                }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",", realVals);

            //Go thru all columns in rs and UpdateRow
            String region = rs.getString("Region");
            String country = rs.getString("Country");
            String countryregion = rs.getString("Country_Region");
            if (region == null) {
                region = "";
            }
            if (country == null) {
                country = "";
            }
            if (countryregion == null) {
                countryregion = "";
            }
            List<String> regions = Arrays.asList(region.split(";"));
            List<String> countries = Arrays.asList(country.split(";")); //list of actual countries in the row
            List<String> countryregions = Arrays.asList(countryregion.split(";")); //list of actual countryregions in the row

            String region2 = UpdateRow.get("Region");
            String country2 = UpdateRow.get("Country");
            String countryregion2 = UpdateRow.get("Country_Region");
            if (region2 == "") {
                region2 = region;
            }
            if (country2 == "") {
                country2 = country;
            }
            if (countryregion2 == "") {
                countryregion2 = countryregion;
            }
            List<String> regions2 = Arrays.asList(region2.trim().split(";"));
            List<String> countries2 = Arrays.asList(country2.trim().split(";"));
            List<String> countryregions2 = Arrays.asList(countryregion2.trim().split(";"));

            ////Operate on rs value
            //Find COUNTRIES that are ok for COUNTRYREGIONS
            List<String> okcountries_for_countryreg = new ArrayList();
            if (!(countryregion.equals(""))) {
                for (String coureg : countryregions) {
                    for (String k : keys_cr) {
                        if (cols_cr.get(k).contains(coureg)) {
                            okcountries_for_countryreg.add(k);
                        }
                    }
                }
            }
            //Find REGIONS that are ok for COUNTRIES
            List<String> okregions_for_country = new ArrayList();
            if (!(country.equals(""))) {
                for (String cou : countries) {
                    for (String k : keys) {
                        if (cols.get(k).contains(cou)) {
                            okregions_for_country.add(k);
                        }
                    }
                }
            }
            //Find REGIONS that are ok for COUNTRYREGIONS
            List<String> okregions_for_countryreg = new ArrayList();
            if (!okcountries_for_countryreg.equals("")) {
                for (String okcou : okcountries_for_countryreg) {
                    for (String k : keys) {
                        if (cols.get(k).contains(okcou)) {
                            okregions_for_countryreg.add(k);
                        }
                    }
                }
            }
            //Find COUNTRIES that are ok for REGIONS
            List<String> okcountries_for_region = new ArrayList();
            if (!(region.equals(""))) {
                for (String reg : regions) {
                    for (String k : keys) {
                        if (k.equals(reg)) {
                            okcountries_for_region.addAll(cols.get(k));
                        }
                    }
                }
            }
            //Find COUNTRYREGIONS that are ok for COUNTRIES
            List<String> okcountryreg_for_country = new ArrayList();
            if (!(country.equals(""))) {
                for (String cou : countries) {
                    for (String k : keys_cr) {
                        if (k.equals(cou)) {
                            okcountryreg_for_country.addAll(cols_cr.get(k));
                        }
                    }
                }
            }
            //Find COUNTRYREGIONS that are ok for REGIONS
            List<String> okcountryreg_for_region = new ArrayList();
            if (!okcountries_for_region.equals("")) {
                for (String okcou : okcountries_for_region) {
                    for (String k : keys_cr) {
                        if (k.equals(okcou)) {
                            okcountryreg_for_region.addAll(cols_cr.get(k));
                        }
                    }
                }
            }

            //NOW CHECK IF REGION VALUES ARE OK//
            List<String> errs_reg = new ArrayList();
            List<String> keeps_reg = new ArrayList();
            List<String> addnews_reg = new ArrayList();
            //First check if existing REGIONS are correct
            if (!(region.equals(""))) {
                for (String reg : regions) {
                    if (okregions_for_country.contains(reg)|okregions_for_countryreg.contains(reg)) {
                        keeps_reg.add(reg);
                    }
                    else {
                        errs_reg.add(reg);
                    }
                    if(keeps_reg.size()==0){
                        Set<String> intersection = new HashSet<String>(okregions_for_country); // use the copy constructor
                        intersection.retainAll(okregions_for_countryreg);
                        addnews_reg.addAll(intersection);
                    }
                }
            }
            //Then check if missing REGION needs to be added
            else {
                if (okregions_for_country.size()>0 | okregions_for_countryreg.size()>0){
                    Set<String> intersection = new HashSet<String>(okregions_for_country); // use the copy constructor
                    intersection.retainAll(okregions_for_countryreg);
                    addnews_reg.addAll(intersection);
                }
            }

            if (errs_reg.size() > 0 | addnews_reg.size() > 0) {
                List<String> Suggestion = new ArrayList();
                Suggestion.addAll(addnews_reg);Suggestion.addAll(keeps_reg);
                String suggestion = String.join(";",Suggestion);
                errors.put("Region", "SUGGESTION: REMOVE/ADD '" + String.join(";", errs_reg) + "/"+String.join(";",addnews_reg)+"'(Region doesn't match Country/CountryReg); ");
            }

            //NOW CHECK IF COUNTRY VALUES ARE OK//
            List<String> errs_cou = new ArrayList();
            List<String> keeps_cou = new ArrayList();
            List<String> addnews_cou = new ArrayList();
            //First check if existing COUNTRIES are correct
            if (!(country.equals(""))) {
                for (String cou : countries) {
                    if (okcountries_for_region.contains(cou)|okcountries_for_countryreg.contains(cou)) {
                        keeps_cou.add(cou);
                    }
                    else {
                        errs_cou.add(cou);
                    }
                    if(keeps_cou.size()==0){
                        Set<String> intersection = new HashSet<String>(okcountries_for_region); // use the copy constructor
                        intersection.retainAll(okcountries_for_countryreg);
                        addnews_cou.addAll(intersection);
                    }
                }
            }
            //Then check if missing COUNTRY needs to be added
            else {
                if (okcountries_for_region.size()>0 | okcountries_for_countryreg.size()>0) {
                    Set<String> intersection = new HashSet<String>(okcountries_for_region); // use the copy constructor
                    intersection.retainAll(okcountries_for_countryreg);
                    addnews_cou.addAll(intersection);
                }
            }
            if (errs_cou.size() > 0 | addnews_cou.size() > 0) {
                List<String> Suggestion = new ArrayList();
                Suggestion.addAll(addnews_cou);Suggestion.addAll(keeps_cou);
                String suggestion = String.join(";",Suggestion);
                errors.put("Country", "SUGGESTION: REMOVE/ADD '" + String.join(";", errs_cou) + "/ "+String.join(";",addnews_cou)+"'(Country doesn't match Region/CountryReg); ");
            }

            //NOW CHECK IF COUNTRYREGION VALUES ARE OK//
            List<String> errs_coureg = new ArrayList();
            List<String> keeps_coureg = new ArrayList();
            List<String> addnews_coureg = new ArrayList();
            //First check if existing COUNTRYREGIONS are correct
            if (!(countryregion.equals(""))) {
                for (String coureg : countryregions) {
                    if (okcountryreg_for_country.contains(coureg) | okcountryreg_for_region.contains(coureg)) {
                        keeps_coureg.add(coureg);
                    }
                    else {
                        errs_coureg.add(coureg);
                    }
                }
            }
            if (errs_coureg.size() > 0 | addnews_coureg.size() > 0) {
                List<String> Suggestion = new ArrayList();
                Suggestion.addAll(addnews_coureg);Suggestion.addAll(keeps_coureg);
                String suggestion = String.join(";",Suggestion);
                errors.put("Country_Region", "SUGGESTION: REMOVE '" + String.join(";", errs_coureg) + "'(CountryReg doesn't match Region/Country); ");
            }



            ////Operate on UpdateRow value
            if (Updater) {
                //Find COUNTRIES that are ok for COUNTRYREGIONS
                List<String> okcountries_for_countryreg2 = new ArrayList();
                if (!(countryregion2.equals(""))) {
                    for (String coureg : countryregions2) {
                        for (String k : keys_cr) {
                            if (cols_cr.get(k).contains(coureg)) {
                                okcountries_for_countryreg2.add(k);
                            }
                        }
                    }
                }
                //Find REGIONS that are ok for COUNTRIES
                List<String> okregions_for_country2 = new ArrayList();
                if (!(country2.equals(""))) {
                    for (String cou : countries2) {
                        for (String k : keys) {
                            if (cols.get(k).contains(cou)) {
                                okregions_for_country2.add(k);
                            }
                        }
                    }
                }
                //Find REGIONS that are ok for COUNTRYREGIONS
                List<String> okregions_for_countryreg2 = new ArrayList();
                if (!okcountries_for_countryreg2.equals("")) {
                    for (String okcou : okcountries_for_countryreg2) {
                        for (String k : keys) {
                            if (cols.get(k).contains(okcou)) {
                                okregions_for_countryreg2.add(k);
                            }
                        }
                    }
                }
                //Find COUNTRIES that are ok for REGIONS
                List<String> okcountries_for_region2 = new ArrayList();
                if (!(region2.equals(""))) {
                    for (String reg : regions2) {
                        for (String k : keys) {
                            if (k.equals(reg)) {
                                okcountries_for_region2.addAll(cols.get(k));
                            }
                        }
                    }
                }
                //Find COUNTRYREGIONS that are ok for COUNTRIES
                List<String> okcountryreg_for_country2 = new ArrayList();
                if (!(country2.equals(""))) {
                    for (String cou : countries2) {
                        for (String k : keys_cr) {
                            if (k.equals(cou)) {
                                okcountryreg_for_country2.addAll(cols_cr.get(k));
                            }
                        }
                    }
                }
                //Find COUNTRYREGIONS that are ok for REGIONS
                List<String> okcountryreg_for_region2 = new ArrayList();
                if (!okcountries_for_region2.equals("")) {
                    for (String okcou : okcountries_for_region2) {
                        for (String k : keys_cr) {
                            if (k.equals(okcou)) {
                                okcountryreg_for_region2.addAll(cols_cr.get(k));
                            }
                        }
                    }
                }
                //NOW CHECK IF REGION VALUES ARE OK//
                List<String> errs_reg2 = new ArrayList();
                List<String> keeps_reg2 = new ArrayList();
                List<String> addnews_reg2 = new ArrayList();
                //First check if existing REGIONS are correct
                if (!(region2.equals(""))) {
                    for (String reg : regions2) {
                        if (okregions_for_country2.contains(reg)|okregions_for_countryreg2.contains(reg)) {
                            keeps_reg2.add(reg);
                        }
                        else {
                            errs_reg2.add(reg);
                        }
                        if(keeps_reg2.size()==0){
                            Set<String> intersection = new HashSet<String>(okregions_for_country2); // use the copy constructor
                            intersection.retainAll(okregions_for_countryreg2);
                            addnews_reg2.addAll(intersection);
                        }
                    }
                }
                //Then check if missing REGION needs to be added
                else {
                    if (okregions_for_country2.size()>0 | okregions_for_countryreg2.size()>0){
                        Set<String> intersection = new HashSet<String>(okregions_for_country2); // use the copy constructor
                        intersection.retainAll(okregions_for_countryreg2);
                        addnews_reg2.addAll(intersection);
                    }
                }


                if (errs_reg2.size() > 0 | addnews_reg2.size() > 0) {
                    List<String> Suggestion = new ArrayList();
                    Suggestion.addAll(addnews_reg2);
                    Suggestion.addAll(keeps_reg2);
                    String suggestion = String.join(";", Suggestion);
                    UpdateRow.put("Region", suggestion);
                }

                //NOW CHECK IF COUNTRY VALUES ARE OK//
                List<String> errs_cou2 = new ArrayList();
                List<String> keeps_cou2 = new ArrayList();
                List<String> addnews_cou2 = new ArrayList();
                //First check if existing COUNTRIES are correct
                if (!(country2.equals(""))) {
                    for (String cou : countries2) {
                        if (okcountries_for_region2.contains(cou)|okcountries_for_countryreg2.contains(cou)) {
                            keeps_cou2.add(cou);
                        }
                        else {
                            errs_cou2.add(cou);
                        }
                        if(keeps_cou2.size()==0){
                            Set<String> intersection = new HashSet<String>(okcountries_for_region2); // use the copy constructor
                            intersection.retainAll(okcountries_for_countryreg2);
                            addnews_cou2.addAll(intersection);
                        }
                    }
                }
                //Then check if missing COUNTRY needs to be added
                else {
                    if (okcountries_for_region2.size()>0 | okcountries_for_countryreg2.size()>0) {
                        Set<String> intersection = new HashSet<String>(okcountries_for_region2); // use the copy constructor
                        intersection.retainAll(okcountries_for_countryreg2);
                        addnews_cou2.addAll(intersection);
                    }
                }
                if (errs_cou2.size() > 0 | addnews_cou2.size() > 0) {
                    List<String> Suggestion = new ArrayList();
                    Suggestion.addAll(addnews_cou2);Suggestion.addAll(keeps_cou2);
                    String suggestion = String.join(";",Suggestion);
                    UpdateRow.put("Country", suggestion);
                }

                //NOW CHECK IF COUNTRYREGION VALUES ARE OK//
                List<String> errs_coureg2 = new ArrayList();
                List<String> keeps_coureg2 = new ArrayList();
                List<String> addnews_coureg2 = new ArrayList();
                //First check if existing COUNTRYREGIONS are correct
                if (!(countryregion2.equals(""))) {
                    for (String coureg : countryregions2) {
                        if (okcountryreg_for_country2.contains(coureg) | okcountryreg_for_region2.contains(coureg)) {
                            keeps_coureg2.add(coureg);
                        }
                        else {
                            errs_coureg2.add(coureg);
                        }
                    }
                }

                if (errs_coureg2.size() > 0 | addnews_coureg2.size() > 0) {
                    List<String> Suggestion = new ArrayList();
                    Suggestion.addAll(addnews_coureg2);Suggestion.addAll(keeps_coureg2);
                    String suggestion = String.join(";",Suggestion);
                    UpdateRow.put("Country_Region", suggestion);
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