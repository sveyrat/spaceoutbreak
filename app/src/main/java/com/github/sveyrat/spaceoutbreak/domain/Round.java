package com.github.sveyrat.spaceoutbreak.domain;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "round")
public class Round {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Game game;

    @DatabaseField(canBeNull = false)
    private int order;

    @ForeignCollectionField(orderColumnName = "order")
    private ForeignCollection<NightAction> nightActions;

    public Round() {
    }

    public Round(Game game) {
        this.game = game;
        this.order = game.getRounds().size() + 1;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ForeignCollection<NightAction> getNightActions() {
        return nightActions;
    }

    public void setNightActions(ForeignCollection<NightAction> nightActions) {
        this.nightActions = nightActions;
    }
}
