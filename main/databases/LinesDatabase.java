package oop.ex6.main.databases;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class that only contains static methods and fields.
 * is the database of code lines read from path.
 */
public class LinesDatabase {
    public static final String EMPTY_LINE_REGEX = "^\\s*$";
    public static final Pattern emptyLine = Pattern.compile(EMPTY_LINE_REGEX);
    public static final String COMMENT_PREFIX_REGEX = "^//.*$";
    private static final Pattern commentPrefix = Pattern.compile(COMMENT_PREFIX_REGEX);
    public static final String BAD_PATH_ERR_MSG = "Bad path, please check path and try again";
    static List<String> lines;

    /**
     * reads lines of code from path and stores in database
     *
     * @param path path of code
     * @throws IOException if problem reading from file
     */
    public static void readLines(String path) throws IOException {
        lines = new ArrayList<>();
        Matcher m;
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line = reader.readLine();
            while (line != null) {
                m = emptyLine.matcher(line);
                if (!m.matches() && !commentPrefix.matcher(line).find()) {
                    lines.add(line);
                }
                // read next line
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new IOException(BAD_PATH_ERR_MSG);
        }
    }

    /**
     * gets line from database using line number as key
     *
     * @param lineNum number of line to get
     * @return line of code
     */
    public static String getLine(int lineNum) {
        return lines.get(lineNum);
    }

    /**
     * gets total amount of lines of code
     *
     * @return number of lines
     */
    public static int getLinesNum() {
        return lines.size();
    }
}
