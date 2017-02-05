package com.scarabcoder.mail;

import java.util.Arrays;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.scarabcoder.mail.managers.FilterManager;
import com.scarabcoder.mail.managers.MailManager;
import com.scarabcoder.mail.managers.PlayerManager;
import com.scarabcoder.mail.strings.ChatMessage;
import com.scarabcoder.mail.strings.HelpMessage;
import com.scarabcoder.mail.strings.MailPermissions;

public class MailCommand implements CommandExecutor{
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] args) {
		
		List<String> playerArgs = Arrays.asList("send", "read", "clear", "help", "block", "unblock");
		List<String> adminArgs = Arrays.asList("readfrom", "filter", "remove");
		
		//REFER TO HelpMessage.java for command usage
		
		
		if(sender.hasPermission(MailPermissions.USECOMMAND)){
			if(args.length > 0){
				if(playerArgs.contains(args[0]) || adminArgs.contains(args[0])){
					if(sender instanceof Player){
						Player p = (Player) sender;
						ScarabPlayer player = new ScarabPlayer(p);
						
						if(args.length > 2){
							// /mail send <user> <content>
							if(args[0].equalsIgnoreCase(playerArgs.get(0))){
								
								List<String> argList = Arrays.asList(args);
								
								String message = StringUtils.join(argList.subList(2, argList.size()), " ");
								
								
								MailSendError result = player.sendMail(args[1], message);
								switch(result){
								case BLOCKED:
									p.sendMessage(ChatMessage.BLOCKED);
									break;
								case NOTEXISTS:
									p.sendMessage(ChatMessage.NOTFOUND);
									break;
								case FILTERED:
									p.sendMessage(ChatMessage.FILTERED);
									for(Player pl : Bukkit.getOnlinePlayers()){
										if(pl.hasPermission(MailPermissions.LOGS)){
											p.sendMessage(Main.prefix + ChatColor.RED + p.getName() + " tried sending a message to " + args[1] + " with a filtered word: " + message);
										}
									}
									break;
								case NONE:
									p.sendMessage(ChatMessage.SENT);
									break;
								}
							}else if(args[0].equalsIgnoreCase(adminArgs.get(1))){
								if(p.hasPermission(MailPermissions.MODIFYFILTER)){
									if(args[1].equalsIgnoreCase("add")){
										FilterManager.addWord(args[2]);
										p.sendMessage(ChatMessage.WORDFILTERADD);
									}else if(args[1].equalsIgnoreCase("remove")){
										FilterManager.removeWord(args[2]);
										p.sendMessage(ChatMessage.WORDFILTERREMOVE);
									}else if(args[1].equalsIgnoreCase("modify")){
										if(args[2].equalsIgnoreCase("upload")){
											p.sendMessage(ChatMessage.UPLOADING);
											FilterManager.loadFilterFromFile();
											p.sendMessage(ChatMessage.UPLOADED);
										}else if(args[2].equalsIgnoreCase("download")){
											
											p.sendMessage(ChatMessage.DOWNLOADING);
											FilterManager.saveFilterToFile();
											p.sendMessage(ChatMessage.DOWNLOADED);
											
										}else{
											p.sendMessage(ChatMessage.INVALIDARGS);
										}
									}else{
										p.sendMessage(ChatMessage.INVALIDARGS);
									}
								}
							}else{
								p.sendMessage(ChatMessage.INVALIDARGS);
							}
						}else if(args.length > 1){
							// /mail readfrom <date>
							// Date format: year-month-day
							// Ex: 2017-2-4
							if(args[0].equalsIgnoreCase(adminArgs.get(0))){
								if(p.hasPermission(MailPermissions.READOTHER)){
									
									//Return message format:
									//
									//[Mail] Mail sent on 2017-02-03:
									//[Mail] (43) [ScarabCoder > BuildsByGideon] Hello, can you do this thing?
									//[Mail] (44) [BuildsByGideon > ScarabCoder] Sure!
									
									p.sendMessage(Main.prefix + "Mail sent on " + args[1] + ":");
									
									List<Mail> mails = MailManager.getMailsFromDate(args[1]);
									
									if(!mails.isEmpty()){
										for(Mail mail : mails){
											p.sendMessage(Main.prefix + "(" + mail.getId() + ") [" + mail.getFromName() + " > " + mail.getToName() + "] " + mail.getMessage());
										}
									}else{
										p.sendMessage(ChatMessage.NOMAIL);
									}
									
								}else{
									p.sendMessage(ChatMessage.NOPERMS);
								}
							}else if(args[0].equalsIgnoreCase(adminArgs.get(2))){
								if(p.hasPermission(MailPermissions.REMOVEMAIL)){
									try {
										int id = Integer.parseInt(args[1]);
										boolean changed = MailManager.removeMail(id);
										if(changed){
											p.sendMessage(ChatMessage.REMOVEDMAIL);
										}else{
											p.sendMessage(ChatMessage.MAILNOTFOUND);
										}
									} catch (NumberFormatException e){
										p.sendMessage(ChatMessage.NOTID);
									}
								}else{
									p.sendMessage(ChatMessage.NOPERMS);
								}
								// /mail block/unblock <user>
							}else if(args[0].equalsIgnoreCase(playerArgs.get(4)) || (args[0].equalsIgnoreCase(playerArgs.get(5)))){
								if(p.hasPermission(MailPermissions.BLOCK)){
									String uuid = PlayerManager.usernameToUUID(args[1]);
									if(uuid != null){
										if(args[0].equalsIgnoreCase(playerArgs.get(4))){
											player.blockPlayer(uuid);
											p.sendMessage(ChatMessage.BLOCKEDPLAYER);
										}else{
											player.unblockPlayer(uuid);
											p.sendMessage(ChatMessage.UNBLOCKEDPLAYER);
										}
									}else{
										p.sendMessage(ChatMessage.NOTFOUND);
									}
								}else{
									p.sendMessage(ChatMessage.NOPERMS);
								}
							}else{
								p.sendMessage(ChatMessage.INVALIDARGS);
							}
						}else if(args.length > 0){
							// /mail read
							if(args[0].equalsIgnoreCase(playerArgs.get(1))){
								if(p.hasPermission(MailPermissions.READMAIL)){
									//Mail read format:
									//
									//[Mail] Inbox:
									//[Mail] [2017-02-03] ScarabCoder: Hello, can you do this thing?
									//[Mail] [2017-02-03] BuildsByGideon: Sure!
									
									p.sendMessage(ChatMessage.INBOX);
									
									List<Mail> inbox = player.getMail();
									
									if(!inbox.isEmpty()){
										for(Mail mail : inbox){
											p.sendMessage(Main.prefix + "[" + mail.getDate().toString() + "] " + mail.getFromName() + ": " + mail.getMessage());
										}
									}else{
										p.sendMessage(ChatMessage.NOMAIL);
									}
									
								}else{
									p.sendMessage(ChatMessage.NOPERMS);
								}
								// /mail clear
							}else if(args[0].equalsIgnoreCase(playerArgs.get(2))){
								player.clearMail();
								p.sendMessage(ChatMessage.CLEARED);
							}else if(args[0].equalsIgnoreCase(playerArgs.get(3))){
								HelpMessage.helpMessage(p);
							}
						}else{
							p.sendMessage(ChatMessage.INVALIDARGS);
						}
						
						
					}
				}else{
					sender.sendMessage(ChatMessage.INVALIDARGS);
				}
			}
		}else{
			sender.sendMessage(ChatMessage.NOPERMS);
		}
		
		
		
		
		
		return true;
	}

}
