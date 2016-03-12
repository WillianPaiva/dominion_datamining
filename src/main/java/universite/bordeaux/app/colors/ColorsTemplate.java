package universite.bordeaux.app.colors;

/**
 * simple utility class to define some color for the text output.
 * @author ebayol
 */
public final class ColorsTemplate {
    /**
     * setting the constructor in private.
     */
    private ColorsTemplate() { }
    /**
     * Reset the color output.
     */
    public static final String ANSI_RESET = "\u001B[0m";
    /**
     * set the color output to black.
     */
    public static final String ANSI_BLACK = "\u001B[30m";
    /**
     * set the color output to red.
     */
    public static final String ANSI_RED = "\u001B[31m";
    /**
     * set the color output to green.
     */
    public static final String ANSI_GREEN = "\u001B[32m";
    /**
     * set the color output to yellow.
     */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /**
     * set the color output to blue.
     */
    public static final String ANSI_BLUE = "\u001B[34m";
    /**
     * set the color output to purple.
     */
    public static final String ANSI_PURPLE = "\u001B[35m";
    /**
     * set the color output to cyan.
     */
    public static final String ANSI_CYAN = "\u001B[36m";
    /**
     * set the color output to white.
     */
    public static final String ANSI_WHITE = "\u001B[37m";
}
