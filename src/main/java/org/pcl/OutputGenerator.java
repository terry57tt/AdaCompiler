package org.pcl;

import java.io.FileWriter;
import java.io.IOException;

/** Write the output specified in the corespond file
 * HOW TO USE IT
 * Set the NumberOfTabulation you want with the function increment decrement and reset
 * it is recommended to use decrement and increment and not reset because you can have more than 1 tabulation
 * Than write for new Line or writeNoNewLine if you don't want them */
public class OutputGenerator {

    private static String filename = "output.s";

    public static final String NEW_LINE = System.lineSeparator();

    private static int NumberOfTabulation = 0;

    /** Write the output in the file and add a New Line*/
    public static void write(String value) throws IOException {
        FileWriter fileWriter = new FileWriter(filename, true);
        fileWriter.write("\t".repeat(NumberOfTabulation) + value + NEW_LINE);
        fileWriter.close();
    }

    /** Write the output in the file without newLine */
    public static void writeNoNewLine(String value) throws IOException {
        FileWriter fileWriter = new FileWriter(filename, true);
        fileWriter.write("\t".repeat(NumberOfTabulation) + value);
        fileWriter.close();
    }

    /** Reset the file to write the output */
    public static void resetFile() throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write("");
        fileWriter.close();
    }

    public static void incrementTabulation() {
        NumberOfTabulation++;
    }

    public static void decrementTabulation() {
        NumberOfTabulation--;
    }

    public static void resetTabulation() {
        NumberOfTabulation = 0;
    }

}
