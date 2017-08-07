package com.github.sveyrat.spaceoutbreak.dao.dto;

public enum RoundPhase {
    CAPTAIN_ELECTION,
    /*
     * Next step in the round is the night step.
     * This differs from the new step in that you don't need to start a new round.
     */
    NIGHT,
    NIGHT_AUTOPSY,
    VOTE,
    DAY_AUTOPSY,
    /*
     * The next step in the round is to create a new round and launch the night activity.
     * This differs from the night step in that you need to create a new round before launching the night view.
     */
    NEW_ROUND,
    /*
     * The game is over
     */
    END;
}
