package map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import map.tiles.Blank;
import map.tiles.City;
import map.tiles.General;
import map.tiles.Mountain;
import map.tiles.Tile;
import map.tiles.TileType;

public class Map
{
	private static final double HALF = 0.5;
	private static final double TWO_PI = 2 * Math.PI;
	private static final double SURFACE_RATIO_THRESHOLD = 0.9d;
	private static final int MINIMUM_SPAWN_DISTANCE = 6;

	private Tile[][] map;

	private int nbPlayers;

	private double widthSetting;
	private double heightSetting;
	private double mountainSetting;
	private double citySetting;
	
	private int width;
	private int height;
	
	private List<General> spawns;

	private Random rand;
	
	//All the magic numbers in this class are arbitrary values that were decided to be a good match. 
	//They come from the data collection that was done.

	public Map(int nbPlayers, double widthSetting, double heightSetting, double mountainSetting, double citySetting)
	{
		this.nbPlayers = nbPlayers;

		this.widthSetting = widthSetting;
		this.heightSetting = heightSetting;
		this.mountainSetting = mountainSetting;
		this.citySetting = citySetting;
		
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
		do 
		{
			while (!this.generate());
		}
		while (!this.isFairPlay());
	}
	
	private boolean isFairPlay()
	{
		this.scorePreProcessing();

		this.scoreSpawns();
		
		return this.areSpawnsFair();
	}

	private void scorePreProcessing()
	{
		this.buildDistances();
		this.setupTileDistanceData();
		this.calculateTileScore();
	}
	
	private void buildDistances()
	{
		for (General spawn : this.spawns)
		{
			List<Tile> toVisit = new ArrayList<>();
			toVisit.add(spawn);
			
			this.propagateDistancesFrom(spawn, toVisit, 0);
		}
	}

	private void propagateDistancesFrom(General spawn, List<Tile> toVisit, int distance)
	{
		while (!toVisit.isEmpty())
		{
			List<Tile> nextToVisit = new ArrayList<>();

			for (Tile tile : toVisit)
			{
				if (!tile.getDistances().containsKey(spawn)) 
				{
					tile.getDistances().put(spawn, distance);
					
					for (Tile adjacent : tile.getAdjacent())
					{
						if (adjacent.getType().isStrongConnexity() && !adjacent.getDistances().containsKey(spawn))
						{
							nextToVisit.add(adjacent);
						}
					}
				}
			}
			
			toVisit.clear();
			toVisit.addAll(nextToVisit);
			distance++;
		}
	}

	private void setupTileDistanceData()
	{
		for (int i = 0; i < this.width; i++)
		{
			for (int j = 0; j < this.height; j++)
			{
				this.map[i][j].setupDistanceData();
			}
		}
	}

	private void calculateTileScore()
	{
		for (int i = 0; i < this.width; i++)
		{
			for (int j = 0; j < this.height; j++)
			{
				TileType type = this.map[i][j].getType();
				if (type == TileType.CITY || type == TileType.BLANK || type == TileType.GENERAL)
				{
					this.map[i][j].buildScore();
				}
			}
		}
	}

	private void scoreSpawns()
	{
		for (int i = 0; i < this.width; i++)
		{
			for (int j = 0; j < this.height; j++)
			{
				General concerned = this.map[i][j].getConcerned();
				
				if (concerned != null)
				{
					concerned.addTotalScore(this.map[i][j].getScore());
				}
			}
		}
	}

	private boolean areSpawnsFair()
	{
		float[] scores = new float[this.spawns.size()];
		
		float min = Float.MAX_VALUE;
		
		for (int i = 0; i < this.spawns.size(); i++)
		{
			scores[i] = this.spawns.get(i).getTotalScore();
			
			if (scores[i] < min)
			{
				min = scores[i];
			}
		}
		
		System.out.println("Minimum score: " + min);
		
		for (int i = 0; i < this.spawns.size(); i++)
		{
			if (scores[i] > min * 1.1)
			{
				return false;
			}
		}
		
		return true;
	}

	public Tile insert(int x, int y, Tile newCase)
	{
		Tile oldCase = this.map[x][y];
		
		this.map[x][y] = newCase;
		
		newCase.addAdjacentOf(oldCase);
		
		for (Tile adj : newCase.getAdjacent())
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
		
		this.buildCities();
		
		Tile spawn = this.buildSpawns();
		
		return this.verifyIntegrity(spawn);
	}

	private void generateMapSize()
	{
		this.width = this.generateSize(this.widthSetting);
		this.height = this.generateSize(this.heightSetting);

		this.width = this.width < 16 ? 16 : this.width;
		this.height = this.height < 16 ? 16 : this.height;
		
		System.out.println("\nWidth: " + this.width + "\nHeight: " + this.height);
		
		this.map = new Tile[this.width][this.height];
		
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
				this.map[i][j] = new Blank(i, j);
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

	private void buildCities()
	{
		int expected = this.getExpectedCityAmount();
		double variance = this.getCityAmountVariance();
		
		int actualAmount = (int) Math.round(this.gaussian(expected, variance));
		
		System.out.println("Number of Cities: " + actualAmount);
		
		this.insertCities(actualAmount);
	}
	
	private int getExpectedCityAmount()
	{
		double territoryPerCity = 16 / citySetting;
		int totalTerritory = this.width * this.height;
		return (int) Math.round(totalTerritory / territoryPerCity);
	}

	private double getCityAmountVariance()
	{
		double sizeSetting = (this.widthSetting + this.heightSetting) / 2.0;
		return sizeSetting * this.nbPlayers * 0.583;
	}

	private void insertCities(int n)
	{
		int nbCities = 0;
		
		while (nbCities < n)
		{
			int x = this.rand.nextInt(this.width);
			int y = this.rand.nextInt(this.height);
			
			if (this.map[x][y].getType() == TileType.BLANK)
			{
				this.insert(x, y, new City(x, y, this.getCityCost()));
				
				nbCities++;
			}
		}
	}

	private int getCityCost()
	{
		return this.rand.nextInt(11) + 40;
	}

	private Tile buildSpawns()
	{
		int nbPlayersSet = 0;
		
		General newCase = null;
		
		while (nbPlayersSet < this.nbPlayers)
		{
			int x = this.rand.nextInt(this.width);
			int y = this.rand.nextInt(this.height);
			
			if (this.map[x][y].getType() == TileType.BLANK)
			{
				newCase = new General(x, y);
				this.insert(x, y, newCase);
				
				this.spawns.add(newCase);
				
				nbPlayersSet++;
			}
		}
		
		return newCase;
	}

	private boolean verifyIntegrity(Tile spawn)
	{
		List<Tile> zoneConnexe = this.getZoneConnexe(spawn);
		
		boolean spawnsConnexe = this.verifySpawnsConnexes(zoneConnexe);
		boolean distanceSpanws = this.verifySpawnsDistance(zoneConnexe);
		boolean surfaceConnexeValide = this.verifySurfaceConnexe(zoneConnexe);
		
		return spawnsConnexe && distanceSpanws && surfaceConnexeValide;
	}

	private List<Tile> getZoneConnexe(Tile spawn)
	{
		List<Tile> zone = new ArrayList<>();
		
		List<Tile> aFaire = new ArrayList<>();
		
		aFaire.add(spawn);
		
		while(aFaire.size() > 0)
		{
			Tile act = aFaire.remove(0);
			zone.add(act);
			
			for (Tile adj : act.getAdjacent())
			{
				if (adj.getType().isWeakConnexity() && !zone.contains(adj) && !aFaire.contains(adj))
				{
					aFaire.add(adj);
				}
			}
		}
		
		return zone;
	}

	private boolean verifySpawnsConnexes(List<Tile> zoneConnexe)
	{
		int cpt = 0;
		
		for (Tile act : zoneConnexe)
		{
			if (act.getType() == TileType.GENERAL)
			{
				cpt++;
			}
		}
		
		return cpt == this.nbPlayers;
	}

	private boolean verifySpawnsDistance(List<Tile> zoneConnexe)
	{
		for (Tile spawn : this.spawns)
		{
			List<Tile> zoneAround = this.getZoneAround(spawn, MINIMUM_SPAWN_DISTANCE + 1);
			
			int cpt = 0;
			
			for (Tile act : zoneAround)
			{
				if (act.getType() == TileType.GENERAL)
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
	
	private List<Tile> getZoneAround(Tile initial, int deepness)
	{
		List<Tile> zone = new ArrayList<Tile>();
		List<Tile> aFaire = new ArrayList<Tile>();
		List<Tile> nextStep = new ArrayList<Tile>();
		
		aFaire.add(initial);
		
		for (int i = 0; i < deepness + 1; i++)
		{
			while (aFaire.size() > 0)
			{
				Tile act = aFaire.remove(0);
				zone.add(act);
				
				for (Tile adj : act.getAdjacent())
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

	private boolean verifySurfaceConnexe(List<Tile> zoneConnexe)
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
				if (this.map[i][j].getType() == TileType.MOUNTAIN)
				{
					cpt++;
				}
			}
		}
		
		return cpt;
	}

	public Tile[][] getMap()
	{
		return this.map;
	}
	
	/*
	 * From https://en.wikipedia.org/wiki/Box?Muller_transform#Implementation
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
