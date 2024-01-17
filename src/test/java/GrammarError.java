import org.junit.jupiter.api.Test;
import org.pcl.Token;
import org.pcl.grammaire.Grammar;
import org.pcl.grammaire.GrammarErrorUtility;
import org.pcl.structure.automaton.TokenType;

import java.util.ArrayList;
import java.util.List;

public class GrammarError {

    @Test
    public void utilityClass() {
        String values = "number ident character ehfhua ahudhuad";
        List<Token> tokens = GrammarErrorUtility.fromString(values, -1);
        assert tokens.size() == 5;
        assert tokens.get(0).getType().equals(TokenType.NUMBER);
        assert tokens.get(1).getType().equals(TokenType.IDENTIFIER);
        assert tokens.get(2).getType().equals(TokenType.CHARACTER);
        assert tokens.get(3).getValue().equals("ehfhua");
        assert tokens.get(4).getValue().equals("ahudhuad");
    }

    @Test
    public void createGrammarError() {
        ArrayList<Token> tokens = (ArrayList<Token>) GrammarErrorUtility.fromString("number ident character ehfhua ahudhuad", -1);
        assert tokens.size() == 5;
        int decal = 2;
        Grammar g = new Grammar(tokens);
        Grammar g2 = Grammar.createGrammarError(g, decal, new Token("."), "");


        assert !g.getTokens().get(decal).equals(g2.getTokens().get(decal));
    }

}
