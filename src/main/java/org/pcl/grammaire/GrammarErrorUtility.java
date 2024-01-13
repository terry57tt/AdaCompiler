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
    public static List<Grammar> generateGrammars(String value, Grammar g, Callable<Void> callback, long currentline) {
        List<Token> tokens = fromString(value, currentline);
        List<Grammar> grammars = new ArrayList<>();
        for (Token token : tokens) {
            grammars.add(Grammar.createGrammarError(g, 0, token, callback));
             }
        return grammars;
    }

    public static void ProceedAnalysis(String value, Grammar g, Callable<Void> callback, long currentLine) {
        System.out.println("proceed analysis");
        List<Grammar> grammars = generateGrammars(value, g, callback, currentLine);
        PrintStream originalOut = System.out;
        int initialError = g.getIndexFirstError();
        String output = "";
        try {
            for (Grammar grammar : grammars) {
                System.out.println(grammar.getTokens().get(grammar.getIndexFirstError()));
                //Redirect output to save them

                //ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                //PrintStream printStream = new PrintStream(outputStream);
                //System.setOut(printStream);
                //grammar.getCallback().call();
                grammar.getSyntaxTree();
                //System.out.println("finish callback");
                if (grammar.getIndexFirstError() > initialError) {
                    //output = outputStream.toString();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            //System.setOut(originalOut);
        }

        System.out.println(output);

    }

}
