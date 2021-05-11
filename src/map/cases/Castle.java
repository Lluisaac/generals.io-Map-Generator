package map.cases;

import org.newdawn.slick.Color;

public class Castle implements Case
{
	private int cost;
	private Color col;
	
	public Castle(int castleCost)
	{
		this.cost = castleCost;
		this.col = new Color(128, 128, 128);
	}

	@Override
	public String getName()
	{
		return "castle";
	}

	@Override
	public Color getColor()
	{
		return this.col;
	}
}
