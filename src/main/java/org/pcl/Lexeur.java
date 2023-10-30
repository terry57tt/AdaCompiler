package org.pcl;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.ArrayList;
import org.pcl.Token;

import org.pcl.structure.automaton.Graph;
import org.pcl.structure.automaton.InvalidStateExeception;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.AutomatonState;
import org.pcl.structure.automaton.TokenType;

public class Lexeur {
    private Automaton automaton;
    private Stream<Character> stream;
    private int lineNumber;
    private String currentToken;

    // Methode permettant d'initialiser le lexeur
    public Lexeur(String input) {
        this.automaton = Graph.create();
        this.stream = null;
        this.lineNumber = 1;
        this.currentToken = "";
    }

    public Lexeur(Automaton automaton, Stream<Character> stream) {
        this.automaton = Graph.create();
        this.stream = stream;
    }

    //Methode permettant de recuperer les tokens
    public ArrayList<Token> getTokens() throws IOException {
        ArrayList<Token> tokens = tokenize();
        for (Token token : tokens) {
            System.out.println(token);
        }
        return tokens;
    }

    //Methode verifiant si le caractere est un separateur
    public boolean isSeparator(char c) {
        String separator = " \n\t(){}[];,:.+-*/<>=";
        return separator.contains(String.valueOf(c));
    }

    //Cas particulier
    public boolean specificSeparator(char c) {
        String separator = "=<>";
        return separator.contains(String.valueOf(c));
    }

    public boolean tokenSeparator(char c) {
        String separator = "(){}[];,:.+-*/<>=";
        return separator.contains(String.valueOf(c));
    }

    public ArrayList<Token> tokenize() {
        ArrayList<Token> tokens = new ArrayList<>();
    
        stream.forEach(c -> {
            if (isSeparator(c)) {   
                if (c == '\n') {
                    this.lineNumber++;
                }
    
                if (!currentToken.isEmpty()) {
                    addToken(tokens, currentToken, this.lineNumber);
                }
                
                if (tokenSeparator(c)) {
                    tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), this.lineNumber));
                }

                this.currentToken = "";
                automaton.reset();
            } else {
                try {
                    this.currentToken += c;
                    automaton.advance(c);
                } catch (InvalidStateExeception e) {
                    System.out.println("2 Invalid state: " + e.getMessage());
                }
            }
        });
    
        if (!currentToken.isEmpty()) {
            addToken(tokens, currentToken, this.lineNumber);
        }
    
        return tokens;
    }

    public void addToken(ArrayList<Token> tokens, String currentToken, int lineNumber) {
        try {
            TokenType tokenType = automaton.getCurrentState().getTokenType();
            tokens.add(new Token(tokenType, currentToken, lineNumber));
        } catch (InvalidStateExeception e) {
            System.out.println("3 Invalid state: " + e.getMessage());
        }
    }
}