package com.github.sveyrat.spaceoutbreak.domain;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

@DatabaseTable(tableName = "player")
public class Player {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign=true, foreignAutoRefresh=true)
    private Game game;

    @DatabaseField
    private String name;

    @DatabaseField
    private Role role;

    @DatabaseField
    private Genome genome;

    @DatabaseField
    private boolean infected;

    // Required by the ORM to create instances
    public Player() {
    }

    public Player(Game game, String name) {
        this.game = game;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
