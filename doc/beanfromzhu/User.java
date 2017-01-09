package com.ifarm.bean;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {
	@Id
	private String userId;
	private String userPhone;
	private String userOtherAssociateId;
	private String userName;
	private String userPwd;
	private String userSex;
	private String userEmail;
	private String userRealName;
	private Timestamp userRegisterTime;
	private Timestamp userLastLoginTime;
	private String userImageUrl;
	private String userSignature;

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserOtherAssociateId() {
		return userOtherAssociateId;
	}

	public void setUserOtherAssociateId(String userOtherAssociateId) {
		this.userOtherAssociateId = userOtherAssociateId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPwd() {
		return userPwd;
	}

	public void setUserPwd(String userPwd) {
		this.userPwd = userPwd;
	}

	public String getUserSex() {
		return userSex;
	}

	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserRealName() {
		return userRealName;
	}

	public void setUserRealName(String userRealName) {
		this.userRealName = userRealName;
	}

	public Timestamp getUserRegisterTime() {
		return userRegisterTime;
	}

	public void setUserRegisterTime(Timestamp userRegisterTime) {
		this.userRegisterTime = userRegisterTime;
	}

	public Timestamp getUserLastLoginTime() {
		return userLastLoginTime;
	}

	public void setUserLastLoginTime(Timestamp userLastLoginTime) {
		this.userLastLoginTime = userLastLoginTime;
	}

	public String getUserImageUrl() {
		return userImageUrl;
	}

	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}

	public String getUserSignature() {
		return userSignature;
	}

	public void setUserSignature(String userSignature) {
		this.userSignature = userSignature;
	}
}
