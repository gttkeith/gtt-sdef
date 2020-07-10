package cfg;

import java.io.File;
import java.io.FileNotFoundException; 
import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;

/* project-specialised CSV reader */
public abstract class CSVReader {
    // configuration
    static final String SEPARATOR = ";";
    static final String TXT_SEPARATOR = ",";

    /**
     * parses a CSV file as a list of string arrays, according to the preset separator symbol
     *
     * @param filePath path to the file, relative to project root directory
     * @return a list of string arrays representing individual CSV values
     */
    public static List<String[]> parse(String filePath) {
        List<String[]> result = new LinkedList<String[]>();
        String separatorToUse = filePath.substring(filePath.length()-4, filePath.length()).equals(".txt") ? TXT_SEPARATOR : SEPARATOR;
        try {
            Scanner scan = new Scanner(new File(filePath));
            while (scan.hasNextLine()) {result.add(scan.nextLine().split(separatorToUse));}
            scan.close();
        } catch (FileNotFoundException e) {e.printStackTrace();}
        return result;
    }
}