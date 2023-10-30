package org.pcl;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.ArrayList;
import org.pcl.structure.automaton.Graph;
import org.pcl.structure.automaton.InvalidStateException;
import org.pcl.structure.automaton.Automaton;
import java.util.List;
import org.pcl.structure.automaton.TokenType;


public class Lexeur {
    private Automaton automaton;
    private Stream<Character> stream;
    private int lineNumber;
    private String currentToken;
 
    public Lexeur(String input) {
        this.automaton = Graph.create();
        this.stream = null;
        this.lineNumber = 1;
        this.currentToken = "";
    }

    public Lexeur(Automaton automaton, Stream<Character> stream) {
        this.automaton = Graph.create();
        this.stream = stream;
        this.lineNumber = 1;
        this.currentToken = "";
    }

    public ArrayList<Token> getTokens() throws IOException {
        ArrayList<Token> tokens = tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        return tokens;
    }

    public boolean isSeparator(char c) {
        String separator = " \n\t(){}[];,:.+-*/<>=\"";
        return separator.contains(String.valueOf(c));
    }

    
    public boolean specificSeparator(char c) {
        String separator = "-/=<>:";
        return separator.contains(String.valueOf(c));
    }
    
    public boolean tokenSeparator(char c) {
        String separator = "(){}[];,:.+-*/<>=";
        return separator.contains(String.valueOf(c));
    }

    public ArrayList<Token> tokenize() throws InvalidStateException {
        ArrayList<Token> tokens = new ArrayList<>();

        List<Character> characterList = stream.collect(Collectors.toList());

        for (int i = 0; i < characterList.size(); i++) {

            char c = characterList.get(i);

            if (isSeparator(c)) {    
                if (c == '\n') {
                    this.lineNumber++;
                }
    
                if (!this.currentToken.isEmpty()) {
                    addToken(tokens, this.currentToken, this.lineNumber);
                }
                
                if(tokenSeparator(c)) {
                    if(specificSeparator(c)) i = treatCompoundSeparator(tokens, c, i, characterList);
                    else tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), this.lineNumber));
                }

                this.currentToken = "";
                automaton.reset();
            } else {
                try {
                    this.currentToken += c;
                    automaton.advance(c);
                } catch (InvalidStateException e) {
                    //System.out.println("Invalid state: " + e.getMessage());
                }
            }
        };
    
        if (!this.currentToken.isEmpty()) {
            addToken(tokens, this.currentToken, this.lineNumber);
        }
    
        return tokens;
    }

    public void addToken(ArrayList<Token> tokens, String currentToken, int lineNumber) {
        TokenType tokenType = automaton.getCurrentState().getTokenType();
        tokens.add(new Token(tokenType, currentToken, lineNumber));
    }

    public int treatCompoundSeparator(ArrayList<Token> tokens, char c, int i, List<Character> characterList) {
        
        String separator;

        /* case end of file */
        if(i + 1 < characterList.size()) separator = String.valueOf(c) + String.valueOf(characterList.get(i + 1));
        else return i;

        switch(separator) {
            case "--":
                while(i + 1 < characterList.size() && characterList.get(i + 1) != '\n') {
                    i++;
                }
                this.lineNumber++;
                return i;
            case "/=":
                tokens.add(new Token(TokenType.SEPARATOR, separator, this.lineNumber));
                return i+1;
            case "<=":
                tokens.add(new Token(TokenType.SEPARATOR, separator, this.lineNumber));
                return i+1;
            case ">=":
                tokens.add(new Token(TokenType.SEPARATOR, separator, this.lineNumber));
                return i+1;
            case ":=":
                tokens.add(new Token(TokenType.SEPARATOR, separator, this.lineNumber));
                return i+1;
            default:
                tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), this.lineNumber));
                return i;
        }
    }
}