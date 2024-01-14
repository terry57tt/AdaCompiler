package org.pcl.grammaire;

import org.pcl.Token;
import org.pcl.structure.automaton.TokenType;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class GrammarErrorUtility {


    /** Create tokens from a string */
    public static List<Token> fromString(String value, long currentline) {
        List<Token> tokens = Arrays.stream(value.split(" "))
                .map(String::trim)
                .map(Token::new).collect( Collectors.toCollection(ArrayList::new));
        tokens.forEach(token -> {
            token.setLineNumber(currentline);
            if (token.getValue().equalsIgnoreCase("ident")) {
                token.setType(TokenType.IDENTIFIER);
            } else if (token.getValue().equalsIgnoreCase("number")) {
                token.setType(TokenType.NUMBER);
                token.setValue("0");
            } else if (token.getValue().equalsIgnoreCase("character")) {
                token.setType(TokenType.CHARACTER);
                token.setValue("a");
            }
        });
        return tokens;
    }

    /** DeepClone tokens */
    public static ArrayList<Token> deepClone(ArrayList<Token> tokens) {
        ArrayList<Token> clone = new ArrayList<>();
        tokens.forEach(token -> {
            Token token1 = new Token(token.getValue());
            clone.add(token1);
            token1.setLineNumber(token.getLineNumber());
            token1.setType(token.getType());
        });
        return clone;
    }

    /** Generate all the alternate Grammars to be tried to find other errors */
    public static List<Grammar> generateGrammars(String value, Grammar g, long currentline) {
        List<Token> tokens = fromString(value, currentline);
        List<Grammar> grammars = new ArrayList<>();
        for (Token token : tokens) {
            grammars.add(Grammar.createGrammarError(g, 0, token));
             }
        return grammars;
    }

    public static void ProceedAnalysis(String value, Grammar g, long currentLine) {
        List<Grammar> grammars = generateGrammars(value, g, currentLine);
        PrintStream originalOut = System.out;
        int initialError = g.getTokensIndex();
        String output = "";
        int numberErrors = g.getNumberErrors();

        try {
            for (Grammar grammar : grammars) {
               //Redirect output to save them

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                System.setOut(printStream);
                grammar.getSyntaxTree();
                if (grammar.getTokensIndex() > g.getTokensIndex() && initialError < grammar.getTokensIndex()) {
                    output = outputStream.toString();
                    initialError = grammar.getTokensIndex();
                    numberErrors = grammar.getNumberErrors();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            System.setOut(originalOut);
        }
        System.out.println(output);
        g.setNumberErrors(g.getNumberErrors() + numberErrors);


    }

}
