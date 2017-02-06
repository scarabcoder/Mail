package com.scarabcoder.mail.strings;

import com.scarabcoder.mail.Main;

import net.md_5.bungee.api.ChatColor;

public class ChatMessage {
	
	public static final String INVALIDARGS = Main.prefix + ChatColor.RED + "Invalid arugments, use /mail help for commands.";
	
	public static final String NOPERMS = Main.prefix + ChatColor.RED + "You don't have permission to use this command.";
	
	public static final String BLOCKED = Main.prefix + ChatColor.RED + "This player has blocked you.";
	
	public static final String NOTFOUND = Main.prefix + ChatColor.RED + "Player does not exist in database";
	
	public static final String SENT = Main.prefix + "Mail sent!";

	public static final String NOMAIL = "=== No mail ===";
	
	public static final String INBOX = Main.prefix + "Mail in inbox:";
	
	public static final String FILTERED = Main.prefix + ChatColor.RED + "Warning: Message contains blacklisted words, message not sent.";

	public static final String CLEARED = Main.prefix + "Cleared mail.";

	public static final String WORDFILTERREMOVE = Main.prefix + "Removed word from word filter.";

	public static final String WORDFILTERADD = Main.prefix + "Added word to word filter.";

	public static final String UPLOADING = Main.prefix + "Uploading local file to word filter database...";
	
	public static final String UPLOADED = Main.prefix + "Filter database updated.";
	
	public static final String DOWNLOADING = Main.prefix + "Downloading word filter database to file...";
	
	public static final String DOWNLOADED = Main.prefix + "Word filter downloaded to Mail/filter.txt";
	
	public static final String NOTID = Main.prefix + ChatColor.RED + "Please enter an ID (number)";

	public static final String REMOVEDMAIL = Main.prefix + "Removed mail.";

	public static final String MAILNOTFOUND = Main.prefix + ChatColor.RED + "Mail with ID not found.";

	public static final String BLOCKEDPLAYER = Main.prefix + "Blocked player.";

	public static final String UNBLOCKEDPLAYER = Main.prefix + "Unblocked player.";

	public static final String PLAYERCOMMAND = Main.prefix + ChatColor.RED + "Player-only command!";
	
	
}
