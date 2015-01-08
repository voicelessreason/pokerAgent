import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


public class PokerAgent extends Player{
	
	//fields
	private LinkedList<Player> opponents;
	protected PrologInterface kB;
	private float pot;
    private Scanner dealer;
	private LinkedList<PrologCard> playedCards;
	private LinkedList<String> predicates;

	//constructor
	public PokerAgent(String myID, String knowledge)
	{
		this.ID = myID;
		this.moolah = 100000;
		this.dealer = new Scanner(System.in);
		this.opponents = new LinkedList<Player>();
		this.playedCards = new LinkedList<PrologCard>();
		this.myHand = new LinkedList<PrologCard>();
		this.kB = new PrologInterface(knowledge);
		
		//initialize predicate strings 
		this.predicates = new LinkedList<String>();
		this.predicates.add("one_pair");
		this.predicates.add("two_pair");
		this.predicates.add("three_of_a_kind");
		this.predicates.add("straight");
		this.predicates.add("flush");
		this.predicates.add("full_house");
		this.predicates.add("four_of_a_kind");
		this.predicates.add("straight_flush");
		this.predicates.add("royal_flush");
	}
	
	public void addToHand(PrologCard c)
	{
		this.myHand.add(c);
	}
	
	public void printOpponents()
	{
		for(Player p : opponents)
		{
			System.out.println(p.getID());
		}
	}
	
	public float getHandProb(LinkedList<PrologCard> hand, LinkedList<PrologCard> omit, String whichHand)
	{
		float ret = 0;
		LinkedList<String> sol = new LinkedList<String>();
		sol = kB.HandQuery(whichHand, hand);
		if(sol.size() == 1)
		{
			ret = 1;
		}else
		{
			LinkedList<PrologCard> hand2 = 	new LinkedList<PrologCard>();
			hand2.addAll(hand);
			String addOn = "1";
			while(hand2.size() < 6)
			{
				hand2.add( new PrologCard("X" + addOn));
				addOn += addOn;
			}
			sol = kB.HandQuery2(whichHand, hand2, omit);
			float numer = sol.size();
			float denom = 52 - playedCards.size();
			denom = calcBC(denom, 5);
			ret = numer/denom;
		}
		return ret;
	}
	
	public float calcBC(float initVal, int num)
	{
		float ret = 1;
		for(int i = num; i > 0; i--)
		{
			ret *= initVal;
			initVal--;
		}
		return ret;
	}
	
	public void initBayesNets()

	{
		for(Player p : opponents)
		{
			p.initMyNet();
		}
	}
	
	public void updateNet(Player p)
	{
		BayesianNetwork bn = p.getNet();
		ArrayList<node> nodeList = bn.getNodeList();
		
		for(String s : this.predicates)
		{
			bn.updateProb(nodeList.get(0), this.getHandProb(p.getHand(), this.playedCards, s));
		}
	}
	
	public void bet(float min)
	{
		float betTotal = min;

		if(betTotal < this.getMoolah())
		{
			System.out.println("Bet " + betTotal);
		}else
		{
			System.out.println("FOLD");
		}
	}
	
	public int findBestHand()
	{
		int value = -1;
		for(String s : predicates)
		{
			if(this.getHandProb(this.getHand(), this.playedCards, s) == 1)
				{
					value = (predicates.indexOf(s));
				}
		}
		return value;
	}
	
	public void startGame()
	{
		String announce = dealer.nextLine();
		String[] announceArray = new String[25];
		
		while(!(announce.equals("END INTRO")))
		{
			if(announce.equals("HELO"))
			{
				System.out.println("IAM " + ID);
			}
			else if(announce.equals("ID " + ID + " TAKEN"))
			{
				this.setID(ID + "1");
				System.out.println("IAM " + ID);
			}else
			{
				announceArray = announce.split(" ");
				if(announceArray[0].equals("PLAYER") && !(announceArray[0].equals(ID)))
				{
					Player temp = new Player(announceArray[1], Integer.parseInt(announceArray[5]));
					this.addOpponent(temp);
				}
			}
			announce = dealer.nextLine();
		}
		this.initBayesNets();
	}
	
	public void playRound()
	{
		String announce = dealer.nextLine();
		String[] announceArray = new String[25];
		announceArray = announce.split(" ");
		boolean gameOver = false;
		float minBet = 0;
		
		while(!(gameOver))
		{
			announceArray = announce.split(" ");
			if(announceArray[0].equals("BET?"))
			{
				this.bet(minBet);
			}
			else if(!(announceArray.length < 2) && announceArray[2].equals("HAS"))
			{
				for(Player p : opponents)
				{	
					if(p.getID().equals(announceArray[1]))
					{
						p.setMoolah(Integer.parseInt(announceArray[3]));
					}
				}
			}else if(!(announceArray.length < 7) && announceArray[7].equals("UP"))
			{
				PrologCard c = new PrologCard(announceArray[4],announceArray[5]);
				for(Player p : opponents)
				{
					if(p.getID().equals(announceArray[1]))
					{
						p.addToHand(c);
						this.addPlayedCard(c);
						this.updateNet(p);
					}
				}
			}else if(!(announceArray.length < 7) && announceArray[7].equals("DOWN"))
			{
				PrologCard c = new PrologCard(announceArray[4],announceArray[5]);
				this.addToHand(c);
				this.addPlayedCard(c);
			}else if(announceArray[0].equals("POT"))
			{
				this.setPot(Float.parseFloat(announceArray[3]));
			}else if(!(announceArray.length < 2) && announceArray[2].equals("BET"))
			{
				minBet = Float.parseFloat(announceArray[3]);
				for(Player p : opponents)
				{
					if(p.getID().equals(announceArray[1]))
					{
						p.setMoolah(p.getMoolah() - Float.parseFloat(announceArray[3]));
					}
				}
			}else if(!(announceArray.length < 2) && announceArray[2].equals("WINS"))
			{
				gameOver = true;
			}

			announce = dealer.nextLine();
		}
		
		for(Player p : opponents)
		{
			System.out.println("Opponent: " + p.getID());
			System.out.println(p.printHand());
		}
		System.out.println("Me: " + this.printHand());
		
	}	

	public void addOpponent(Player e)
	{
		this.opponents.add(e);
	}	
	
	public LinkedList<Player> getOpponents()
	{
		return opponents;
	}
	
	public void addPlayedCard(PrologCard c)
	{
		this.playedCards.add(c);
	}
	
	public float getPot()
	{
		return this.pot;
	}
	
	public void setPot(float f)
	{
		this.pot = f;
	}		
}
