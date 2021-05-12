package map.cases;

import org.newdawn.slick.Color;

public class Castle extends Case
{
	private int cost;
	
	public Castle(int x, int y, int castleCost)
	{
		super(CaseType.CASTLE, new Color(128, 128, 128), x, y);
		this.cost = castleCost;
	}
}
