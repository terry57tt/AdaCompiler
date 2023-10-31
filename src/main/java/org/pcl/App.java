package org.pcl;


import org.pcl.structure.automaton.Graph;

import java.io.IOException;

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
            if (!FileHandler.isPathValid(file)) {
                System.out.println(ANSI_RED + "- Invalid path: " + file + ANSI_RESET + "\n");
                continue;
            }
            if (!FileHandler.isExtensionValid(file)) {
                    System.out.println(ANSI_RED + "- Invalid extension: " + file + ANSI_RESET + "\n");
                    continue;
            }

            System.out.println("- Compiling file: " + file + "\n");

            new Lexeur(Graph.create(), FileHandler.getCharacters(file), file).getTokens();

            //TODO
        }
    }

}