package com.Server;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.Dao.messageDao;
import com.Dao.userDao;
import com.JavaBean.message;
import com.JavaBean.user;
import com.Server.login.MyJPanel;

/**
 * 客户端
 * @author 任孟凯
 * @version 1.0 2017-5-20
 */
public class ClientForm extends JFrame implements ActionListener {
    private JTextField tfdUserName;	//用户姓名标识
    private JList<String> list;			//在线列表
    private DefaultListModel<String> lm;//临时在线列表
    private JTextArea allMsg;	//所有消息
    private JTextField tfdMsg;	//发送的消息
    private JButton btnCon;		//连接按钮
    private JButton btnExit;	//退出按钮
    private JButton btnSend;	//发送按钮
    private JComboBox box;		//下拉框
    private JLabel label;		//设置标签
    private String state;		//用户状态

    //private static String HOST="192.168.1.45";
    private static String HOST = "127.0.0.1";// 自己机子，服务器的ip地址
    private static int PORT = 9090;// 服务器的端口号
    private static Socket clientSocket;	//客户端socket
    private PrintWriter pw;			//打印流

    public ClientForm(String account,Socket cSocket,PrintWriter pwriter) {

        super("即时通讯工具--"+account);
        
        // 菜单条
        addJMenu();

        // 上面的面板
        JPanel p = new JPanel();
        //p.setLayout(new GridLayout(1,1,1,5));
        JLabel jlb1 = new JLabel("用户标识:");
        tfdUserName = new JTextField(10);
        tfdUserName.setText(account);
        tfdUserName.setEnabled(false);//不能选中和修改
        // tfdUserName.setEditable(false);//不能修改

        // 链接按钮
        //ImageIcon icon = new ImageIcon("a.png");
        //btnCon = new JButton("连接", icon);
    /*	btnCon = new JButton("连接");
        btnCon.setActionCommand("c");	//设置此组件激发的操作事件的命令名称。
        btnCon.addActionListener(this);	//设置监听链接按钮
*/
        
        label = new JLabel("状态:");
        //下拉框
        box = new JComboBox();
        box.addItem("在线");
        box.addItem("隐身");
        box.setSelectedIndex(0);//默认选中第一行
        
        
        
        // 退出按钮
        //icon = new ImageIcon("b.jpg");
        //btnExit = new JButton("退出");
        //btnExit.setActionCommand("exit");

      //设置监听退出按钮，按钮初始不可用
        //btnExit.addActionListener(this);
        //btnExit.setEnabled(false);
        p.add(jlb1);
        p.add(tfdUserName);
        p.add(label);
        p.add(box);
        //p.add(btnExit);
        getContentPane().add(p, BorderLayout.NORTH);

        // 中间的面板
        JPanel cenP = new JPanel(new BorderLayout());
        this.getContentPane().add(cenP, BorderLayout.CENTER);

        // 在线列表
        lm = new DefaultListModel<String>();
        list = new JList<String>(lm);
        lm.addElement("全部");
        
        //---------获取user表，所有用户显示在用户列表----------------------------------------
        userDao uD = new userDao();
        List<user> all = uD.queryAll();
        for( int i=0; i<all.size(); i++ ){
        	if( all.get(i).getState().equals("在线") ){
        		lm.addElement(all.get(i).getAccount()+"["+all.get(i).getState()+"]");
        	}
        }
        for( int i=0; i<all.size(); i++ ){
        	if( all.get(i).getState().equals("离线") ){
        		lm.addElement(all.get(i).getAccount()+"[..............."+all.get(i).getState()+"]");
        	}
        }
        for( int i=0; i<all.size(); i++ ){
        	if( all.get(i).getState().equals("隐身") ){
        		lm.addElement(all.get(i).getAccount()+"[...............离线"+"]");
        	}
        }
        
        
        list.setSelectedIndex(0);// 设置默认显示
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);// 只能选中一行（只能选一个用户，或全部用户）
        list.setVisibleRowCount(10);
        JScrollPane js = new JScrollPane(list);
        Border border = new TitledBorder("好友列表");
        js.setBorder(border);
        Dimension preferredSize = new Dimension(150, cenP.getHeight());
        js.setPreferredSize(preferredSize);
        cenP.add(js, BorderLayout.EAST);

        
        
        // 聊天消息框
        final ImageIcon imageIcon = new ImageIcon("chat1.jpg"); //聊天框背景
        allMsg = new JTextArea(){
        	Image image = imageIcon.getImage(); 
			Image grayImage = GrayFilter.createDisabledImage(image); 
			{ setOpaque(false); } 
			// instance initializer 
			public void paint(Graphics g) { 
				//g.drawImage(imageIcon.getImage(), 0, 0, this);
				g.drawImage(imageIcon.getImage(), 0, 0, getWidth(), getHeight(), imageIcon.getImageObserver());
				super.paint(g); 
				} 
        };
        //allMsg = new JTextArea();
        allMsg.setFont(new Font("正楷",Font.BOLD,20));
        allMsg.setLineWrap(true);		//激活自动换行功能
		allMsg.setWrapStyleWord(true);	//激活断行不断子功能

        allMsg.setEditable(false);
        cenP.add(new JScrollPane(allMsg), BorderLayout.CENTER);
        
        //获取离线消息---加入到聊天消息框
        messageDao uDao = new messageDao();
        List<message> offLineMsg = uDao.queryByAccount(account);
        if( offLineMsg.size() > 0 ){
        	allMsg.append("-----------------------------------------离线消息---------------------------------------------------\n");
            for(int i=0; i<offLineMsg.size(); i++){
            	String time = offLineMsg.get(i).getTime();	//时间
            	String msg = offLineMsg.get(i).getMsg();	//内容
            	String sendAuthor = offLineMsg.get(i).getAccount_from();//发送者
            	String content = time+" : " + "用户[ " + sendAuthor + " ]对我说:  " + msg + "\r\n";//显示到文本框中的格式
            	System.out.println("客户端....."+content);
            	allMsg.append(content);
            }
            allMsg.append("\n-----------------------------------------在线消息---------------------------------------------------");
        }
        
        
        // 消息发送面板
        JPanel p3 = new JPanel();
        JLabel jlb2 = new JLabel("消息:");
        p3.add(jlb2);
        tfdMsg = new JTextField(20);
        p3.add(tfdMsg);
        btnSend = new JButton("发送");
        //btnSend.setEnabled(false);	//初始不可用(未连接时)
        btnSend.setActionCommand("send");//设置此组件激发的操作事件的命令名称。
        btnSend.addActionListener(this);//设置监听发送按钮
        p3.add(btnSend);
        this.getContentPane().add(p3, BorderLayout.SOUTH);

        // *************************************************
        // 右上角的X-关闭按钮-添加事件处理
        addWindowListener(new WindowAdapter() {
            // 适配器
            @Override
            public void windowClosing(WindowEvent e) {
                if (pw == null) {
                    System.exit(0);
                }
                //发送到服务器--->来通知其他用户当前用户下线通知--->改变其他用户列表中此用户的状态
                String msg = "exit@#@#全部@#@#null@#@#" + tfdUserName.getText();
                pw.println(msg);
                pw.flush();
                
                //改表此用户--数据库中的状态--为离线
                userDao uDao1 = new userDao();
                user u1 = uDao1.getUser(account); //通过账号获取用户信息
                uDao1.setStateOffline(u1);//改变用户状态为--离线
                System.out.println("用户["+account+"]--下线了！！");
                
                //关闭之后---删除数据库中有关该用户的离线消息-------->即，使下次重新登录时，刷新纪录
                messageDao msDao = new messageDao();
                List<message> message = msDao.queryByAccount(account);
                if( message.size() > 0 ){	//判断是否用离线消息
                	int flag = msDao.deleteMessage(account);
                    if( flag == 1 ){
            			System.out.println("...客户端操作显示.....数据库删除成功------所有有关用户[ "+account+" ]的离线消息！！");
            		}else{
            			System.out.println("...客户端操作显示.....数据库删除失败------所有有关用户[ "+account+" ]的离线消息！！");
            		}
                }

                System.exit(0);
            }
        });
        
        //-------------------------
        //给下拉框事件注册
        box.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if( ItemEvent.SELECTED==e.getStateChange() ){
					//这个判断是选择只会得到一个结果，如果没有判断，会得到两个相同的值，从而获取不到所要选中的值。
					state = box.getSelectedItem().toString();//获取当前下拉框中的内容
					userDao uD = new userDao();
					uD.setState(account, state);
					
					if( state.equals("隐身") ){
						//向服务器发消息，通知其他用户该用户的状态为“离线”，设置自己为“隐身”
						String msg = "offline@#@#全部@#@#隐身@#@#" + tfdUserName.getText();
		                pw.println(msg);
		                pw.flush();
		                
					}else if( state.equals("在线") ){
						//向服务器发消息，通知其他用户该用户的状态为“在线”
						String msg = "online@#@#全部@#@#在线@#@#" + tfdUserName.getText();
		                pw.println(msg);
		                pw.flush();
					}
					
				}
			}
        	
        });
        
        
        setBounds(300, 300, 900, 600);
        setVisible(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        //接收登录也面传递过来的socket和pw
        clientSocket = cSocket;
        pw = pwriter;
        new ClientThread().start();// 接受服务器发来的消息---(建立连接之后)一直开着的-------------------------------------
        this.setTitle("用户[ " + account + " ]上线...");
        //---连接服务器-------------------------------------------------
        /*connecting(account);// 连接服务器的动作
        if (pw == null) {
            JOptionPane.showMessageDialog(this, "服务器未开启或网络未连接，无法连接！");
            return;
        }*/
        
    }
    
    // 菜单条
    private void addJMenu() {
        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu menu = new JMenu("选项");
        menuBar.add(menu);

        JMenuItem menuItemSet = new JMenuItem("设置");
        JMenuItem menuItemHelp = new JMenuItem("帮助");
        menu.add(menuItemSet);
        menu.add(menuItemHelp);
        
        //设置按钮监听事件
        menuItemSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final JDialog dlg = new JDialog(ClientForm.this);// 弹出一个界面
                // 不能直接用this

                dlg.setBounds(ClientForm.this.getX()+20, ClientForm.this.getY()+30,
                        350, 150);
                dlg.setLayout(new FlowLayout());
                dlg.add(new JLabel("服务器IP和端口:"));

                final JTextField tfdHost = new JTextField(10);
                tfdHost.setText(ClientForm.HOST);
                dlg.add(tfdHost);

                dlg.add(new JLabel(":"));

                final JTextField tfdPort = new JTextField(5);
                tfdPort.setText(""+ClientForm.PORT);
                dlg.add(tfdPort);

                JButton btnSet = new JButton("设置");
                dlg.add(btnSet);
                btnSet.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String ip = tfdHost.getText();//解析并判断ip是否合法
                        String strs[] = ip.split("\\.");
                        if(strs==null||strs.length!=4){
                            JOptionPane.showMessageDialog(ClientForm.this, "IP类型有误！");
                            return ;
                        }
                        try {
                            for(int i=0;i<4;i++){
                                int num = Integer.parseInt(strs[i]);
                                if(num>255||num<0){
                                    JOptionPane.showMessageDialog(ClientForm.this, "IP类型有误！");
                                    return ;
                                }
                            }
                        } catch (NumberFormatException e2) {
                            JOptionPane.showMessageDialog(ClientForm.this, "IP类型有误！");
                            return ;
                        }
                        
                        //修改后赋值
                        ClientForm.HOST=tfdHost.getText();//先解析并判断ip是否合法

                        try {
                            int port = Integer.parseInt( tfdPort.getText() );//获取端口号
                            if(port<0||port>65535){
                                JOptionPane.showMessageDialog(ClientForm.this, "端口范围有误！");
                                return ;
                            }
                        } catch (NumberFormatException e1) {
                            JOptionPane.showMessageDialog(ClientForm.this, "端口类型有误！");
                            return ;
                        }
                        ClientForm.PORT=Integer.parseInt( tfdPort.getText() );

                        dlg.dispose();//关闭这个界面
                    }
                });
                dlg.setVisible(true);//显示出来
            }
        });
        
        //帮助按钮监听事件
        menuItemHelp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dlg = new JDialog(ClientForm.this);

                dlg.setBounds(ClientForm.this.getX()+30,ClientForm.this.getY()+30, 500, 100);
                dlg.setLayout(new FlowLayout());
                //dlg.setFont(new Font("正楷",Font.BOLD,20));
                dlg.add(new JLabel("版本所有@任孟凯&张琦.2017.5.20&& tel:18279175436--即时通讯工具"));
                dlg.setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        /*if (e.getActionCommand().equals("c")) {	//连接按钮
            if (tfdUserName.getText() == null
                    || tfdUserName.getText().trim().length() == 0
                    || "@#@#".equals(tfdUserName.getText())
                    || "@#".equals(tfdUserName.getText())) {
                JOptionPane.showMessageDialog(this, "用户名输入有误，请重新输入！");
                return;
            }

            connecting();// 连接服务器的动作
            if (pw == null) {
                JOptionPane.showMessageDialog(this, "服务器未开启或网络未连接，无法连接！");
                return;
            }

            ((JButton) (e.getSource())).setEnabled(false);	//使连接按钮不可用
            // 获得btnCon按钮--获得源
            // 相当于btnCon.setEnabled(false);
            btnExit.setEnabled(true);	//使退出按钮可用
            btnSend.setEnabled(true);	//使发送按钮可用
            tfdUserName.setEditable(false);	//使用户标识栏不可操作
            
        } else 
        */	
        if (e.getActionCommand().equals("send")) {//发送按钮
            if (tfdMsg.getText() == null
                    || tfdMsg.getText().trim().length() == 0) {
                return;
            }
            String msg = "on@#@#" + list.getSelectedValue() + "@#@#"
                    + tfdMsg.getText() + "@#@#" + tfdUserName.getText();	//on @#@#  发送对象  @#@# 消息  @#@#  用户名 
            System.out.println("...客户端发送用户..="+list.getSelectedValue());
            pw.println(msg);
            pw.flush();

            // 将发送消息的文本设为空
            tfdMsg.setText("");
            
        } else if (e.getActionCommand().equals("exit")) {//退出按钮
            //先把自己在线的菜单清空
            lm.removeAllElements();

            sendExitMsg();// 向服务器发送退出消息
            //btnCon.setEnabled(true);
            btnExit.setEnabled(false);
            btnSend.setEnabled(false);
            tfdUserName.setEditable(true);
        }

    }

    // 向服务器发送退出消息
    private void sendExitMsg() {
        String msg = "exit@#@#全部@#@#null@#@#" + tfdUserName.getText();
        System.out.println("退出:" + msg);	//向服务器发送消息
        pw.println(msg);
        pw.flush();
    }

/*    private void connecting(String account) {//连接服务器
        try {
            // 先根据用户名防范
            //String userName = tfdUserName.getText();
        	String userName = account;
            if (userName == null || userName.trim().length() == 0) {
                JOptionPane.showMessageDialog(this, "连接服务器失败!\r\n用户名有误，请重新输入！");
                return;
            }

            clientSocket = new Socket(HOST, PORT);//跟服务器握手
            pw = new PrintWriter(clientSocket.getOutputStream(), true);// 加上true自动刷新
            pw.println(userName);// 向服务器报上自己的用户名------>向服务器发送消息
            this.setTitle("用户[ " + userName + " ]上线...");

            new ClientThread().start();// 接受服务器发来的消息---(建立连接之后)一直开着的-------------------------------------
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                Scanner sc = new Scanner(clientSocket.getInputStream());	//获取服务器输入流，读取消息
                while (sc.hasNextLine()) {	//阻塞式方法
                	//获取当前时间
                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
                	Date d = new Date();
                	String time = sdf.format(d);
                	
                    String str = sc.nextLine();
                    String msgs[] = str.split("@#@#");
                    System.out.println(tfdUserName.getText() + ": " + str);
                    if ("msg".equals(msgs[0])) {	//服务器通知
                    	//System.out.println("....ClientThread1...."+msgs[0]);
                        if ("server".equals(msgs[1])) {// 服务器发送的官方消息--有用户上线
                            str = time+ " [ 通知 ]:" + msgs[2] ;	//msgs[2]:用户名
                            //System.out.println("....ClientThread2...."+msgs[0]+":"+msgs[1]+":"+msgs[2]);
                        }else if( "offlineMsg".equals(msgs[1]) ){//发送离线消息
                        	str = time + msgs[2];
                        }else if( "我".equals(msgs[1]) ){
                        	str = time + msgs[1] + msgs[2]+"说: " + msgs[3];		//[谁谁谁对你 ]+ 说 + 内容（个人）
                            System.out.println("....ClientThread22...."+msgs[0]+":"+msgs[1]+":"+msgs[2]);
                        }else {// 服务器转发的聊天消息						//[谁谁谁 ]+ 说 + 内容（全部）
                            str = time+ " [ " + msgs[1] + " ]"+msgs[2]+"说: " + msgs[3];		//[谁谁谁对你 ]+ 说 + 内容（个人）
                            System.out.println("....ClientThread2...."+msgs[0]+":"+msgs[1]+":"+msgs[2]);
                        }													
                        //System.out.println("....ClientThread3...."+str);
                        allMsg.append("\r\n" + str);	//聊天消息框+添加 服务器通知 消息
                    }
                    if ("cmdAdd".equals(msgs[0])) {				//服务器通知--->添加在线列表
                        boolean eq = false;
                        for (int i = 1; i < lm.getSize(); i++) {
                        	int length = lm.getElementAt(i).length();
                        	String userName = lm.getElementAt(i).substring(0, lm.getElementAt(i).indexOf("["));
                        	System.out.println("ClientThread.4......"+length+":"+userName);
                            if ( userName.equals(msgs[2]) ) {	//若相等，则用户当前列表已有用户名
                            	System.out.println("...ClientThread...4...2...............");
                            	if( "在线".equals(msgs[3]) ){
                            		lm.setElementAt(msgs[2]+"[在线]", i);
                                	System.out.println("ClientThread.5..1...."+lm.getElementAt(i));
                                    eq = true;
                            	}else
                            	if( "隐身".equals(msgs[3]) ){
                            		lm.setElementAt(msgs[2]+"[...............离线]", i);
                                	System.out.println("ClientThread.5..2...."+lm.getElementAt(i));
                                    eq = true;
                            	}
                            }
                        }
                        if (!eq) {			//若列表中无当前用户，则添加
                            lm.addElement(msgs[2]);// 用户上线--添加
                        }
                    }
                    if ("cmdRed".equals(msgs[0])) {//用户退出了----->修改状态为离线
                    	for (int i = 1; i < lm.getSize(); i++) {
                        	int length = lm.getElementAt(i).length();
                        	String userName = lm.getElementAt(i).substring(0, lm.getElementAt(i).indexOf("["));
                        	if( userName.equals(msgs[2]) ){
                        		lm.setElementAt(msgs[2]+"[...............离线]", i);
                            	System.out.println("ClientThread.6.离线....."+lm.getElementAt(i));
                        	}else if( (userName.equals(msgs[3])) && ("Offline".equals(msgs[2])) ){	//修改自身状态为-->隐身
                        		lm.setElementAt(msgs[3]+"[隐身]", i);
                        		System.out.println("ClientThread.7.隐身....."+lm.getElementAt(i));
                        	}
                        }
                    }
                    

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        //JFrame.setDefaultLookAndFeelDecorated(true);// 设置装饰
        //new ClientForm("任孟凯",clientSocket,pw);
    }
}