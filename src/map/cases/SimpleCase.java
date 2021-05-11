package map.cases;

import org.newdawn.slick.Color;

public enum SimpleCase implements Case
{
	BLANK(new Color(255, 255, 255)),
	KING(new Color(0, 128, 128)),
	MOUNTAIN(new Color(255, 255, 255)),
	SWAMP(new Color(128, 128, 128));
	
	private Color col;
	
	private SimpleCase(Color col)
	{
		this.col = col;
	}

	@Override
	public String getName()
	{
		return this.name().toLowerCase();
	}

	@Override
	public Color getColor()
	{
		return this.col;
	}
}
