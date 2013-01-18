package org.wimi.tools.einkaufszettel.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Article {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
//	@Persistent
//	private User user;
	@Persistent
	private String articleNamem;
	@Persistent
	private Date createDate;

	public Article() {
		this.createDate = new Date();
	}

//	public Article(User user, String articleName) {
//		this();
//		this.user = user;
//		this.articleNamem = articleName;
//	}

	public Article(String articleName) {
		this();
		this.articleNamem = articleName;
	}

	public Long getId() {
		return this.id;
	}

//	public User getUser() {
//		return this.user;
//	}

	public String getSymbol() {
		return this.articleNamem;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

//	public void setUser(User user) {
//		this.user = user;
//	}

	public void setSymbol(String symbol) {
		this.articleNamem = symbol;
	}
}