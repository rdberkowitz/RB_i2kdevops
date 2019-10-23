package com.rbop.examples;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class OPMain {


    public static void main(String args[]) {
        try {

            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/i2kdocs","root","Qw58v6A8");
            //ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM alist_geography WHERE id > 0 LIMIT 600");
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM alist_geography WHERE country = 'Peru'");
            /* Connection conn = DriverManager.getConnection("jdbc:h2:file://Users/rachelberkowitz/i2k/OPRB_rb3");
            ResultSet rs = conn.createStatement().executeQuery("SELECT TOP 28 * FROM ALIST_GEOGRAPHY_rb3");
            Connection conn = DriverManager.getConnection("jdbc:h2:file://Users/rachelberkowitz/i2k/OPRB_all_rb4");
            ResultSet rs = conn.createStatement().executeQuery("SELECT TOP 3500 * FROM ALIST_GEOGRAPHY_ALL_rb4");
         */  // List<String> headers = Arrays.asList(new String[]{"ROW NO.","Mapped_Term","Tag","Oilfield_Places","Source","Comments","Date","Editor","Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field", "Formation", "Well", "Rock_Type", "Geologic_Age", "Type","County","Size_Class","Fully_Resolved","Companies","Operator","Onshore_Offshore"});
            List<String> headers = Arrays.asList(new String[]{"Id","Term","Region", "Country", "Country_Region", "Basin", "Leasing_Area", "Block", "Field", "Formation", "Well"});
            HashMap<Integer, ArrayList> OUTPUT = new HashMap<>();
            List<SingleRowRule> rules = new ArrayList();

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            System.out.println("===========HEADER NAMES===========");
            for (int i = 1; i <= columnCount; i++ ) {
                String name = rsmd.getColumnName(i);
                System.out.println(name);
                // Do stuff with name
            }

            ////Check single columns: ORDER MATTERS!!////

            //RULE No. 1
            rules.add(new IllegalCharactersRule()); //Catch & replace illegal characters, for hyphens, dashes, and invisible spaces (all columns)
            //Rule No. 2-3
            rules.add(new SuggestDifferentColumn());  //If a cell contains a reserved word for a column, suggest moving the term to the correct column

            rules.add(new SeparateMultipleFieldsAndBasins()); //Separate slashed A/B Fields and Basins into multiple items (but not dashes)
            rules.add(new CleanPunctuation()); //Clean general puncuation from canonical names: replace [-/]+ with 1 space; replace ['.?:]+ with 0 space
            //Rule No. 4
            rules.add(new CheckCaps()); //Check for Title Case values in (all columns)
            //Rule No. 5-6 (general grammar rules come first)
            rules.add(new BrokenSemicolonLists()); //Semicolon lists need not to have spaces between items. Lists of Blocks, Fields, and Basins need to have canonical name for each item (X Field;Y Field;Z Field)
            rules.add(new CheckTrailingSpaces()); //Trim leading & trailing spaces (all columns)
            rules.add(new CheckIntermediateSpaces()); //Trim intermediate multiple spaces (all columns)
            //Rule No. 7-9
            rules.add(new CheckPluralValues()); ////If a column contains the plural of a legal values (value + ‘s’), replace the plural with the singular (e.g. Icelands --> Iceland)
            rules.add(new MappedValuesRule()); //For specific columns, replace value A with a mapped value B from fixed list col:(A,B)
            rules.add(new IllegalValueRule()); //Only allows certain values for Rock_Type, Geologic_Age, Type (oil/gas/etc), Onshore_Offshore

            //Rules No. 10-15 (these are ordered together)
            rules.add(new CellHasFacilityType());  //Flag any cell that has {"LNG Project","Gas Plant", "Gas Refinery", "Oil Refinery", "Pipeline","Platform"}
            rules.add(new MissingCanonicalSynonymRule()); //Check that item's canonical name must include a value in synonym list

            rules.add(new CanonicalOrderRule()); //Check that Field and Well are at end of name; Block is at beginning of name
            rules.add(new MoveWordsToOtherColumns()); ////If a cell contains a reserved word for a column, copy the word to the correct column (don't remove from original)
            rules.add(new RemoveWordsReservedForOtherColumns()); ////If a cell contains a reserved word for a column, delete that word from the original column
            rules.add(new CountryRegionHasAnd()); //Splits values in COUNTRYREGION that have the word 'and'
            rules.add(new AntarcticaCountryName()); // For REGION = Antarctica treat COUNTRY as 'Antarctica'
            rules.add(new IllegalGeoValues()); //Requires REGION and COUNTRY and COUNTRYREGION values to contain only legal values

            ////Check two columns together////
            //Rules No. 16-18
            rules.add(new GeoValuesMatch()); //Requires REGION and COUNTRY and COUNTRYREGION values to align according to lists
         //   rules.add(new OffshoreRules()); //Check value of 'offshore' words in CountryRegion and Basin vs Offshore_Onshore column
            rules.add(new AustraliaBlockRule()); //Check specific errors in Australia blocks & leasing areas.

            ////Final capitalisation & titlecase cleanup
            rules.add(new CheckTrailingSpaces()); //Trim leading & trailing spaces (all columns)
            rules.add(new CheckCaps()); //Check for Title Case values in (all columns)
            rules.add(new CheckIntermediateSpaces()); //Trim intermediate multiple spaces (all columns)

            ///Final output printer
            rules.add(new CleanupUpdateRowPrinting()); //Add all "correct" values to UpdateRow, instead of leaving no-changes blank

            int rowIndex = 1;
            do {
                Map<String, String> UpdateRow = new HashMap<>(); //This is a copy of rs that will be updated in each step
                for (String h:headers){UpdateRow.put(h,"");}//;UpdateRow.put("ROW NO.",String.valueOf(rowIndex));}
                //System.out.println("INITIAL "+UpdateRow);
                rs.next(); //rs is a HashMap of keys
                //define what the r value is..for each SingleRow Rule in rules, do this...
                System.out.println("row number "+rowIndex);
                for (SingleRowRule r : rules) {
                    RuleResult current = r.evaluateRule(rs, rowIndex, UpdateRow); //returns a rule-result
                    UpdateRow = current.getUpdateRow();
                    if (current.isBad()){ //if we find an error
                        for (Map.Entry<String, String> k : current.Errs.entrySet()){
                            //if current row already has an error associated with it
                            if (OUTPUT.containsKey(rowIndex)) {
                                //grab the row's info from OUTPUT
                                ArrayList currentErrList = OUTPUT.get(rowIndex);
                                //add another key,value pair to that row's OUTPUT info
                                currentErrList.add(k);
                                OUTPUT.put(rowIndex, currentErrList); //arraylist is list of errors mapped to the correct row
                            }
                            //if current row has not yet had an error associated with it
                            else {
                                ArrayList firstErrList = new ArrayList();
                                //create the Real Values row for the row's OUTPUT info
                                firstErrList.add(r.getRealValues());
                                //then add the first error info for that row
                                firstErrList.add(k);
                                OUTPUT.put(rowIndex, firstErrList);
                            }

                        }
                    }
                }
                //transform UpdateRow into an ordered list and a string
                if (OUTPUT.containsKey(rowIndex)) {
                    List<String> listUpdateRow = new ArrayList<>();
                    for (String h:headers){listUpdateRow.add(UpdateRow.get(h));}
                    String stringUpdateRow = String.join(",",listUpdateRow);
                    //add UpdateRow as a 3rd/final value in the OUTPUT list for rowIndex
                    ArrayList finalErrList = OUTPUT.get(rowIndex);
                    finalErrList.add(stringUpdateRow);
                    OUTPUT.put(rowIndex, finalErrList);
                }
                rowIndex++;
            } while (!rs.isLast());
            conn.close();
            //Here's where to order OUPTUT by rowIndex
            Map<Integer,ArrayList> sortedOUTPUT = new TreeMap<Integer,ArrayList>(OUTPUT);
           // File file = new File("/Users/rachelberkowitz/i2k/myfile.csv");
            File file = new File("/home/i2kdevops/rdberkowitz/RB_October/RB_OP14/myfile.csv");
            //FileWriter fileWriter = null;
            Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            try {
                //fileWriter = new FileWriter(file, true);
                headers.replaceAll(String::toUpperCase);
                fileWriter.append(String.join(",",headers));
                fileWriter.append("\r\n");
                fileWriter.append("\r\n");
                //This loop grabs each entry from the error map.
                //The entry has the form of (rowIndex, ArrayList)
                //ArrayList is all the errors for that row in (key,value) format
                //Key = column name; Value = string describing the error
                for (Map.Entry<Integer, ArrayList> o : sortedOUTPUT.entrySet()){
                    ArrayList Values = o.getValue();
                    int last = (Values.size());
                    //This grabs the first 'real values' row
                    fileWriter.append(String.valueOf(o.getKey()));
                    fileWriter.append(" (OP vals)");
                    fileWriter.append(",");
                    fileWriter.append(Values.get(0).toString());
                    fileWriter.append("\r\n");
                    fileWriter.append(String.valueOf(o.getKey()));
                    fileWriter.append(" (errs)");
                    //Now go through all the OP headers
                    for (String h: headers){
                        //for (int i = 1; i < Values.size(); i++){
                        for (int i = 1; i < last-1; i++){
                            //Check each (key, value) pair in the row
                            Map.Entry<String, String> x = (Map.Entry<String, String>) Values.get(i);
                            //Add the error message in the header's place if there's an error
                            if (x.getKey().toLowerCase().equals(h.toLowerCase())){
                                fileWriter.append(x.getValue());
                            }
                            //If there's no error message for that header, add a blank space
                            else{
                                fileWriter.append("");
                            }
                        }
                        fileWriter.append(",");
                    }
                    fileWriter.append("\r\n");
                    //now add the UpdateRow as a 3rd row in each row of the file
                    fileWriter.append(String.valueOf(o.getKey()));
                    //fileWriter.append(" (updates)");
                    fileWriter.append(Values.get(last-1).toString());
                    fileWriter.append("\r\n");
                    fileWriter.append("\r\n");
                }
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch(SQLException se){
            // Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            // Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            // finally block used to close resources
        } // end finally try
    } // end try
}