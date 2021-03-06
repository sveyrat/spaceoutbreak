package com.github.sveyrat.spaceoutbreak.domain;

import com.github.sveyrat.spaceoutbreak.domain.constant.Genome;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "player")
public class Player implements Serializable {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private transient Game game;

    @DatabaseField(canBeNull = false)
    private String name;

    @DatabaseField(canBeNull = false)
    private transient Role role = Role.ASTRONAUT;

    @DatabaseField(canBeNull = false)
    private transient Genome genome = Genome.NORMAL;

    @DatabaseField(canBeNull = false)
    private boolean mutant = false;

    @DatabaseField(canBeNull = false)
    private boolean alive = true;

    @DatabaseField(canBeNull = false)
    private boolean paralyzed = false;

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

    public boolean isMutant() {
        return mutant;
    }

    public void setMutant(boolean mutant) {
        this.mutant = mutant;
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

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean resistant() {
        return Genome.RESISTANT == this.genome;
    }

    public boolean host() {
        return Genome.HOST == this.genome;
    }

    public boolean isParalyzed() {
        return paralyzed;
    }

    public void setParalyzed(boolean paralyzed) {
        this.paralyzed = paralyzed;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player)) {
            return false;
        }
        Player that = (Player) obj;
        if (this.id == null || that.id == null) {
            return false;
        }
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        if (this.id == null) {
            return 0;
        }
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return "Player " + getName() + " :"//
                + " [ alive : " + isAlive() + " ," //
                + " mutant : " + isMutant() + " ," //
                + " paralyzed : " + isParalyzed() + " ," //
                + " role : " + getRole() + " ," //
                + " genome : " + getGenome() + " ]";
    }
}
