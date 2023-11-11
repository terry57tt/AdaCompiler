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
    public boolean isPathValid(String path) {
        Path chemin = Paths.get(path);
        return Files.exists(chemin);
    }

    /** Check is the extension of the file is valid.
     * ADA extension are [.ads .adb]*/
    public boolean isExtensionValid(String path) {
        Path chemin = Paths.get(path);
        String fileName = chemin.getFileName().toString();
        String extension = getExtension(fileName);
        if (extension.equals("ads") || extension.equals("adb")){
            return true;
        }
        return false;
    }

    /** Get the extension in a fileName */
    public String getExtension(String fileName){
        String[] parties = fileName.split(".");
        return parties[parties.length-1];
    }

    /** Return the character stream of the file */
    public Stream<Character> getCharacters(String file) throws IOException { //file can be a path or a file name
        Stream<String> lines = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charset.defaultCharset())).lines();
        Stream<Character> characterStream = lines.flatMap(str -> str.chars().mapToObj(c -> (char) c));
        return characterStream;
    }

}
