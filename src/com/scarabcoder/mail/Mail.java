package com.scarabcoder.mail;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Mail {
	
	private int id;
	
	private String author;
	
	private String receiver;
	
	private String message;
	
	private Date date;

	private String fromName;

	private String toName;
	
	
	public Mail(ResultSet set){
		try {
			this.id = set.getInt("id");
			this.author = set.getString("sender");
			this.receiver = set.getString("receiver");
			this.message = set.getString("content");
			this.date = set.getDate("date");
			this.toName = set.getString("toname");
			this.fromName = set.getString("fromname");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public String getToName(){
		return this.toName;
	}
	
	public String getFromName(){
		return this.fromName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
	
}
