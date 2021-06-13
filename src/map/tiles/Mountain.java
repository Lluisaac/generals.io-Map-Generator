package map.tiles;

import org.newdawn.slick.Color;

public class Mountain extends Tile
{
	public Mountain(int x, int y)
	{
		super(TileType.MOUNTAIN, new Color(255, 255, 255), x, y);
	}
}
