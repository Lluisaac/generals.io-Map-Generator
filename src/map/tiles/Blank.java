package map.tiles;

import org.newdawn.slick.Color;

public class Blank extends DistanceScoredTile
{	
	public Blank(int x, int y)
	{
		super(TileType.BLANK, new Color(255, 255, 255), x, y);
	}

	public float getTotalScore()
	{
		return 0;
	}
}
