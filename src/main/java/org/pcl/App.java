package org.pcl;


import java.util.ArrayList;

/** Entry point of the application. */
public class App {

    public static void main(String[] args) {
        FileHandler fileHandler = new FileHandler();
        ArrayList<String> filesToCompile = new ArrayList<>();

        if (args.length == 0) {
            System.out.println("No files to compile.\n" +
                    "Please enter the path of the files you want to compile with the command line argument -Pfiles=\"file1\"");
            System.exit(0);
        }

        for (String file: args) {
            if (fileHandler.isPathValid(file)) {
                if (fileHandler.isExtensionValid(file))
                    filesToCompile.add(file);
                else {
                    System.out.println("- Invalid extension: " + file);
                }
            } else {
                System.out.println("- Invalid path: " + file);
            }
        }

        if (filesToCompile.isEmpty()) {
            System.out.println("No valid files to compile.\n" +
                    "Exit program.");
            System.exit(0);
        }

        for (String file: filesToCompile) {
            System.out.println("- Compiling file: " + file);
            //TODO go to the lexeur
        }
    }

}