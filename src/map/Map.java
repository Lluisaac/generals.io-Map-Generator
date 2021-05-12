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
	private static final float MOUNTAIN_PROBABILITY_RATIO = 11f;
	private static final double SURFACE_RATIO_THRESHOLD = 0.9d;
	private static final int MINIMUM_SPAWN_DISTANCE = 6;

	private Case[][] map;

	private int nbPlayers;

	private int width;
	private int height;
	private double mountainSetting;
	private double castleSetting;
	
	private List<Case> spawns;

	private Random rand;

	public Map(int nbPlayers, int width, int height, double mountainSetting, double castleSetting)
	{
		this.nbPlayers = nbPlayers;

		this.width = width;
		this.height = height;
		this.mountainSetting = mountainSetting;
		this.castleSetting = castleSetting;
		
		this.spawns = new ArrayList<>();

		this.map = new Case[this.width][this.height];
		this.rand = new Random();
		this.reroll();
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
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
		
		buildBlanks();
		linkCases();
		
		int nbVides = this.buildMountains();
		nbVides = this.buildCastles(nbVides);
		Case spawn = this.buildSpawns();
		
		return this.verifyIntegrity(spawn);
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

	private int buildMountains()
	{
		int cpt = 0;

		double proba = this.getMountainProbability();

		for(int i = 0; i < this.width; i++)
		{
			for(int j = 0; j < this.height; j++)
			{
				if(this.rand.nextDouble() <= proba)
				{
					this.insert(i, j, new Mountain(i, j));
				}
				else
				{
					cpt++;
				}

			}
		}

		return cpt;
	}

	private double getMountainProbability()
	{
		double setting = this.mountainSetting > 1 ? 1 : this.mountainSetting < 0 ? 0 : this.mountainSetting;
		return Math.sqrt(setting / MOUNTAIN_PROBABILITY_RATIO);
	}

	private int buildCastles(int nbVides)
	{
		double proba = this.getCastleProbability(nbVides);
		
		int cpt = 0;

		for(int i = 0; i < this.width; i++)
		{
			for(int j = 0; j < this.height; j++)
			{
				if(this.map[i][j].getType() != CaseType.MOUNTAIN)
				{
					if(this.rand.nextDouble() <= proba)
					{
						this.insert(i, j, new Castle(i, j, this.getCastleCost()));
						nbVides--;
						cpt ++;
					}
				}

			}
		}

		return nbVides;
	}

	private double getCastleProbability(int nbVides)
	{
		return (10d * this.castleSetting * this.nbPlayers) / (nbVides * 1.0d);
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
}
