package org.pcl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
public class FileHandler {

    /** Check if the path is valid and the file exists. */
    public static boolean isPathValid(String path) {
        Path chemin = Paths.get(path);
        return Files.exists(chemin);
    }

    /** Check is the extension of the file is valid.
     * ADA extension are [.ads .adb .ada]*/
    public static boolean isExtensionValid(String path) {
        Path chemin = Paths.get(path);
        String fileName = chemin.getFileName().toString();
        String extension = getExtension(fileName);
        return (extension.equals("ads") || extension.equals("adb")  || extension.equals("ada"));
    }

    /** Get the extension in a fileName */
    public static String getExtension(String fileName){
        String[] parties = fileName.split("\\.");
        return parties[parties.length-1];
    }


    /** Return only the name of the file remove the path part */
    public static String getFileName(String path) {
        Path chemin = Paths.get(path);
        return chemin.getFileName().toString();
    }

    /** Return the character stream of the file */
    public static Stream<Character> getCharacters(String path) throws FileNotFoundException { //file can be a path or a file name
        Stream<String> lines = new BufferedReader(new InputStreamReader(new FileInputStream(path), Charset.defaultCharset())).lines();
        return lines.flatMap(line -> line
                .concat(String.valueOf('\n'))
                .chars()
                .mapToObj(i -> (char) i));

    }

}
