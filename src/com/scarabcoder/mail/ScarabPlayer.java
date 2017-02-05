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
		return id.toString();
	}
	
	
	public String getUsername(){
		return username;
	}
	
	public void blockPlayer(String id){
		
		ResultSet set = Main.executeQuery("SELECT blocked FROM ScarabMailUsers WHERE uuid='" + this.id.toString().replaceAll("-", "") + "'");
		try {
			if(set.next()){
				List<String> blocked = new ArrayList<String>(Arrays.asList(set.getString("blocked").split(":")));
				System.out.println(blocked.size());
				blocked.add(id);
				Main.executeUpdate("UPDATE ScarabMailUsers SET blocked='" + StringUtils.join(blocked, ":") + "' WHERE uuid='" + this.id.toString().replaceAll("-", "") + "'");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void unblockPlayer(String id){
		ResultSet set = Main.executeQuery("SELECT blocked FROM ScarabMailUsers WHERE uuid='" + this.id.toString().replaceAll("-", "") + "'");
		try {
			if(set.next()){
				List<String> blocked = new ArrayList<String>(Arrays.asList(set.getString("blocked").split(":")));
				blocked.remove(id);
				Main.executeUpdate("UPDATE ScarabMailUsers SET blocked='" + StringUtils.join(blocked, ":") + "' WHERE uuid='" + this.id.toString().replaceAll("-", "") + "'");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public MailSendError sendMail(String receiver, String content){
		receiver = receiver.replaceAll("-", "");
		ResultSet set = Main.executeQuery("SELECT uuid, username, blocked FROM ScarabMailUsers WHERE username='" + receiver + "'");
		
		try {
			if(!set.next()){
				return MailSendError.NOTEXISTS;
			}else{
				if(Arrays.asList(set.getString("blocked").split(":")).contains(id.toString().replaceAll("-", "")) && !this.bypassBlock){
					return MailSendError.BLOCKED;
				}else{
					if(!FilterManager.containsFilteredWord(content)){
						String update = "INSERT INTO ScarabMail (id, content, sender, receiver, date, toname, fromname) VALUES (null, '" + content + "', '" + id.toString().replaceAll("-", "") + "', '" + set.getString("uuid") + "', CURDATE(), '" + receiver + "', '" + this.username + "')";
						
						PreparedStatement st = Main.getConnection().prepareStatement(update, Statement.RETURN_GENERATED_KEYS);
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
		
		ResultSet set = Main.executeQuery("SELECT * FROM ScarabMail WHERE receiver='" + id.toString().replaceAll("-", "") + "'");
		
		try {
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
		String update = "DELETE FROM ScarabMail WHERE receiver='" + this.id.toString().replaceAll("-", "") + "'";
		Main.executeUpdate(update);
	}
	
	
	
}
