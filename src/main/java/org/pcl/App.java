package org.pcl;


import org.pcl.grammaire.Grammar;
import org.pcl.structure.automaton.Graph;
import org.pcl.structure.tds.Symbol;
import org.pcl.structure.tds.SymbolType;
import org.pcl.structure.tds.Tds;
import org.pcl.structure.tds.Semantic;
import org.pcl.structure.tree.SyntaxTree;
import org.pcl.ig.PClWindows;


import java.io.IOException;
import java.util.ArrayList;

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

            Lexeur lexeur = new Lexeur(Graph.create(), FileHandler.getCharacters(file), FileHandler.getFileName(file));
            ArrayList<Token> tokens = lexeur.getTokens();

            Grammar grammar = new Grammar(tokens, FileHandler.getFileName(file));
            SyntaxTree tree = grammar.getSyntaxTree();
            grammar.createAST();
            if (!grammar.error) {
                grammar.nameNodes();
            }
            tree = grammar.ast;

            //if (!grammar.error)
            //new PCLWindows(tokens, tree,!grammar.error).start();

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

            if (grammar.error || lexeur.getNumber_errors() != 0) {
                return;
            }

            Semantic semantic = new Semantic(tree);

            semantic.getGlobalTds().displayWithChild();
        }
    }


}