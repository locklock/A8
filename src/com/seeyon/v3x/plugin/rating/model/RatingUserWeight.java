package com.seeyon.v3x.plugin.rating.model;

import com.seeyon.v3x.common.domain.BaseModel;

public class RatingUserWeight  extends BaseModel{
	


	private long userId;
	
	private String userName;
	
	private long procId;
	
	private int weight;
	


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getProcId() {
		return procId;
	}

	public void setProcId(long procId) {
		this.procId = procId;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
}
