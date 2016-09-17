package com.github.sveyrat.spaceoutbreak.domain;

import java.util.Date;

public class Player {

    private Game game;
    private String name;
    private Role role;
    private Genome genome;
    private boolean infected;

    public Player(Game game, String name) {
        this.game = game;
        this.name = name;
    }

    public boolean isInfected() {
        return infected;
    }

    public void setInfected(boolean infected) {
        this.infected = infected;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
