package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import de.htwmaps.algorithm.Node;


public class DBAdapterRouteToText {

	public static ResultSet getStreetnameRS(int wayID) {
		PreparedStatement pStmt;
		ResultSet resultSet = null;
		
		String streetQuery = "SELECT nameValue, cityname, is_in, highwayValue, ref FROM ways WHERE ID = ? ;";
		
		try {
			pStmt = DBConnector.getConnection().prepareStatement(streetQuery);
			pStmt.setInt(1, wayID);
			resultSet =  pStmt.executeQuery();
			pStmt = null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultSet;
	}


	
}
