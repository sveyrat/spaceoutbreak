package com.github.sveyrat.spaceoutbreak.domain;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@DatabaseTable(tableName = "game")
public class Game {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(canBeNull = false)
    private Date creationDate;

    @ForeignCollectionField
    private ForeignCollection<Player> players;

    @ForeignCollectionField(orderColumnName = "order")
    private ForeignCollection<Round> rounds;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Player captain;

    public Game() {
        this.creationDate = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ForeignCollection<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ForeignCollection<Player> players) {
        this.players = players;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public ForeignCollection<Round> getRounds() {
        return rounds;
    }

    public void setRounds(ForeignCollection<Round> rounds) {
        this.rounds = rounds;
    }

    public Player getCaptain() {
        return captain;
    }

    public void setCaptain(Player captain) {
        this.captain = captain;
    }

    public Round latestRound() {
        if (rounds == null || rounds.isEmpty()) {
            return null;
        }
        List<Round> roundList = new ArrayList<>(rounds);
        return roundList.get(roundList.size() - 1);
    }
}
