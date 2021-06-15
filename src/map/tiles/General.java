package map.tiles;

import org.newdawn.slick.Color;

public class General extends Tile
{	
	private float totalScore;
	
	public General(int x, int y)
	{
		super(TileType.GENERAL, new Color(0, 128, 128), x, y);
		this.totalScore = 0;
	}
	
	public void addTotalScore(float value)
	{
		this.totalScore += value;
	}

	public float getTotalScore()
	{
		return this.totalScore;
	}

	public General getConcerned()
	{
		return this;
	}
	
	public String toString()
	{
		return this.getName() + " : [" + this.x + ", " + this.y + "], Score: " + this.getTotalScore();
	}
}
