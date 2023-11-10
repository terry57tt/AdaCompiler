package org.pcl;


import org.pcl.structure.automaton.Graph;

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
    }

}