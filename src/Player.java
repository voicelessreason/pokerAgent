import java.util.ArrayList;
import java.util.LinkedList;
import java.util.LinkedList;
import java.util.Scanner;
import java.lang.Math;

public class Player {

    protected String ID;
	protected LinkedList<PrologCard> myHand;
	protected float moolah;
	private int position;
	private BayesianNetwork bayesNet;
	
	public Player()
	{
		
	}
	
	public Player(String myID, int pos)
	{
		ID = myID;
		position = pos;
		moolah = 100000;
		myHand = new LinkedList<PrologCard>();
	}

	public void initMyNet()
	{
		bayesNet = new BayesianNetwork();
		
		// initialize nodes
		node no_hand = new node("no_hand");
		node one_pair = new node("one_pair");
		node two_pair = new node("two_pair");
		node three_of_a_kind = new node("three_of_a_kind");
		node straight = new node("straight");
		node flush = new node("flush");
		node full_house = new node("full_house");
		node four_of_a_kind = new node("four_of_a_kind");
		node royal_flush = new node("one_pair");
		node straight_flush = new node("one_pair");
		node bet_high = new node("bet_high");
		node bet_middle = new node("bet_middle");
		node bet_low = new node("bet_high");
		
		// initialize lists of parents and list of true vales for cond.prob call
		ArrayList<node> betParents = new ArrayList<node>();

		// set node parents
		
		betParents.add(one_pair);
		betParents.add(two_pair);
		betParents.add(three_of_a_kind);
		betParents.add(straight);
		betParents.add(flush);
		betParents.add(full_house);
		betParents.add(four_of_a_kind);
		betParents.add(straight_flush);
		betParents.add(royal_flush);
		
		// add nodes to network
		bayesNet.addRootNode(no_hand, 0.5);
		bayesNet.addRootNode(one_pair, .4225);
		bayesNet.addRootNode(two_pair, .0475);
		bayesNet.addRootNode(three_of_a_kind, .0211);
		bayesNet.addRootNode(full_house, .0014);
		bayesNet.addRootNode(four_of_a_kind, .0002);
		bayesNet.addRootNode(straight, .0039);
		bayesNet.addRootNode(flush, .0019);
		bayesNet.addRootNode(straight_flush, .00001);
		bayesNet.addRootNode(royal_flush, .000001);
		
		//setCPT's

		String lowString = ".95 .80 .20 .15 .10 .05 .04 .03 .02 .01";
		String midString = ".05 .25 .50 .40 .30 .15 .09 .07 .03 .01";
		String hiString  = ".10 .25 .75 .85 .90 .95 .96 .97 .98 .99";
		
		bayesNet.addNode(bet_high, betParents, genCPTString(hiString));
		bayesNet.addNode(bet_middle, betParents, genCPTString(midString));
		bayesNet.addNode(bet_low, betParents, genCPTString(lowString));
		
	}
	
	
	public String genCPTString(String s)
	{
		String[] probs = s.split(" ");
		String ret = probs[0] + " ";
		
		for(int i = 1; i < probs.length; i ++)
		{
			for(int j = 0; j < Math.pow(Double.parseDouble(probs[i]),2); j++)
			{
				ret += probs[i] + " ";
			}
		}
		return ret;
	}
	
	public String genCPTStringHelper(int count, double d)
	{
		String ret = "";
		for(int i = 0; i< count; i++)
		{
			ret += d + " ";
		}
		return ret;
	}
	
	public String printHand()
	{
		String ret = ""; 
		for(PrologCard c : this.getHand())
		{
			ret +=  c.getValue() + " " +  c.getSuit() + " ";
		}
		return ret;
	}
	
	public LinkedList<PrologCard> getHand()
	{
		return this.myHand;
	}
	
	public void addToHand(PrologCard c)
	{
		this.myHand.add(c);
	}
	
	public void setMoolah(float newTotal)
	{
		this.moolah = newTotal;
	}
	
	public float getMoolah()
	{
		return this.moolah;		
	}

	public void setPos(int pos)
	{
		this.position = pos;
	}
	
	public int getPos()
	{
		return this.position;
	}
	
	public void setID(String myID)
	{
		this.ID = myID;
	}
	
	public String getID()
	{
		return ID;
	}

	public BayesianNetwork getNet()
	{
		return bayesNet;
	}
	
}
