/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StudyResults;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.mahout.cf.taste.common.TasteException;

/**
 *
 * @author Ricard
 */
public class FeatureSelection {

    public static void SelectFeaturesCase1() throws ClassNotFoundException, SQLException, FileNotFoundException, UnsupportedEncodingException, IOException, TasteException {
        DBConnection connector = new DBConnection("jdbc:mysql://localhost/final_multi_party", "root", "");
        HashMap<Integer, Scenario> scenarios = FileDataModelCreator.RetrieveScenarios(connector);
        HashMap<String, ArrayList<Response>> responsesByUser = FileDataModelCreator.RetrieveResponsesByUser(connector);

        String[] relationships = new String[]{"family", "friends", "colleagues"};

        PrintWriter writer;
        Evaluator evaluator;
        boolean[] availableFeatures = new boolean[3];
        for (int sensitivity = 0; sensitivity < 2; sensitivity++) {
            for (int sentiment = 0; sentiment < 2; sentiment++) {
                for (int relationship = 0; relationship < 2; relationship++) {
                    if (sensitivity > 0 || sentiment > 0 || relationship > 0) {
                        availableFeatures[Scenario.SENSITIVITY_INDEX] = sensitivity > 0;
                        availableFeatures[Scenario.SENTIMENT_INDEX] = sentiment > 0;
                        availableFeatures[Scenario.RELATIONSHIP_INDEX] = relationship > 0;
                        String inputFilename = "Ressources/FeatureSearch/Input/featureSearchCase1_Sens_" + sensitivity + "_Sent_" + sentiment + "_Rel_" + relationship + ".csv";
                        writer = new PrintWriter(inputFilename, "UTF-8");
                        writer.write(FileDataModelCreator.GetCSVCase1(scenarios, responsesByUser, relationships, availableFeatures, false));
                        writer.close();
                        evaluator = new Evaluator();
                        evaluator.Evaluate(inputFilename, "Ressources/FeatureSearch/Output/Sens_" + sensitivity + "_Sent_" + sentiment + "_Rel_" + relationship + ".txt");
                    }
                }
            }
        }
    }

    public static void SelectFeaturesCase2() throws ClassNotFoundException, SQLException, FileNotFoundException, UnsupportedEncodingException, IOException, TasteException {
        DBConnection connector = new DBConnection("jdbc:mysql://localhost/final_multi_party", "root", "");
        HashMap<Integer, Scenario> scenarios = FileDataModelCreator.RetrieveScenarios(connector);
        HashMap<String, ArrayList<Response>> responsesByUser = FileDataModelCreator.RetrieveResponsesByUser(connector);

        PrintWriter writer;
        Evaluator evaluator;
        boolean[] availableFeatures = new boolean[3];
        for (int mostRestrictivePolicy = 0; mostRestrictivePolicy < 2; mostRestrictivePolicy++) {
            for (int leastRestrictivePolicy = 0; leastRestrictivePolicy < 2; leastRestrictivePolicy++) {
                for (int majorityPolicy = 0; majorityPolicy < 2; majorityPolicy++) {
                    if (mostRestrictivePolicy > 0 || leastRestrictivePolicy > 0 || majorityPolicy > 0) {
                        availableFeatures[Scenario.MOST_RESTRICTIVE_INDEX] = mostRestrictivePolicy > 0;
                        availableFeatures[Scenario.LEAST_RESTRICTIVE_INDEX] = leastRestrictivePolicy > 0;
                        availableFeatures[Scenario.MAJORITY_INDEX] = majorityPolicy > 0;
                        String inputFilename = "Ressources/FeatureSearch/Input/featureSearchCase2_Most_" + mostRestrictivePolicy + "_Least_" + leastRestrictivePolicy + "_Maj_" + majorityPolicy + ".csv";
                        writer = new PrintWriter(inputFilename, "UTF-8");
                        writer.write(FileDataModelCreator.GetCSVCase2(scenarios, responsesByUser, availableFeatures, false));
                        writer.close();
                        evaluator = new Evaluator();
                        evaluator.Evaluate(inputFilename, "Ressources/FeatureSearch/Output/featureSearchCase2_Most_" + mostRestrictivePolicy + "_Least_" + leastRestrictivePolicy + "_Maj_" + majorityPolicy + ".txt");
                    }
                }
            }
        }
    }

    public static void SelectFeaturesCase3() throws ClassNotFoundException, SQLException, FileNotFoundException, UnsupportedEncodingException, IOException, TasteException {
        DBConnection connector = new DBConnection("jdbc:mysql://localhost/final_multi_party", "root", "");
        HashMap<Integer, Scenario> scenarios = FileDataModelCreator.RetrieveScenarios(connector);
        HashMap<String, ArrayList<Response>> responsesByUser = FileDataModelCreator.RetrieveResponsesByUser(connector);

        PrintWriter writer;
        Evaluator evaluator;
        boolean[] availableFeatures = new boolean[6];
        for (int posSupAll = 0; posSupAll < 2; posSupAll++) {
            for (int posSupCommon = 0; posSupCommon < 2; posSupCommon++) {
                for (int negSupCommon = 0; negSupCommon < 2; negSupCommon++) {
                    for (int negSupSelf = 0; negSupSelf < 2; negSupSelf++) {
                        for (int excSupAll = 0; excSupAll < 2; excSupAll++) {
                            for (int excSupSelf = 0; excSupSelf < 2; excSupSelf++) {
                                if (posSupAll > 0 || posSupCommon > 0 || negSupCommon > 0 || negSupSelf > 0 || excSupAll > 0 || excSupSelf > 0) {
                                    availableFeatures[Scenario.POSITIVE_SUP_ALL] = posSupAll > 0;
                                    availableFeatures[Scenario.POSITIVE_SUP_COMMON] = posSupCommon > 0;
                                    availableFeatures[Scenario.NEGATIVE_SUP_COMMON] = negSupCommon > 0;
                                    availableFeatures[Scenario.NEGATIVE_SUP_SELF] = negSupSelf > 0;
                                    availableFeatures[Scenario.EXCEPTIONAL_SUP_ALL] = excSupAll > 0;
                                    availableFeatures[Scenario.EXCEPTIONAL_SUP_SELF] = excSupSelf > 0;
                                    String inputFilename = "Ressources/FeatureSearch/Input/featureSearchCase3_PA_" + posSupAll + "_PC_" + posSupCommon + "_NC_" + negSupCommon + "_NS_" + negSupSelf + "_EA_" + excSupAll + "_ES_" + excSupSelf +".csv";
                                    writer = new PrintWriter(inputFilename, "UTF-8");
                                    writer.write(FileDataModelCreator.GetCSVCase3(scenarios, responsesByUser, availableFeatures, false));
                                    writer.close();
                                    evaluator = new Evaluator();
                                    evaluator.Evaluate(inputFilename, "Ressources/FeatureSearch/Output/featureSearchCase3_PA_" + posSupAll + "_PC_" + posSupCommon + "_NC_" + negSupCommon + "_NS_" + negSupSelf + "_EA_" + excSupAll + "_ES_" + excSupSelf + ".txt");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
