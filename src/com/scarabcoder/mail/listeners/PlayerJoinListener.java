package com.scarabcoder.mail.listeners;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.scarabcoder.mail.Main;
import com.scarabcoder.mail.ScarabPlayer;

public class PlayerJoinListener implements Listener{
	
	@SuppressWarnings("resource")
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerJoin(PlayerJoinEvent e){
		ScarabPlayer p = new ScarabPlayer(e.getPlayer());
		
		
		
		
		
		
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("SELECT uuid, username FROM ScarabMailUsers WHERE uuid=?");
			st.setString(1, p.getID());
			ResultSet set = st.executeQuery();
			
			if(!set.next()){
				
				st = Main.getConnection().prepareStatement("INSERT INTO ScarabMailUsers (uuid, username, blocked) VALUES (?, ?, '')");
				st.setString(1, p.getID());
				st.setString(2, p.getUsername());
				st.executeUpdate();
				
				System.out.println("[Mail] Created empty player data for " + p.getUsername());
			}else{
				
				if(!set.getString("username").equals(p.getUsername())){
					st = Main.getConnection().prepareStatement("UPDATE ScarabMailUsers SET username=? WHERE uuid=?");
					st.setString(1, p.getUsername());
					st.setString(2, p.getID());
					st.executeUpdate();
				}
				
				
				st = Main.getConnection().prepareStatement("SELECT COUNT(*) FROM ScarabMail WHERE receiver=?");
				st.setString(1, p.getID());
				set = st.executeQuery();
				if(set.next()){
					int mailCount = set.getInt("COUNT(*)");
					if(mailCount > 0){
						e.getPlayer().sendMessage(Main.prefix + "You have " + ChatColor.GREEN + mailCount + "" + ChatColor.GOLD + " messages.");
					}else{
						e.getPlayer().sendMessage(Main.prefix + "You don't have any new messages.");
					}
				}
			}
			
			
			
			
		} catch (SQLException e1) {
			
			e1.printStackTrace();
		}
		
	}
	
}
