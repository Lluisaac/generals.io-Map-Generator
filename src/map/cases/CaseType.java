package map.cases;

public enum CaseType
{
	BLANK(true, true),
	MOUNTAIN(false, false),
	CASTLE(true, false),
	KING(true, true);

	private boolean isStrongConnexity;
	private boolean isWeakConnexity;
	
	private CaseType(boolean isStrong, boolean isWeak)
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
