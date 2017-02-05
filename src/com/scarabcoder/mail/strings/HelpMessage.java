package com.scarabcoder.mail.strings;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.entity.Player;

import com.scarabcoder.mail.Main;

public class HelpMessage {
	
	public static void helpMessage(Player p){
		
		p.sendMessage(Main.prefix + "== Mail Commands ==");
		p.sendMessage("");
		p.sendMessage(Main.prefix + "/mail send <player> <message>" + ChatColor.RESET + ": Send mail to player.");
		p.sendMessage(Main.prefix + "/mail read" + ChatColor.RESET + ": Read mail.");
		p.sendMessage(Main.prefix + "/mail clear" + ChatColor.RESET + ": Clear mail in inbox.");
		p.sendMessage(Main.prefix + "/mail help" + ChatColor.RESET + ": Displays this help text.");
		
		if(p.hasPermission(MailPermissions.READOTHER)){
			p.sendMessage(Main.prefix + "/mail readfrom <date>" + ChatColor.RESET + ": Displays mail sent on date (ex: 2017-02-04, YYYY-MM-DD)");
		}
		if(p.hasPermission(MailPermissions.MODIFYFILTER)){
			p.sendMessage(Main.prefix + "/mail filter <add/modify/remove> [keyword/download/upload]" + ChatColor.RESET + ": Add or remove a word, reset database filter by loading from local file (upload) or populating local file from database (download).");
		}
		
		
		
	}
	
}
