package com.Server;
/*
 * 登录界面
 * */
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

/*
 * 登录窗口
 * */
import javax.swing.*;

import com.Dao.userDao;
import com.JavaBean.user;
import com.Server.ClientForm.ClientThread;


public class login extends JFrame{
	
	//private static String HOST = "192.168.1.45";// 服务器的ip地址,(自己机子，)
	private static String HOST = "127.0.0.1";// 服务器的ip地址,(自己机子，)
	private static int PORT = 9090;// 服务器的端口号
	private Socket clientSocket;	//客户端socket
	private PrintWriter pw;			//打印流

	JFrame f = null;
	JPanel p,pTitle,pName,pPsw,pBt,pWanning;
	JLabel lName,lPsw,lTitle,lWanning;
	JTextField tName; 
	JPasswordField tPsw;
	JButton bt,bt_register;
	public login(){
		f = new JFrame("Chat登录");
		f.setBounds(400, 200, 410, 330);
		f.setLayout(null);
		//f.setResizable(false);
		
		Image image = new ImageIcon("login.jpg").getImage();
		p = new MyJPanel(image);
		
		p.setBounds(0, 0, 400, 300);
		p.setLayout(null);
		pTitle = new JPanel();
		pTitle.setBounds(0, 0, 400, 100);
		pTitle.setOpaque(false);
//		pTitle.setBackground(Color.LIGHT_GRAY);
		pName = new JPanel();
		pName.setBounds(0, 100, 400, 50);
		pName.setOpaque(false);
//		pName.setBackground(Color.BLUE);
		pPsw = new JPanel();
		pPsw.setBounds(0, 150, 400, 50);
		pPsw.setOpaque(false);
//		pPsw.setBackground(Color.CYAN);
		pBt = new JPanel();
		pBt.setBounds(0, 200, 400, 50);
		pBt.setOpaque(false);
//		pBt.setBackground(Color.GRAY);
		pWanning = new JPanel();
		pWanning.setBounds(0, 250, 400, 40);
		pWanning.setOpaque(false);
		
		lName = new JLabel("账号：");
		lPsw = new JLabel("密码：");
		lTitle = new JLabel("We Chat O(∩_∩)O~~");
		lTitle.setFont(new Font("宋体", Font.ITALIC, 30));
		lTitle.setForeground(new Color(6,6,6));
		lWanning = new JLabel("账号或密码错误！");
		lWanning.setForeground(Color.RED);
		
		tName = new JTextField(15);
		tName.setBackground(Color.WHITE);
		tPsw = new JPasswordField(15);
		tPsw.setBackground(Color.WHITE);
		
		bt = new JButton("  登录  ");
		bt.setSize(90, 30);
		bt_register = new JButton("  注册  ");
		bt.setSize(90, 30);
		
		f.add(p);
		p.add(pTitle);
		p.add(pName);
		p.add(pPsw);
		p.add(pBt);
		p.add(pWanning);
		
		pTitle.add(lTitle);
		pName.add(lName);
		pName.add(tName);
		pPsw.add(lPsw);
		pPsw.add(tPsw);
		pBt.add(bt);
		pBt.add(bt_register);
		pWanning.add(lWanning);
		pWanning.setVisible(false);
		
		MyEvent();
		f.setVisible(true);
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	//鼠标点击事件
	public void MyEvent(){
		//监听登录按钮
		bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String account,psw;
				//获取账号、密码
				account = tName.getText();
				psw = tPsw.getText();
				System.out.println("账号："+account+"\n密码："+psw);
				//判断账号、密码
				userDao uDao = new userDao();
				user ua = uDao.getUser(account);
				if( ua != null ){	//说明该账号存在
					
					if(psw.equals(ua.getPsw())){//如果密码也验证成功
						
						//---连接服务器-------------------------------------------------
				        connecting(account);// 连接服务器的动作
				        
				        if (pw == null) {//连接失败
				            JOptionPane.showMessageDialog(login.this, "服务器未开启或网络未连接，无法连接！");
				            return;
				        }else{//连接成功
				        	f.dispose();
				        	
				        	//----改变用户状态为在线----------------------
					        user u = uDao.getUser(account); //通过账号获取用户信息
					        System.out.println(".................a................."+u.getAccount()+".."+u.getState());
					        uDao.setStateOnline(u);	//改变用户状态为--在线
					        
				        	JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰
							new ClientForm(account,clientSocket,pw);
				        }
						
					}else if( psw.equals("") ){
						lWanning.setText("密码为空！！！");
						pWanning.setVisible(true);
					}else{//密码错误
						lWanning.setText("密码错误！！！");
						pWanning.setVisible(true);
					}
					
				}else{//说明该账号不存在
					lWanning.setText("账号不存在！");
					pWanning.setVisible(true);
				}
				
			}
		});
		
		//监听注册按钮
		bt_register.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				f.dispose();
				new register();
			}
		});
		
	}
	
	//---连接服务器--------
	private void connecting(String account) {//连接服务器
        try {
            // 先根据用户名防范
            //String userName = tfdUserName.getText();
        	String userName = account;
            if (userName == null || userName.trim().length() == 0) {
                JOptionPane.showMessageDialog(login.this, "连接服务器失败!\r\n用户名有误，请重新输入！");
                return;
            }

            clientSocket = new Socket(HOST, PORT);//跟服务器握手
            pw = new PrintWriter(clientSocket.getOutputStream(), true);// 加上true自动刷新
            pw.println(userName);// 向服务器报上自己的用户名------>向服务器发送消息
            this.setTitle("用户[ " + userName + " ]上线...");

            //new ClientThread().start();// 接受服务器发来的消息---(建立连接之后)一直开着的-------------------------------------
            
        } catch (UnknownHostException e) {
        	System.out.println("服务器未开启！！.........");
        	//e.printStackTrace();
        } catch (IOException e) {
            System.out.println("服务器未开启！！.........无法连接");
        	//e.printStackTrace();
        }
    }
	
	public static void main(String[] args){
		JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰
		login l = new login();

	}
	
	//添加背景图片
	class MyJPanel extends JPanel{
		
		Image image;
		public  MyJPanel(Image img){
			this.image = img;
		}
		// 固定背景图片，允许这个JPanel可以在图片上添加其他组件  
	    protected void paintComponent(Graphics g) {  
	        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);  
	    }  
	}

}
