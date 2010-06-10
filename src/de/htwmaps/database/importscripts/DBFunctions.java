/**
 * 
 */
package de.htwmaps.database.importscripts;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import de.htwmaps.database.DBConnector;

/**
 * @author tobiaslana
 *
 */
public class DBFunctions {

	   /**
	    * 
	    * bekommt Resultset und liefert eine Arraylist
	    * 
	    * @param rst
	    * @return
	    * @throws SQLException
	    */
	    public static ArrayList resultsetTOarraylist (ResultSet rst) throws SQLException {
	    	ResultSetMetaData rstmd = rst.getMetaData();
	        int columnCount = rstmd.getColumnCount();
	    	ArrayList<String[]> rows = new ArrayList();
	    	while (rst.next()) {
	            String[] row = new String[columnCount];
	            for (int i = 1; i <= columnCount; i++) {
	                row[i - 1] = rst.getString(i);
	            }
	            rows.add(row);
	        }
	    	System.out.println("Umwandlung rst in Arraylist erfolgreich");
			return rows;
	    }
	    
	    /**
	     * erwartet einen SQL Query als String und uebergibt das Resultset der Abfrage
	     * 
	     */
	    public static ResultSet queryTOrst(DBConnector dbc, String query) {
	    	ResultSet rst = null;
            //rst = dbc.select(query);
			return rst;
	    }
	
}
