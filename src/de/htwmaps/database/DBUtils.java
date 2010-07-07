package de.htwmaps.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtils {
	
	private final static String CITYSTREET_SELECT = "SELECT nodeID FROM streets WHERE cityname LIKE ? AND streetname = ?";
	
	private DBUtils(){ }
	
	public static int getNodeId(String city, String street) throws SQLException, NodeNotFoundException{
		PreparedStatement select = DBConnector.getConnection().prepareStatement(CITYSTREET_SELECT);
		select.setString(1, city);
		select.setString(2, street);
		ResultSet rs = select.executeQuery(CITYSTREET_SELECT);
		if(!rs.next())
			throw new NodeNotFoundException();
		return rs.getInt(1);
	}
}
