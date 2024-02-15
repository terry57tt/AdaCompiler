package org.pcl;

import java.io.FileWriter;
import java.io.IOException;

/** Write the output specified in the corespond file */
public class OutputGenerator {

    private static String filename = "output.s";


    /** Write the output in the file*/
    public static void write(String value) throws IOException {
        FileWriter fileWriter = new FileWriter(filename, true);
        fileWriter.write(value);
        fileWriter.close();
    }

    /** Reset the file to write the output */
    public static void resetFile() throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write("");
        fileWriter.close();

    }

}
