package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//Requires ONSHORE-OFFSHORE values and BASIN or COUNTRYREGION values to align according to lists

public class OffshoreRules extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        Map<String, List<String>> cols = FixedLists.OffshoreWords;
        Map<String, String> errors = new HashMap<>();
        List<String> basinvalues = cols.get("Offshore_Basin"); //this is a list of offshore-words for basins
        List<String> countryregionvalues = cols.get("Offshore_CountryRegion"); //this is a list of offshore-words for countryregions
        List<String> legalvalues = cols.get("Offshore_Legals"); //this is a list of legal words for Onshore_Offshore
        List<String> realVals = new ArrayList();
        boolean Updater = true; //allow operation UpdateRow if TRUE

        try {
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);

            //Go thru all columns in rs
            String onshore = rs.getString("ONSHORE_OFFSHORE");
            String basin = rs.getString("BASIN");
            String countryregion = rs.getString("COUNTRY_REGION");
            if (onshore == null) { onshore = ""; }
            if (basin == null) { basin = ""; }
            if (countryregion == null) { countryregion = ""; }
            //Manage items that include lists of values separated by semicolons
            List<String> onshores = Arrays.asList(onshore.split(";| "));
            List<String> countryregions = Arrays.asList(countryregion.split(";| "));
            List<String> basins = Arrays.asList(basin.split(";| "));
            //Create empty list for values that are legal as offshore
            List<String> okonshores = new ArrayList<>();
            List<String> okcountryregions = new ArrayList<>();
            List<String> okbasins = new ArrayList<>();

            //Go thru all columns in UpdateRow
            String onshore2 = UpdateRow.get("Onshore_Offshore");
            String basin2 = UpdateRow.get("Basin");
            String countryregion2 = UpdateRow.get("Country_Region");
            if (onshore2 == "") { onshore2 = onshore; }
            if (basin2 == "") { basin2 = basin; }
            if (countryregion2 == "") { countryregion2 = countryregion; }
            //Manage items that include lists of values separated by semicolons
            List<String> onshores2 = Arrays.asList(onshore2.split(";| "));
            List<String> countryregions2 = Arrays.asList(countryregion2.split(";| "));
            List<String> basins2 = Arrays.asList(basin2.split(";| "));
            //Create empty list for values that are legal as offshore
            List<String> okonshores2 = new ArrayList<>();
            List<String> okcountryregions2 = new ArrayList<>();
            List<String> okbasins2 = new ArrayList<>();


            //List all CountryRegion that contain an offshore-word
            for (String cr : countryregionvalues) {
                if (countryregions.contains(cr)) { okcountryregions.add(cr); }
                if (countryregions2.contains(cr)) { okcountryregions2.add(cr); }
            }
            //List all Basins that contain an offshore-word
            for (String ba : basinvalues) {
                if (basins.contains(ba)) { okbasins.add(ba); }
                if (basins2.contains(ba)) { okbasins2.add(ba); }
            }
            //List all Onshore values that contain a legal word
            for (String le : legalvalues){
                if (onshores.contains(le)){ okonshores.add(le); }
                if (onshores2.contains(le)){ okonshores2.add(le); }
            }


            ////Operate on rs value for Onshore
            boolean add1 = okcountryregions.isEmpty(); //no offshore words in countryregion
            boolean add2 = okbasins.isEmpty(); //no offshore words in basins
            boolean add3 = Objects.equals(okonshores, onshores); //no legal onshore/offshore words in onshore_offshore
            String err1 = onshore.toUpperCase()+" but BASIN = " + basin + ", COUNTRYREGION = " + countryregion;
            if (!(onshore.equals(""))) {
                //Add a mistake if OnshoreOffshore is not a legal value
                //if (!add3) {
                //    errors.put("ONSHORE_OFFSHORE","Illegal value for onshore/offshore; ");}
                //First check rows that have OnshoreOffshore value = "Offshore"
                if (onshores.contains("Offshore")) {
                    //Add mistake if OnshoreOffshore contains Offshore but neither CountryRegion nor Basin have an offshore word
                    if (add1 && add2 && !(basin=="" && countryregion == "")) {
                        //exception for 'Gulf'
                        if (!countryregion.contains("Gulf") && !basin.contains("Gulf")) {
                            String suggestion = "Onshore";
                            errors.put("Onshore_Offshore", suggestion + " ('BASIN' & 'COUNTRYREGION' both lack offshore-words); ");

                        }
                    }
                }
            }
            //Check what Onshore_Offshore should be according to basin and countryregion
            if (!(basin=="" && countryregion == "")) {
                //either basin or country has an offshore implication
                if ((add2 && !add1)) {
                    //add a mistake if Onshore_Offshore does not include the word Offshore
                    if (!onshores.contains("Offshore")) {
                        {
                            String suggestion = "Offshore";
                            errors.put("Onshore_Offshore", suggestion+" (Value of COUNTRYREGION implies offshore); ");
                        }
                    }
                }
                if ((add1 && !add2)) {
                    //add a mistake if Onshore_Offshore does not include the word Offshore
                    if (!onshores.contains("Offshore")) {
                        {
                            String suggestion = "Offshore";
                            errors.put("Onshore_Offshore", suggestion+" (Value of BASIN implies offshore); ");
                        }
                    }
                }
                //both contain Gulf
                else if (basin.contains("Gulf") && countryregion.contains("Gulf")) {
                    //add a mistake if Onshore_Offshore does not include the word Offshore
                    if (!onshores.contains("Offshore")) {
                        {
                            String suggestion = "Offshore";
                            errors.put("Onshore_Offshore", suggestion+" (the word 'Gulf' implies offshore); ");
                        }
                    }
                }
            }

            ////Operate on UpdateRow value for Onshore
            if (Updater) {
                boolean add1a = okcountryregions2.isEmpty(); //no offshore words in countryregion
                boolean add2a = okbasins2.isEmpty(); //no offshore words in basins
                //boolean add3 = Objects.equals(okonshores, onshores); //no legal onshore/offshore words in onshore_offshore
                //String err1 = onshore.toUpperCase()+" but BASIN = " + basin + ", COUNTRYREGION = " + countryregion;
                if (!(onshore2.equals(""))) {
                    //First check rows that have OnshoreOffshore value = "Offshore"
                    if (onshores2.contains("Offshore")) {
                        //Add mistake if OnshoreOffshore contains Offshore but neither CountryRegion nor Basin have an offshore word
                        if (add1a && add2a && !(basin2 == "" && countryregion2 == "")) {
                            //exception for 'Gulf'
                            if (!countryregion.contains("Gulf") && !basin.contains("Gulf")) {
                                String suggestion = "Onshore";
                                //Change the key,value pair in UpdateRow
                                UpdateRow.put("Onshore_Offshore", suggestion);
                            }
                        }
                    }
                }
                //Check what Onshore_Offshore should be according to basin and countryregion
                if (!(basin2 == "" && countryregion2 == "")) {
                    //either basin or country has an offshore implication
                    if ((add2a && !add1a)) {
                        //add a mistake if Onshore_Offshore does not include the word Offshore
                        if (!onshores2.contains("Offshore")) {
                            {
                                String suggestion = "Offshore";
                                //Change the key,value pair in UpdateRow
                                UpdateRow.put("Onshore_Offshore", suggestion);
                            }
                        }
                    }
                    if ((add1a && !add2a)) {
                        //add a mistake if Onshore_Offshore does not include the word Offshore
                        if (!onshores2.contains("Offshore")) {
                            {
                                String suggestion = "Offshore";
                                //Change the key,value pair in UpdateRow
                                UpdateRow.put("Onshore_Offshore", suggestion);
                            }
                        }
                    }
                    //both contain Gulf
                    else if (basin2.contains("Gulf") && countryregion2.contains("Gulf")) {
                        //add a mistake if Onshore_Offshore does not include the word Offshore
                        if (!onshores2.contains("Offshore")) {
                            {
                                String suggestion = "Offshore";
                                //Change the key,value pair in UpdateRow
                                UpdateRow.put("Onshore_Offshore", suggestion);
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