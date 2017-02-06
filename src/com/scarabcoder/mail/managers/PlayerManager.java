package com.scarabcoder.mail.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.scarabcoder.mail.Main;

public class PlayerManager {
	
	public static String usernameToUUID(String name){
		
		
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("SELECT uuid FROM ScarabMailUsers WHERE username=?");
			st.setString(1, name);
			ResultSet set = st.executeQuery();
			if(set.next()){
				return set.getString("uuid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
