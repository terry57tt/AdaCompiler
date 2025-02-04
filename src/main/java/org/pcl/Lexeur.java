package org.pcl;

import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.InvalidStateException;
import org.pcl.structure.automaton.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Lexeur {

    private static final int MAX_LEN_ID = 1000;

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
        String separator = ".-/=<>:\"'";
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
            if (currentToken.length() >= MAX_LEN_ID && automaton.getCurrentState().getTokenType() == TokenType.IDENTIFIER) {
                handlingDataError();
                print_error(lineStack, c, characterList, i, "Identifiant too long ", false);
            }
            lineStack.add(c);
            if (isSeparator(c)) {

                /* case ' is a separator */
                boolean isVal = false;
                if(this.currentToken.equalsIgnoreCase("character")) {
                    isVal = true;
                }

                /* add current token to the list */
                if (!this.currentToken.isEmpty()) {
                    addToken(tokens, this.currentToken, this.lineNumber);
                }

                if (c == '\n') {
                    this.lineNumber++;
                    lineStack.clear();
                }

                if(tokenSeparator(c)) {
                    if (isVal) {
                        /* special case for character ' val */
                        tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), this.lineNumber));
                        isVal = false;
                    }
                    else if(specificSeparator(c)) i = treatCompoundSeparator(tokens, c, i, characterList, lineStack);
                    else tokens.add(new Token(TokenType.SEPARATOR, String.valueOf(c), this.lineNumber));
                }

                this.currentToken = "";
                automaton.reset();
            } else {
                try {
                    this.currentToken += c;
                    automaton.advance(Character.toLowerCase(c));
                } catch (InvalidStateException e) {
                    String tokenContent = this.currentToken.substring(0, currentToken.length() - 1);
                    if(tokenContent.length()!=0) {
                        addToken(tokens, tokenContent, this.lineNumber);
                    }
                    handlingDataError();
                    print_error(lineStack, c, characterList, i, "invalid character ", true);
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
        if (currentToken.equalsIgnoreCase("CHARACTER")) {
            tokenType = TokenType.KEYWORD;
        }
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

        /* case of Strings */
        if (String.valueOf(c).equals("\"")) {

            while (((i + 1) < characterList.size()) && (characterList.get(i + 1) != '\n') && ((i + 2) < characterList.size()) && (characterList.get(i + 2) != '\n')
                    && ((characterList.get(i + 1) != '\"') || (characterList.get(i + 2) == '\"') || (characterList.get(i) == '\"'))) {
                separator += characterList.get(i + 1);
                i++;
            } // when leaving the loop, separator do not contain the second '"',  characterList.get(i + 1) = '"'
            separator += characterList.get(i + 1); //add to separator the closing quote

            // case : end of line or file before the string close
            if ((characterList.get(i + 1) == '\n') || ((i + 1) > characterList.size())) { //error string unfinished
                separator = removeEscapeCharacter(separator);
                tokens.add(new Token(TokenType.STRING, separator, this.lineNumber));
                System.out.println("unfinished string");
                return i;
            }
            // case : characterList.get(i+1) is the last character of the line
            else if (((i + 2) > characterList.size()) && (characterList.get(i + 2) == '\n')) {
                i++; // characterList.get(i) = '\"'
                separator += characterList.get(i);
                separator = removeEscapeCharacter(separator);
                tokens.add(new Token(TokenType.STRING, separator, this.lineNumber));
                return i + 1;
            } else {
                separator = removeEscapeCharacter(separator);
                tokens.add(new Token(TokenType.STRING, separator, this.lineNumber));
                return i + 1;
            }
        }

        /* case single character : everything between ' is considered as a character */
        if(String.valueOf(c).equals("'")) {
            if((i + 1 < characterList.size()) && (characterList.get(i + 1) != '\n' && ((i + 2) < characterList.size()) && (characterList.get(i + 2) != '\n'))
             && (characterList.get(i + 2) == '\'') && (characterList.get(i) == '\'')){
                separator = characterList.get(i + 1) + "";
                tokens.add(new Token(TokenType.CHARACTER, separator, this.lineNumber));
                return i + 2;
        }} else 

        // case of characters or strings with ' --> the same as ", with count
        if (String.valueOf(c).equals("'")){

            while ((i + 1 < characterList.size()) && (characterList.get(i + 1) != '\n' && ((i + 2) < characterList.size()) && (characterList.get(i + 2) != '\n'))
                    && ((characterList.get(i + 1) != '\'') || (characterList.get(i + 2) == '\'') || (characterList.get(i) == '\''))) {
                separator += characterList.get(i + 1);
                i++;
            } // when leaving the loop, separator do not contain the second '"',  characterList.get(i + 1) = '"'
            separator += characterList.get(i + 1); //add to separator the closing quote

            // case : end of line or file before the string close
            if (characterList.get(i + 1) == '\n' || i + 1 > characterList.size()) { //error string unfinished
                if (separator.length() <= 3) {
                    separator = removeEscapeCharacter(separator);
                    tokens.add(new Token(TokenType.CHARACTER, separator, this.lineNumber));
                }
                else {
                    separator = removeEscapeCharacter(separator);
                    tokens.add(new Token(TokenType.STRING, separator, this.lineNumber));
                }
                System.out.println("unfinished string");
                return i;
            }
            // case : characterList.get(i+1) is the last character of the line
            else if (i + 2 > characterList.size() && characterList.get(i + 2) == '\n') {
                i++; // characterList.get(i) = '\"'
                separator += characterList.get(i);
                if (separator.length() <= 3) {
                    separator = removeEscapeCharacter(separator);
                    tokens.add(new Token(TokenType.CHARACTER, separator, this.lineNumber));
                }
                else {
                    separator = removeEscapeCharacter(separator);
                    tokens.add(new Token(TokenType.STRING, separator, this.lineNumber));
                }
                return i + 1;

            } else {
                if (separator.length() <= 3) {
                    separator = removeEscapeCharacter(separator);
                    tokens.add(new Token(TokenType.CHARACTER, separator, this.lineNumber));
                }
                else {
                    separator = removeEscapeCharacter(separator);
                    tokens.add(new Token(TokenType.STRING, separator, this.lineNumber));
                }
                return i + 1;
            }
        }

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
            case ".." -> {
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

    private void handlingDataError() {
        automaton.reset();
        number_errors++;
        this.currentToken = "";
    }

    private void print_error(List<Character> lineStack, char c, List<Character> characterList, int i, String message, boolean displayLocalisator) {
        lineStack.remove(lineStack.size() - 1);
        System.out.print( fileName + ':' + lineNumber + ':' + (lineStack.size()+1) + ": " +
                ColorAnsiCode.ANSI_RED + "error: " + ColorAnsiCode.ANSI_RESET + message +
                "'" + ColorAnsiCode.ANSI_RED + c + ColorAnsiCode.ANSI_RESET + "'\n" +
                lineStack.stream().map(String::valueOf).collect(Collectors.joining()));

        System.out.print(c);

        printRestOfLine(characterList, i + 1);
        if (displayLocalisator)
            System.out.println("\n" + " ".repeat(lineStack.size()) + ColorAnsiCode.ANSI_GREEN + "^"
                    + ColorAnsiCode.ANSI_RESET + "\n");
        else
            System.out.println("\n");

        lineStack.add(c);
    }

}
