package map.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
	
	private Zone zone;
	private General concerned;
	
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

	public void setupDistanceData()
	{
		this.setupConcerned();
		this.setupZone();
	}


	private void setupConcerned()
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
		
		for (Entry<General, Integer> pair : this.distances.entrySet())
		{
			if (pair.getKey() != best && Math.abs(pair.getValue() - min) <= 1)
			{
				best = null;
			}
		}
		
		this.concerned = best;
	}

	private void setupZone()
	{
		int distanceToBorder = this.getDistanceToBorder();
		
		if (this.getConcerned() == null)
		{
			this.zone = Zone.BORDER;
		}
		else if (this.getDistanceToConcerned() <= 5)
		{
			this.zone = Zone.CLOSE;
		}
		else if (distanceToBorder / 2 <= 3)
		{
			this.zone = Zone.FAR;
		}
		else
		{
			this.zone = Zone.MID;
		}
	}
	
	public int getDistanceToBorder()
	{
		int min = Integer.MAX_VALUE;
		
		for (Entry<General, Integer> distance : this.getDistances().entrySet())
		{
			if (this.getConcerned() != null && !distance.getKey().equals(this.getConcerned()))
			{
				int distanceToBorder = Math.abs(this.getDistanceToConcerned() - distance.getValue());
				
				if (min > distanceToBorder)
				{
					min = distanceToBorder; 
				}
			}
		}
		
		return min;
	}

	public int getDistanceToConcerned()
	{
		return this.getDistances().get(this.getConcerned());
	}

	public Zone getZone()
	{
		return this.zone;
	}

	public General getConcerned()
	{
		return this.concerned;
	}

	public Map<General, Integer> getDistances()
	{
		return this.distances;
	}

	public void buildScore()
	{
		
	}
}
