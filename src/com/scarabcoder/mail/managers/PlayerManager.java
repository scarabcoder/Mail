package com.scarabcoder.mail.managers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.scarabcoder.mail.Main;

public class PlayerManager {
	
	public static String usernameToUUID(String name){
		ResultSet set = Main.executeQuery("SELECT uuid FROM ScarabMailUsers WHERE username='" + name + "'");
		try {
			if(set.next()){
				return set.getString("uuid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
