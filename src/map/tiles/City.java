package map.tiles;

import org.newdawn.slick.Color;

public class City extends Tile
{
	private int cost;
	
	public City(int x, int y, int cityCost)
	{
		super(TileType.CITY, new Color(128, 128, 128), x, y);
		this.cost = cityCost;
	}
}
