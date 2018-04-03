# Instant-messaging-system
Instant messaging system  即时通讯系统（类似qq聊天系统）

一、系统功能说明

      1.1系统简介
           该系统名为《即时通讯系统》，别名为wechat，是基于Java网络编程TCP协议的一个及时通讯桌面程序。
           系统分为两部分，服务端和客户端，在服务端主要功能有控制服务的开关，在线人数的显示，在后台的功
           能上，服务端为客户端提供信息转发和在线、离线判断的功能，当发送方的接收方为离线时，服务端会将
           信息存储在数据库中，在接收方下次登录的时候，会发出离线消息。客户端的主要功能是与某个人进行实
           时通讯聊天，当接收方不在线时，可发送离线消息，同时客户端还具有群发的功能和设置在线或隐身的状态。
      1.2系统主要功能
          《即时通讯系统》，本系统由2部分组成：
          即时通讯终端，管理后台 ，主要功能如下：
          1.2.1服务端功能
              1.查看在线人数列表
              2.控制服务的开关
              3.客户端消息的转发
          1.2.2客户端功能
              1.终端登录需要后台验证，并能够设置在线或隐身状态 
              2.当通讯双方均在线时，双方可互发消息 
              3.当对方不在线时，可发离线消息
      1.3系统开发环境
          本即时通讯系统采用了Eclipse开发平台，jdk采用1.8版本，数据库采用Mysql数据库以及Mysql Navicat的
          可视化的数据库管理工具，Tomcat7.0 版本和window8.1操作系统。
          
二、系统设计
      
      2.1系统总体设计
        2.1.1功能结构图
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/FunctionalStructure.png)
        
        2.1.2服务端设计
          在服务器端 用一个HashMap< userName,socket> 维护所有用户相关的信息，从而能够保证和所有的用户进行通讯。
          服务器向客户端发的消息格式设计： 
          命令关键字@#发送方@#消息内容 
          登录： 
            1) msg @#server @# 用户[userName]登录了 (给客户端显示用的) 
            2) cmdAdd@#server @# userName (给客户端维护在线用户列表用的) 
          退出： 
            1) msg @#server @# 用户[userName]退出了 (给客户端显示用的) 
            2) cmdRed@#server @# userName (给客户端维护在线用户列表用的)
          发送: 
            msg @#消息发送者( msgs[3] ) @# 消息内容 (msgs[2])
        2.1.3客户端设计
          客户端向服务器发的消息格式设计： 
          命令关键字@#接收方@#消息内容@#发送方 
          1)连接：userName —-握手的线程serverSocket专门接收该消息，其它的由服务器新开的与客户进行通讯的socket来接收 
          2)退出：exit@#全部@#null@#userName 
          3)发送: on @# JList.getSelectedValue() @# tfdMsg.getText() @# tfdUserName.getText()

      2.2网络通信协议
        该系统为了保证消息的可靠性，全部采用TCP协议，用户登录时通过TCP协议与服务端建立连接，
        服务端根据客户端建立的连接向客户端发送系统消息或者是转发发送方的消息。
        

      2.3数据库设计
        系统包括服务端后台管理和客户端用户两部分。
          服务端后台管理可以管理服务器的连接，监听所有的用户状态，控制服务器的连接。
          客户端用户可以给列表中的其他用户发送消息，包括离线消息和在线消息；还可以设置ip地址和端口号，以及查看帮助的版本信息。
        系统数据库是利用Mysql数据库的navicat软件创建的，
        本系统用到的数据库表主要有：用户表（user）、离线消息表（message），具体如下：
        (1)	用户表：用于存储用户的账号信息。如表3.1所示
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/User.png)
        
        (2)	离线消息表：用于存储发送的离线信息。如表3.2所示
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/OfflineMessage.png)
   
      2.4数据存储
        该系统使用MySQL作为数据存储的数据库，主要有两张表，
        一张用户表用于存储用户信息数据，当用户新注册一个账号时，用户数据表会增加一条记录；
        另一张表是离线消息表，用来存储用户所发送的离线消息。两张表的字段如下：

        2.4.1用户表
          初始的数据如下：
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserData.png)
   
          -- 向用户表中插入数据 --
          insert into user(account,pwd,state) values('王嫣兰','1','离线');
          insert into user(account,pwd,state) values('赵雯楚','1','离线');
          insert into user(account,pwd,state) values('李嘉丽','1','离线');
          insert into user(account,pwd,state) values('廖美欢','1','离线');
          insert into user(account,pwd,state) values('卢超素','1','离线');
          insert into user(account,pwd,state) values('张琦','1','离线');
          insert into user(account,pwd,state) values('何昶源','1','离线');
          insert into user(account,pwd,state) values('王启宁','1','离线');
          insert into user(account,pwd,state) values('任孟凯','1','离线');
          insert into user(account,pwd,state) values('孙明峰','1','离线');
        2.4.2离线消息表
          数据如下：（初始时无数据）
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/OfflineMessageData.png)
   
      2.5 关键类和包结构设计
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/Package.png)
   
          messageDao：
            用于离线消息表的数据库访问对象，包含了对message对象的各种数据库操作
          userDao：
            用于用户表的数据库访问对象，包含了对user对象的各种数据库操作
          JDBCutils：
            封装了数据库连接、关闭、修改和查询等操作的集合
          message：
            离线消息表对应的实体类
          user：
            用户表对应的实体类
          login：
            登录界面与业务逻辑处理类
          register：
            注册界面与业务逻辑处理类
          ClientForm:
            客户端平台与业务逻辑处理类
          ServerForm：
            服务端平台与业务逻辑处理类

      2.6 界面设计
         2.6.1登录界面
            说明：输入账号密码，点击“登录”按钮，进入用户页面；点击“注册”按钮进入，注册界面。
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/LoginInterface.png)  
            
         2.6.2注册界面
            说明：输入账号密码，点击“注册”按钮，若账号密码无误，提示注册成功；否则，注册失败。点击“返回”按钮，返回到登录界面。
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/RegisterInterface.png)             
            
         2.6.3用户界面
            说明：输入正确的账号密码，进入到用户界面，界面右边可以查看好友状态，界面上边可以修改自己的状态，
                  选中右边的好友就可以在界面下边给他发送消息。
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserInterface.png)   
        
         2.6.4 用户互发消息功能
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserMutualMessage1.png)
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserMutualMessage2.png)
         
         2.6.5 用户登陆显示离线消息(如无不显示)
            如下图所示用户“任孟凯”先给用户“何昶源”发送了离线消息，登陆用户“何昶源”查看是否有离线消息。
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserLoginDisplayOfflineMessage1.png)
   
            如下图所示，用户“何昶源”已收到离线消息。
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserLoginDisplayOfflineMessage2.png) 

         2.6.6 用户设置“隐身”状态
            如下图所示，用户在任孟凯设置为“隐身”状态
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserSetStealthState.png)
   
         2.6.7 用户群发消息
            如下图所示，用户“任孟凯”给所有在线用户发送了消息。
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserGroupMessages1.png)

            如下图，两个在线用户“孙明峰”和“张琦”都收到了”任孟凯”发送的消息。
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/UserGroupMessages2.png)     

         2.6.8后台界面
            说明：后台服务器管理界面，左边页面记录用户状态，右边显示在线的用户列表。
            左上角控制菜单栏，有开启服务器按钮，只有开启之后，用户才能连接上服务器。
   ![image](https://github.com/TouchDreamRen/Instant-messaging-system/raw/master/screenshots/ServerInterface.png)   
   
            

