package com.Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import com.DBconnection.JDBCutils;
import com.JavaBean.user;


public class userDao {

	JDBCutils jdbc;
	Connection conn;
	Statement st;
	ResultSet rs;
	public List<user> queryAll(){
		List<user> listUser = new ArrayList<user>();
		String sql = "select * from user";
	    String url = "jdbc:mysql://localhost:3306/test" ;    
		String username;
		String password;		
		try {
			//加载MySql的驱动类   
		    Class.forName("com.mysql.jdbc.Driver") ;   
		    username = "root" ;   
		    password = "" ; 
		    conn = DriverManager.getConnection(url,username,password);
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				user u = new user();
				u.setAccount(rs.getString(1));
				u.setPsw(rs.getString(2));
				u.setState(rs.getString(3));
				listUser.add(u);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listUser;
		
	}
	
	//设置登陆后的用户为”在线“状态
	public void setStateOnline(user u){
		userDao uDao = new userDao();
		List<user> listUser = uDao.queryAll();
		System.out.println("...........userDao..0..........."+u.getAccount());
		if( listUser.size() > 0 ){
			System.out.println("...........userDao..1..........."+u.getAccount());
			for( int i=0; i<listUser.size(); i++){
				System.out.println("...........userDao..2..........."+u.getAccount());
				if( listUser.get(i).getAccount().equals(u.getAccount()) ){
					System.out.println("...........userDao..3..........."+u.getAccount());
					String sql = "update user set state='在线' where account='"+u.getAccount()+"' ";
					
					//SqlConn s = new SqlConn();
					jdbc = new JDBCutils();
					int x = jdbc.sqlUpdate(sql);	//更新状态
					if( x==1 ){
						System.out.println("设置登陆后的用户为”在线“状态成功！");
					}
					//listUser.get(i).setState("在线");
					
					/*if( u.getState().equals("隐身") ){
						listUser.get(i).setState("在线");
					}else if( u.getState().equals("在线") ){
						listUser.get(i).setState("离线");
					}*/
					
				}
			}
		}else{
			System.out.println("user表暂无数据，出错！");
		}
	}
	
	//下拉框改变用户状态之后，就修改用户的状态
	public void setState(String account,String state){
		userDao uDao = new userDao();
		List<user> listUser = uDao.queryAll();
		if( listUser.size() > 0 ){
			for( int i=0; i<listUser.size(); i++){
				if( listUser.get(i).getAccount().equals(account) ){
					
					String sql = "update user set state='"+state+"' where account='"+account+"' ";
					//SqlConn s = new SqlConn();
					jdbc = new JDBCutils();
					int x = jdbc.sqlUpdate(sql);	//更新状态
					if( x==1 ){
						System.out.println("设置登陆后的用户为”在线“状态成功！");
					}
				}
			}
		}else{
			System.out.println("user表暂无数据，出错！");
		}
	}
	
	//用户退出后设置为”离线“状态
	public void setStateOffline(user u){
		userDao uDao = new userDao();
		List<user> listUser = uDao.queryAll();
		System.out.println(".................................."+u.getAccount());
		if( listUser.size() > 0 ){
			System.out.println(".......1..........................."+u.getAccount());
			for( int i=0; i<listUser.size(); i++){
				System.out.println(".....2............................."+u.getAccount());
				if( listUser.get(i).getAccount().equals(u.getAccount()) ){
					System.out.println("......3............................"+u.getAccount());
					String sql = "update user set state='离线' where account='"+u.getAccount()+"' ";
					
					
					//SqlConn s = new SqlConn();
					jdbc = new JDBCutils();
					int x = jdbc.sqlUpdate(sql);	//更新状态
					if( x==1 ){
						System.out.println("设置登陆后的用户为”在线“状态成功！");
					}
				}
			}
		}else{
			System.out.println("user表暂无数据，出错！");
		}
	}
	
	//通过账号获取用户信息
	public user getUser(String account){
		user u = null;
		userDao uDao = new userDao();
		List<user> listUser = uDao.queryAll();
		if( listUser.size() > 0 ){
			for( int i=0; i<listUser.size(); i++){
				if( listUser.get(i).getAccount().equals(account) ){
					u = new user();
					u = listUser.get(i);
				}
			}
		}else{
			System.out.println("user表暂无数据，出错！");
		}
		
		return u;
	}
	
	
	/**
	 * 注册账号，插入一条数据
	 * **/
	public int insertOne(user u){
		JDBCutils jdbc = new JDBCutils();
		String sql = "insert into user values('"+u.getAccount()+"','"+u.getPsw()+"','"+u.getState()+"');";
		return jdbc.sqlUpdate(sql);
	}
	

	
	
}
