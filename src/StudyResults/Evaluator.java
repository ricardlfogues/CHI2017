/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StudyResults;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

/**
 *
 * @author Ricard
 */
public class Evaluator {

    private class MyRecommenderBuilder implements RecommenderBuilder {
        @Override
        public Recommender buildRecommender(DataModel dm) throws TasteException {
            UserSimilarity similarity = new PearsonCorrelationSimilarity(dm);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dm);
            return new GenericUserBasedRecommender(dm, neighborhood, similarity);
        }        
    }   
    
    public void Evaluate(String fileDataModelName, String resultFile)throws IOException, TasteException {
        DataModel model = new FileDataModel(new File(fileDataModelName));
        RecommenderEvaluator evaluator = new RMSRecommenderEvaluator();
        RecommenderBuilder builder = new MyRecommenderBuilder();
        PrintWriter writer = new PrintWriter(resultFile, "UTF-8");
        double result = 0;
        for(int i = 0; i < 1000; i++){
            result += evaluator.evaluate(builder, null, model, 0.9, 1.0);
            //writer.println(result);            
        }        
        result /= 1000;
        writer.println(result);
        writer.close();
    }    
}
