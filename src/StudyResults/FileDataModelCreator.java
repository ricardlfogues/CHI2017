/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StudyResults;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Ricard
 */
public class FileDataModelCreator {
    public static String GetCSVCase1(HashMap<Integer, Scenario> scenarios, HashMap<String, ArrayList<Response>> responsesByUser, String[] relationships, boolean useTurkerID) {
        String result = "";
        int turkerIDIndex = 0;
        for (String turkerID : responsesByUser.keySet()) {
            for (int sensitivity = 1; sensitivity < 4; sensitivity++) {
                for (int relIndex = 0; relIndex < relationships.length; relIndex++) {
                    int scenarioID = sensitivity * 10 + relIndex;
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
                            boolean equalSensitivity = false;
                            if ((response.sensitivity == 1 || response.sensitivity == 2)
                                    && sensitivity == 1) {
                                equalSensitivity = true;
                            } else if (response.sensitivity == 3 && sensitivity == 2) {
                                equalSensitivity = true;
                            } else if ((response.sensitivity == 4 || response.sensitivity == 5)
                                    && sensitivity == 3) {
                                equalSensitivity = true;
                            }
                            if (equalSensitivity && response.relationship.trim().equalsIgnoreCase(relationships[relIndex].trim())) {
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
            turkerIDIndex++;
        }
        return result;
    }

    public static String GetCSVCase2(HashMap<Integer, Scenario> scenarios, HashMap<String, ArrayList<Response>> responsesByUser, boolean useTurkerID) {
        String result = "";
        int turkerIDIndex = 0;
        for (String turkerID : responsesByUser.keySet()) {
            for (int mostRestrictivePolicyIndex = 0; mostRestrictivePolicyIndex < 3; mostRestrictivePolicyIndex++) {
                for (int leastRestrictivePolicyIndex = 0; leastRestrictivePolicyIndex < 3; leastRestrictivePolicyIndex++) {
                    for (int majorityPolicyIndex = 0; majorityPolicyIndex < 4; majorityPolicyIndex++) {
                        int scenarioID = mostRestrictivePolicyIndex * 100 + leastRestrictivePolicyIndex * 10 + majorityPolicyIndex;
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
                                //least restrictive               
                                if (minPolicy == leastRestrictivePolicyIndex
                                        && maxPolicy == mostRestrictivePolicyIndex
                                        && majorityPolicy == majorityPolicyIndex) {
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
}
