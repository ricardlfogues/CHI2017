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
public class Response {    

    public String mturkId;
    public int sensitivity;
    public int sentiment;
    public String relationship;
    public Integer[] result = new Integer[3];
    public int scenarioID;
    
    public Response(ResultSet res) throws SQLException {
        this.sensitivity = Integer.parseInt(res.getString("image_sensitivity"));
        this.sentiment = Integer.parseInt(res.getString("image_sentiment"));
        this.relationship = res.getString("image_relationship");   
        this.mturkId = res.getString("mturk_id");
        for(int i = 0; i < 3; i++){
            String aux = res.getString("case" + (i + 1) + "_policy");
            if(aux.equals("a"))
                this.result[i] = Scenario.ALL;
            else if(aux.equals("b"))
                this.result[i] = Scenario.COMMON;
            else if(aux.equals("c"))
                this.result[i] = Scenario.SELF;            
            else
                this.result[i] = Scenario.INVALID; 
        }        
        this.scenarioID = res.getInt("scenario_id");
    }
}
