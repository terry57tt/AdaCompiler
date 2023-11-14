package org.pcl;

import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.InvalidStateException;
import org.pcl.structure.automaton.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Lexeur {
    private final Automaton automaton;
    private final Stream<Character> stream;
    private int lineNumber;
    private String currentToken;

    private final String fileName;

    private int number_errors;

    public Lexeur(Automaton automaton, Stream<Character> stream, String path) {
        this.automaton = automaton;
        this.stream = stream;
        this.lineNumber = 1;
        this.currentToken = "";
        this.number_errors = 0;
        this.fileName = FileHandler.getFileName(path);
    }

    public ArrayList<Token> getTokens() {
        resetState();
        return tokenize();
    }

    private void resetState() {
        this.lineNumber = 1;
        this.currentToken = "";
        this.number_errors = 0;
        this.automaton.reset();
    }

    public boolean isSeparator(char c) {
        String separator = " \n\t();,:.+-*/<>='";
        return separator.contains(String.valueOf(c));
    }


    public boolean specificSeparator(char c) {
        String separator = "-/=<>:'";
        return separator.contains(String.valueOf(c));
    }

    public boolean tokenSeparator(char c) {
        String separator = "();,:.+-*/<>='";
        return separator.contains(String.valueOf(c));
    }

    public ArrayList<Token> tokenize() throws InvalidStateException {
        ArrayList<Token> tokens = new ArrayList<>();

        List<Character> characterList = stream.collect(Collectors.toList());
        List<Character> lineStack = new ArrayList<>();
        for (int i = 0; i < characterList.size(); i++) {
            char c = characterList.get(i);
            lineStack.add(c);
            if (isSeparator(c)) {
                if (!this.currentToken.isEmpty()) {
                    addToken(tokens, this.currentToken, this.lineNumber);
                }

                if (c == '\n') {
                    this.lineNumber++;
                    lineStack.clear();
                }

                if(tokenSeparator(c)) {
                    if(specificSeparator(c)) i = treatCompoundSeparator(tokens, c, i, characterList, lineStack);
                    else tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), this.lineNumber));
                }

                this.currentToken = "";
                automaton.reset();
            } else {
                try {
                    this.currentToken += c;
                    automaton.advance(c);
                } catch (InvalidStateException e) {
                    automaton.reset();
                    lineStack.remove(lineStack.size() - 1);
                    number_errors++;
                    System.out.print( fileName + ':' + lineNumber + ':' + (lineStack.size()+1) + ": " +
                            ColorAnsiCode.ANSI_RED + "error: " + ColorAnsiCode.ANSI_RESET + "invalid character " +
                            "'" + ColorAnsiCode.ANSI_RED + c + ColorAnsiCode.ANSI_RESET + "'\n" +
                            lineStack.stream().map(String::valueOf).collect(Collectors.joining()));

                    System.out.print(c);

                    printRestOfLine(characterList, i + 1);

                    System.out.println("\n" + " ".repeat(lineStack.size()) + ColorAnsiCode.ANSI_GREEN + "^"
                            + ColorAnsiCode.ANSI_RESET + "\n");

                    lineStack.add(c);
                    this.currentToken = "";
                }
            }
        }
        if (!this.currentToken.isEmpty()) {
            addToken(tokens, this.currentToken, this.lineNumber);
        }

        return tokens;
    }

    public void addToken(ArrayList<Token> tokens, String currentToken, int lineNumber) {
        TokenType tokenType = automaton.getCurrentState().getTokenType();
        tokens.add(new Token(tokenType, currentToken, lineNumber));
    }

    /** Print the rest of the line for the error display */
    public void printRestOfLine(List<Character> characters, int index) {
        while (index < characters.size() && characters.get(index) != '\n') {
            System.out.print(characters.get(index));
            index++;
        }
    }

    public int treatCompoundSeparator(ArrayList<Token> tokens, char c, int i, List<Character> characterList, List<Character> lineStack) {

        String separator;
        separator = "" + c;

        /* case end of file */
        if (i + 1 < characterList.size()) separator += String.valueOf(characterList.get(i + 1));
        else return i;

        return switch (separator) {
            case "--" -> {
                while (i + 1 < characterList.size() && characterList.get(i + 1) != '\n') {
                    i++;
                }
                yield i;
            }
            case "/=", "<=", ">=", ":=" -> {
                tokens.add(new Token(TokenType.SEPARATOR, separator, this.lineNumber));
                yield i + 1;
            }
            default -> {
                tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), this.lineNumber));
                yield i;
            }
        };
    }

    // remove the escape character of a string
    public static String removeEscapeCharacter(String separator) {
        // case "
        if(separator.charAt(0) == '\"') {
            String string = "\"";
            for (int i = 1; i < separator.length() - 1; i ++) {
                if(i == 1 || !(separator.charAt(i) == '\"' && separator.charAt(i - 1) == '\"')) {
                    string += separator.charAt(i);
                }
            }
            string += "\"";
            return string;
        }

        // case '
        if(separator.charAt(0) == '\'') {
            String string = "\'";
            for (int j = 1; j < separator.length() - 1; j ++) {
                if(j == 1 || !(separator.charAt(j) == '\'' && string.charAt(string.length() - 1) == '\'')) {
                    string += separator.charAt(j);
                }
            }
            string += "\'";
            return string;
        }
        else {
            System.out.println("error in string : do not begin with quotes");
            return null;
        }

    }

    public int getNumber_errors() {
        return number_errors;
    }
}
