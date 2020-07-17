package main;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class LoginFrame extends JFrame implements MouseListener {
	public static final int WIDTH = 400; // 窗口的宽
	public static final int HEIGHT = 654; // 窗口的高
	
	JLabel userLabel;// 用户姓名
	JLabel pwdLabel;// 用户密码
	JTextField userText;// 用户名文本输入框
	JPasswordField pwdText;// 密码输入框
	JLabel loginLabel, signinLabel;// 登录取消按钮
	JLabel surprise1;//彩蛋1
	JLabel surprise2;//彩蛋2
	JLabel titlelabel;//"飞机大战"
	String username;// 文本输入框内容
	String userpwd;// 密码框内容
	
	public LoginFrame(){
		userLabel = new JLabel("用户名");
		userLabel.setBounds(80, 250, 120, 30);
		this.add(userLabel);

		pwdLabel = new JLabel("密   码");
		pwdLabel.setBounds(80, 300, 120, 30);
		this.add(pwdLabel);
		
		// 添加用户名输入框
		userText = new JTextField(10);
		userText.setBounds(140, 250, 160, 25);
		// 获取鼠标焦点
		userText.setFocusable(true);
		this.add(userText);

		// 添加密码输入框
		pwdText = new JPasswordField();
		pwdText.setBounds(140, 300, 160, 25);
		pwdText.setFocusable(true);
		this.add(pwdText);

		titlelabel = new JLabel(new ImageIcon("E:\\桌面\\飞机大战\\飞机大战3.0\\src\\main\\start1.png"));
		titlelabel.setBounds(40, 80, 330, 101);
		titlelabel.setEnabled(false);
		titlelabel.setEnabled(true);
		this.add(titlelabel);
		
		loginLabel = new JLabel(new ImageIcon("E:\\桌面\\飞机大战\\飞机大战3.0\\src\\main\\login.png"));
		loginLabel.setBounds(70, 375, 130, 35);
		loginLabel.setEnabled(false);
		loginLabel.setEnabled(true);
		//监听
		loginLabel.addMouseListener(this);
		this.add(loginLabel);
		
		signinLabel = new JLabel(new ImageIcon("E:\\桌面\\飞机大战\\飞机大战3.0\\src\\main\\signin.png"));
		signinLabel.setBounds(175, 375, 130, 35);
		signinLabel.setEnabled(false);
		signinLabel.setEnabled(true);
		//监听
		signinLabel.addMouseListener(this);
		this.add(signinLabel);
				
		surprise1 = new JLabel(new ImageIcon("E:\\桌面\\飞机大战\\飞机大战3.0\\src\\main\\airplane.png"));
		surprise1.setBounds(30, 170, 49, 36);
		surprise1.setEnabled(false);
		surprise1.setEnabled(true);
		//监听
		surprise1.addMouseListener(this);
		this.add(surprise1);
		
		surprise2 = new JLabel(new ImageIcon("E:\\桌面\\飞机大战\\飞机大战3.0\\src\\main\\bee.png"));
		surprise2.setBounds(280, 480, 58, 88);
		surprise2.setEnabled(false);
		surprise2.setEnabled(true);
		//监听
		surprise2.addMouseListener(this);
		this.add(surprise2);
				
		//窗口设置
		BackImage back = new BackImage();
		back.setBounds(0, 0, WIDTH, HEIGHT);
		this.add(back);
		//不设置的话，排版会乱
		this.setTitle("飞机大战");
		this.setLayout(null);
		this.setSize(WIDTH, HEIGHT);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setIconImage(new ImageIcon("E:\\桌面\\飞机大战\\飞机大战3.0\\src\\main\\hero0.png").getImage());
		this.setVisible(true);
				
	}
	
	public static void main(String[] args) {
		LoginFrame frame = new LoginFrame();
	}
	
	String[] id = new String[999]; 
	String[] pwd = new String[999];
	int i = 0;
	
	public boolean find(String uname,String upwd){
		for(int i = 0;i < id.length;i++)
			if(id[i].equals(uname) && pwd[i].equals(upwd))
				return true;
		return false;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(arg0.getSource() == loginLabel){ // 登录
			username = userText.getText();
			char[] chPWD = pwdText.getPassword();
			userpwd = new String(chPWD);

			if (username.length() == 0) {
				JOptionPane.showMessageDialog(null, "用户名和密码不能为空");
			} 
			if (find(username,userpwd)) {
				 // 创建生成主界面
				dispose();// 关闭当前界面
				JFrame frame = new JFrame("飞机大战  developed by 这咋能运行啊"); // 窗口对象
				//	ShootGame game = new ShootGame(Integer.parseInt(score)); // 面板
				ShootGame game = null;
				try {
					game = new ShootGame(getHighestScore(), username);
					System.out.print(username);
				} catch (ClassNotFoundException | SQLException e) {
					e.printStackTrace();
				}
				frame.add(game); // 将面板添加到窗口中
			    	Image image;
					image = new ImageIcon("E:\\桌面\\飞机大战\\飞机大战3.0\\src\\main\\icon.png").getImage();
					frame.setIconImage(image);

					frame.setSize(WIDTH, HEIGHT); // 设置窗口的宽和高
					frame.setAlwaysOnTop(true); // 设置一直在最上面
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置默认关闭的操作：窗口关闭时退出程序
					frame.setLocationRelativeTo(null); // 设置窗口初始位置（居中)
					frame.setVisible(true); // 设置窗体可见

				game.action();
			} else {
				JOptionPane.showMessageDialog(null, "用户名或密码错误");
			}
		}
		
		if(arg0.getSource() == signinLabel){ // 注册
			username = userText.getText();
			char[] chPWD = pwdText.getPassword();
			userpwd = new String(chPWD);
			
			if (username.length() == 0 || userpwd.length() == 0) {
				JOptionPane.showMessageDialog(null, "用户名和密码不能为空");
			}else{
				id[i] = username;
				pwd[i++] = userpwd;
//				System.out.println(id[i] + pwd[i++]);
				JOptionPane.showMessageDialog(null, "注册成功");
			}
		}
		
		if(arg0.getSource() == surprise1){ // 彩蛋
			JOptionPane.showMessageDialog(null, "小提醒：我的速度会越来越快哦！","你居然会点我！", JOptionPane.INFORMATION_MESSAGE);
		}
		if(arg0.getSource() == surprise2){ // 彩蛋
			JOptionPane.showMessageDialog(null, "小提醒：我是个奖励哦！","你居然会点我！", JOptionPane.INFORMATION_MESSAGE);
		}
		
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public int getHighestScore() throws ClassNotFoundException, SQLException {
		String user = "root";
		String password = "zyh20000205";
		Connection con;
		Statement stat;
		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://127.0.0.1:3306/planewar?" +
				"useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8";
		con = DriverManager.getConnection(url, user, password);
		con = DriverManager.getConnection(url, user, password);
		stat = con.createStatement();//找到借口
		String sql = "select max(score) score from score;";//查询语句
		ResultSet rs=stat.executeQuery(sql);//查询
//将查询出的结果输出
		String score = "0";
		while (rs.next()) {
			score=rs.getString("score");
		}
		rs.close();
		stat.close();
		con.close();
		return Integer.parseInt(score);
	}

}

