package map.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public abstract class Tile
{
	private TileType type;
	private Color col;
	
	public final int x;
	public final int y;
	
	private float score;
	
	private List<Tile> adjacent;
	
	private Map<General, Integer> distances;
	
	protected Tile(TileType type, Color color, int x, int y)
	{
		this.type = type;
		this.col = color;
		
		this.x = x;
		this.y = y;
		
		this.score = 0;
		
		this.adjacent = new ArrayList<>();
		
		this.distances = new HashMap<>();
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
		return this.getName() + " : [" + this.x + ", " + this.y + "]: " + this.distances;
	}

	public void addScore(float value)
	{
		this.score += value;
	}

	public float getScore()
	{
		return this.score;
	}

	public General getConcerned()
	{
		General best = null;
		int min = Integer.MAX_VALUE;
		
		for (Entry<General, Integer> pair : this.distances.entrySet())
		{
			if (pair.getValue() < min)
			{
				min = pair.getValue();
				best = pair.getKey();
			}
		}
		
		return best;
	}

	public Map<General, Integer> getDistances()
	{
		return this.distances;
	}
}
