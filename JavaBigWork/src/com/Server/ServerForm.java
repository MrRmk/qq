package com.Server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.Dao.messageDao;
import com.JavaBean.message;

/**
 * 服务器
 * @author 任孟凯
 * @version 1.0 2017-5-20
 */
/*
     登录 或 退出时，要发送通知消息，还要改变 在线列表，因此要向客户端发送两次消息
   发消息时，只用发一次通知消息即可
 */
public class ServerForm extends JFrame {
    private JList<String> list;			//在线列表
    private JTextArea area;
    private DefaultListModel<String> lm;//临时在线列表

    public ServerForm() {
        JPanel p = new JPanel(new BorderLayout());
        // 最右边的用户在线列表
        lm = new DefaultListModel<String>();
        list = new JList<String>(lm);
        JScrollPane js = new JScrollPane(list);
        Border border = new TitledBorder("在线");
        js.setBorder(border);
        Dimension d = new Dimension(150, p.getHeight());
        js.setPreferredSize(d);// 设置位置
        p.add(js, BorderLayout.EAST);

        // 通知文本区域
        final ImageIcon imageIcon = new ImageIcon("chat1.jpg"); //聊天框背景
        area = new JTextArea(){
        	Image image = imageIcon.getImage(); 
			Image grayImage = GrayFilter.createDisabledImage(image); 
			{ setOpaque(false); } 
			// instance initializer 
			public void paint(Graphics g) { 
				g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), imageIcon.getImageObserver());//图片随面板的大小变化而变化
				super.paint(g); 
				} 
        };
        //area.setEnabled(false);//不能选中和修改
        area.setEditable(false);
        area.setFont(new Font("宋体",Font.BOLD,20));
        area.setLineWrap(true);//激活自动换行功能
		area.setWrapStyleWord(true);//激活断行不断子功能

        p.add(new JScrollPane(area), BorderLayout.CENTER);
        this.getContentPane().add(p);

        // 添加菜单项
        JMenuBar bar = new JMenuBar();// 菜单条
        this.setJMenuBar(bar);
        JMenu jm = new JMenu("控制(C)");
        jm.setMnemonic('C');// 设置助记符---Alt+'C'，显示出来，但不运行
        bar.add(jm);
        final JMenuItem jmi1 = new JMenuItem("开启");
        jmi1.setAccelerator(KeyStroke.getKeyStroke('R', KeyEvent.CTRL_MASK));// 设置快捷键Ctrl+'R'，运行
        jmi1.setActionCommand("run");	//设置此组件激发的操作事件的命令名称。
        jm.add(jmi1);

        JMenuItem jmi2 = new JMenuItem("退出");
        jmi2.setAccelerator(KeyStroke.getKeyStroke('E', KeyEvent.CTRL_MASK));// 设置快捷键Ctrl+'R'
        jmi2.setActionCommand("exit");
        jm.add(jmi2);


        // 监听
        ActionListener a1 = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("run")) {
                    startServer();//--------开启服务器
                    jmi1.setEnabled(false);//内部方法~访问的只能是final对象
                }else if(e.getActionCommand().equals("exit")){
                    System.exit(0);
                }else{
                	System.exit(0);
                }
            }
        };

        jmi1.addActionListener(a1);
        jmi2.addActionListener(a1);

        //Toolkit 的子类被用于将各种组件绑定到特定本机工具包实现。
        Toolkit tk = Toolkit.getDefaultToolkit();//通过静态方法getDefaultToolkit()获取对象;	Toolkit是抽象类
        int width = (int) tk.getScreenSize().getWidth();
        int height = (int) tk.getScreenSize().getHeight();
        this.setBounds(width / 4, height / 4, width / 2, height / 2);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);// 关闭按钮器作用
        this.setTitle("后台管理");
        /*Font titleFont = new Font("Courier",Font.ITALIC,30);
        UIManager.getDefaults().put( "Title.font",titleFont);*/
        setVisible(true);
    }

    private static final int PORT = 9090;	//端口号

    protected void startServer() {
        try {
            ServerSocket server = new ServerSocket(PORT);
            area.append("启动服务：" + server + "\r\n");
            new ServerThread(server).start();//运行服务端线程，来监听客户端
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 用来保存所有在线用户的名字和Socket----池
    private Map<String, Socket> usersMap = new HashMap<String, Socket>();
    
    // 用来保存所有在线用户的名字和状态State(在线、隐身、离线)----池
    private Map<String, String> usersMap2 = new HashMap<String, String>();

    class ServerThread extends Thread {
        private ServerSocket server;

        public ServerThread(ServerSocket server) {
            this.server = server;
        }

        @Override
        public void run() {
            try {// 和客户端握手
                while (true) {
                    Socket socketClient = server.accept();
                    Scanner sc = new Scanner(socketClient.getInputStream());
                    if (sc.hasNext()) {
                        String userName = sc.nextLine();
                        area.append("\r\n用户[ " + userName + " ]登录 " + socketClient + "\r\n");// 在客户端通知
                        lm.addElement(userName);// 添加到用户在线列表

                        new ClientThread(socketClient).start();// 专门为这个客户端服务

                        usersMap.put(userName, socketClient);// 把当前登录的用户加到“在线用户”池中
                        usersMap2.put(userName, "在线");			// 把当前登录的用户和-状态-加到“在线用户”池中

                        msgAll(userName);// 把“当前用户登录的消息即用户名”通知给所有其他已经在线的人
                        msgSelf(socketClient);// 通知当前登录的用户，有关其他在线人的信息

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    class ClientThread extends Thread {
        private Socket socketClient;

        public ClientThread(Socket socketClient) {
            this.socketClient = socketClient;
        }

        @Override
        public void run() {
            System.out.println("一个与客户端通讯的线程启动并开始通讯...");
            try {
                Scanner sc = new Scanner(socketClient.getInputStream());
                while (sc.hasNext()) {
                    String msg = sc.nextLine();
                    System.out.println(msg);
                    String msgs[] = msg.split("@#@#");
                    //防黑
                    if(msgs.length!=4){
                        System.out.println("防黑处理...");
                        continue;
                    }

                    if("on".equals(msgs[0])){//on @#@#  发送对象  @#@# 消息  @#@#  用户名 
                    	boolean flag = false;
                    	//给全部的在线的人-->发送消息
                    	if(msgs[1].equals("全部")){
                    		sendMsgToSb(msgs);
                    	}else{
                    		String str = msgs[1].toString();
                    		String userName = str.substring(0, str.indexOf("["));	//接收者姓名
                    		String state = str.substring(str.indexOf("[")+1, str.indexOf("]"));//接收者的状态
                    		//判断 接收者 是 在线用户 还是 离线用户
                    		if( state.equals("在线") ){//发送个人在线消息
                    			//服务器把客户端的聊天消息转发给相应的其他客户端
                                sendMsgToSb(msgs);
                    		}else{//判断接收者是否是"离线"（隐身用户）	
                    			//(发送者：用户列表中 显示为接收者为离线状态,但是此用户不一定是真正的离线，可能是隐身)
                    			//如果服务器端在线列表中有的此接收者的话，即该接收者是隐身状态；若无，则是真正的离线状态。
                    			if( lm.size() > 0 ){//服务器用户列表的人数>0
                    				for(int i=0; i<lm.size(); i++){
                    					if( userName.equals(lm.get(i)) ){//用户表中有此接收者，隐身状态
                    						sendMsgToSb(msgs);
                    						flag = true;	//是隐身状态
                    					}
                    				}
                    			}
                    			//判断接收者是否是离线状态
                    			if( flag == false ){//接收者是离线状态---------发送离线消息，存到都数据库
                    				sendMsgToSbOffline(msgs);//发送离线消息，存到都数据库
                    			}
                    		}
                    		
                    	}
                    	
                    }
                    
                    //用户退出
                    if("exit".equals(msgs[0])){	//String msg = "exit@#@#全部@#@#null@#@#" + tfdUserName.getText();
                        //服务器显示
                        area.append("\r\n用户[ " + msgs[3] + " ]已退出!" + usersMap.get(msgs[3]) + "\r\n");

                        //从在线用户池中把该用户删除
                        usersMap.remove(msgs[3]);
                        usersMap2.remove(msgs[3]);

                        //服务器的在线列表中把该用户删除
                        lm.removeElement(msgs[3]);

                        //通知其他用户，该用户已经退出
                        sendExitMsgToAll(msgs);
                    }
                    
                    //用户设置隐身状态---->通知其他用户
                    if( "offline".equals(msgs[0]) ){
                    	System.out.println("-----off-----");
                    	//服务器显示
                        area.append("\r\n用户[ " + msgs[3] + " ]设置状态为--“隐身”!" + usersMap.get(msgs[3]) + "\r\n");
                    	
                        usersMap2.remove(msgs[3]);
                        usersMap2.put(msgs[3], "隐身");			// 把当前登录的用户和-状态-加到“在线用户”池中
                        
                        //修改其他用户在线列表中的该用户状态为-->离线; 同时修改自己的用户在线列表状态为-->隐身
                        sendOfflineMsgToAll(msgs);	//String msg = "offline@#@#全部@#@#隐身@#@#" + tfdUserName.getText();
                    }
                    
                    //用户设置在线状态---->通知其他用户
                    if( "online".equals(msgs[0]) ){
                    	System.out.println("-----on-----");
                    	//服务器显示
                        area.append("\r\n用户[ " + msgs[3] + " ]设置状态为--“在线”!" + usersMap.get(msgs[3]) + "\r\n");
                    	
                        usersMap2.remove(msgs[3]);
                        usersMap2.put(msgs[3], "在线");			// 把当前登录的用户和-状态-加到“在线用户”池中
                        
                        //修改其他用户在线列表中的该用户状态为-->在线; 同时修改自己的用户在线列表的状态 隐身-->为在线
                        sendOnlineMsgToAll(msgs);	//String msg = "online@#@#全部@#@#在线@#@#" + tfdUserName.getText();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    //服务器把客户端的聊天消息转发给相应的----离线客户------发送离线消息，存到都数据库
    public void sendMsgToSbOffline(String[] msgs) throws IOException {
    	
		//on @#@#  发送对象  @#@# 消息  @#@#  发送者
    	String s1 = msgs[1].toString();//接收者:xxx[......离线]
    	String userName = s1.substring(0,s1.indexOf("["));//接收者姓名
    	//获取当前时间
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
    	Date d = new Date();
    	String time = sdf.format(d);
        
    	//获取消息数据
    	message m = new message();
    	m.setAccount_from(msgs[3]);
    	m.setAccount_to(userName);
    	m.setMsg(msgs[2]);
    	m.setTime(time);
    	
    	//将离线消息存到数据库中
    	messageDao mDao = new messageDao();
    	int flag = 0;
    	flag = mDao.addOne(m);//将离线消息存到数据库中
    	if( flag == 1 ){
			System.out.println("---服务器---数据库成功插入离线消息！");
		}else{
			System.out.println("---服务器---数据库插入数据失败！");
		}
    	
    	//将操作结果反馈给发送者
    	Socket s = usersMap.get(msgs[3]);//给单独一个人发消息，通过对象姓名找到Map中的socket
    	PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
    	//String str = "msg@#@#server@#@#---你成功给离线用户[  "+userName+"  ]发送了离线消息";
    	String str = "msg@#@#offlineMsg@#@#--[我]成功给离线用户[  "+userName+"  ]发送了离线消息:"+msgs[2];
    	//String str = "msg@#@#"+"[你]"+"@#@#对[ "+msgs[1]+"]@#@#--离线消息："+msgs[2];	//msg + 发送者 +　＂对你＂　 +  内容
    	pw.println(str);
    	pw.flush();
        
    }

    //通知其他用户。该用户已经退出
    private void sendExitMsgToAll(String[] msgs) throws IOException {
        Iterator<String> userNames = usersMap.keySet().iterator();	//迭代器（Iterator）
        
        while(userNames.hasNext()){
            String userName = userNames.next();	
            Socket s = usersMap.get(userName);
            PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
            String str = "msg@#@#server@#@#用户[ "+msgs[3]+" ]已退出！";	//msgs[3]:用户名
            pw.println(str);
            pw.flush();//用于清空缓冲区的数据流

            str = "cmdRed@#@#server@#@#"+msgs[3]+"@#@#--";
            pw.println(str);
            pw.flush();
        }

    }

    /*
     msgs[0]:表示状态   on/exit 
     msgs[1]:表示对象   全部/单个人
     msgs[2]:表示内容   ***消息***
     msgs[3]:表示用户   张三
     */

    
    //修改其他用户在线列表中的该用户状态(隐身)为-->离线; 同时修改自己的用户在线列表状态为-->隐身
    //String msg = "offline@#@#全部@#@#隐身@#@#" + tfdUserName.getText();
    public void sendOfflineMsgToAll(String[] msgs)  {
    	
    	if( "全部".equals(msgs[1]) ){
    		if("隐身".equals(msgs[2])){
    			
    			Iterator<String> userNames = usersMap.keySet().iterator();
        		while( userNames.hasNext() ){
        			String userName = userNames.next();
        			if( !userName.equals(msgs[3]) ){	//除了当前用户,发给其他用户  离线状态
        				Socket s = usersMap.get(userName);
        				PrintWriter pw;
    					try {
    						pw = new PrintWriter(s.getOutputStream(),true);
    						String str = "msg@#@#server@#@#用户[ "+msgs[3]+" ]已退出！";	//msgs[3]:用户名
    	    	            pw.println(str);
    	    	            pw.flush();//用于清空缓冲区的数据流

    	    	            str = "cmdRed@#@#server@#@#"+msgs[3]+"@#@#**";//修改其他用户列表---使此用户状态为离线
    	    	            pw.println(str);
    	    	            pw.flush();
    	    	            
    	    	            
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
        				
        			}else if( userName.equals(msgs[3]) ){	//修改自己的用户在线列表状态为-->隐身
        				Socket s = usersMap.get(userName);
        				PrintWriter pw;
    					try {
    						pw = new PrintWriter(s.getOutputStream(),true);
    						String str = "msg@#@#server@#@#[ "+msgs[3]+" ]你已成功修改为--隐身状态！";	//msgs[3]:用户名
    	    	            pw.println(str);
    	    	            pw.flush();//用于清空缓冲区的数据流

    	    	            str = "cmdRed@#@#server@#@#Offline@#@#"+msgs[3];//修改自己状态为---隐身
    	    	            pw.println(str);
    	    	            pw.flush();
    	    	            
    	    	            
    					} catch (IOException e) {
    						// TODO Auto-generated catch block
    						e.printStackTrace();
    					}
        			}
        		}
    			
    		}//if("离线")
    		
    	}//if("全部")
    }
    
    //修改其他用户在线列表中的该用户状态为-->在线
    public void sendOnlineMsgToAll(String[] msgs)  {
    	
    	if( "全部".equals(msgs[1]) ){
    		Iterator<String> userNames = usersMap.keySet().iterator();
    		while( userNames.hasNext() ){
    			String userName = userNames.next();
    			if( !userName.equals(msgs[3]) ){	//除了当前用户，发给其他用户在线消息
    				Socket s = usersMap.get(userName);
    				PrintWriter pw;
					try {
						pw = new PrintWriter(s.getOutputStream(),true);
						String str = "msg@#@#server@#@#用户[ "+msgs[3]+" ]上线！";	//msgs[3]:用户名
	    	            pw.println(str);
	    	            pw.flush();//用于清空缓冲区的数据流

	    	            
	    	            String state = usersMap2.get(userName);
	                	System.out.println("登录用户，获取其他用户状态sendOnlineMsgToAll...1...："+state);
	    	            str = "cmdAdd@#@#server@#@#"+msgs[3]+"@#@#"+state;//修改其他用户列表---使此用户状态为在线
	    	            pw.println(str);
	    	            pw.flush();
	    	            
	    	            
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    				
    			}else{
    				Socket s = usersMap.get(userName);
    				PrintWriter pw;
					try {
						pw = new PrintWriter(s.getOutputStream(),true);
						String str = "msg@#@#server@#@#[ "+msgs[3]+" ]你已成功修改为--在线状态！";	//msgs[3]:用户名
	    	            pw.println(str);
	    	            pw.flush();//用于清空缓冲区的数据流

	    	            
	    	            String state = usersMap2.get(userName);
	                	System.out.println("登录用户，获取其他用户状态sendOnlineMsgToAll...2...："+state);
	    	            str = "cmdAdd@#@#server@#@#"+msgs[3]+"@#@#"+state;//修改其他用户列表---使此用户状态为在线
	    	            pw.println(str);
	    	            pw.flush();
	    	            
	    	            
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
    		}
    	}
    }
    
    //服务器把客户端的聊天消息转发给相应的其他客户端
    public void sendMsgToSb(String[] msgs) throws IOException {

        if("全部".equals(msgs[1])){//发送全部在线人的消息
            Iterator<String> userNames = usersMap.keySet().iterator();
            //遍历每一个在线用户，把聊天消息发给他
            while(userNames.hasNext()){
                String userName = userNames.next();
                if( userName.equals(msgs[3]) ){	//如果是自身
                	Socket s = usersMap.get(userName);
                    PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
                    //String s1 = msgs[1].toString();
                    String str = "msg@#@#"+"我"+"@#@#对所有人@#@#"+msgs[2];	//msg + 发送者  +  内容
                    pw.println(str);
                    pw.flush();
                }else{	//如果是其他人
                	 Socket s = usersMap.get(userName);
                     PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
                     //String s1 = msgs[1].toString();
                     String str = "msg@#@#"+msgs[3]+"@#@#对我@#@#"+msgs[2];	//msg + 发送者  +  内容
                     pw.println(str);
                     pw.flush();
                }
               
            }
        }else{//发送个人的消息		//on @#@#  发送对象  @#@# 消息  @#@#  发送者
        	
        	String s1 = msgs[1].toString();
        	String userName = s1.substring(0,s1.indexOf("["));//解析 接收者的姓名
            Socket receiveSocket = usersMap.get(userName);	//接收者的socket;;给单独一个人发消息，通过对象姓名找到Map中的socket
            Socket sendSocket = usersMap.get(msgs[3]);	//发送者的socket
            String state = usersMap2.get(userName);//获取接收者的状态
            if( state.equals("在线") ){
            	//发送给接收者的消息
            	System.out.println("sendMsgToSb..给在线的另一个人发送消息...........");
                PrintWriter pw = new PrintWriter(receiveSocket.getOutputStream(),true);
                String str = "msg@#@#"+msgs[3]+"@#@#对我@#@#"+msgs[2];	//msg + 发送者 +　＂对你＂　 +  内容
                System.out.println("....服务器.sendMsgToSb.1.."+msgs[0].toString()+":"+s1.substring(0,s1.indexOf("["))+":"+msgs[2].toString()+":"+msgs[3].toString());
                pw.println(str);
                pw.flush();
                
                //给别人发送消息时，同时在自己面板上也显示（发送给自己显示）
                System.out.println("sendMsgToSb..给在线的另一个人发送消息...........");
                PrintWriter sendPw = new PrintWriter(sendSocket.getOutputStream(),true);
                String sendStr = "msg@#@#"+"我"+"@#@#对["+userName+"]@#@#"+msgs[2];	//msg + 发送者 +　＂对你＂　 +  内容
                System.out.println("....服务器.sendMsgToSb.2.."+msgs[0].toString()+":"+s1.substring(0,s1.indexOf("["))+":"+msgs[2].toString()+":"+msgs[3].toString());
                sendPw.println(sendStr);
                sendPw.flush();
            }else if( state.equals("隐身") ){
            	//发送给接收者的消息
            	System.out.println("sendMsgToSb...发送给接收者的消息....隐身......");
                PrintWriter sendPw = new PrintWriter(receiveSocket.getOutputStream(),true);
                String sendStr = "msg@#@#"+msgs[3]+"@#@#对我@#@#"+msgs[2];	//msg + 发送者 +　＂对你＂　 +  内容
                //String s2 = msgs[2].toString();
                System.out.println("....服务器..sendMsgToSb.1..隐身..."+msgs[0].toString()+":"+s1.substring(0,s1.indexOf("["))+":"+msgs[2].toString()+":"+msgs[3].toString());
                sendPw.println(sendStr);
                sendPw.flush();
                
                //给别人发送消息时，同时在自己面板上也显示（发送给自己显示）
            	System.out.println("sendMsgToSb...发送给自己显示...隐身......");
                PrintWriter pw = new PrintWriter(sendSocket.getOutputStream(),true);
                String str = "msg@#@#offlineMsg@#@#--[我]成功给离线用户[  "+userName+"  ]发送了离线消息:"+msgs[2];
                System.out.println("....服务器..sendMsgToSb.2..隐身..."+msgs[0].toString()+":"+s1.substring(0,s1.indexOf("["))+":"+msgs[2].toString()+":"+msgs[3].toString());
                pw.println(str);
                pw.flush();
            }
            
        }
    }



    /**
     * 把“当前用户登录的消息即用户名”通知给所有其他已经在线的人
     * 
     * @param userName
     */
    // 技术思路:从池中依次把每个socket(代表每个在线用户)取出，向它发送userName
    public void msgAll(String userName) {
    	Iterator<String> uNames = usersMap.keySet().iterator();	//通过键遍历usersMap
        //遍历每一个在线用户，把聊天消息发给他
        while(uNames.hasNext()){
        	String uName = uNames.next();	//用户名---即，键
        	Socket s = usersMap.get(uName);//Socket---即，值
        	try {
            	if( uName.equals(userName) ){	//如果是当前用户--发送消息是---你已成功登录
            		PrintWriter pw = new PrintWriter(s.getOutputStream(), true);// 加true为自动刷新
                    String msg = "msg@#@#server@#@#[ " + userName + " ]你已成功登录!";// 通知客户端显示消息
                    pw.println(msg);
                    pw.flush();
                    
                    String state = usersMap2.get(userName);
                	System.out.println("登录用户，获取其他用户状态msgAll："+state);
                    msg = "cmdAdd@#@#server@#@#" + userName+"@#@#"+state;// 通知客户端在在线列表添加用户在线。  +"[在线]"
                    pw.println(msg);
                    pw.flush();
            	}else{	//否则--通知其他用户
	                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);// 加true为自动刷新
	                String msg = "msg@#@#server@#@#用户[ " + userName + " ]已登录!";// 通知客户端显示消息
	                pw.println(msg);
	                pw.flush();
	                
	                String state = usersMap2.get(userName);
	            	System.out.println("登录用户，获取其他用户状态msgAll："+state);
	                msg = "cmdAdd@#@#server@#@#" + userName+"@#@#"+state;// 通知客户端在在线列表添加用户在线。  +"[在线]"
	                pw.println(msg);
	                pw.flush();
            	}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    	
        /*Iterator<Socket> it = usersMap.values().iterator();
        while (it.hasNext()) {
            Socket s = it.next();
            try {
            	
                PrintWriter pw = new PrintWriter(s.getOutputStream(), true);// 加true为自动刷新
                String msg = "msg@#@#server@#@#用户[ " + userName + " ]已登录!";// 通知客户端显示消息
                pw.println(msg);
                pw.flush();
                
                String state = usersMap2.get(userName);
            	System.out.println("登录用户，获取其他用户状态msgAll："+state);
                msg = "cmdAdd@#@#server@#@#" + userName+"@#@#"+state;// 通知客户端在在线列表添加用户在线。  +"[在线]"
                pw.println(msg);
                pw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }


    /**
     * 通知当前登录的用户，有关其他在线人的信息
     * 
     * @param socketClient
     */
    // 把原先已经在线的那些用户的名字发给该登录用户，让他给自己界面中的lm添加相应的用户名
    public void msgSelf(Socket socketClient) {
        try {
            PrintWriter pw = new PrintWriter(socketClient.getOutputStream(),true);// 加true为自动刷新
            Iterator<String> it = usersMap.keySet().iterator();
            while (it.hasNext()) {
            	String userName = it.next();
            	
            	String state = usersMap2.get(userName);
            	System.out.println("登录用户，获取其他用户状态msgSelf："+state);
                String msg = "cmdAdd@#@#server@#@#" + userName+"@#@#"+state;	//it.next()为用户名
                pw.println(msg);
                pw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰，这个方法要写在new JFrame（）对象之前。
        new ServerForm();
    }
}
