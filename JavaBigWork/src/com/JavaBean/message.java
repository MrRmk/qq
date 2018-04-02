package com.JavaBean;

public class message {

	private int id;
	private String account_from;	//发送者
	private String account_to;		//接收者
	private String msg;				//发送的消息
	private String time;			//时间
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccount_from() {
		return account_from;
	}
	public void setAccount_from(String account_from) {
		this.account_from = account_from;
	}
	public String getAccount_to() {
		return account_to;
	}
	public void setAccount_to(String account_to) {
		this.account_to = account_to;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
}
