/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package StudyResults;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author rlopezf
 */
public class DBConnection {
    private Connection connect = null;    
    
    public DBConnection(String address, String user, String password) throws ClassNotFoundException, SQLException{
        // This will load the MySQL driver, each DB has its own driver
      Class.forName("com.mysql.jdbc.Driver");      
      //connect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/final_multi_party","root", "");
      connect = (Connection) DriverManager.getConnection(address,user, password);
    }
    
    public ResultSet Query(String query) throws SQLException{
        Statement s = (Statement) connect.createStatement();
        ResultSet rs = s.executeQuery (query);
        return rs;
    }
    
    public Integer insertQueryGetId(String query) throws SQLException, Exception {  
        Statement stmt = (Statement)connect.createStatement();
        stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        ResultSet rs = stmt.getGeneratedKeys();
        if(rs != null && rs.next()){
            return rs.getInt(1);
        }
        else return -1;
    }
    
    public void insertQuery(String query) throws SQLException, Exception {  
        Statement stmt = (Statement)connect.createStatement();
        stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        stmt.getGeneratedKeys();
    }
}
