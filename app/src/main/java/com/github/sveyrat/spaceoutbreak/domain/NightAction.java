package com.github.sveyrat.spaceoutbreak.domain;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "night_action")
public class NightAction {

    @DatabaseField(generatedId = true)
    private Long id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private Round round;

    @DatabaseField(canBeNull = false)
    private int order;

    @DatabaseField(canBeNull = false)
    private Role actingPlayerRole;

    @DatabaseField(canBeNull = false)
    private NightActionType type;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Player targetPlayer;

    // For the hacker only
    @DatabaseField(canBeNull = true)
    private Role hackedRole;

    public NightAction() {
    }

    public NightAction(Round round, Role actingPlayerRole, NightActionType type, Player targetPlayer) {
        this.round = round;
        this.order = round.getNightActions().size() + 1;
        this.actingPlayerRole = actingPlayerRole;
        this.type = type;
        this.targetPlayer = targetPlayer;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Role getActingPlayerRole() {
        return actingPlayerRole;
    }

    public void setActingPlayerRole(Role actingPlayerRole) {
        this.actingPlayerRole = actingPlayerRole;
    }

    public NightActionType getType() {
        return type;
    }

    public void setType(NightActionType type) {
        this.type = type;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public Role getHackedRole() {
        return hackedRole;
    }

    public void setHackedRole(Role hackedRole) {
        this.hackedRole = hackedRole;
    }
}
