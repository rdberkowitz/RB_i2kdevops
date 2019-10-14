package com.rbop.examples;


import java.util.HashMap;
import java.util.Map;

//access boolean or string1 or string2: call RuleResult.getXX
//initialise RuleResult somewhere else; create it
public class RuleResult{
    //these are how you access the variables within the class RuleResult
    public boolean isRes() {
        return Res;
    }
    public void setRes(boolean res) {
        Res = res;
    }
    public Map<String, String> getErrs() {return Errs;}
    public Map<String, String> getUpdateRow() {return UpdateRow;}
    public void setErrs(Map<String, String> Errs) {this.Errs = Errs;}
    public void setNewVals(Map<String, String> UpdateRow) {this.UpdateRow = UpdateRow;}

    //define RuleResult
    public boolean Res;
    public Map<String, String> Errs;
    public Map<String, String> UpdateRow;
    public RuleResult(boolean Res){ this.Res = Res; }

    //constructors
    //need to learn about creating maps with multiple variable types...
    public RuleResult(boolean Res, Map<String, String> Errs, Map<String, String> UpdateRow) {
        this.Res = Res;
        this.Errs = Errs;
        this.UpdateRow = UpdateRow;
        //this distinguishes between items that are part of the class, vs items that belong to the class
    }

    //create methods for RuleResult class
    public boolean isBad(){
        return Res;
    }
}

//set a property in this class: create get or set method.
//when RuleResult is created, pass in result bool. and string; assign properties of the class...but no way to access the class now.
//TO ACCESS VARS: create setter and getter methods.