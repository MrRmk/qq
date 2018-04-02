
package com.Dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.DBconnection.JDBCutils;
import com.JavaBean.message;


public class messageDao {

	JDBCutils jdbc;
	Connection conn;
	Statement st;
	ResultSet rs;
	public List<message> queryAll(){
		List<message> listMessage = new ArrayList<message>();
		String sql = "select * from message";
		
		try {
			jdbc = new JDBCutils();
			conn = jdbc.getConnection();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()){
				message msg = new message();
				msg.setId(rs.getInt(1));
				msg.setAccount_from(rs.getString(2));
				msg.setAccount_to(rs.getString(3));
				msg.setMsg(rs.getString(4));
				msg.setTime(rs.getString(5));
				listMessage.add(msg);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return listMessage;
		
	}
	/**
	 * 根据账号查离线消息
	 **/
	public List<message> queryByAccount(String account){
		List<message> listMsg = new ArrayList<message>();
		List<message> all = queryAll();
		for(int i=0; i<all.size(); i++){
			if(all.get(i).getAccount_to().equals(account)){
				listMsg.add(all.get(i));
			}
		}		
		return listMsg;	
	}
	/**
	  增加一条离线消息
	 **/
	public int addOne(message msg){
		int i = 0;
		JDBCutils jdbc = new JDBCutils();
		String sql = "insert into message(account_from,account_to,msg,time) "
				+ "values('"+msg.getAccount_from()+"','"+msg.getAccount_to()+"','"+msg.getMsg()+"','"+msg.getTime()+"');";
		i = jdbc.sqlUpdate(sql);
		if( i == 1 ){
			System.out.println("数据库成功插入离线消息！---"+msg.getMsg());
		}else{
			System.out.println("数据库插入数据失败！");
		}
		return i;
	}
	
	/**
	 * 根据账号---删除有关该账号的所有离线消息
	 */
	public int deleteMessage(String account){
		
		int flag = 0;
		
		JDBCutils jdbc = new JDBCutils();
		String sql = "delete from message where account_to='"+account+"' ";
		flag = jdbc.sqlDelete(sql);
		if( flag == 1 ){
			System.out.println("数据库删除成功------所有有关用户[ "+account+" ]的离线消息！！");
		}else{
			System.out.println("数据库删除失败------所有有关用户[ "+account+" ]的离线消息！！");
		}
		
		return flag;
	}
	/**
	 * 根据账号--判断用户在离线消息表中是否有数据----->即，是否有人发送给该用户离线消息
	 */
	/*public int judgeIsOrNotMessage(){
		int flag = 0;
		JDBCutils jdbc = new JDBCutils();
		String 
		return flag;
	}*/
}
