package com.DBconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCutils {

	//连接MySql数据库，用户名和密码都是root   
    String url = "jdbc:mysql://localhost:3306/test" ;    
	String username = "root" ;   
	String password = " ;
	String driver = "com.mysql.jdbc.Driver";
	Connection conn = null;
	Statement s = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	public Connection getConnection(){
		try{   
		    //加载MySql的驱动类   
		    Class.forName(driver) ;   
		    conn = DriverManager.getConnection(url,username,password);
		    System.out.println("数据库连接成功！。。。");
		    }catch(ClassNotFoundException e){   
		    System.out.println("找不到驱动程序类 ，加载驱动失败！");   
		    e.printStackTrace() ;   
		    } catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		 return conn;
	}
	// 实现查询功能的方法
	public ResultSet query(String sql){
		getConnection();
		try {
			s = conn.createStatement();
			rs = s.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			
			try {
					if(s!=null)
						s.close();
					if(conn!=null)
						conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return rs;
		
	}
	
	// 实现添加，修改功能 的方法
	public int sqlUpdate(String sql) {
		int i = 0;
		try {
			// 加载驱动+得到连接
			conn = getConnection();
			s = conn.createStatement();
			System.out.println("**连接数据库**添加/修改功能**连接成功了！");
			// 执行添加，更新操作
			i = s.executeUpdate(sql);// 返回一个值，如果为1则表示添加成功。
			if (i == 1) {
				System.out.println("数据添加/修改成功！");
			} else {
				System.out.println("数据添加/修改失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			try {
				if(conn!=null)
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return i;
	}
	
	// 实现删除学生记录的方法
	public int sqlDelete(String sql) {
		int i = 0;
		try {
			// 加载驱动
			conn = getConnection();
			ps = conn.prepareStatement(sql);
			System.out.println("**连接数据库**删除功能**连接成功了！");
			// 执行删除，更新操作
			i = ps.executeUpdate();// 返回一个值，如果为1则表示删除成功。
			if (i == 1) {
				System.out.println("数据删除成功！");
			} else {
				System.out.println("数据删除失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}
	// 实现关闭数据库连接的功能
	public void closeSqlConn() {
		try {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
