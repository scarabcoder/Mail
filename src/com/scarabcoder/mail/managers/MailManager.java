package com.scarabcoder.mail.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.scarabcoder.mail.Mail;
import com.scarabcoder.mail.Main;

public class MailManager {
	
	public static boolean removeMail(int id){
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("DELETE FROM ScarabMail WHERE id=" + id);
			return st.executeUpdate() > 0;
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return false;
		
	}
	
	public static List<Mail> getMailsFromDate(String date){
		List<Mail> mails = new ArrayList<Mail>();
		
		String query = "SELECT * FROM ScarabMail WHERE date between '" + date + "' and '" + date + "'";
		
		ResultSet mailQuery = Main.executeQuery(query);
		
		try {
			while(mailQuery.next()){
				mails.add(new Mail(mailQuery));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return mails;
		
	}
	
	
}
