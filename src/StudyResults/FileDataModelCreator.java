/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StudyResults;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ricard
 */
public class FileDataModelCreator {
    public static HashMap<Integer, Scenario> RetrieveScenarios(DBConnection connector) throws SQLException{
        HashMap<Integer, Scenario> scenarios = new HashMap<>();
        ResultSet res = connector.Query("SELECT * FROM scenario");
        while (res.next()) {
            Scenario scenario = new Scenario(res, connector);
            scenarios.put(scenario.id, scenario);
        }
        return scenarios;
    }
    
    public static HashMap<String, ArrayList<Response>> RetrieveResponsesByUser(DBConnection connector) throws SQLException{
        HashMap<String, ArrayList<Response>> responsesByUser = new HashMap<>();
        ResultSet res = connector.Query("SELECT * FROM turker_picturesurvey_response");
        while (res.next()) {
            Response response = new Response(res);
            if (!responsesByUser.containsKey(response.mturkId)) {
                responsesByUser.put(response.mturkId.trim(), new ArrayList());
            }
            responsesByUser.get(response.mturkId.trim()).add(response);
        }
        return responsesByUser;
    }
    
    public static String GetCSVCase1(HashMap<Integer, Scenario> scenarios, HashMap<String, ArrayList<Response>> responsesByUser, String[] relationships, boolean useTurkerID) {
        return GetCSVCase1(scenarios, responsesByUser, relationships, new boolean[]{true, true, true}, useTurkerID);        
    }

    public static String GetCSVCase2(HashMap<Integer, Scenario> scenarios, HashMap<String, ArrayList<Response>> responsesByUser, boolean useTurkerID) {
        return GetCSVCase2(scenarios, responsesByUser, new boolean[]{true, true, true}, useTurkerID);        
    }

    public static String GetCSVCase3(HashMap<Integer, Scenario> scenarios, HashMap<String, ArrayList<Response>> responsesByUser, boolean useTurkerID) {
        String result = "";
        int turkerIDIndex = 0;
        for (String turkerID : responsesByUser.keySet()) {
            for (int posAllIndex = 0; posAllIndex < 2; posAllIndex++) {
                for (int posCommonIndex = 0; posCommonIndex < 2; posCommonIndex++) {
                    for (int negCommonIndex = 0; negCommonIndex < 2; negCommonIndex++) {
                        for (int negSelfIndex = 0; negSelfIndex < 2; negSelfIndex++) {
                            for (int excAllIndex = 0; excAllIndex < 2; excAllIndex++) {
                                for (int excSelfIndex = 0; excSelfIndex < 2; excSelfIndex++) {
                                    int scenarioID = posAllIndex * 100000 + posCommonIndex * 10000
                                            + negCommonIndex * 1000 + negSelfIndex * 100
                                            + excAllIndex * 10 + excSelfIndex;
                                    double avg = 0;
                                    double countResponses = 0;
                                    for (Response response : responsesByUser.get(turkerID)) {
                                        boolean exAndCommon = false;
                                        Scenario scenario = scenarios.get(response.scenarioID);
                                        for (int i = 0; i < 3; i++) {
                                            if (scenario.policies[i] == Scenario.COMMON
                                                    && scenario.arguments[i] == Scenario.EXCEPTIONAL_CASE) {
                                                exAndCommon = true;
                                                break;
                                            }
                                        }
                                        if (response.result[0] != Scenario.INVALID
                                                && response.result[1] != Scenario.INVALID
                                                && response.result[2] != Scenario.INVALID
                                                && !exAndCommon) {

                                            Double[] argPol = new Double[6];
                                            /*
                                            Integer[] policiesCount = new Integer[3];
                                            for (int i = 0; i < 3; i++) {
                                                policiesCount[i] = 0;
                                            }
                                            */
                                            for (int i = 0; i < 6; i++) {
                                                argPol[i] = 0.0;
                                            }
                                            int minPolicy = 5;
                                            int maxPolicy = -1;
                                            for (int i = 0; i < 3; i++) {
                                                if (scenario.policies[i] > maxPolicy) {
                                                    maxPolicy = scenario.policies[i];
                                                }
                                                if (scenario.policies[i] < minPolicy) {
                                                    minPolicy = scenario.policies[i];
                                                }
                                                int index = Scenario.GetArgPolIndex(scenario.policies[i], scenario.policies[i]);
                                                argPol[index]++;
                                                //policiesCount[scenario.policies[i]]++;
                                            }
                                            
                                            if(argPol[0] >= posAllIndex &&
                                                    argPol[1] >= posCommonIndex &&
                                                    argPol[2] >= negCommonIndex &&
                                                    argPol[3] >= negSelfIndex &&
                                                    argPol[4] >= excAllIndex &&
                                                    argPol[5] >= excSelfIndex)
                                             {
                                                avg += response.result[2];
                                                countResponses++;
                                            }
                                        }
                                    }
                                    String id = Integer.toString(turkerIDIndex);
                                    if(useTurkerID)
                                        id = turkerID;
                                    if (countResponses > 0) {
                                        result += id + "," + scenarioID + "," + (avg / countResponses) + "\r\n";
                                    }
                                }
                            }
                        }
                    }
                }
            }
            turkerIDIndex++;
        }
        return result;
    }
    
    public static String GetCSVCase1(HashMap<Integer, Scenario> scenarios, HashMap<String, ArrayList<Response>> responsesByUser, String[] relationships, boolean[] availableFeatures, boolean useTurkerID) {
        String result = "";
        int turkerIDIndex = 0;
        int maxSensitivity = 6;
        if(!availableFeatures[Scenario.SENSITIVITY_INDEX])
            maxSensitivity = 2;
        int maxSentiment = 6;
        if(!availableFeatures[Scenario.SENTIMENT_INDEX])
            maxSentiment = 2;
        int maxRelIndex = relationships.length;
        if(!availableFeatures[Scenario.RELATIONSHIP_INDEX])
            maxRelIndex = 1;
        for (String turkerID : responsesByUser.keySet()) {
            for (int sensitivity = 1; sensitivity < maxSensitivity; sensitivity++) {
                for(int sentiment = 1; sentiment < maxSentiment; sentiment++){
                    for (int relIndex = 0; relIndex < maxRelIndex; relIndex++) {
                        int scenarioID = 0;
                        if(availableFeatures[Scenario.SENSITIVITY_INDEX])
                            scenarioID += sensitivity * 100;
                        if(availableFeatures[Scenario.SENTIMENT_INDEX])
                            scenarioID += sentiment * 10;
                        if(availableFeatures[Scenario.RELATIONSHIP_INDEX])
                            scenarioID += relIndex;
                        double avg = 0;
                        double countResponses = 0;
                        for (Response response : responsesByUser.get(turkerID)) {
                            boolean exAndCommon = false;
                            Scenario scenario = scenarios.get(response.scenarioID);
                            for (int i = 0; i < 3; i++) {
                                if (scenario.policies[i] == Scenario.COMMON
                                        && scenario.arguments[i] == Scenario.EXCEPTIONAL_CASE) {
                                    exAndCommon = true;
                                    break;
                                }
                            }
                            if (response.result[0] != Scenario.INVALID                                    
                                    && !exAndCommon) {
                                boolean equalSentiment = sentiment == response.sentiment;                                
                                if(!availableFeatures[Scenario.SENTIMENT_INDEX])
                                    equalSentiment = true;
                                boolean equalSensitivity = sensitivity == response.sensitivity;                               
                                if(!availableFeatures[Scenario.SENSITIVITY_INDEX])
                                    equalSensitivity = true;
                                boolean equalRelationship = response.relationship.trim().equalsIgnoreCase(relationships[relIndex].trim());
                                if(!availableFeatures[Scenario.RELATIONSHIP_INDEX])
                                    equalRelationship = true;
                                if (equalSentiment && equalSensitivity && equalRelationship) {
                                    avg += response.result[0];
                                    countResponses++;
                                }
                            }
                        }
                        String id = Integer.toString(turkerIDIndex);
                        if(useTurkerID)
                            id = turkerID;
                        if (countResponses > 0) {
                            result += id + "," + scenarioID + "," + (avg / countResponses) + "\r\n";
                        }
                    }
                }
            }
            turkerIDIndex++;
        }
        return result;
    }
    
    public static String GetCSVCase2(HashMap<Integer, Scenario> scenarios, HashMap<String, ArrayList<Response>> responsesByUser, boolean[] availableFeatures, boolean useTurkerID) {
        String result = "";
        int turkerIDIndex = 0;
        int maxMostRestrictivePolicy = 3;
        if(!availableFeatures[Scenario.MOST_RESTRICTIVE_INDEX])
            maxMostRestrictivePolicy = 1;
        int maxLeastRestrictivePolicy = 3;
        if(!availableFeatures[Scenario.LEAST_RESTRICTIVE_INDEX])
            maxLeastRestrictivePolicy = 1;
        int maxMajorityPolicy = 4;
        if(!availableFeatures[Scenario.MAJORITY_INDEX])
            maxMajorityPolicy = 1;        
        for (String turkerID : responsesByUser.keySet()) {
            for (int mostRestrictivePolicyIndex = 0; mostRestrictivePolicyIndex < maxMostRestrictivePolicy; mostRestrictivePolicyIndex++) {
                for (int leastRestrictivePolicyIndex = 0; leastRestrictivePolicyIndex < maxLeastRestrictivePolicy; leastRestrictivePolicyIndex++) {
                    for (int majorityPolicyIndex = 0; majorityPolicyIndex < maxMajorityPolicy; majorityPolicyIndex++) {                        
                        int scenarioID = 0;
                        if(availableFeatures[Scenario.MOST_RESTRICTIVE_INDEX])
                            scenarioID += (mostRestrictivePolicyIndex + 1) * 100;
                        if(availableFeatures[Scenario.LEAST_RESTRICTIVE_INDEX])
                            scenarioID += (leastRestrictivePolicyIndex + 1) * 10;
                        if(availableFeatures[Scenario.MAJORITY_INDEX])
                            scenarioID += majorityPolicyIndex;                        
                        double avg = 0;
                        double countResponses = 0;
                        for (Response response : responsesByUser.get(turkerID)) {
                            boolean exAndCommon = false;                            
                            Scenario scenario = scenarios.get(response.scenarioID);
                            boolean allPoliciesEqual = scenario.policies[0] == scenario.policies[1] && scenario.policies[1] == scenario.policies[2];
                            for (int i = 0; i < 3; i++) {
                                if (scenario.policies[i] == Scenario.COMMON
                                        && scenario.arguments[i] == Scenario.EXCEPTIONAL_CASE) {
                                    exAndCommon = true;
                                    break;
                                }
                            }
                            if (response.result[0] != Scenario.INVALID
                                    && response.result[1] != Scenario.INVALID
                                    && response.result[2] != Scenario.INVALID
                                    && !exAndCommon
                                    && !allPoliciesEqual) {

                                Integer[] policiesCount = new Integer[3];
                                for (int i = 0; i < 3; i++) {
                                    policiesCount[i] = 0;
                                }
                                int minPolicy = 5;
                                int maxPolicy = -1;
                                for (int i = 0; i < 3; i++) {
                                    if (scenario.policies[i] > maxPolicy) {
                                        maxPolicy = scenario.policies[i];
                                    }
                                    if (scenario.policies[i] < minPolicy) {
                                        minPolicy = scenario.policies[i];
                                    }
                                    policiesCount[scenario.policies[i]]++;
                                }
                                int majorityPolicy = 3;
                                for (int i = 0; i < 3; i++) {
                                    if (policiesCount[i] >= 2) {
                                        majorityPolicy = i;
                                    }
                                }
                                
                                boolean equalMostRestrictivePolicy = maxPolicy == mostRestrictivePolicyIndex;
                                if(!availableFeatures[Scenario.MOST_RESTRICTIVE_INDEX])
                                    equalMostRestrictivePolicy = true;
                                boolean equalLeastRestrictivePolicy = minPolicy == leastRestrictivePolicyIndex;
                                if(!availableFeatures[Scenario.LEAST_RESTRICTIVE_INDEX])
                                    equalLeastRestrictivePolicy = true;
                                boolean equalMajorityPolicy = majorityPolicy == majorityPolicyIndex;
                                if(!availableFeatures[Scenario.MAJORITY_INDEX])
                                    equalMajorityPolicy = true;
                                if (equalMostRestrictivePolicy && equalLeastRestrictivePolicy && equalMajorityPolicy) {
                                    avg += response.result[1];
                                    countResponses++;
                                }
                            }
                        }
                        String id = Integer.toString(turkerIDIndex);
                        if(useTurkerID)
                            id = turkerID;
                        if (countResponses > 0) {
                            result += id + "," + scenarioID + "," + (avg / countResponses) + "\r\n";
                        }
                    }
                }
            }
            turkerIDIndex++;
        }
        return result;
    }
    
    public static String GetCSVCase3(HashMap<Integer, Scenario> scenarios, HashMap<String, ArrayList<Response>> responsesByUser, boolean[] availableFeatures, boolean useTurkerID) {
        String result = "";
        int turkerIDIndex = 0;
        int maxPosAll = 2;
        if(!availableFeatures[Scenario.POSITIVE_SUP_ALL])
            maxPosAll = 1;
        int maxPosCommon = 2;
        if(!availableFeatures[Scenario.POSITIVE_SUP_COMMON])
            maxPosCommon = 1;
        int maxNegCommon = 2;
        if(!availableFeatures[Scenario.NEGATIVE_SUP_COMMON])
            maxNegCommon = 1;
        int maxNegSelf = 2;
        if(!availableFeatures[Scenario.NEGATIVE_SUP_SELF])
            maxNegSelf = 1;
        int maxExcAll = 2;
        if(!availableFeatures[Scenario.EXCEPTIONAL_SUP_ALL])
            maxExcAll = 1;
        int maxExcSelf = 2;
        if(!availableFeatures[Scenario.EXCEPTIONAL_SUP_SELF])
            maxExcSelf = 1;
        for (String turkerID : responsesByUser.keySet()) {
            for (int posAllIndex = 0; posAllIndex < maxPosAll; posAllIndex++) {
                for (int posCommonIndex = 0; posCommonIndex < maxPosCommon; posCommonIndex++) {
                    for (int negCommonIndex = 0; negCommonIndex < maxNegCommon; negCommonIndex++) {
                        for (int negSelfIndex = 0; negSelfIndex < maxNegSelf; negSelfIndex++) {
                            for (int excAllIndex = 0; excAllIndex < maxExcAll; excAllIndex++) {
                                for (int excSelfIndex = 0; excSelfIndex < maxExcSelf; excSelfIndex++) {
                                    int scenarioID = 0;
                                    if(availableFeatures[Scenario.POSITIVE_SUP_ALL])
                                        scenarioID += (posAllIndex + 1) * 100000;
                                    if(availableFeatures[Scenario.POSITIVE_SUP_COMMON])
                                        scenarioID += (posCommonIndex + 1) * 10000;
                                    if(availableFeatures[Scenario.NEGATIVE_SUP_COMMON])
                                        scenarioID += (negCommonIndex + 1) * 1000;
                                    if(availableFeatures[Scenario.NEGATIVE_SUP_SELF])
                                        scenarioID += (negSelfIndex + 1) * 100;
                                    if(availableFeatures[Scenario.EXCEPTIONAL_SUP_ALL])
                                        scenarioID += (excAllIndex + 1) * 10;
                                    if(availableFeatures[Scenario.EXCEPTIONAL_SUP_SELF])
                                        scenarioID += excSelfIndex;                                    
                                    double avg = 0;
                                    double countResponses = 0;
                                    for (Response response : responsesByUser.get(turkerID)) {
                                        boolean exAndCommon = false;
                                        Scenario scenario = scenarios.get(response.scenarioID);
                                        boolean allPoliciesEqual = scenario.policies[0] == scenario.policies[1] && scenario.policies[1] == scenario.policies[2];
                                        for (int i = 0; i < 3; i++) {
                                            if (scenario.policies[i] == Scenario.COMMON
                                                    && scenario.arguments[i] == Scenario.EXCEPTIONAL_CASE) {
                                                exAndCommon = true;
                                                break;
                                            }
                                        }
                                        if (response.result[0] != Scenario.INVALID
                                                && response.result[1] != Scenario.INVALID
                                                && response.result[2] != Scenario.INVALID
                                                && !exAndCommon
                                                && !allPoliciesEqual) {

                                            Double[] argPol = new Double[6];
                                            /*
                                            Integer[] policiesCount = new Integer[3];
                                            for (int i = 0; i < 3; i++) {
                                                policiesCount[i] = 0;
                                            }
                                            */
                                            for (int i = 0; i < 6; i++) {
                                                argPol[i] = 0.0;
                                            }
                                            int minPolicy = 5;
                                            int maxPolicy = -1;
                                            for (int i = 0; i < 3; i++) {
                                                if (scenario.policies[i] > maxPolicy) {
                                                    maxPolicy = scenario.policies[i];
                                                }
                                                if (scenario.policies[i] < minPolicy) {
                                                    minPolicy = scenario.policies[i];
                                                }
                                                int index = Scenario.GetArgPolIndex(scenario.arguments[i], scenario.policies[i]);
                                                argPol[index]++;
                                                //policiesCount[scenario.policies[i]]++;
                                            }
                                            boolean equalPosAll = (argPol[Scenario.POSITIVE_SUP_ALL] == 0 && posAllIndex == 0) || (argPol[Scenario.POSITIVE_SUP_ALL] > 0 && posAllIndex > 0) || !availableFeatures[Scenario.POSITIVE_SUP_ALL];
                                            boolean equalPosCommon = (argPol[Scenario.POSITIVE_SUP_COMMON] == 0 && posCommonIndex == 0) || (argPol[Scenario.POSITIVE_SUP_COMMON] > 0 && posCommonIndex > 0) || !availableFeatures[Scenario.POSITIVE_SUP_COMMON];
                                            boolean equalNegCommon = (argPol[Scenario.NEGATIVE_SUP_COMMON] == 0 && negCommonIndex == 0) || (argPol[Scenario.NEGATIVE_SUP_COMMON] > 0 && negCommonIndex > 0) || !availableFeatures[Scenario.NEGATIVE_SUP_COMMON];
                                            boolean equalNegSelf = (argPol[Scenario.NEGATIVE_SUP_SELF] == 0 && negSelfIndex == 0) || (argPol[Scenario.NEGATIVE_SUP_SELF] > 0 && negSelfIndex > 0) || !availableFeatures[Scenario.NEGATIVE_SUP_SELF];
                                            boolean equalExcAll = (argPol[Scenario.EXCEPTIONAL_SUP_ALL] == 0 && excAllIndex == 0) || (argPol[Scenario.EXCEPTIONAL_SUP_ALL] > 0 && excAllIndex > 0) || !availableFeatures[Scenario.EXCEPTIONAL_SUP_ALL];
                                            boolean equalExcSelf = (argPol[Scenario.EXCEPTIONAL_SUP_SELF] == 0 && excSelfIndex == 0) || (argPol[Scenario.EXCEPTIONAL_SUP_SELF] > 0 && excSelfIndex > 0) || !availableFeatures[Scenario.EXCEPTIONAL_SUP_SELF];
                                            if(equalPosAll && equalPosCommon && equalNegCommon && equalNegSelf && equalExcAll && equalExcSelf)
                                             {
                                                avg += response.result[2];
                                                countResponses++;
                                            }
                                        }
                                    }
                                    String id = Integer.toString(turkerIDIndex);
                                    if(useTurkerID)
                                        id = turkerID;
                                    if (countResponses > 0) {
                                        result += id + "," + scenarioID + "," + (avg / countResponses) + "\r\n";
                                    }
                                }
                            }
                        }
                    }
                }
            }
            turkerIDIndex++;
        }
        return result;
    }
}
