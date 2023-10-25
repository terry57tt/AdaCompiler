package org.pcl;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
public class FileHandler {

    /** Check if the path is valid and the file exists. */
    public static boolean isPathValid(String path) {
        Path chemin = Paths.get(path);
        return Files.exists(chemin);
    }

    /** Check is the extension of the file is valid.
     * ADA extension are [.ads .adb]*/
    public static boolean isExtensionValid(String path) {
        Path chemin = Paths.get(path);
        String fileName = chemin.getFileName().toString();
        String extension = getExtension(fileName);
        return (extension.equals("ads") || extension.equals("adb"));
    }

    /** Get the extension in a fileName */
    public static String getExtension(String fileName){
        String[] parties = fileName.split(".");
        return parties[parties.length-1];
    }

    /** Return the character stream of the file */
    public static Stream<Character> getCharacters(String path) throws IOException { //file can be a path or a file name
        //Stream<String> lines = new BufferedReader(new InputStreamReader(new FileInputStream(path), Charset.defaultCharset())).lines();
        //Stream<Character> characterStream = lines.flatMap(str -> str.chars().mapToObj(c -> (char) c));
        Path cheminPath = Paths.get(path);
        Stream<String> lines = Files.lines(cheminPath, StandardCharsets.UTF_8);
        Stream<Character> characterStream = lines.flatMap(str ->
                Stream.concat(str.chars().mapToObj(c -> (char) c), Stream.concat(Stream.of('\\'), Stream.of('n'))));
        return characterStream;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getCharacters("test.txt"));
    }

}
