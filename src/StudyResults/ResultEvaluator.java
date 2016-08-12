/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StudyResults;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author Ricard
 */
public class ResultEvaluator {

    /**
     * *
     *
     * @param filename must be a cvs file: userID, ItemID, rating
     * @return
     */
    public static HashMap<String, Double> GetRMSEPerUser(String filename) throws FileNotFoundException, IOException, TasteException {
        HashMap<String, Integer> idToNumericId = new HashMap<>();
        HashMap<Integer, HashMap<Integer, Double>> ratingsPerUser = new HashMap<>();
        HashMap<String, ArrayList<Double>> allErrors = new HashMap<>();
        HashMap<String, Double> result = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int userCount = -1;
        while ((line = br.readLine()) != null) {
            // use comma as separator
            String[] columns = line.split(",");
            if (!idToNumericId.containsKey(columns[0])) {
                userCount++;
                idToNumericId.put(columns[0], userCount);
            }
            if (!ratingsPerUser.containsKey(userCount)) {
                ratingsPerUser.put(userCount, new HashMap<>());
            }
            ratingsPerUser.get(userCount).put(Integer.parseInt(columns[1]), Double.parseDouble(columns[2]));
        }

        for (String userID : idToNumericId.keySet()) {
            int intUserID = idToNumericId.get(userID);
            if (ratingsPerUser.get(intUserID).keySet().size() > 1) {
                for (int scenarioID : ratingsPerUser.get(intUserID).keySet()) {
                    PrintWriter writer = new PrintWriter("temp.csv", "UTF-8");
                    for (String userID2 : idToNumericId.keySet()) {
                        int intUserID2 = idToNumericId.get(userID2);
                        for (int scenarioID2 : ratingsPerUser.get(intUserID2).keySet()) {
                            if (intUserID2 != intUserID || (intUserID2 == intUserID && scenarioID != scenarioID2)) {
                                writer.println(intUserID2 + "," + scenarioID2 + "," + ratingsPerUser.get(intUserID2).get(scenarioID2));
                            }
                        }
                    }
                    writer.close();
                    DataModel model = new FileDataModel(new File("temp.csv"));
                    UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
                    UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
                    UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
                    double predicted = recommender.estimatePreference(intUserID, scenarioID);
                    if (!Double.isNaN(predicted)) {
                        double error = ratingsPerUser.get(intUserID).get(scenarioID) - predicted;
                        error *= error;
                        if (!allErrors.containsKey(userID)) {
                            allErrors.put(userID, new ArrayList<>());
                        }
                        allErrors.get(userID).add(error);
                    }
                }
            }
        }

        for (String userID : allErrors.keySet()) {
            double mean = 0;
            for (Double error : allErrors.get(userID)) {
                mean += error;
            }
            mean /= allErrors.get(userID).size();
            mean = Math.sqrt(mean);
            result.put(userID, mean);
        }
        return result;
    }
}
