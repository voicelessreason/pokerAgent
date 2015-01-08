/*//////////////////////////////////////
 * Author: Arlen Strausman            //
 * AI Project Part Two                //
 * Task: Implement a Bayesian Network //
 * Last Edited: 10/21/13   			  //
 *//////////////////////////////////////

import java.util.*;

public class BayesianNetwork {

	// fields
	private ArrayList<node> nodeList = new ArrayList<node>();

	// constructor
	public BayesianNetwork() 
	{
		
	}

	// Method to print nodes from an ArrayList.
	// Param nL = arrayList to print
	public void printNodesNames(ArrayList<node> nL) {
		for (int i = 0; i < nL.size(); i++)
			System.out.println(nL.get(i).getName());
	}

	// Method to add a root node to the network
	// Param n = node to add, 
	// Param tProb = probability node is true
	public void addRootNode(node n, double tProb) {
		n.setPList(null);
		n.setCondProb(null);
		this.updateProb(n, tProb);
		this.getNodeList().add(n);
	}

	// Method to add non-root node to network
	// Param n = node to add
	// Param pList = list of parents
	// Param sProb = string of probabilities, separated by " "
	public void addNode(node n, ArrayList<node> pList, String sProb) {
		
		ArrayList<Double> cProb = new ArrayList<Double>();
		String[] sArray = sProb.split(" ");
		for(String s : sArray)
		{
			cProb.add(Double.valueOf(s));
		}
		n.setPList(pList);
		this.updateCondProbTable(n, cProb);
		this.getNodeList().add(n);
	}

	// Method to return node's parents
	// Param n = node in question
	public ArrayList<node> nodeParents(node n) {
		return n.getPList();
	}

	// Method to specify probability table for given node
	// Param n = node to update
	// Param cPTable = probability table to be assigned
	public void updateCondProbTable(node n, ArrayList<Double> cPTable) {
		n.setCondProb(cPTable);
	}

	// Method to specify PRIOR probability of a node
	// Param n = node in question
	// Param tProb = probability node is true
	public void updateProb(node n, double tProb) {
		n.setTProb(tProb);
		n.setFProb(1.0 - tProb);
	}

	// Method to return the probability that a node is true under specific circumstances
	// Param n = node in question 
	// Param tList = nodes that are true
	// Param fList = nodes that are false
	public double conditionalProbability(node n, ArrayList<node> tList,
			ArrayList<node> fList) {
		double ret = 0.0;
		tList.add(n);
		//System.out.println("P(BJM): \n");
		double numerator = this.conditionalProbHelper(n, tList, fList);
		tList.remove(n);
		fList.add(n);
	//	System.out.println("P(-BJM) + P(BJM): \n");
		double denominator = this.conditionalProbHelper(n, tList, fList)
				+ numerator;
	//	System.out.println("numerator: " + numerator);
	//	System.out.println("denominator: " + denominator);
		ret = numerator / denominator;
	//	System.out.println(" \nThis is the conditional probability returned: "
	//			+ ret);
		return ret;
	}

	// Method that assists Conditional Probability
	// Param n = node in question
	// Param tList = nodes that are true
	// Param fList = nodes that are false
	public double conditionalProbHelper(node n, ArrayList<node> tList,
			ArrayList<node> fList) {
		// Declare return variable
		double ret = 0.0;

		// ArrayList containing possible worlds with given truth values
		ArrayList<String> worlds = buildWorlds(tList, fList);

		// An ArrayList that contains the order of nodes as they appear
		ArrayList<node> order = new ArrayList<node>();
		order.addAll(fList);
		order.addAll(tList);
		for (node n1 : this.getNodeList())
			if (!tList.contains(n1) && !fList.contains(n1)) {
				order.add(n1);
			}
		// Loop through all possible worlds
		for (String s : worlds) {
			// Declare a double to hold each worlds total probability
			double worldProbSum = 1.0;

			// Splitting worlds into ind. node values
			char[] sArray = s.toCharArray();

			// Iterate through order, and compute correct probabilities
			for (int i = 0; i < order.size(); i++) {
				ArrayList<node> pList = order.get(i).getPList();
				String prTaIndex = "";
				// If there are no parents...
				if (order.get(i).getPList() == null) {
					// if true, get tProb
					if (sArray[i] == '1') {
						worldProbSum *= order.get(i).getTProb();
						//else grab fProb
					} else {
						worldProbSum *= order.get(i).getFProb();
					}
					// if the node has parents
				} else {
					// Find the correct index within the node's prob table
					for (int k = 0; k < pList.size(); k++) {
						prTaIndex = sArray[order.indexOf(pList.get(k))]
								+ prTaIndex;
					}
					// if node is true, return item in index
					if (sArray[i] == '1') {
						worldProbSum *= order.get(i).getCondProb()
								.get(Integer.parseInt(prTaIndex, 2));
						//else return 1 - the item in index
					} else {
						worldProbSum *= 1 - order.get(i).getCondProb()
								.get(Integer.parseInt(prTaIndex, 2));
					}

				}
			}
			System.out
					.println("This is the prob of " + s + ": " + worldProbSum);
			
			// Add world probability to prob of the entire event (ret)
			ret += worldProbSum;
		}
		System.out.println("this is the probability of the entire event: "
				+ ret);
		return ret;
	}

	// Creates an ArrayList of binary numbers representing possible worlds
	// This is then used by condProbHelper to computer the numerator and
	// denominator of a cond. prob
	// Param tList = list of given true nodes, fList = list of given false nodes
	public ArrayList<String> buildWorlds(ArrayList<node> tList,
			ArrayList<node> fList) {
		// create return arraylist
		ArrayList<String> ret = new ArrayList<String>();

		// creating and filling an arraylist of variable nodes
		ArrayList<node> unknownList = new ArrayList<node>();

		for (int i = 0; i < this.getNodeList().size(); i++) {
			if (!tList.contains(this.getNodeList().get(i))
					&& !fList.contains(this.getNodeList().get(i))) {
				unknownList.add(this.getNodeList().get(i));
			}
		}

		// Create binary representation of each combination of variable nodes
		for (Integer i = 0; i < Math.pow(2, unknownList.size()); i++) {
			String binString = Integer.toBinaryString(i);
			;
			while (binString.length() < unknownList.size()) { // pad with necessary 0's
				binString = "0" + binString;
			}
			ret.add(binString);
		}

		String knownBString = "";

		// Tack on bits that represent known true and false nodes
		for (node n1 : tList) {
			knownBString = "1" + knownBString;
		}
		for (node n1 : fList) {
			knownBString = "0" + knownBString;
		}

		// Combine the pieces and place them in the return array
		for (int i = 0; i < ret.size(); i++) {
			String s = knownBString + ret.get(i);
			ret.set(i, s);
		}
		return ret;
	}

	//Method to set nodeList field
	public void setNodeList(ArrayList<node> nodeList) {
		this.nodeList = nodeList;
	}
	//Method to return nodeList field
	public ArrayList<node> getNodeList() {
		return nodeList;
	}

}

// Node class

class node {

	// fields
	private ArrayList<Double> condProbTable;
	private ArrayList<node> parentList;
	private double truthProb;
	private double falseProb;
	private String name;
	private boolean truthValue;

	// constructor
	public node(String n) {
		this.setName(n);
	}

	// various getters and setters
	public void setName(String n) {
		this.name = n;
	}

	public String getName() {
		return this.name;
	}

	public void setPList(ArrayList<node> pList) {
		this.parentList = pList;
	}

	public ArrayList<node> getPList() {
		return this.parentList;
	}

	public void setTProb(double prob) {
		this.truthProb = prob;
	}

	public double getTProb() {
		return this.truthProb;
	}

	public void setFProb(double prob) {
		this.falseProb = prob;
	}

	public double getFProb() {
		return this.falseProb;
	}

	public void setCondProb(ArrayList<Double> cPTable) {
		this.condProbTable = cPTable;
	}

	public ArrayList<Double> getCondProb() {
		return this.condProbTable;
	}

	public Boolean getBool() {
		return this.truthValue;
	}
}