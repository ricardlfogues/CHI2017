/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tastetest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author Ricard
 */
public class TasteTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, TasteException {
        // TODO code application logic here
        //DataModel model = new FileDataModel(new File("/ml-1m/ratings.txt"));
        /*DataModel model = new FileDataModel(new File("collaborativeFilteringCase1.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(2, 3);
        for (RecommendedItem recommendation : recommendations) {
          System.out.println(recommendation);
        }*/
        HashMap<String, Double> result = ResultEvaluator.GetRMSEPerUser("Resources/case3TukerID.csv");
        PrintWriter writer = new PrintWriter("Resources/resultEvaluationCase3.txt", "UTF-8");
        for(String userID : result.keySet()){
            writer.println(userID + "\t" + result.get(userID));
        }
        writer.close();
    }
    
}
