
/* A card can be either an explicit card or a variable. */
public class PrologCard {
    private String Value;
    private String Suit;
    private String VariableName;
    private boolean IsVariable;
    
    public void setValue(String V) { Value = V; IsVariable = false;}
    public void setSuit(String S) {Suit = S; IsVariable = false; }
    public void setVariableName(String V) {VariableName = V; IsVariable = true; }
    public PrologCard(String V, String S) { Value = V; Suit = S; VariableName = ""; IsVariable = false;}
    public PrologCard(String V) { Value = ""; Suit = ""; VariableName = V; IsVariable = true;}
    public String getValue() { return Value;}
    public String getSuit() { return Suit;}
    public String getVariableName() { return VariableName; }
    public boolean isVariable() {return IsVariable;}
}
