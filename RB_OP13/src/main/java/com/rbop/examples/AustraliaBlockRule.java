package com.rbop.examples;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

//All Australia
// (set up for only Western Australia|North West Shelf and Northern Territory Offshore)
//Move items from leasing area to block and check format (e.g. Block WA-xxx-xx)

public class AustraliaBlockRule extends SingleRowRule {
    public String rowRealValues = "";

    @Override
    public String getRealValues() {
        return rowRealValues;
    }

    public RuleResult evaluate(ResultSet rs, Map<String, String> UpdateRow) {
        List<String> cols = Arrays.asList(new String[]{"Country", "Country_Region", "Leasing_Area", "Block"});
        Map<String, String> errors = new HashMap<>();
        List<String> realVals = new ArrayList();

        boolean Updater = true; //allow operation UpdateRow if TRUE


        try{
            for (String h: headers){
                String item = rs.getString(h);
                if (item == null) { item = "<empty>"; }
                realVals.add(item);
            }
            this.rowRealValues = rowRealValues.join(",",realVals);

            //Start by grabbing rows that have Country = Oz
            String country = rs.getString("Country");
            if (country == null) { country = ""; }
            if (!country.equals("") && country.contains("Australia")){
                String country_region = rs.getString("Country_Region");
                String leasing_area = rs.getString("Leasing_Area");
                if (leasing_area == null) { leasing_area = ""; }
                String block = rs.getString("Block");
                if (block == null) { block = ""; }
                List <String> leasing_areas = Arrays.asList(leasing_area.split(";|\\|"));
                List <String> blocks = Arrays.asList(block.split(";"));
                //Then look at country-region specific rules
            //    if (country_region.equals("Western Australia|North West Shelf")||country_region.equals("Northern Territory Offshore")){
                boolean all_cr = true; //allow ALL countryregions within the country
                if(all_cr){
                    List<String> newblocks = new ArrayList();
                    List<String> okblocks = new ArrayList();
                    List<String> cleanleasingareas = new ArrayList(); //these are things to keep as leasing areas
                    List<String> removeblocks = new ArrayList();
                    List<String> removeleasingareas = new ArrayList();
                    Pattern p1 = Pattern.compile("(WA[- ]\\d+[- ]\\w+)"); //pattern for WA-365-R...
                    Pattern p2 = Pattern.compile("(TL[- /]\\d+)|(NT[- /]\\w+)|(EP[- /]\\d+)|(PPL[- /]\\d+)"); //pattern for TL/6 or NT/RL1 or EP/342
                    //Pattern p3 = Pattern.compile("(EP[- /]\\d+)"); //pattern for EP 342

                    //Check if legal Leasing Area has legal block values
                    for (String la:leasing_areas) {
                        Matcher m1 = p1.matcher(la);
                        if (m1.find()){
                            newblocks.add(String.join(" ","Block",m1.group()));
                            cleanleasingareas.add(la);
                            removeleasingareas.add(m1.group().replaceAll("[-/]"," "));
                        }
                        Matcher m2 = p2.matcher(la);
                        if (m2.find()){
                            newblocks.add(String.join(" ","Permit",m2.group()));
                            cleanleasingareas.add(la);
                            removeleasingareas.add(m2.group().replaceAll("[-/]"," "));
                        }
                    }
                    //Leasing areas to keep
                    List<String> okleasingareas = new ArrayList();
                    okleasingareas.addAll(leasing_areas);
                    okleasingareas.removeAll(cleanleasingareas);
                    //Suggest removing Leasing Areas that should be blocks
                    if(removeleasingareas.size()>0) {
                        String suggestion = String.join(";", removeleasingareas);
                        errors.put("Leasing_Area", "SUGGESTION: remove " +suggestion+" (should be Block in Australia); ");
                    }

                    //CHECK IF BLOCK VALUES ARE LEGAL AND IF LEASING AREAS NEED TO BE ADDED

                    for (String b:blocks) {
                        String b_ = b.replaceAll("[-/]"," ");
                        Matcher m1 = p1.matcher(b_);
                        if (m1.find()){
                            //Suggest the full name for the block
                            String blockname = String.join(" ","Block",m1.group());
                            //Don't try to re-add item from Leasing Area if it's already in Block
                            if (newblocks.contains(blockname)) {
                                newblocks.remove(blockname);
                            }
                            newblocks = newblocks;
                            //Check naming convention for ok Blocks
                            if (!(blockname.equals(b_)) && !(newblocks.contains(blockname))) {
                                okblocks.add(blockname);
                                removeblocks.add(b_);
                            }
                        }
                        Matcher m2 = p2.matcher(b_);
                        if (m2.find()){
                            //Suggest the full name for the block
                            String blockname = String.join(" ","Permit",m2.group());
                            //Don't try to re-add item from Leasing Area if it's already in Block
                            if (newblocks.contains(blockname)) {
                                newblocks.remove(blockname);
                            }
                            newblocks = newblocks;
                            //Check naming convention for ok Blocks
                            if (!(blockname.equals(b_)) && !(newblocks.contains(blockname))) {
                                okblocks.add(blockname);
                                removeblocks.add(b_);
                            }
                        }
                    }
                    //Suggest removing and adding Blocks
                    if(newblocks.size()>0 | okblocks.size()>0) {
                        List<String> finalblocks = new ArrayList<String>();
                        finalblocks.addAll(newblocks);
                        finalblocks.addAll(okblocks);
                        String suggestion1 = String.join(";", finalblocks)+" (update/correct Australia Block naming)";
                        String suggestion2 = "";
                        if(removeblocks.size()>0){
                            suggestion2 = String.join(";", removeblocks)+"(remove illegal Australia Blocks)";
                        }
                        errors.put("Block", "SUGGESTION: " +suggestion1+" "+suggestion2);
                    }
                }

            }


                //OPERATE ON UPDATE ROW

            if (Updater) {
                String country2 = UpdateRow.get("Country");
                if (country2 == "") { country2 = country; }
                if (!country2.equals("") &&country2.contains("Australia")) {
                    String country_region = rs.getString("Country_Region");
                    if (country_region == null) { country_region = ""; }
                    String leasing_area = rs.getString("Leasing_Area");
                    if (leasing_area == null) { leasing_area = ""; }
                    String block = rs.getString("Block");
                    if (block == null) { block = ""; }
                    String country_region2 = UpdateRow.get("Country_Region");
                    if (country_region2 == "") { country_region2 = country_region; }
                    String leasing_area2 = UpdateRow.get("Leasing_Area");
                    if (leasing_area2 == "") { leasing_area2 = leasing_area;}
                    String block2 = UpdateRow.get("Block");
                    if (block2 == "") { block = block; }
                    List<String> leasing_areas = Arrays.asList(leasing_area2.split(";|\\|"));
                    List<String> blocks = Arrays.asList(block2.split(";"));

                    //Then look at country-region specific rules
                    //if (country_region2.equals("Western Australia|North West Shelf") || country_region2.equals("Northern Territory Offshore")) {
                      boolean all_cr = true; //allow ALL countryregions within the country
                      if(all_cr){
                        List<String> newblocks = new ArrayList();
                        List<String> okblocks = new ArrayList();
                        List<String> cleanleasingareas = new ArrayList();
                        List<String> removeblocks = new ArrayList();
                        List<String> removeleasingareas = new ArrayList();
                        Pattern p1 = Pattern.compile("(WA[- ]\\d+[- ]\\w+)"); //pattern for WA-365-R...
                        Pattern p2 = Pattern.compile("(TL[- /]\\d+)|(NT[- /]\\w+)|(EP[- /]\\d+)|(PPL[- /]\\d+)"); //pattern for TL/6 or NT/RL1 or EP/342
                        //Pattern p3 = Pattern.compile("(EP[- /]\\d+)"); //pattern for EP 342
                            //Check if legal Leasing Area has legal block values
                        for (String la:leasing_areas) {
                            Matcher m1 = p1.matcher(la);
                            if (m1.find()){
                                newblocks.add(String.join(" ","Block",m1.group()));
                                cleanleasingareas.add(la);
                                removeleasingareas.add(m1.group().replaceAll("[-/]"," "));
                            }
                            Matcher m2 = p2.matcher(la);
                            if (m2.find()){
                                newblocks.add(String.join(" ","Permit",m2.group()));
                                cleanleasingareas.add(la);
                                removeleasingareas.add(m2.group().replaceAll("[-/]"," "));
                            }
                        }
                        //Leasing areas to keep
                        List<String> okleasingareas = new ArrayList();
                        okleasingareas.addAll(leasing_areas);
                        okleasingareas.removeAll(cleanleasingareas);
                            //Suggest removing Leasing Areas that should be blocks
                        if (removeleasingareas.size() > 0) {
                            String suggestion = String.join(";", okleasingareas);
                            UpdateRow.put("Leasing_Area", suggestion);
                        }

                        //CHECK IF BLOCK VALUES ARE LEGAL AND IF LEASING AREAS NEED TO BE ADDED
                        for (String b : blocks) {
                            String b_ = b.replaceAll("[-/]", " ");
                            Matcher m1 = p1.matcher(b_);
                            if (m1.find()) {
                                //Suggest the full name for the block
                                String blockname = String.join(" ", "Block", m1.group());
                                //Don't try to re-add item from Leasing Area if it's already in Block
                                if (newblocks.contains(blockname)) {
                                    newblocks.remove(blockname);
                                }
                                newblocks = newblocks;
                                //Check naming convention for ok Blocks
                                if (!(blockname.equals(b_)) && !(newblocks.contains(blockname))) {
                                    okblocks.add(blockname);
                                    removeblocks.add(b_);
                                }
                            }
                            Matcher m2 = p2.matcher(b_);
                            if (m2.find()) {
                                //Suggest the full name for the block
                                String blockname = String.join(" ", "Permit", m2.group());
                                //Don't try to re-add item from Leasing Area if it's already in Block
                                if (newblocks.contains(blockname)) {
                                    newblocks.remove(blockname);
                                }
                                newblocks = newblocks;
                                //Check naming convention for ok Blocks
                                if (!(blockname.equals(b_)) && !(newblocks.contains(blockname))) {
                                    okblocks.add(blockname);
                                    removeblocks.add(b_);
                                }
                            }
                        }
                        //Suggest removing and adding Blocks
                          if(newblocks.size()>0 | okblocks.size()>0) {
                              List<String> finalblocks = new ArrayList<String>();
                              finalblocks.addAll(newblocks);
                              finalblocks.addAll(okblocks);
                            String suggestion1 = String.join(";", finalblocks);
                            UpdateRow.put("Block", suggestion1);
                        }
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