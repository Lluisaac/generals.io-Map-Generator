package map.tiles;

public enum TileType
{
	BLANK(true, true),
	MOUNTAIN(false, false),
	CITY(true, false),
	GENERAL(true, true);

	private boolean isStrongConnexity;
	private boolean isWeakConnexity;
	
	private TileType(boolean isStrong, boolean isWeak)
	{
		this.isStrongConnexity = isStrong;
		this.isWeakConnexity = isWeak;
	}
	
	public boolean isStrongConnexity()
	{
		return this.isStrongConnexity;
	}
	
	public boolean isWeakConnexity()
	{
		return this.isWeakConnexity;
	}
}
