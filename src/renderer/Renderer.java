package renderer;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

import map.Map;
import map.tiles.Tile;

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
		this.renderMap(g, map);
	}

	private void renderMap(Graphics g, Map map) throws SlickException
	{
		float centerOffsetX = (Renderer.SQUARE_SIZE + Renderer.SEPARATION) * (map.getWidth() / 2);
		float centerOffsetY = (Renderer.SQUARE_SIZE + Renderer.SEPARATION) * (map.getHeight() / 2);
		
		for(int i = 0; i < map.getWidth(); i++)
		{
			for(int j = 0; j < map.getHeight(); j++)
			{
				Tile maCase = map.getMap()[i][j];
				
				g.setColor(maCase.getColor());

				float squarePositionX = (i * Renderer.SQUARE_SIZE) + (Renderer.SEPARATION * i) - centerOffsetX;
				float squarePositionY = (j * Renderer.SQUARE_SIZE) + (Renderer.SEPARATION * j) - centerOffsetY;

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
