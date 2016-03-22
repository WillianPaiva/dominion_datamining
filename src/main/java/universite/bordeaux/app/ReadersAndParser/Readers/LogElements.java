package universite.bordeaux.app.ReadersAndParser.Readers;

public final class LogElements {

    public static boolean isPlaysMove(final String line) {
        return line.matches("(.*) plays (a|the|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    public static boolean isBuysMove(final String line) {
        return line.matches("(.*) buys (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    public static boolean isDrawsLastAction(final String line) {
        return line.trim().matches("\\<span class\\=logonly\\>\\((.*)"
                                   + " draws: (.*)");
    }

    public static boolean isMoveDrawDiscardCard(final String line) {
        return line.trim().matches("(.*) draws and discard "
                                   + "(a|an|[0-9]+) \\<span class(.*)");
    }

    public static boolean isGainsAction(final String line) {
        return line.matches("(.*) gains (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    public static boolean isActionDiscardsAndGains(final String line) {
        return line.matches("(.*) discards (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    public static boolean existTrashes(final String line) {
        return line.matches("(.*) trashes (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    public static boolean isReveals(final String line) {
        return line.matches("(.*) reveals (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    public static boolean isTrashingAction(final String line) {
        return line.trim().matches("(.*) trashing (a|an|the|[0-9]+)"
                                   + " \\<span class(.*)");
    }

    public static boolean isRevealingAction(final String line) {
        return line.trim().matches("(.*) revealing (a|an|[0-9]+)"
                                   + " \\<span class(.*)");
    }

    public static boolean isPuttingAction(final String line) {
        return line.trim().matches("(.*) putting (a|the|an|[0-9]+)"
                                   + " \\<span class(.*)");
    }

    public static boolean isGainingAction(final String line) {
        return line.trim().matches("(.*) gaining "
                                   + "(a|the|another|an|[0-9]+)"
                                   + " \\<span class(.*)");
    }

    public static boolean isDiscardingAction(final String line) {
        return line.trim().matches("(.*) discarding"
                                   + " (a|an|[0-9]+)+"
                                   + " \\<span class(.*)");
    }

    public static boolean isPlayingAction(final String line) {
        return line.trim().matches("(.*) playing "
                                   + "(a|an|[0-9]+)+ "
                                   + "\\<span class(.*)");
    }

    public static boolean isDrawingAction(final String line) {
        return line.trim().matches("(.*) drawing (a|an|[0-9]+)+ "
                                  + " \\<span class(.*)");
    }
}
