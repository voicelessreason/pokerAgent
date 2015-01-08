/* A significant amount of the code here was copied from:  http://sujitpal.blogspot.com/2009/09/calling-prolog-rules-from-java.html */

import java.util.List;
import java.util.*;
import jpl.JPL.*;
import jpl.*;
import jpl.Util.*;


public class PrologInterface {
    private String KnowledgeBase;
    private JPL Prolog;

    public PrologInterface(String KBFile) {
	KnowledgeBase = KBFile;
	JPL.init();
	Query consultQuery = new Query("consult", new Term[] {
		new Atom(KBFile)});
	if (! consultQuery.hasSolution()) {
	    System.err.println("File not found: " + KBFile);
	}
	consultQuery.close();
    }

    /**
     * Stops the prolog engine.
     */
    public void destroy()  {
	Query haltQuery = new Query("halt");
	haltQuery.hasSolution();
	haltQuery.close();
    }


    /* Copied from StackOverflow -- thanks to Jonas Klemming. */
    private static boolean isInteger(String str) {
	if (str == null) {
	    return false;
	}
	int length = str.length();
	if (length == 0) {
		return false;
	}
	int i = 0;
	if (str.charAt(0) == '-') {
		if (length == 1) {
			return false;
		}
		i = 1;
	}
	for (; i < length; i++) {
		char c = str.charAt(i);
		if (c <= '/' || c >= ':') {
			return false;
		}
	}
	return true;
    }

    /* Runs a query on a list of cards.  Returns a list of strings
     * corresponding to the solution set.  Might make sense to have an
     * auxilliary version that processes that list and returns a list
     * of PrologCards. */
    
    public LinkedList<String> HandQuery2(String Predicate, LinkedList<PrologCard> Hand, LinkedList<PrologCard> Omit) {
	// We'll create a term array of the individual cards and then use termArrayToList
	Term t;
	PrologCard CurrentCard;
	Term[] termArray;
	Term[] omitArray;
	int Size = Hand.size();
	LinkedList<String> result;
	
	termArray = new Term[Hand.size()];
	for (int i=0; i < Size; i++) {
	    CurrentCard = Hand.get(i);
	    if (CurrentCard.isVariable()) {
		termArray[i] =  new Variable(CurrentCard.getVariableName());
	    } else {
		if (isInteger(CurrentCard.getValue())) {
		    int v = java.lang.Integer.parseInt(CurrentCard.getValue());
		    termArray[i] = jpl.Util.termArrayToList(new Term[] { new jpl.Integer(v), new Atom(CurrentCard.getSuit()) });
		} else {
		    termArray[i] = jpl.Util.termArrayToList(new Term[] { new Atom(CurrentCard.getValue()), new Atom(CurrentCard.getSuit()) });
		}
	    }
	}
	
	
	omitArray = new Term[Omit.size()];
	for (int i=0; i < Omit.size(); i++) {
	    CurrentCard = Omit.get(i);
	    if (CurrentCard.isVariable()) {
		omitArray[i] =  new Variable(CurrentCard.getVariableName());
	    } else {
		if (isInteger(CurrentCard.getValue())) {
		    int v = java.lang.Integer.parseInt(CurrentCard.getValue());
		    omitArray[i] = jpl.Util.termArrayToList(new Term[] { new jpl.Integer(v), new Atom(CurrentCard.getSuit()) });
		} else {
		    omitArray[i] = jpl.Util.termArrayToList(new Term[] { new Atom(CurrentCard.getValue()), new Atom(CurrentCard.getSuit()) });
		}
	    }
	}

	Query s = new Query( new Compound(Predicate, new Term[] {jpl.Util.termArrayToList(termArray), jpl.Util.termArrayToList(omitArray) }  )    );

	result = new LinkedList<String>();
	while ( s.hasMoreSolutions() ) {
	    java.util.Hashtable sol = s.nextSolution();
	    result.add(sol.toString());
	}
	return result;
    }

    public LinkedList<String> HandQuery(String Predicate, LinkedList<PrologCard> Hand) {
	// We'll create a term array of the individual cards and then use termArrayToList
	Term t;
	PrologCard CurrentCard;
	Term[] termArray;
	int Size = Hand.size();
	LinkedList<String> result;
	
	termArray = new Term[Hand.size()];
	for (int i=0; i < Size; i++) {
	    CurrentCard = Hand.get(i);
	    if (CurrentCard.isVariable()) {
		termArray[i] =  new Variable(CurrentCard.getVariableName());
	    } else {
	    	if (isInteger(CurrentCard.getValue())) {
			    int v = java.lang.Integer.parseInt(CurrentCard.getValue());
			    termArray[i] = jpl.Util.termArrayToList(new Term[] { new jpl.Integer(v), new Atom(CurrentCard.getSuit()) });
			} else {
			    termArray[i] = jpl.Util.termArrayToList(new Term[] { new Atom(CurrentCard.getValue()), new Atom(CurrentCard.getSuit()) });
			}	
	    }
	}
	
	Query s = new Query( new Compound(Predicate, new Term[] {jpl.Util.termArrayToList(termArray) }  )    );

	result = new LinkedList<String>();
	while ( s.hasMoreSolutions() ) {
	    java.util.Hashtable sol = s.nextSolution();
	    result.add(sol.toString());
	}
	return result;
    }

}

class Test {
    private static PrologInterface test1;
    public static void main(String[] args) {
	// To run with your knowledge base, put the appropriate path here. 
    //	test1 = new PrologInterface("/home/justin/public/cs340/poker-hands.pl");
    	test1 = new PrologInterface("/home/astrausm/cs340/prolog/pokerhands.pl");
	//test1.TestQuery();
	
	LinkedList<String> solutions;
	LinkedList<PrologCard> testHand;

/*	// Creating the hand we want to test
	testHand = new LinkedList<PrologCard>();
	testHand.add( new PrologCard("7", "hearts"));
	testHand.add( new PrologCard("queen", "clubs"));
	testHand.add( new PrologCard("X"));

	// Querying the Prolog Database
	solutions = test1.HandQuery("one_pair", testHand);

	System.out.println("Here");
	// Enumerating the solutions
	for (String s: solutions) {
	    System.out.println(s);
	}

	System.out.println("THERE");
	//Now try the same thing with one_pair/2
	LinkedList<PrologCard> omitHand;

	// Creating the set of cards we want to omit
	omitHand = new LinkedList<PrologCard>();
	omitHand.add( new PrologCard("queen", "hearts"));


	// Querying the Prolog Database
	solutions = test1.HandQuery2("one_pair", testHand, omitHand);

	// Enumerating the solutions
	for (String s: solutions) {
	    System.out.println(s);
	}

	// Let's try a query that fails to make sure it works as expected. 
	LinkedList<PrologCard> testHand2 = new LinkedList<PrologCard>();
	testHand2.add( new PrologCard("ace", "hearts"));
	testHand2.add( new PrologCard("queen", "hearts"));
		   
	solutions = test1.HandQuery("one_pair", testHand2);
	for (String s: solutions) {
	    System.out.println(s);
	}

	// Now try one without variables
	LinkedList<PrologCard> testHand3 = new LinkedList<PrologCard>();
	testHand3.add( new PrologCard("ace", "hearts"));
	testHand3.add( new PrologCard("ace", "clubs"));
		   
	solutions = test1.HandQuery("one_pair", testHand3);
	for (String s: solutions) {
	    System.out.println("no variables");
	    System.out.println(s);
	}


	// Try HandQuery2
	LinkedList<PrologCard> testHand4 = new LinkedList<PrologCard>();
	testHand4.add( new PrologCard("3", "hearts"));
	testHand4.add( new PrologCard("X"));

	LinkedList<PrologCard> omitHand4 = new LinkedList<PrologCard>();
	omitHand4.add( new PrologCard("3", "spades"));


	
	solutions = test1.HandQuery2("one_pair", testHand4, omitHand4);
	
	for (String s: solutions) {
	    System.out.println("Inner loop");
	    System.out.println(s);
	}


	System.out.println("finshed!");
	
	LinkedList<PrologCard> testHand5 = new LinkedList<PrologCard>();
	testHand5.add( new PrologCard("ace", "spades"));
	testHand5.add( new PrologCard("ace", "hearts"));
	testHand5.add( new PrologCard("4", "hearts"));
	testHand5.add( new PrologCard("3", "spades"));
	testHand5.add( new PrologCard("2", "clubs"));
	//added
	 //testHand.add(new PrologCard("Y"));
	testHand5.add( new PrologCard("X"));
	 
	 // Querying the Prolog Database
	 //solutions = test1.HandQuery("one_pair", testHand);
	 
    // Enumerating the solutions
    //for (String s: solutions) {
    //    System.out.println(s);
    //}

    //Now try the same thing with one_pair/2
	LinkedList<PrologCard> omitHand5;
	
	 // Creating the set of cards we want to omit
	 omitHand5 = new LinkedList<PrologCard>();
	 omitHand5.add( new PrologCard("5", "spades"));
	 omitHand5.add( new PrologCard("7", "hearts"));
	 omitHand5.add( new PrologCard("8", "hearts"));
	 omitHand5.add( new PrologCard("9", "spades"));
	 omitHand5.add( new PrologCard("10", "clubs"));
	 omitHand5.add( new PrologCard("2", "diamonds"));
	 omitHand5.add( new PrologCard("3", "diamonds"));
	 omitHand5.add( new PrologCard("4", "diamonds"));
	 omitHand5.add( new PrologCard("5", "diamonds"));
	 omitHand5.add( new PrologCard("7", "diamonds"));
	 
	 solutions = test1.HandQuery2("one_pair", testHand5, omitHand5);
	 
	 // Enumerating the solutions
	 for (String s: solutions) {
	     System.out.println(s);
	 }*/
	 
	 LinkedList<PrologCard> testHand6 = new LinkedList<PrologCard>();
	 testHand6.add( new PrologCard("ace", "spades"));
	 testHand6.add( new PrologCard("ace", "hearts"));
	 
	solutions = test1.HandQuery("one_pair", testHand6);
	System.out.println(solutions.size());
	
	testHand6.remove(0);
	solutions = test1.HandQuery("one_pair", testHand6);
	System.out.println(solutions.size());
	
	
	// Close up the prolog interface.
	test1.destroy();
    }
}
