package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import map.cases.Case;
import map.cases.CaseType;
import map.cases.Castle;
import map.cases.King;
import map.cases.Mountain;

public class Map
{
	private static final double HALF = 0.5;
	private static final double TWO_PI = 2 * Math.PI;
	private static final double SURFACE_RATIO_THRESHOLD = 0.9d;
	private static final int MINIMUM_SPAWN_DISTANCE = 6;

	private Case[][] map;

	private int nbPlayers;

	private double widthSetting;
	private double heightSetting;
	private double mountainSetting;
	private double castleSetting;
	
	private int width;
	private int height;
	
	private List<Case> spawns;

	private Random rand;
	
	//All the magic numbers in this class are arbitrary values that were decided to be a good match. 
	//They come from the data collection that was done.

	public Map(int nbPlayers, double widthSetting, double heightSetting, double mountainSetting, double castleSetting)
	{
		this.nbPlayers = nbPlayers;

		this.widthSetting = widthSetting;
		this.heightSetting = heightSetting;
		this.mountainSetting = mountainSetting;
		this.castleSetting = castleSetting;
		
		this.spawns = new ArrayList<>();
		this.rand = new Random();
		this.reroll();
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void reroll()
	{
		while (!this.generate());
	}
	
	public Case insert(int x, int y, Case newCase)
	{
		Case oldCase = this.map[x][y];
		
		this.map[x][y] = newCase;
		
		newCase.addAdjacentOf(oldCase);
		
		for (Case adj : newCase.getAdjacent())
		{
			adj.switchAdjacent(oldCase, newCase);		
		}
		
		return oldCase;
	}
	
	private boolean generate()
	{
		this.spawns.clear();
		
		this.generateMapSize();
		
		this.buildMountains();
		
		this.buildCastles();
		
		Case spawn = this.buildSpawns();
		
		return this.verifyIntegrity(spawn);
	}

	private void generateMapSize()
	{
		this.width = this.generateSize(this.widthSetting);
		this.height = this.generateSize(this.heightSetting);

		this.width = this.width < 16 ? 16 : this.width;
		this.height = this.height < 16 ? 16 : this.height;
		
		System.out.println("Width: " + this.width + "\nHeight: " + this.height);
		
		this.map = new Case[this.width][this.height];
		
		buildBlanks();
		
		linkCases();
	}

	private int generateSize(double setting)
	{
		int expected = this.getExpectedSize(setting);
		double variance = this.getSizeVariance();
		
		double actualSize = this.gaussian(expected, variance);
		
		return (int) Math.round(actualSize);
	}

	private int getExpectedSize(double setting)
	{
		double first;
		
		if (setting <= HALF)
		{
			first = setting * 44;
		}
		else
		{
			first = 22 + (setting - HALF) * 164;
		}
		
		double second = 240 / (nbPlayers * 1.0) + 40;
		
		return (int) Math.round(Math.sqrt((first + second) * this.nbPlayers));
	}

	private double getSizeVariance()
	{
		return 1.35 + (this.nbPlayers * 0.15);
	}

	private void buildBlanks()
	{
		for(int i = 0; i < this.width; i++)
		{
			for(int j = 0; j < this.height; j++)
			{
				this.map[i][j] = new Case(i, j);
			}
		}
	}

	private void linkCases()
	{
		for(int i = 0; i < this.width; i++)
		{
			for(int j = 0; j < this.height; j++)
			{
				if (i > 0)
				{
					this.map[i][j].addAdjacent(this.map[i - 1][j]);
				}
				
				if (i < this.width - 1)
				{
					this.map[i][j].addAdjacent(this.map[i + 1][j]);
				}
				
				if (j > 0)
				{
					this.map[i][j].addAdjacent(this.map[i][j - 1]);
				}
				
				if (j < this.height - 1)
				{
					this.map[i][j].addAdjacent(this.map[i][j + 1]);
				}
			}
		}
	}

	private void buildMountains()
	{
		double proba = this.getMountainProbability();
		
		int cpt = 0;

		for(int i = 0; i < this.width; i++)
		{
			for(int j = 0; j < this.height; j++)
			{
				if(this.rand.nextDouble() <= proba)
				{
					this.insert(i, j, new Mountain(i, j));
					cpt++;
				}

			}
		}
		
		System.out.println("Mountain proportion: " + cpt / (this.width * this.height * 1.0));
	}

	private double getMountainProbability()
	{
		if (this.mountainSetting <= HALF)
		{
			return 0.42 * this.mountainSetting;
		}
		else
		{
			return 0.21 + (0.18 * (this.mountainSetting - HALF));
		}
	}

	private void buildCastles()
	{
		int expected = this.getExpectedCastleAmount();
		double variance = this.getCastleAmountVariance();
		
		int actualAmount = (int) Math.round(this.gaussian(expected, variance));
		
		System.out.println("Number of Castles: " + actualAmount);
		
		this.insertCastles(actualAmount);
	}
	
	private int getExpectedCastleAmount()
	{
		double territoryPerCastle = 16 / castleSetting;
		int totalTerritory = this.width * this.height;
		return (int) Math.round(totalTerritory / territoryPerCastle);
	}

	private double getCastleAmountVariance()
	{
		double sizeSetting = (this.widthSetting + this.heightSetting) / 2.0;
		return sizeSetting * this.nbPlayers * 0.583;
	}

	private void insertCastles(int n)
	{
		int nbCastles = 0;
		
		while (nbCastles < n)
		{
			int x = this.rand.nextInt(this.width);
			int y = this.rand.nextInt(this.height);
			
			if (this.map[x][y].getType() == CaseType.BLANK)
			{
				this.insert(x, y, new Castle(x, y, this.getCastleCost()));
				
				nbCastles++;
			}
		}
	}

	private int getCastleCost()
	{
		return this.rand.nextInt(11) + 40;
	}

	private Case buildSpawns()
	{
		int nbPlayersSet = 0;
		
		King newCase = null;
		
		while (nbPlayersSet < this.nbPlayers)
		{
			int x = this.rand.nextInt(this.width);
			int y = this.rand.nextInt(this.height);
			
			if (this.map[x][y].getType() == CaseType.BLANK)
			{
				newCase = new King(x, y);
				this.insert(x, y, newCase);
				
				this.spawns.add(newCase);
				
				nbPlayersSet++;
			}
		}
		
		return newCase;
	}

	private boolean verifyIntegrity(Case spawn)
	{
		List<Case> zoneConnexe = this.getZoneConnexe(spawn);
		
		boolean spawnsConnexe = this.verifySpawnsConnexes(zoneConnexe);
		boolean distanceSpanws = this.verifySpawnsDistance(zoneConnexe);
		boolean surfaceConnexeValide = this.verifySurfaceConnexe(zoneConnexe);
		
		return spawnsConnexe && distanceSpanws && surfaceConnexeValide;
	}

	private List<Case> getZoneConnexe(Case spawn)
	{
		List<Case> zone = new ArrayList<>();
		
		List<Case> aFaire = new ArrayList<>();
		
		aFaire.add(spawn);
		
		while(aFaire.size() > 0)
		{
			Case act = aFaire.remove(0);
			zone.add(act);
			
			for (Case adj : act.getAdjacent())
			{
				if (adj.getType().isStrongConnexity() && !zone.contains(adj) && !aFaire.contains(adj))
				{
					aFaire.add(adj);
				}
			}
		}
		
		return zone;
	}

	private boolean verifySpawnsConnexes(List<Case> zoneConnexe)
	{
		int cpt = 0;
		
		for (Case act : zoneConnexe)
		{
			if (act.getType() == CaseType.KING)
			{
				cpt++;
			}
		}
		
		return cpt == this.nbPlayers;
	}

	private boolean verifySpawnsDistance(List<Case> zoneConnexe)
	{
		for (Case spawn : this.spawns)
		{
			List<Case> zoneAround = this.getZoneAround(spawn, MINIMUM_SPAWN_DISTANCE + 1);
			
			int cpt = 0;
			
			for (Case act : zoneAround)
			{
				if (act.getType() == CaseType.KING)
				{
					cpt++;
				}
			}
			
			if (cpt > 1)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private List<Case> getZoneAround(Case initial, int deepness)
	{
		List<Case> zone = new ArrayList<Case>();
		List<Case> aFaire = new ArrayList<Case>();
		List<Case> nextStep = new ArrayList<Case>();
		
		aFaire.add(initial);
		
		for (int i = 0; i < deepness + 1; i++)
		{
			while (aFaire.size() > 0)
			{
				Case act = aFaire.remove(0);
				zone.add(act);
				
				for (Case adj : act.getAdjacent())
				{
					if (adj.getType().isWeakConnexity() && !zone.contains(adj) && !aFaire.contains(adj) && !nextStep.contains(adj))
					{
						nextStep.add(adj);
					}
				}
			}
			
			aFaire.addAll(nextStep);
			nextStep.clear();
		}
		
		return zone;
	}

	private boolean verifySurfaceConnexe(List<Case> zoneConnexe)
	{
		double tailleZoneConnexe = zoneConnexe.size();
		double nbMountains = this.getMountainsAmount();
		double mapSize = this.width * this.height;
		
		double surfaceLibre = mapSize - nbMountains;
		
		return tailleZoneConnexe / surfaceLibre >= SURFACE_RATIO_THRESHOLD;
	}
	
	private int getMountainsAmount()
	{
		int cpt = 0;
		
		for (int i = 0; i < this.width; i++)
		{
			for (int j = 0; j < this.height; j++)
			{
				if (this.map[i][j].getType() == CaseType.MOUNTAIN)
				{
					cpt++;
				}
			}
		}
		
		return cpt;
	}

	public Case[][] getMap()
	{
		return this.map;
	}
	
	/*
	 * From https://en.wikipedia.org/wiki/Box–Muller_transform#Implementation
	 */
	private double gaussian(double expected, double variance)
	{
		double u1 = this.rand.nextDouble();
		double u2 = this.rand.nextDouble();
		
		double stdDev = Math.sqrt(Math.sqrt(variance));
		double mag = stdDev / Math.sqrt(-2 * Math.log(u1));
		
		double result = mag * Math.cos(TWO_PI * u2) + expected;
		
		return result;
	}
}
