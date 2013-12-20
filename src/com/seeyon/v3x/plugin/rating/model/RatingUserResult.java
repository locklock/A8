package com.seeyon.v3x.plugin.rating.model;

import java.util.Date;

import com.seeyon.v3x.common.domain.BaseModel;

public class RatingUserResult  extends BaseModel{
/**
 Id                  NUMBER,   //id
  cor_id              NUMBER,       //流程id
  Evaluate            VARCHAR2(255),  //简单评价
  Score               NUMBER,       //分数
  scoredate           DATE,           //打分时间
  Commentators_Id     NUMBER,         //打分人id
  Commentators_Name   VARCHAR2(255),  //打分人名称

 */


public Long getId() {
	return id;
}

public void setId(Long id) {
	this.id = id;
}
//private long id;
	/*userid*/
	private long userId;
	
	private String userName;
	
	/**
	 * 流程ID
	 */
	private long procId;
	
	/*维度名称*/
	private String ratingItem;
	
	/*分数*/
	private int score;
	/**
	 * 实例ID
	 */
	private long instanceId;
	
	
	private String instanceName;
	
	/*评价*/
	private String comment;
	
	/*权重*/
	private int weight = 50;
	
	private Date time;
	

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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public String getRatingItem() {
		return ratingItem;
	}

	public void setRatingItem(String ratingItem) {
		this.ratingItem = ratingItem;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	
	
	
	
	
}
