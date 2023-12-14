package org.pcl;


import org.pcl.grammaire.Grammar;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;
import org.pcl.structure.tree.SyntaxTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.pcl.ColorAnsiCode.ANSI_RED;
import static org.pcl.ColorAnsiCode.ANSI_RESET;


import java.io.IOException;
import java.util.ArrayList;

import org.pcl.FileHandler;
import org.pcl.Lexeur;
import org.pcl.Token;

import org.pcl.grammaire.Grammar;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;


import org.pcl.structure.automaton.TokenType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

/** Entry point of the application. */
public class App {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println(ANSI_RED + "No files to compile.\n" +
                    "Please enter the path of the files you want to compile with the command line argument -Pfiles=\"file1\""
            + ANSI_RESET);
            System.exit(0);
        }



        for (String file: args) {
            System.out.println();
            if (!FileHandler.isPathValid(file)) {
                System.out.println(ANSI_RED + "- Invalid path: " + file + ANSI_RESET + "\n");
                continue;
            }
            if (!FileHandler.isExtensionValid(file)) {
                    System.out.println(ANSI_RED + "- Invalid extension: " + file + ANSI_RESET + "\n");
                    continue;
            }

            System.out.println("- Compiling file: " + file + "\n");

            Lexeur lexeur = new Lexeur(Graph.create(), FileHandler.getCharacters(file), file);
            ArrayList<Token> tokens = lexeur.getTokens();
            if (lexeur.getNumber_errors() != 0) {
                System.out.println( ANSI_RED + lexeur.getNumber_errors() + " lexical error" +
                        ((lexeur.getNumber_errors() > 1) ? "s": "") + " generated" +
                        ANSI_RESET);
            }

            System.out.println();
            for (Token token: tokens) {
                System.out.println(token);
            }
            //TODO
        }
        String file = "demo/SyntaxError/syntaxError2.ada";
        Automaton automaton = Graph.create();
        Stream<Character> stream = FileHandler.getCharacters(file);

        Lexeur lexeur = new Lexeur(automaton, stream, file);
        
        ArrayList<Token> tokens = lexeur.tokenize();

        Grammar grammar = new Grammar(tokens);
        SyntaxTree tree = grammar.getSyntaxTree();
        System.out.println(tokens);
    }

}