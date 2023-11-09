import org.junit.jupiter.api.Test;
import org.pcl.structure.automaton.TokenType;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;

public class GraphTest {


    @Test
    public void graphSymbolTest() {
        Automaton automaton = Graph.create();
        String[] symbolKeys = {
                "access", "and", "begin", "else", "elsif", "end", "false", "for", "function", "if", "in", "is", "loop", "new", "not", "null",
                "or", "out", "procedure", "record", "rem", "return", "reverse", "then", "true", "type", "use", "while", "with"
        };
        for (String key : symbolKeys) {
            navigateWordHelper(key, automaton);
            assert automaton.isFinal(): "expected final state";
            assert automaton.getCurrentState().getTokenType() == TokenType.KEYWORD: "expected TokenType.KEYWORD got " + automaton.getCurrentState().getTokenType() + " instead for " + key;
            automaton.reset();
        }
    }

    @Test
    public void graphNumber() {
        Automaton automaton = Graph.create();
        String[] testKey = {
                "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "-1", "-2", "-3", "-4", "-5", "-6", "-7", "-8", "-9",
                "100", "200", "300", "400", "500", "600", "700", "800", "900",
                "42", "4138", "18381", "173", "123", "123456789", "1234567890", "12345678901", "123456789012", "1234567890123",
                "1413", "131385", "131313", "1213", "1234432"
        };
        for (String key : testKey) {

            navigateWordHelper(key, automaton);
            assert automaton.isFinal(): "expected final state";
            assert automaton.getCurrentState().getTokenType() == TokenType.NUMBER: "expected TokenType.NUMBER got " + automaton.getCurrentState().getTokenType() + " instead for " + key;
            automaton.reset();
        }
    }

    @Test
    public void graphOperator() {
        Automaton automaton = Graph.create();
        String[] testKey = {
                "=" , "/=", "<", "<=", ">", ">=", "+", "-", "*", "/"//, "rem", "and", "and then", "or", "or else"
        };
        for (String key : testKey) {
            navigateWordHelper(key, automaton);
            assert automaton.isFinal(): "expected final state";
            assert automaton.getCurrentState().getTokenType() == TokenType.OPERATOR : "expected operator TokenType.OPERATOR got " + automaton.getCurrentState().getTokenType() + " instead for " + key ;
            automaton.reset();
        }
    }

    @Test
    public void graphIdentifier() {
        Automaton automaton = Graph.create();
        String[] testKey = {
               "a", "A", "aA", "AA", "Aa", "a_", "A_", "a1", "A1", "a1_", "A1_", "a1_a1_a", "A1_a1_a", "a1_A1_a", "A1_A1_a", "a1_a1_A", "A1_a1_A", "a1_A1_A", "A1_A1_A",
                "a1111", "a_____", "A11111", "A_____", "a8a_8a8"
        };
        for (String key : testKey) {
            navigateWordHelper(key, automaton);
            assert automaton.isFinal(): "expected final state";
            assert automaton.getCurrentState().getTokenType() == TokenType.IDENTIFIER: "expected operator TokenType.IDENTIFER got " + automaton.getCurrentState().getTokenType() + " instead for " + key ;
            automaton.reset();
        }
    }

    @Test
    public void graphSeparator() {
        Automaton automaton = Graph.create();
        String[] testKey = {
                ";", ",", "(", ")", "{", "}", "[", "]", ".", ":", "::", ":="
        };
        for (String key : testKey) {
            navigateWordHelper(key, automaton);
            assert automaton.isFinal(): "expected final state";
            assert automaton.getCurrentState().getTokenType() == TokenType.SEPARATOR: "expected operator TokenType.OPERATOR got " + automaton.getCurrentState().getTokenType() + " instead for " + key ;
            automaton.reset();
        }
    }

    @Test
    public void graphInvalid() {
        Automaton automaton = Graph.create();
        String[] testKey = {
                ";aad", ",1", "(as", ")1zd", "1aead", "1a_", "6aaa_a1", "_aeadh", "Begin", "beGin", "begiN", "1313a1313", "1313.121.121", "ajiè'", ">==", "++", "-t", "%a1"
        };
        for (String key : testKey) {
            if (!navigateWordHelper(key, automaton)) {
                continue;
            }
            assert !automaton.isFinal();
            automaton.reset();
        }
    }

    public boolean navigateWordHelper(String word, Automaton automaton) {
        for (char c : word.toCharArray()) {
            automaton.advance(c);
        }
        return true;
    }
}

