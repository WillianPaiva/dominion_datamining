package universite.bordeaux.app.ReadersAndParser.Readers;

/**
 * class for all types of action that can make a player on each move 
 * @author mlfarfan
 *
 */
public final class LogElements {

    /**
     * @param line
     * @return boolean indicate if there is a Plays Move
     */
    public static boolean isPlaysMove(final String line) {
        return line.matches("(.*) plays (a|the|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return boolean indicate if there is a buys move
     */
    public static boolean isBuysMove(final String line) {
        return line.matches("(.*) buys (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return boolean indicate if there is the draws on the finish of the turn
     */
    public static boolean isDrawsLastAction(final String line) {
        return line.trim().matches("\\<span class\\=logonly\\>\\((.*)"
                                   + " draws: (.*)");
    }

    /**
     * @param line
     * @return indicate if the move draw and discard a same card
     */
    public static boolean isMoveDrawDiscardCard(final String line) {
        return line.trim().matches("(.*) draws and discard "
                                   + "(a|an|[0-9]+) \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if is the gains action
     */
    public static boolean isGainsAction(final String line) {
        return line.matches("(.*) gains (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if the double action discards and gains
     */
    public static boolean isActionDiscardsAndGains(final String line) {
        return line.matches("(.*) discards (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if these is a trashes
     */
    public static boolean isTrashes(final String line) {
        return line.matches("(.*) trashes (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if these is a  reveals
     */
    public static boolean isReveals(final String line) {
        return line.matches("(.*) reveals (a|an|[0-9]+)"
                            + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if there is a Trashing Action
     */
    public static boolean isTrashingAction(final String line) {
        return line.trim().matches("(.*) trashing (a|an|the|[0-9]+)"
                                   + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate id there is a Revealing Action
     */
    public static boolean isRevealingAction(final String line) {
        return line.trim().matches("(.*) revealing (a|an|[0-9]+)"
                                   + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if there is a Putting Action
     */
    public static boolean isPuttingAction(final String line) {
        return line.trim().matches("(.*) putting (a|the|an|[0-9]+)"
                                   + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if there is a Gaining Action
     */
    public static boolean isGainingAction(final String line) {
        return line.trim().matches("(.*) gaining "
                                   + "(a|the|another|an|[0-9]+)"
                                   + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if there is a Discarding Action
     */
    public static boolean isDiscardingAction(final String line) {
        return line.trim().matches("(.*) discarding"
                                   + " (a|an|[0-9]+)+"
                                   + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if there is a Playing Action
     */
    public static boolean isPlayingAction(final String line) {
        return line.trim().matches("(.*) playing "
                                   + "(a|an|[0-9]+)+ "
                                   + "\\<span class(.*)");
    }

    /**
     * @param line
     * @return indicate if there is a Drawing Action
     */
    public static boolean isDrawingAction(final String line) {
        return line.trim().matches("(.*) drawing (a|an|[0-9]+)+ "
                                   + " \\<span class(.*)");
    }

    /**
     * @param line
     * @return return the number of the action type.
     */
    public static ActionTypes type(final String line) {
        if (isPlaysMove(line)) {
            return ActionTypes.PLAYS;
        } else if (isBuysMove(line)) {
            return ActionTypes.BUYS;
        } else if (isDrawsLastAction(line)) {
            return ActionTypes.DRAWS_LAST_ACTION;
        } else if (isMoveDrawDiscardCard(line)) {
            return ActionTypes.DRAWS_DISCARD;
        } else if (isGainsAction(line)) {
            return ActionTypes.GAINS;
        } else if (isActionDiscardsAndGains(line)) {
            return ActionTypes.DISCARD_AND_GAINS;
        } else if (isTrashes(line)) {
            return ActionTypes.TRASHES;
        } else if (isReveals(line)) {
            return ActionTypes.REVEALS;
        } else if (isTrashingAction(line)) {
            return ActionTypes.TRASHING;
        } else if (isRevealingAction(line)) {
            return ActionTypes.REVEALING;
        } else if (isPuttingAction(line)) {
            return ActionTypes.PUTTING;
        } else if (isGainingAction(line)) {
            return ActionTypes.GAINING;
        } else if (isDiscardingAction(line)) {
            return ActionTypes.DISCARDING;
        } else if (isPlayingAction(line)) {
            return ActionTypes.PLAYING;
        } else if (isDrawingAction(line)) {
            return ActionTypes.DRAWING;
        } else {
            return ActionTypes.NOT_DEFINED;
        }
    }
}