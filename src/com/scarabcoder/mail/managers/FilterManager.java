package com.scarabcoder.mail.managers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.scarabcoder.mail.Main;

public class FilterManager {
	
	private static List<String> filteredWords = new ArrayList<String>();
	
	public static List<String> getFilteredWords(){
		return filteredWords;
	}
	
	public static boolean containsFilteredWord(String text){
		
		List<String> splitText = Arrays.asList(text.split(" "));
		
		for(String str : splitText){
			if(filteredWords.contains(str.toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
	public static String replaceFilteredWords(String text){
		List<String> splitText = new CopyOnWriteArrayList<String>(Arrays.asList(text.split(" ")));
		int x = 0;
		for(String str : splitText){
			if(filteredWords.contains(str)){
				
				splitText.set(x, "***");
				
			}
			x++;
		}
		return StringUtils.join(splitText, " ");
	}
	
	public static void loadFilterFromFile(){
		try {
			String filterWords = FileUtils.readFileToString(Main.filter);
			filteredWords = Arrays.asList(filterWords.split("\n"));
			
			Main.executeUpdate("DELETE FROM ScarabMailFilter");
			String query = "INSERT INTO ScarabMailFilter (id, word) VALUES ";
			query = query + "(null, '" + filteredWords.get(0) + "')";
			
			int x = 0;
			for(String word : filteredWords){
				if(x > 0){
					query = query + ", (null, '" + word + "')";
				}
				x++;
			}
			Main.executeUpdate(query);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void saveFilterToFile(){
		String text = StringUtils.join(filteredWords, "\n");
		try {
			FileUtils.writeStringToFile(Main.filter, text);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void reloadFilter(){
		filteredWords = new ArrayList<String>();
		ResultSet data = Main.executeQuery("SELECT word FROM ScarabMailFilter");
		try {
			while(data.next()){
				filteredWords.add(data.getString("word"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void addWord(String word){
		
		PreparedStatement st;
		try {
			st = Main.getConnection().prepareStatement("INSERT INTO ScarabMailFilter (id, word) VALUES (null, ?)");
			st.setString(1, word);
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		reloadFilter();
	}
	
	public static void removeWord(String word){
		
		try {
			PreparedStatement st = Main.getConnection().prepareStatement("DELETE FROM ScarabMailFilter WHERE word=?");
			st.setString(1, word);
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		reloadFilter();
	}
	
	
	
	
}
