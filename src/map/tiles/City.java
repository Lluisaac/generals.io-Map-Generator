package map.tiles;

import org.newdawn.slick.Color;

public class City extends DistanceScoredTile
{
	private int cost;
	
	public City(int x, int y, int cityCost)
	{
		super(TileType.CITY, new Color(128, 128, 128), x, y);
		this.cost = cityCost;
	}

	@Override
	public void buildScore()
	{
		super.buildScore();
		this.addScore(this.getScoreAvecCout());
	}

	@Override
	protected float getScoreAvecDistance()
	{
		return 20 * super.getScoreAvecDistance();
	}
	
	private float getScoreAvecCout()
	{
		return 1f/(this.cost - 39f);
	}
}
