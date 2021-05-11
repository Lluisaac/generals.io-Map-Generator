package map.cases;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public interface Case
{

	public String getName();

	public default Image getImage() throws SlickException
	{
		return new Image("assets/" + this.getName() + ".png");
	}

	public Color getColor();
}
