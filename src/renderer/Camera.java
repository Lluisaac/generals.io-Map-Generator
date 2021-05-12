package renderer;

import org.newdawn.slick.Graphics;

import main.Main;

public class Camera
{
	private static final float ZOOM_SPEED = 0.1f;

	private float offsetX;
	private float offsetY;

	private float zoomMultiplicator;

	private boolean moving;

	public Camera()
	{
		this.center();

		this.zoomMultiplicator = 1;
	}

	public void update(int delta)
	{
		
	}

	public void render(Graphics g)
	{
		g.scale(this.zoomMultiplicator, this.zoomMultiplicator);
		g.translate(this.offsetX / this.zoomMultiplicator, this.offsetY / this.zoomMultiplicator);
		g.flush();
	}

	private void offsetXBy(float i)
	{
		this.offsetX += i;
	}

	private void offsetYBy(float i)
	{
		this.offsetY += i;
	}

	public float getOffsetX()
	{
		return this.offsetX;
	}

	public float getOffsetY()
	{
		return this.offsetY;
	}

	public float getZoomMultiplicator()
	{
		return this.zoomMultiplicator;
	}

	public void zoom()
	{
		this.zoomMultiplicator += Camera.ZOOM_SPEED;

		if(this.zoomMultiplicator > 8)
		{
			this.zoomMultiplicator = 8;
		}
	}

	public void unzoom()
	{
		this.zoomMultiplicator -= Camera.ZOOM_SPEED;

		if(this.zoomMultiplicator < 0.2)
		{
			this.zoomMultiplicator = 0.2f;
		}
	}

	public void center()
	{
		this.offsetX = Main.SCREEN_WIDTH / 2;
		this.offsetY = Main.SCREEN_HEIGHT / 2;
	}

	public void startMoving()
	{
		this.moving = true;
	}

	public void stopMoving()
	{
		this.moving = false;
	}

	public void mouseMoved(int oldX, int oldY, int newX, int newY)
	{
		if (this.moving)
		{
			this.offsetXBy(newX - oldX);
			this.offsetYBy(newY - oldY);
		}
	}
}
