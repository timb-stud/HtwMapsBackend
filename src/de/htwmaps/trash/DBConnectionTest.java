package de.htwmaps.trash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.htwmaps.database.DBConnector;

/**
 * 
 * Trash Klasse - wurde nur zum Test erstellt
 *
 */

public class DBConnectionTest {
	
	private String sqlQuery = "select count(*) from `edges`";

	private DBConnectionTest() {
				
		System.out.println("Database connected: " + DBConnector.isConnected());
		
		ResultSet rs = null;
		
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(sqlQuery);
		PreparedStatement sqlRS;
		
		try {
			sqlRS = DBConnector.getConnection().prepareStatement(sqlString.toString());
			rs = sqlRS.executeQuery();
			
			while(rs.next()) {
				System.out.println(sqlString.toString());
				System.out.println("Datenbank Abfrageergebnis: \n" + rs.getString(1));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void main(String[] args) {
		DBConnectionTest dbTest = new DBConnectionTest();
	}
	
}
