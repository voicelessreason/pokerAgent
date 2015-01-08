import java.util.ArrayList;


public class AgentTest {

	public static void main(String[] args)
	{
		PokerAgent pa = new PokerAgent("ARLEN","/home/astrausm/cs340/prolog/pokerhands.pl");
		pa.startGame();
		pa.playRound();
	}
}
