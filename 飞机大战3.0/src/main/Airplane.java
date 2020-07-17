package main;

import java.util.Random;

//Airplane----敌机既是飞行物，
public class Airplane extends FlyingObject implements Enemy {
	private int speed = 2;// 敌机走步的步数
	private int type;
	public Airplane() {
		Random rand = new Random();// 随机生成大中小型敌人
		int varity = rand.nextInt(20);
		if (varity <13){
			image = ShootGame.airplane;
			type = 1;
			this.airplane_type=1;
		}
		else if (varity < 17){
			image = ShootGame.airplane2;
			type = 2;
			this.airplane_type=2;
		}
		else {
			image = ShootGame.airplane3;
			type = 3;
			this.airplane_type=3;
		}
		width = image.getWidth();
		height = image.getHeight();
		x = rand.nextInt(ShootGame.WIDTH - this.width);
		y = -this.height; // y:负的敌机的高
	}

	// 重写 getScore();
	@Override
	public int getScore() {
		if (type==1)
			return 5;  //小飞机
		else if (type ==2)
			return 10;	//中飞机
		else
			return 15; //大飞机
	}
	public int gettype() {
		return type;
	}
	public void step() {
		y += speed;
	}

	public boolean outOfBounds() {
		return this.y > ShootGame.HEIGHT; // 敌机的y坐标大于窗口的高
	}
}
