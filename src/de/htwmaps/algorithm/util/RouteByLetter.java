package de.htwmaps.algorithm.util;

import java.awt.SystemColor;
import java.sql.SQLException;
import java.util.ArrayList;

import de.htwmaps.algorithm.Node;
import de.htwmaps.database.DBAdapterRouteByLetter;

public class RouteByLetter {

	public RouteByLetter(Node[] result) {
		DBAdapterRouteByLetter test = null;
		try {
			long time = System.currentTimeMillis();
			test = new DBAdapterRouteByLetter(result);
			System.out.println("Algo: " + (System.currentTimeMillis() - time) + "ms");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("\n\n-----------RouteByLetter Test-------------");
//		System.out.println(test.toString());
		System.out.println("------------------------\n\n");
	}
	
	private void getNodeInfo(Node[] result) {	
		ArrayList<String> streetname = new ArrayList<String>();
	}
	
}
