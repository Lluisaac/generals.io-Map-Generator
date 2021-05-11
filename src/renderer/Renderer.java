package renderer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import map.Map;
import map.cases.Case;

public class Renderer
{

	public static final int GAME_HEIGHT = 900;
	public static final int GAME_WIDTH = 1600;

	public static final int SEPARATION = 5;
	public static final int SQUARE_SIZE = 120;
	public static final int IMAGE_SIZE = 100;

	private Camera cam;

	public Renderer() throws SlickException
	{
		this.cam = new Camera();
	}

	public void render(Graphics g, Map map) throws SlickException
	{
		this.cam.render(g);
		this.renderMap(g, map.getMap());
	}

	private void renderMap(Graphics g, Case[][] map) throws SlickException
	{
		for(int i = 0; i < map.length; i++)
		{
			for(int j = 0; j < map[i].length; j++)
			{
				Case maCase = map[i][j];
				
				g.setColor(maCase.getColor());

				int squarePositionX = (i * Renderer.SQUARE_SIZE) + (Renderer.SEPARATION * i);
				int squarePositionY = (j * Renderer.SQUARE_SIZE) + (Renderer.SEPARATION * j);

				g.fillRect(squarePositionX, squarePositionY, Renderer.SQUARE_SIZE, Renderer.SQUARE_SIZE);

				int imageOffset = (Renderer.SQUARE_SIZE - Renderer.IMAGE_SIZE) / 2;

				g.drawImage(maCase.getImage(), squarePositionX + imageOffset, squarePositionY + imageOffset);
			}
		}
	}

	public Camera getCamera()
	{
		return this.cam;
	}
}
