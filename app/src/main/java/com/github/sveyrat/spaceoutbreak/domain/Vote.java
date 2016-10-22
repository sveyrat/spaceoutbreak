package com.github.sveyrat.spaceoutbreak.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "vote")
public class Vote {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Round round;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Player voter;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = true)
    private Player votedFor;

    public Vote() {
    }

    public Vote(Round round, Player voter, Player votedFor) {
        this.round = round;
        this.voter = voter;
        this.votedFor = votedFor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public Player getVoter() {
        return voter;
    }

    public void setVoter(Player voter) {
        this.voter = voter;
    }

    public Player getVotedFor() {
        return votedFor;
    }

    public void setVotedFor(Player votedFor) {
        this.votedFor = votedFor;
    }
}
