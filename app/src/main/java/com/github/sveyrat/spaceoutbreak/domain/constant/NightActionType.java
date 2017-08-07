package com.github.sveyrat.spaceoutbreak.domain.constant;

public enum NightActionType {
    /**
     * In the case where a player is paralyzed, we still need to know if its turn has been played or not,
     * this action represents the turn being played without any action.
     */
    NONE,
    MUTATE,
    KILL,
    PARALYSE,
    HEAL,
    INSPECT;
}
