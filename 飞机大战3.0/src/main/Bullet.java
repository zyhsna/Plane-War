package main;
//子弹--飞行物
class Bullet extends FlyingObject{
	private int speed = 6;  //子弹走步步数，只有y坐标在变
	private int speed2 = 10;
	public Bullet(int x,int y){//子弹的坐标随着英雄机的变化而变化		
		image = ShootGame.bullet;
		width = image.getWidth();
		height = image.getHeight();
		this.x = x;
		this.y = y;
	}
    public void step(){
		y -= speed;
	}
    public boolean outOfBounds(){
		return this.y < -this.height;
	}
}

class Bullet2 extends FlyingObject{
	private int speed = 10;  //子弹走步步数，只有y坐标在变
	public Bullet2(int x,int y){//子弹的坐标随着英雄机的变化而变化
		image = ShootGame.bullet2;
		width = image.getWidth();
		height = image.getHeight();
		this.x = x;
		this.y = y;
	}
	@Override
	public void step() {
		y-=speed;
	}

	@Override
	public boolean outOfBounds() {
		{
			return this.y < -this.height;
	}
}
}