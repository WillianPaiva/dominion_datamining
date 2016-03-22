package universite.bordeaux.app.ReadersAndParser.Readers;

public enum ActionTypes {

    PLAYS(1),
    PLAYING(2),
    BUYS(3),
    DRAWS_LAST_ACTION(4),
    DRAWS_DISCARD(5),
    DRAWING(6),
    GAINS(7),
    GAINING(8),
    DISCARD_AND_GAINS(9),
    TRASHES(10),
    TRASHING(11),
    REVEALS(12),
    REVEALING(13),
    PUTTING(14),
    DISCARDING(15),
    NOT_DEFINED(16);




    private int value;

    private ActionTypes(final int value) { this.value = value;}

};
