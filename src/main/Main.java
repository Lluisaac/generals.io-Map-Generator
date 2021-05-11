package main;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.KeyListener;
import org.newdawn.slick.MouseListener;
import org.newdawn.slick.SlickException;

import map.Map;
import renderer.Camera;
import renderer.Renderer;

public class Main extends BasicGame
{

	public static final int SCREEN_WIDTH = 1600;
	public static final int SCREEN_HEIGHT = 900;

	private Renderer renderer;

	private Map map;

	public Main() throws SlickException
	{
		super("Generals.io map generator");
		this.renderer = new Renderer();
	}

	@Override
	public void init(GameContainer container) throws SlickException
	{
		this.map = new Map(2, 20, 20, 0.5, 0.5);

		container.getInput().addMouseListener(new MouseListener() {

			@Override
			public void setInput(Input arg0)
			{
			}

			@Override
			public boolean isAcceptingInput()
			{
				return true;
			}

			@Override
			public void inputStarted()
			{
			}

			@Override
			public void inputEnded()
			{
			}

			@Override
			public void mouseWheelMoved(int change)
			{
				if(change < 0)
				{
					Main.this.renderer.getCamera().unzoom();
				}
				else
				{
					Main.this.renderer.getCamera().zoom();
				}
			}

			@Override
			public void mouseReleased(int button, int x, int y)
			{
				if(button == Input.MOUSE_LEFT_BUTTON)
				{
					Main.this.map.reroll();
				}
			}

			@Override
			public void mousePressed(int arg0, int arg1, int arg2)
			{
			}

			@Override
			public void mouseMoved(int arg0, int arg1, int arg2, int arg3)
			{
			}

			@Override
			public void mouseDragged(int arg0, int arg1, int arg2, int arg3)
			{
			}

			@Override
			public void mouseClicked(int arg0, int arg1, int arg2, int arg3)
			{
			}
		});

		container.getInput().addKeyListener(new KeyListener() {

			@Override
			public void setInput(Input arg0)
			{
			}

			@Override
			public boolean isAcceptingInput()
			{
				return true;
			}

			@Override
			public void inputStarted()
			{
			}

			@Override
			public void inputEnded()
			{
			}

			@Override
			public void keyReleased(int key, char c)
			{
				Camera cam = Main.this.renderer.getCamera();

				switch(key)
				{
					case Input.KEY_W:
					case Input.KEY_UP:
						cam.stopMovingUp();
						break;
					case Input.KEY_S:
					case Input.KEY_DOWN:
						cam.stopMovingDown();
						break;
					case Input.KEY_A:
					case Input.KEY_LEFT:
						cam.stopMovingLeft();
						break;
					case Input.KEY_D:
					case Input.KEY_RIGHT:
						cam.stopMovingRight();
						break;
				}
			}

			@Override
			public void keyPressed(int key, char c)
			{
				Camera cam = Main.this.renderer.getCamera();

				switch(key)
				{
					case Input.KEY_W:
					case Input.KEY_UP:
						cam.movingUp();
						break;
					case Input.KEY_S:
					case Input.KEY_DOWN:
						cam.movingDown();
						break;
					case Input.KEY_A:
					case Input.KEY_LEFT:
						cam.movingLeft();
						break;
					case Input.KEY_D:
					case Input.KEY_RIGHT:
						cam.movingRight();
						break;
					case Input.KEY_ESCAPE:
						container.exit();
						break;
				}
			}
		});
	}

	@Override
	public void render(GameContainer container, Graphics g) throws SlickException
	{
		this.renderer.render(g, this.map);
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException
	{
		this.renderer.getCamera().update(delta);
	}

	public static void main(String[] args) throws SlickException
	{
		new AppGameContainer(new Main(), 1920, 1080, false).start();
	}
}