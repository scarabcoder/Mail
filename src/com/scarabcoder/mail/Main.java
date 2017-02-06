package com.scarabcoder.mail;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.scarabcoder.mail.listeners.PlayerJoinListener;
import com.scarabcoder.mail.managers.FilterManager;

public class Main extends JavaPlugin{
	
	public static String prefix = "[" + ChatColor.GOLD + "Mail" + ChatColor.RESET + "] " + ChatColor.GOLD;
	
	public static Connection con = null;
	
	public static int lastID;
	
	public static File filter;
	
	private static Plugin plugin;
	
	public static Plugin getPlugin(){
		return plugin;
	}
	
	public void onEnable(){
		
		plugin = this;
		
        Statement st = null;
        ResultSet rs = null;
        
        refreshConnection();
        
        //Create filter.txt if not existing
       this.getDataFolder().mkdirs();
        filter = new File(this.getDataFolder(), "filter.txt");
        if(!filter.exists()){
        	try {
				filter.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        
        
        this.getCommand("mail").setExecutor(new MailCommand());
        
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        
        this.generateConfigDefaults();
        
        System.out.println("[Mail] Connecting to MySQL Database...");
        try {
	        st = con.createStatement();
	        rs = st.executeQuery("SHOW TABLES");
	        System.out.println("[Mail] Connection made, found tables: ");
	        
            while (rs.next()) {
                
                System.out.println("[Mail] " + rs.getString(1));
                
            }
            System.out.println("[Mail] Creating data tables if not existing...");
            
            
            populateSchema();
            
            FilterManager.reloadFilter();
           
            
		} catch (SQLException e) {
			System.out.println("[Mail] There was an error connecting to the database!");
			e.printStackTrace();
		}

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {
				executeQuery("SELECT 1");
			}
			
		}, 10 * 20, 10 * 20);
        

		
	}
	
	
	
	private void generateConfigDefaults(){
		this.saveDefaultConfig();
	}
	
	private static void refreshConnection(){
		String url = "jdbc:mysql://" + getPlugin().getConfig().getString("mysql-server") + ":" + getPlugin().getConfig().getString("mysql-port") + "/" + getPlugin().getConfig().getString("mysql-schema") + "?autoReconnect=true";
        String user = getPlugin().getConfig().getString("mysql-user");
        String password = getPlugin().getConfig().getString("mysql-pass");
		try {
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
	public static Connection getConnection(){
		if(con == null){
			refreshConnection();
		}
		return con;
	}
	
	public static ResultSet executeQuery(String query){
		
		
		Statement st;
		try {
			if(con == null){
				refreshConnection();
			}
			st = con.createStatement();
			return st.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void executeUpdate(String update){
		Statement st;
		try {
			st = con.createStatement();
			st.executeUpdate(update);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	private static void populateSchema(){
		
		String tableName = getPlugin().getConfig().getString("mysql-schema");
		
		 String createTable = "CREATE TABLE IF NOT EXISTS `" + tableName + "`.`ScarabMailUsers` ( `uuid` VARCHAR(32) NOT NULL, `username` VARCHAR(16) NULL, `blocked` LONGTEXT NULL, PRIMARY KEY (`uuid`));";
         
         executeUpdate(createTable);
         
         createTable = "CREATE TABLE IF NOT EXISTS `" + tableName + "`.`ScarabMail` ( `id` INT NOT NULL AUTO_INCREMENT, `content` MEDIUMTEXT NULL, `receiver` VARCHAR(32) NULL, `sender` VARCHAR(32) NULL, `date` DATETIME NULL, `fromname` VARCHAR(16) NULL, `toname` VARCHAR(16) NULL, PRIMARY KEY (`id`));";
         
         executeUpdate(createTable);
         
         createTable = "CREATE TABLE IF NOT EXISTS `" + tableName + "`.`ScarabMailFilter` ( `id` INT NOT NULL AUTO_INCREMENT, `word` VARCHAR(32) NULL, PRIMARY KEY (`id`) );";
         
         executeUpdate(createTable);
	}
	
	
}
