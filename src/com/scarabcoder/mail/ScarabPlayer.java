package com.scarabcoder.mail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.scarabcoder.mail.managers.FilterManager;
import com.scarabcoder.mail.strings.MailPermissions;

public class ScarabPlayer {
	
	private UUID id;
	
	private String username;
	
	private boolean bypassBlock;
	
	public ScarabPlayer(Player p){
		this.id = p.getUniqueId();
		this.username = p.getName();
		this.bypassBlock = p.hasPermission(MailPermissions.BYPASSBLOCK);
	}
	
	public String getID(){
		return id.toString().replaceAll("-", "");
	}
	
	
	public String getUsername(){
		return username;
	}
	
	public void blockPlayer(String id){
		
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("SELECT blocked FROM ScarabMailUsers WHERE uuid=?");
			st.setString(1, this.getID());
			ResultSet set = st.executeQuery();
			if(set.next()){
				List<String> blocked = new ArrayList<String>(Arrays.asList(set.getString("blocked").split(":")));
				System.out.println(blocked.size());
				blocked.add(id);
				
				st = Main.getConnection().prepareStatement("UPDATE ScarabMailUsers SET blocked=? WHERE uuid=?");
				st.setString(1, StringUtils.join(blocked, ":"));
				st.setString(2, this.getID());
				st.executeUpdate();
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void unblockPlayer(String id){
		
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("SELECT blocked FROM ScarabMailUsers WHERE uuid=?");
			st.setString(1, this.getID());
			ResultSet set = st.executeQuery();
			if(set.next()){
				List<String> blocked = new ArrayList<String>(Arrays.asList(set.getString("blocked").split(":")));
				blocked.remove(id);
				
				st = Main.getConnection().prepareStatement("UPDATE ScarabMailUsers SET blocked=? WHERE uuid=?");
				st.setString(1, StringUtils.join(blocked, ":"));
				st.setString(2, this.getID());
				st.executeUpdate();
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public MailSendError sendMail(String receiver, String content){
		receiver = receiver.replaceAll("-", "");
		
		
		
		
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("SELECT uuid, username, blocked FROM ScarabMailUsers WHERE username=?");
			st.setString(1, receiver);
			
			
			ResultSet set = st.executeQuery();
			if(!set.next()){
				return MailSendError.NOTEXISTS;
			}else{
				if(Arrays.asList(set.getString("blocked").split(":")).contains(this.getID()) && !this.bypassBlock){
					return MailSendError.BLOCKED;
				}else{
					if(!FilterManager.containsFilteredWord(content)){
						
						st = Main.getConnection().prepareStatement("INSERT INTO ScarabMail (id, content, sender, receiver, date, toname, fromname) VALUES (null, ?, ?, ?, CURDATE(), ?, ?)", Statement.RETURN_GENERATED_KEYS);
						st.setString(1,content);
						st.setString(2, this.getID().replaceAll("-", ""));
						st.setString(3, set.getString("uuid"));
						st.setString(4, set.getString("username"));
						st.setString(5, this.getUsername());
						
						
						st.executeUpdate();
						
						ResultSet IDSet = st.getGeneratedKeys();
						IDSet.next();
						int number = IDSet.getInt(1);
						
						String logMessage = Main.prefix + username + " sent mail to " + receiver + " with content \"" + content + "\" (ID " + number + ")";
						
						System.out.println(logMessage);
						
						for(Player p : Bukkit.getServer().getOnlinePlayers()){
							if(p.hasPermission(MailPermissions.LOGS)){
								p.sendMessage(logMessage);
							}
						}
					}else{
						return MailSendError.FILTERED;
					}
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return MailSendError.NONE;
	}
	
	public List<Mail> getMail(){
		
		List<Mail> mails = new ArrayList<Mail>();
		
		
		
		
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("SELECT * FROM ScarabMail WHERE receiver=?");
			st.setString(1, this.getID());
			ResultSet set = st.executeQuery();
			while(set.next()){
				Mail mail = new Mail(set);
				mails.add(mail);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return mails;
	}
	
	public void clearMail(){
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("DELETE FROM ScarabMail WHERE receiver=?");
			st.setString(1, this.getID());
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
