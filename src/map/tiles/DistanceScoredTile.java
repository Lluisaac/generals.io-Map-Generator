package map.tiles;

import org.newdawn.slick.Color;

public abstract class DistanceScoredTile extends Tile
{
	
	protected DistanceScoredTile(TileType type, Color color, int x, int y)
	{
		super(type, color, x, y);
	}

	@Override
	public void buildScore()
	{
		this.addScore(this.getScoreAvecDistance());
	}

	protected float getScoreAvecDistance()
	{		
		if (this.getConcerned() == null)
		{
			return 0f;
		}
		else
		{
			int border = this.getDistanceToBorder() + this.getDistanceToConcerned();

			switch(this.getZone())
			{
				case CLOSE:
					return 4.5f - (this.getDistanceToConcerned() / 2f);
				case MID:
					return -((3f * this.getDistanceToConcerned()) - (4f * border) + 17f) / ((2f * border) - 16f);
				case FAR:
					return (border - this.getDistanceToConcerned()) / 6f;
				default:
					return 0f;
			}
		}
	}
}
