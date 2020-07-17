package main;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

//主程序测试类
public class ShootGame extends JPanel {
	public ShootGame(int highestScore, String name){
		setHighestScore(highestScore);
		this.name = name;
	}

	public void setHighestScore(int highestScore) {
		this.highestScore = highestScore;
	}

	public static final int WIDTH = 400; // 窗口的宽
	public static final int HEIGHT = 654; // 窗口的高
	// 静态资源
	public static BufferedImage background; // 背景图
	public static BufferedImage start; // 开始图
	public static BufferedImage pause; // 暂停图
	public static BufferedImage gameover; // 游戏结束图
	public static BufferedImage airplane; // 小敌机图
	public static BufferedImage airplane2; // 中敌机图
	public static BufferedImage airplane3; // 大敌机图
	public static BufferedImage hit1_1;
	public static BufferedImage hit1_2;
	public static BufferedImage hit1_3;
	public static BufferedImage hit1_4;
	public static BufferedImage hit2_1;
	public static BufferedImage hit2_2;
	public static BufferedImage hit2_3;
	public static BufferedImage hit2_4;
	public static BufferedImage hit3_1;
	public static BufferedImage hit3_2;
	public static BufferedImage hit3_3;
	public static BufferedImage hit3_4;
	public static BufferedImage hit3_5;
	private static int count = 0;
	public static BufferedImage bee; // 补给图
	public static BufferedImage bee2;
	public static BufferedImage bullet; // 子弹图
	public static BufferedImage bullet2; // 子弹图
	public static BufferedImage hero0; // 英雄机0图
	public static BufferedImage hero1; // 英雄机1图
	public static String url = "jdbc:mysql://127.0.0.1:3306/planewar?" +
			"useUnicode=true&characterEncoding=UTF-8&userSSL=false&serverTimezone=GMT%2B8";
	public static String user = "root";
	public static String password = "zyh20000205";
	public static Connection con;
	public static AudioClip music;
	public static String select_sql = "select max(score) from score;";
	public  int highestScore ;
	public String name;
	//游戏状态
	public static final int START = 0;
	public static final int RUNNING = 1;
	public static final int PAUSE = 2;
	public static final int GAME_OVER = 3;
	private int state = 0; // 当前状态

	private Hero hero = new Hero(); // 英雄机
	private Bullet[] bullets = {}; // 子弹数组
	private FlyingObject[] flyings = {}; // 敌人数组

	private Timer timer;
	private int interval = 10; // 间隔时间：单位--毫秒

	// 静态块
	static {
		try {
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			airplane2 = ImageIO.read(ShootGame.class.getResource("airplane2.png"));
			airplane3 = ImageIO.read(ShootGame.class.getResource("airplane3.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bee2 = ImageIO.read(ShootGame.class.getResource("bee2.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			bullet2 = ImageIO.read(ShootGame.class.getResource("bullet2.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
			URL musicPath = ShootGame.class.getResource("game_music.wav");
			hit1_1 = ImageIO.read(ShootGame.class.getResource("enemy1_blowup_1.png"));
			hit1_2 = ImageIO.read(ShootGame.class.getResource("enemy1_blowup_2.png"));
			hit1_3 = ImageIO.read(ShootGame.class.getResource("enemy1_blowup_3.png"));
			hit1_4 = ImageIO.read(ShootGame.class.getResource("enemy1_blowup_4.png"));
			hit2_1 = ImageIO.read(ShootGame.class.getResource("enemy2_blowup_1.png"));
			hit2_2 = ImageIO.read(ShootGame.class.getResource("enemy2_blowup_2.png"));
			hit2_3 = ImageIO.read(ShootGame.class.getResource("enemy2_blowup_3.png"));
			hit2_4 = ImageIO.read(ShootGame.class.getResource("enemy2_blowup_4.png"));
			hit3_1 = ImageIO.read(ShootGame.class.getResource("enemy3_blowup_1.png"));
			hit3_2 = ImageIO.read(ShootGame.class.getResource("enemy3_blowup_2.png"));
			hit3_3 = ImageIO.read(ShootGame.class.getResource("enemy3_blowup_3.png"));
			hit3_4 = ImageIO.read(ShootGame.class.getResource("enemy3_blowup_4.png"));
			hit3_5 = ImageIO.read(ShootGame.class.getResource("enemy3_blowup_4.png"));
			con = DriverManager.getConnection(url, user, password);
			music = Applet.newAudioClip(musicPath);
			} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static FlyingObject nextOne() {
		Random rand = new Random();
		int type = rand.nextInt(20); // 生成0到19的随机数
		if (type == 0) { // 随机数为0，返回补给;否则返回敌机
			return new Bee();
		} else {
			return new Airplane();
		}
	}

	int flyEnteredIndex = 0;

	// 敌人登场
	public void enterAction() {// 10毫秒走一次
		flyEnteredIndex++; // 每10毫秒增1
		if (flyEnteredIndex % 40 == 0) {
			FlyingObject obj = nextOne();
			flyings = Arrays.copyOf(flyings, flyings.length + 1);
			flyings[flyings.length - 1] = obj;// 将敌人赋值给flyings数组的最后一个元素
		}
	}

	public void stepAction() { // 增加难度选项
		hero.step(); // 英雄机走一步
		int num = time1 / 15;  //控制难度等级
		if(num>=3)
			num = 3;
		for (FlyingObject flying : flyings) {
			for (int j = 0; j <= num; j++) {
				flying.step(); // 敌人走一步
			}
		}
		for (Bullet value : bullets) {
			for (int j = 0; j <= num / 2; j++) {
				value.step(); // 子弹走一步
			}
		}
	}

	int shootIndex = 0;

	public void shootAction() { // 10毫秒走一次
		shootIndex++; // 每10毫秒增1
		if (shootIndex % 20 == 0) { // 20毫秒发射一次子弹
			Bullet[] bs = hero.shoot();// 获取子弹对象
			bullets = Arrays.copyOf(bullets, bullets.length + bs.length);
			System.arraycopy(bs, 0, bullets, bullets.length - bs.length, bs.length);
		}
	}

	// 删除越界飞行物
	public void outOfBoundsAction() {
		int index = 0;
		FlyingObject[] flyingLives = new FlyingObject[flyings.length];
		for (FlyingObject f : flyings) {
			if (!f.outOfBounds()) {
				flyingLives[index] = f;// 不越界，将其装入flyingLives[]数组中
				index++;
			}
		}
		flyings = Arrays.copyOf(flyingLives, index);

		index = 0;
		Bullet[] bulletsLives = new Bullet[bullets.length];
		for (Bullet bs : bullets) {
			if (!bs.outOfBounds()) {
				bulletsLives[index] = bs;// 不越界，将其装入flyingLives[]数组中
				index++;
			}
		}
		bullets = Arrays.copyOf(bulletsLives, index);
	}

	int score = 0; // 得分
	int level=1;//等级
	static double time = 0.00;//时间
	static int time1 = 0;
	// 所有子弹与所有敌人撞

	public void bangAction() {
		for (Bullet value : bullets) {
			bang(value);
		}
	}

	// 一个子弹与所有敌人撞
	public void bang(Bullet b) {
		int index = -1;// 被撞敌人的索引
		for (int i = 0; i < flyings.length; i++) {// 遍历所有的敌人
			if (flyings[i].shootBy(b)) {// 判断是否撞上
				index = i; // 记录被撞敌人的索引
				break;
			}
		}
		if (index != -1) {// 撞上了
			FlyingObject one = flyings[index];
			if (one instanceof Enemy) {
				Airplane airplane = (Airplane) one;
				// Enemy e = (Enemy) one;
				if(!airplane.ishit)
					score += airplane.getScore();
			}
			if (one instanceof Award) {
				Award a = (Award) one;
				int type = a.getType();
				switch (type) {
				case Award.DOUBLE_FIRE: // 奖励活力值
					hero.addDoubleFire(); // 英雄机增加火力
					break;
				case Award.LIFE: // 奖励命
					hero.addLife(); // 英雄机增命
					break;
				case Award.addscore://奖励分值
					score+=20;
					break;
				}
			}
			// 被撞敌人与flyings数组中的最后一个元素交换
			FlyingObject t = flyings[index];
			flyings[index].ishit=true;
			flyings[index] = flyings[flyings.length - 1];
			flyings[flyings.length - 1] = t;
		}
	}

	public void checkGameOverAction() throws SQLException, ClassNotFoundException {
		if (isGameOver()) { // 结束游戏
			state = GAME_OVER;
			time1 = 0;
			time = 0;
			Date date = new Date();
			SimpleDateFormat simpleDateFormat =  new SimpleDateFormat("yyyy-MM-dd");
			System.out.print(simpleDateFormat.format(date));
			String sql = "insert into score(score, time, user) values (?, ?, ?);";
			//创建一个preparedstatment对象
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setObject(1,score);
			ps.setObject(2,simpleDateFormat.format(date));
			ps.setObject(3,name);
			//执行插入操作
			int rows = ps.executeUpdate();
			System.out.println(rows);
			//关闭
			ps.close();
			highestScore=getHighestScore();
		}
	}

	public boolean isGameOver() {
		for (int i = 0; i < flyings.length; i++) { // 撞上了，
			if (hero.hit(flyings[i])) {
				hero.subtractLife(); // 生命减1
				hero.setDoubleFire(0); // 火力值清零

				// 相撞之后，交换缩容
				FlyingObject t = flyings[i];
				flyings[i] = flyings[flyings.length - 1];
				flyings[flyings.length - 1] = t;
				flyings = Arrays.copyOf(flyings, flyings.length - 1);
			}
		}
		return hero.getLife() <= 0; // 英雄机的命<=0,游戏结束
	}

	// 启动执行代码
	public void action() {

		MouseAdapter l = new MouseAdapter() {
			public void mouseMoved(MouseEvent e) {
				if (state == RUNNING) { // 运行状态下执行
					int x = e.getX(); // 鼠标Y坐标
					int y = e.getY(); // 鼠标X坐标
					hero.moveTo(x, y); // 英雄机随着鼠标移动而移动
				}
			}


			// 鼠标的点击事件
			public void mouseClicked(MouseEvent e) {
				switch (state) {
					case PAUSE:
						state = RUNNING;
						System.out.print("1");
						music.stop();
						music.loop();
						break;
					case RUNNING:
						System.out.print("  2");
						state = PAUSE;
						music.stop();
						break;
					case START:
						state = RUNNING;
						System.out.print("   3");
						music.stop();
						music.loop();
						System.out.print("   4");
						break;
					case GAME_OVER:
						System.out.print("   5");
						System.out.println(score);
						hero = new Hero();// 清理现场
						flyings = new FlyingObject[0];
						bullets = new Bullet[0];
						score = 0;
						state = START;
						music.stop();
						System.out.print("  6");
						break;
				}
			}

			public void mouseEntered(MouseEvent e) {
				if (state == PAUSE) {
					state = RUNNING;
					music.loop();
				}
			}

			public void mouseExited(MouseEvent e) {
				if (state == RUNNING) {
					state = PAUSE;
				}
			}
		};
		this.addMouseListener(l); // 处理鼠标操作事件
		this.addMouseMotionListener(l);// 处理鼠标移动事件

		timer = new Timer(); // 创建定时器对象
		timer.schedule(new TimerTask() {
			public void run() { // 定时--10毫秒走一次
				if (state == RUNNING) { // 运行状态下执行
					enterAction();
					stepAction();// 飞行物走一步
					shootAction();// 子弹入场
					outOfBoundsAction();// 删除越界飞行物
					bangAction(); // 子弹与敌人撞
					time = time + 0.01;
					time1 = (int) time;

					try {
						checkGameOverAction();  //SQL连接数据库获取最高分数
					} catch (SQLException | ClassNotFoundException throwables) {
						throwables.printStackTrace();
					}
				}
				count++;
				repaint(); // 重画，调用paint()
			}
		}, interval, interval);
			// 定时显示难度
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (level <= 2)
							level++;
					}
				}, 15000, 15000);

	}

	// 重写paint()方法 g：表示画笔
	public void paint(Graphics g) {
		g.drawImage(background, 0, 0, null); // 画背景图
		paintHero(g);
		paintFlyingObjects(g);
		paintBullets(g);
		try {
			paintScore(g); // 画分，画名
		} catch (SQLException | ClassNotFoundException throwables) {
			throwables.printStackTrace();
		}
		paintState(g);
	}

	// 画状态
	public void paintState(Graphics g) {
		switch (state) {
		case START: // 启动状态画启动图
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE: // 暂停状态画暂停图
			g.drawImage(pause, 0, 0, null);
			break;
		case GAME_OVER: // 结束状态画结束图
			g.drawImage(gameover, 0, 0, null);
			break;

		}
	}

	public void paintHero(Graphics g) {
		g.drawImage(hero.image, hero.x, hero.y, null); // 画英雄机对象
	}

	public void paintFlyingObjects(Graphics g) {
		for (int i = 0; i < flyings.length; i++) {
			FlyingObject f = flyings[i];
			g.drawImage(f.image, f.x, f.y, null);

			if (flyings[i].ishit) {

				switch (f.airplane_type) {
					case 1: {
						int boomPictureChoose = count % 20;
						if (boomPictureChoose < 5)
							g.drawImage(hit1_1, f.x, f.y, null);
						else if (boomPictureChoose < 10)
							g.drawImage(hit1_2, f.x, f.y, null);
						else if (boomPictureChoose < 15)
							g.drawImage(hit1_3, f.x, f.y, null);
						else{
							g.drawImage(hit1_4, f.x, f.y, null);
							flyings = Arrays.copyOf(flyings, flyings.length - 1);
						}
						break;
					}
					case 2: {
						int boomPictureChoose = count % 20;
						if (boomPictureChoose < 5)
							g.drawImage(hit2_1, f.x, f.y, null);
						else if (boomPictureChoose < 10)
							g.drawImage(hit2_2, f.x, f.y, null);
						else if (boomPictureChoose < 15)
							g.drawImage(hit2_3, f.x, f.y, null);
						else{
							g.drawImage(hit2_4, f.x, f.y, null);
							flyings = Arrays.copyOf(flyings, flyings.length - 1);
						}
						break;
					}
					case 3: {
						int boomPictureChoose = count % 25;
						if (boomPictureChoose < 5)
							g.drawImage(hit3_1, f.x, f.y, null);
						else if (boomPictureChoose < 10)
							g.drawImage(hit3_2, f.x, f.y, null);
						else if (boomPictureChoose < 15)
							g.drawImage(hit3_3, f.x, f.y, null);
						else if(boomPictureChoose<20)
							g.drawImage(hit3_4, f.x, f.y, null);
						else{
							g.drawImage(hit3_5, f.x, f.y, null);
							flyings = Arrays.copyOf(flyings, flyings.length - 1);
						}
						break;
					}
					default: {
						g.drawImage(hit2_4, f.x, f.y, null);
						flyings = Arrays.copyOf(flyings, flyings.length - 1);
						break;
					}
				}
			}
		}
	}


	public void paintBullets(Graphics g) {
		for (Bullet b : bullets) {
			g.drawImage(b.image, b.x, b.y, null);
		}

	}

	public int getHighestScore() throws ClassNotFoundException, SQLException {
		Statement stat;
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, user, password);
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

	public void paintScore(Graphics g) throws SQLException, ClassNotFoundException { // 画分，画命
		//highestScore = getHighestScore();
		g.setColor(new Color(0xFF0000));
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		g.drawString("SCORE: " + score, 20, 25);
		g.drawString("LIFE: " + hero.getLife(), 20, 45);
		g.drawString("TIME:" +time1,20,65);
		g.drawString("HIGHEST: " + highestScore, 20, 85);
		g.drawString("LEVEL:"+level,280,25);
	}


	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		Statement stat;
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection con = DriverManager.getConnection(url, user, password);
		stat = con.createStatement();//找到借口
		String sql = "select max(score) score from score;";//查询语句
		ResultSet rs=stat.executeQuery(sql);//查询
//将查询出的结果输出
		String score = "0";
		while (rs.next()) {
			score=rs.getString("score");
		}
		System.out.print(score);
		rs.close();
		stat.close();
		con.close();
		JFrame frame = new JFrame("飞机大战  developed by 这咋能运行啊"); // 窗口对象
		ShootGame game = new ShootGame(Integer.parseInt(score),"zyh"); // 面板
		//ShootGame game = new ShootGame();
		frame.add(game); // 将面板添加到窗口中
    	BufferedImage image;
		try {
			image = ImageIO.read(ShootGame.class.getResource("icon.png"));
			frame.setIconImage(image);
		} catch (IOException e) {
			System.out.print(Arrays.toString(e.getStackTrace()));
		}

		frame.setSize(WIDTH, HEIGHT); // 设置窗口的宽和高
		frame.setAlwaysOnTop(true); // 设置一直在最上面
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置默认关闭的操作：窗口关闭时退出程序
		frame.setLocationRelativeTo(null); // 设置窗口初始位置（居中)
		frame.setVisible(true); // 设置窗体可见

		game.action(); // 启动执行
	}
}
