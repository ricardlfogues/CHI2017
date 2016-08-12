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
    
    public static void SelectFeaturesCase1() throws ClassNotFoundException, SQLException, FileNotFoundException, UnsupportedEncodingException, IOException, TasteException{
        DBConnection connector = new DBConnection("jdbc:mysql://localhost/final_multi_party","root", "");
        HashMap<Integer, Scenario> scenarios = new HashMap<>();
        HashMap<String, ArrayList<Response>> responsesByUser = new HashMap<>();

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
        
        PrintWriter writer;
        Evaluator evaluator;
        boolean[] availableFeatures = new boolean[3];
        for(int sensitivity = 0; sensitivity < 2; sensitivity++){
            for(int sentiment = 0; sentiment < 2; sentiment++){
                for(int relationship = 0; relationship < 2; relationship++){
                    if(sensitivity > 0 || sentiment > 0 || relationship > 0){
                        availableFeatures[Scenario.SENSITIVITY_INDEX] = sensitivity > 0;
                        availableFeatures[Scenario.SENTIMENT_INDEX] = sentiment > 0;
                        availableFeatures[Scenario.RELATIONSHIP_INDEX] = relationship > 0;
                        String inputFilename = "Resources/FeatureSearch/Input/featureSearchCase1_Sens_"+sensitivity+"_Sent_"+sentiment+"_Rel_"+relationship+".csv";
                        writer = new PrintWriter(inputFilename, "UTF-8");
                        writer.write(FileDataModelCreator.GetCSVCase1(scenarios, responsesByUser, relationships, availableFeatures, false));
                        writer.close();
                        evaluator = new Evaluator();
                        evaluator.Evaluate("Resources/featureSearchCase1.csv", "Resources/FeatureSearch/Output/Sens_"+sensitivity+"_Sent_"+sentiment+"_Rel_"+relationship+".txt");
                    }                    
                }
            }
        }
        
    }
}
