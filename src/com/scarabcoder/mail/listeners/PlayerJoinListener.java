package com.scarabcoder.mail.listeners;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.scarabcoder.mail.Main;

public class PlayerJoinListener implements Listener{
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void playerJoin(PlayerJoinEvent e){
		ResultSet set = Main.executeQuery("SELECT uuid FROM ScarabMailUsers WHERE uuid='" + e.getPlayer().getUniqueId().toString().replaceAll("-", "") + "'");
		
		
		
		try {
			
			if(!set.next()){
				Main.executeUpdate("INSERT INTO ScarabMailUsers (uuid, username, blocked) VALUES ('" + e.getPlayer().getUniqueId().toString().replaceAll("-", "") + "', '" + e.getPlayer().getName() + "', '')");
				System.out.println("[Mail] Created empty player data for " + e.getPlayer().getName());
			}else{
				Main.executeUpdate("UPDATE ScarabMailUsers SET username='" + e.getPlayer().getName() + "' WHERE uuid='" + e.getPlayer().getUniqueId().toString().replaceAll("-", "") + "'");
				set = Main.executeQuery("SELECT COUNT(*) FROM ScarabMail WHERE receiver='" + e.getPlayer().getUniqueId().toString().replace("-", "") + "'");
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
