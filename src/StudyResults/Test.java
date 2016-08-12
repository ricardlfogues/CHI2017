/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package StudyResults;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, TasteException, ClassNotFoundException, SQLException {   
        //GenerateDataModelFiles();
        //BasicRecommendation();
        //EvaluateRecommender("Resources/case1NumericID.csv", "Resources/EvaluationResult.txt");
        FeatureSelection.SelectFeaturesCase1();
    }
    
    /**
     * Basic Taste example of use
     * @throws IOException
     * @throws TasteException 
     */   
    private static void BasicRecommendation() throws IOException, TasteException{
        DataModel model = new FileDataModel(new File("Resources/case1NumericID.csv"));
        UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
        UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
        UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
        List<RecommendedItem> recommendations = recommender.recommend(2, 3);
        for (RecommendedItem recommendation : recommendations) {
          System.out.println(recommendation);
        }
    }
    
    /**
     * Creates the csv files that can be read by Taste
     */
    private static void GenerateDataModelFiles() throws ClassNotFoundException, SQLException, FileNotFoundException, UnsupportedEncodingException{
        DBConnection connector = new DBConnection("jdbc:mysql://localhost/final_multi_party","root", "");
        HashMap<Integer, Scenario> scenarios = new HashMap<>();
        HashMap<String, ArrayList<Response>> responsesByUser = new HashMap<String, ArrayList<Response>>();

        ResultSet res = connector.Query("SELECT * FROM scenario");
        while (res.next()) {
            Scenario scenario = new Scenario(res, connector);
            scenarios.put(scenario.id, scenario);
        }

        res = connector.Query("SELECT * FROM turker_picturesurvey_response");
        while (res.next()) {
            Response response = new Response(res);
            if (!responsesByUser.containsKey(response.mturkId)) {
                responsesByUser.put(response.mturkId.trim(), new ArrayList());
            }
            responsesByUser.get(response.mturkId.trim()).add(response);
        }
        String[] relationships = new String[]{"family", "friends", "colleagues"};

        PrintWriter writer = new PrintWriter("Resources/case1NumericID.csv", "UTF-8");
        writer.write(FileDataModelCreator.GetCSVCase1(scenarios, responsesByUser, relationships, false));
        writer.close();

        PrintWriter writer2 = new PrintWriter("Resources/case2NumericID.csv", "UTF-8");
        writer2.write(FileDataModelCreator.GetCSVCase2(scenarios, responsesByUser, false));
        writer2.close();
        
        PrintWriter writer3 = new PrintWriter("Resources/case3NumericID.csv", "UTF-8");
        writer3.write(FileDataModelCreator.GetCSVCase3(scenarios, responsesByUser, false));
        writer3.close();
    }
    
    /**
     * Creates a file that contains the average prediction error for each separated user. 
     * The average is calculated employing the protocol All but one.
     * Note that the output file employs the turker ID rather than the numerical ID employed by taste, 
     * therefore the input file must contain turker IDs
     * @throws IOException
     * @throws FileNotFoundException
     * @throws TasteException 
     */
    private static void GetUserAveragePredictionError() throws IOException, FileNotFoundException, TasteException{
        HashMap<String, Double> result = ResultEvaluator.GetRMSEPerUser("Resources/case3TukerID.csv");
        PrintWriter writer = new PrintWriter("Resources/resultEvaluationCase3.txt", "UTF-8");
        for(String userID : result.keySet()){
            writer.println(userID + "\t" + result.get(userID));
        }
        writer.close();
    }    
    
    private static void EvaluateRecommender(String inputFile, String outputFile) throws IOException, TasteException{
        Evaluator evaluator = new Evaluator();
        evaluator.Evaluate(inputFile, outputFile);
    }
}
