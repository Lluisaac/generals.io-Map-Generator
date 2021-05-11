package map.cases;

import org.newdawn.slick.Color;

public class Armed implements Case
{
	private int cost; 
	private Color col;
	
	public Armed(int cost)
	{
		this.cost = cost;
		this.col = new Color(128, 128, 128);
	}
	
	@Override
	public String getName()
	{
		return "blank";
	}

	@Override
	public Color getColor()
	{
		return this.col;
	}
}
