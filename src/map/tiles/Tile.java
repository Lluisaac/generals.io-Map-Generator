package map.tiles;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Tile
{
	private TileType type;
	private Color col;
	
	public final int x;
	public final int y;
	
	private List<Tile> adjacent;
	
	public Tile(int x, int y)
	{
		this(TileType.BLANK, new Color(255, 255, 255), x, y);
	}
	
	protected Tile(TileType type, Color color, int x, int y)
	{
		this.type = type;
		this.col = color;
		
		this.x = x;
		this.y = y;
		
		this.adjacent = new ArrayList<>();
	}

	public String getName()
	{
		return this.type.name().toLowerCase();
	}

	public Image getImage() throws SlickException
	{
		return new Image("assets/" + this.getName() + ".png");
	}

	public Color getColor()
	{
		return this.col;
	}
	
	public TileType getType()
	{
		return this.type;
	}
	
	public List<Tile> getAdjacent()
	{
		return this.adjacent;
	}
	
	public void addAdjacent(Tile adj)
	{
		this.adjacent.add(adj);
	}
	
	public void emptyAdjacent()
	{
		this.adjacent.clear();
	}

	public void switchAdjacent(Tile oldCase, Tile newCase)
	{
		this.adjacent.remove(oldCase);
		this.addAdjacent(newCase);
	}

	public void addAdjacentOf(Tile oldCase)
	{
		this.emptyAdjacent();
		
		for (Tile adj : oldCase.getAdjacent())
		{
			this.addAdjacent(adj);
		}
	}
	
	public String toString()
	{
		return this.getName() + " : [" + this.x + ", " + this.y + "]";
	}
}
