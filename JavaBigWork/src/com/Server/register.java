package com.Server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.Dao.userDao;
import com.JavaBean.user;

public class register extends JFrame {

	JFrame f = null;
	JPanel p,pTitle,pName,pPsw,pBt,pWanning;
	JLabel lName,lPsw,lTitle,lWanning;
	JTextField tName; 
	JPasswordField tPsw;
	JButton bt,bt_back;
	public register(){
		f = new JFrame("Chat注册");
		f.setBounds(400, 200, 400, 310);
		f.setLayout(null);
		f.setResizable(false);
		
		Image image = new ImageIcon("register.jpg").getImage();
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
		lTitle = new JLabel("注 册 用 户 信 息");
		lTitle.setFont(new Font("宋体", Font.ITALIC, 30));
		lTitle.setForeground(new Color(6,6,6));
		lWanning = new JLabel("账号或密码错误！");
		lWanning.setForeground(Color.RED);
		
		tName = new JTextField(15);
		tName.setBackground(Color.WHITE);
		tPsw = new JPasswordField(15);
		tPsw.setBackground(Color.WHITE);
		
		bt = new JButton("  注册  ");
		bt.setSize(90, 30);
		bt_back = new JButton("  返回  ");
		bt_back.setSize(90, 30);
		
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
		pBt.add(bt_back);
		pWanning.add(lWanning);
		pWanning.setVisible(false);
		
		MyEvent();
		f.setVisible(true);
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	//鼠标点击事件
	public void MyEvent(){
		bt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String account,psw;
				//获取账号、密码
				account = tName.getText();
				psw = tPsw.getText();
				System.out.println("账号："+account+"\n密码："+psw);
				if( account.equals("") ){
					System.out.println("账号为空！");
					lWanning.setText("账号为空！");
					pWanning.setVisible(true);
				}
				if( psw.equals("") ){
					System.out.println("密码为空！");
					lWanning.setText("密码为空！");
					pWanning.setVisible(true);
				}
				if( account.equals("") && psw.equals("") ){
					System.out.println("账号和密码都为空！");
					lWanning.setText("账号和密码都为空！");
					pWanning.setVisible(true);
				}
				if( (!account.equals("")) && (!psw.equals("")) ){//如果账号密码都不为空
					//判断账号、密码
					userDao uDao = new userDao();
					user u = new user();
					u.setAccount(account);
					u.setPsw(psw);
					u.setState("离线");
					int flag = 0;
					flag = uDao.insertOne(u);
					if(flag == 1){
						lWanning.setText("注册成功！");
						pWanning.setVisible(true);
					}else{
						lWanning.setText("注册失败！");
						pWanning.setVisible(true);
					}
				}
				
			}
		});
		bt_back.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				f.dispose();
				new login();
			}
		});
	}
	public static void main(String[] args){
		JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰
		register l = new register();
		
	}
	
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
