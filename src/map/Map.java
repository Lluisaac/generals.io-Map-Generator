package map;

import java.util.Random;

import map.cases.Case;
import map.cases.Castle;
import map.cases.SimpleCase;

public class Map
{
	private static final float MOUNTAIN_PROBABILITY_RATIO = 11f;

	private Case[][] map;

	private int nbPlayers;

	private int width;
	private int height;
	private double mountainSetting;
	private double castleSetting;

	private Random rand;

	public Map(int nbPlayers, int width, int height, double mountainSetting, double castleSetting)
	{
		this.nbPlayers = nbPlayers;

		this.width = width;
		this.height = height;
		this.mountainSetting = mountainSetting;
		this.castleSetting = castleSetting;

		this.map = new Case[this.width][this.height];
		this.rand = new Random();
		this.generate();
	}

	public void reroll()
	{
		this.generate();
	}

	private void generate()
	{
		int nbVides = this.buildMountains();
		nbVides = this.buildCastles(nbVides);
		this.buildSpawns();
		
		if (!this.verifyIntegrity())
		{
			this.generate();
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
				if(this.rand.nextDouble() > proba)
				{
					this.map[i][j] = SimpleCase.BLANK;
					cpt++;
				}
				else
				{
					this.map[i][j] = SimpleCase.MOUNTAIN;
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

		for(int i = 0; i < this.width; i++)
		{
			for(int j = 0; j < this.height; j++)
			{
				if(!this.map[i][j].equals(SimpleCase.MOUNTAIN))
				{
					if(this.rand.nextDouble() <= proba)
					{
						this.map[i][j] = new Castle(this.getCastleCost());
						nbVides--;
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

	private void buildSpawns()
	{
		int nbPlayersSet = 0;
		
		while (nbPlayersSet < this.nbPlayers)
		{
			int x = this.rand.nextInt(this.width);
			int y = this.rand.nextInt(this.height);
			
			if (this.map[x][y].equals(SimpleCase.BLANK))
			{
				this.map[x][y] = SimpleCase.KING;
				nbPlayersSet++;
			}
		}
	}

	private boolean verifyIntegrity()
	{
		//Les spawns doivent être connexes
		//Les spawns doivent avoir une distance de 5 minimum deux à deux
		//La zone connexe dans lequel les spawns sont doit couvrir au moins 90% des cases vides de la map
		return true;
	}

	public Case[][] getMap()
	{
		return this.map;
	}
}
