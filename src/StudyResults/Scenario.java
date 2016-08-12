/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StudyResults;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Ricard
 */
public class Scenario {    
    
    public static final int POSITIVE_CONSEQUENCE = 0;
    public static final int NEGATIVE_CONSEQUENCE = 1;
    public static final int EXCEPTIONAL_CASE = 2;
    public static final int ALL = 0;
    public static final int COMMON = 1;
    public static final int SELF = 2;
    public static final int INVALID = 3;
    //public static final int[] REGRESS_VALUES = {1, 2, 3};
    public static final String[] REGRESS_VALUES = {"all", "common", "self"};
    public static final int SENSITIVITY_INDEX = 0;
    public static final int SENTIMENT_INDEX = 1;
    public static final int RELATIONSHIP_INDEX = 2;
            
    public int id;
    public Integer[] policies = new Integer[3];
    public Integer[] arguments = new Integer[3];
    
    public static String GetPolicyName(int n){
        if(n == ALL)
            return "ALL";
        if(n == COMMON)
            return "COMMON";
        if(n == SELF)
            return "SELF";
        return "INVALID";
    }

    public Scenario(ResultSet res, DBConnection con) throws SQLException {
        this.id = res.getInt("id");
        ResultSet res1;
        for (int i = 0; i < 3; i++) {
            char c;
            if (i == 0) {
                c = 'a';
            } else if (i == 1) {
                c = 'b';
            } else {
                c = 'c';
            }

            switch (res.getInt("policy_" + c + "_id")) {
                case 1:
                    this.policies[i] = ALL;
                    break;
                case 2:
                    this.policies[i] = COMMON;
                    break;
                case 3:
                    this.policies[i] = SELF;
                    break;
                default:
                    this.policies[i] = INVALID;
                    break;
            }

            res1 = con.Query("SELECT * FROM argument WHERE id = " + res.getInt("argument_" + c + "_id"));
            res1.next();
            switch (res1.getString("name")) {
                case "positive_consequence":
                    this.arguments[i] = POSITIVE_CONSEQUENCE;
                    break;
                case "negative_consequence":
                    this.arguments[i] = NEGATIVE_CONSEQUENCE;
                    break;
                case "exceptional_case":
                    this.arguments[i] = EXCEPTIONAL_CASE;
                    break;
                default:
                    this.arguments[i] = INVALID;
                    break;
            }
        }
    }
    
    public static int GetArgPolIndex(int argument, int policy){
        if(argument == POSITIVE_CONSEQUENCE)
            return policy;
        else if(argument == NEGATIVE_CONSEQUENCE)
            return policy + 1;
        if(policy == ALL)
            return 4;
        else
            return 5;
    }
}
