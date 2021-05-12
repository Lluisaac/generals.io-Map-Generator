package map.cases;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Case
{
	private CaseType type;
	private Color col;
	
	public final int x;
	public final int y;
	
	private List<Case> adjacent;
	
	public Case(int x, int y)
	{
		this(CaseType.BLANK, new Color(255, 255, 255), x, y);
	}
	
	protected Case(CaseType type, Color color, int x, int y)
	{
		this.type = type;
		this.col = color;
		
		this.x = x;
		this.y = y;
		
		this.adjacent = new ArrayList<>();
	}

	public String getName()
	{
		return this.type.name().toLowerCase();
	}

	public Image getImage() throws SlickException
	{
		return new Image("assets/" + this.getName() + ".png");
	}

	public Color getColor()
	{
		return this.col;
	}
	
	public CaseType getType()
	{
		return this.type;
	}
	
	public List<Case> getAdjacent()
	{
		return this.adjacent;
	}
	
	public void addAdjacent(Case adj)
	{
		this.adjacent.add(adj);
	}
	
	public void emptyAdjacent()
	{
		this.adjacent.clear();
	}

	public void switchAdjacent(Case oldCase, Case newCase)
	{
		this.adjacent.remove(oldCase);
		this.addAdjacent(newCase);
	}

	public void addAdjacentOf(Case oldCase)
	{
		this.emptyAdjacent();
		
		for (Case adj : oldCase.getAdjacent())
		{
			this.addAdjacent(adj);
		}
	}
	
	public String toString()
	{
		return this.getName() + " : [" + this.x + ", " + this.y + "]";
	}
}
