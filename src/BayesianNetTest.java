/*///////////////////////////////////////////////////////////
 *  This is the test class for the Bayesian Network Class  //
 *  The program attempts to calculate the probability that //
 *  a burglary has occurred, given that john and mary have //
 *  both called.                                           //
 *///////////////////////////////////////////////////////////

import java.util.ArrayList;

public class BayesianNetTest {

	public static void main(String[] args) {
		
		// initialize the network
		BayesianNetwork bn = new BayesianNetwork();
		
		// initialize nodes
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
		bn.addRootNode(one_pair, .0);
		bn.addRootNode(two_pair, .0);
		bn.addRootNode(three_of_a_kind, .0);
		bn.addRootNode(full_house, 0);
		bn.addRootNode(four_of_a_kind, .0);
		bn.addRootNode(straight, .0);
		bn.addRootNode(flush, .0);
		bn.addRootNode(straight_flush, .0);
		bn.addRootNode(royal_flush, .0);
		bn.addNode(bet_high, betParents, ".001 .29 .94 .95");
		bn.addNode(bet_middle, betParents, ".01 .70");
		bn.addNode(bet_low, betParents, ".05 .90");

		
		//prepare tList for calling cond. prob.
		
		// call conditional probability on P(B|JM). Answer should be .28417 and then some

	}
}
