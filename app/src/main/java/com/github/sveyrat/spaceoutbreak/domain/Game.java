package com.github.sveyrat.spaceoutbreak.domain;

import java.util.Date;
import java.util.List;

public class Game {

    private Date creationDate;
    private List<Player> players;

    public Game() {
        this.creationDate = new Date();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
