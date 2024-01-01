package org.pcl;


import org.pcl.grammaire.Grammar;
import org.pcl.grammaire.Grammar_ast;
import org.pcl.ig.PClWindows;
import org.pcl.structure.automaton.Automaton;
import org.pcl.structure.automaton.Graph;
import org.pcl.structure.tree.Node;
import org.pcl.structure.tree.SyntaxTree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import static org.pcl.ColorAnsiCode.ANSI_RED;
import static org.pcl.ColorAnsiCode.ANSI_RESET;

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

            Grammar_ast grammar = new Grammar_ast(tokens);
            SyntaxTree tree = grammar.getSyntaxTree();


            System.out.println();
            new PClWindows(tokens, tree, !grammar.error).start();
            if (grammar.error) {
                System.out.println(ANSI_RED + "Analysis Syntax failed, no tree to display" + ANSI_RESET);
            }
            if (lexeur.getNumber_errors() != 0) {
                System.out.println( ANSI_RED + lexeur.getNumber_errors() + " lexical error" +
                        ((lexeur.getNumber_errors() > 1) ? "s": "") + " generated" +
                        ANSI_RESET);
            }
            if (grammar.error) {
                System.out.println(ANSI_RED + grammar.getNumberErrors() + " syntax error" +
                        ((grammar.getNumberErrors() > 1) ? "s": "") + " generated" +
                        ANSI_RESET);
            }

        }
    }


}