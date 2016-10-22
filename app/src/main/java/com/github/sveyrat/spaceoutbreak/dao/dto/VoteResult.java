package com.github.sveyrat.spaceoutbreak.dao.dto;

import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.HashMap;
import java.util.Map;

public class VoteResult {

    private int numberOfBlankVotes;
    private Map<Player, Integer> result;

    public VoteResult() {
        this.result = new HashMap<>();
    }

    public int getNumberOfBlankVotes() {
        return numberOfBlankVotes;
    }

    public void setNumberOfBlankVotes(int numberOfBlankVotes) {
        this.numberOfBlankVotes = numberOfBlankVotes;
    }

    public Map<Player, Integer> getResult() {
        return result;
    }

    public void setResult(Map<Player, Integer> result) {
        this.result = result;
    }

    public boolean draw() {
        int maxNumberOfVotes = 0;
        for (Map.Entry<Player, Integer> resultEntry : result.entrySet()) {
            Integer numberOfVotes = resultEntry.getValue();
            if (numberOfVotes > maxNumberOfVotes) {
                maxNumberOfVotes = numberOfVotes;
            }
        }
        if (numberOfBlankVotes == maxNumberOfVotes) {
            return true;
        }
        for (Map.Entry<Player, Integer> resultEntry : result.entrySet()) {
            Integer numberOfVotes = resultEntry.getValue();
            if (numberOfVotes == maxNumberOfVotes) {
                return true;
            }
        }
        return false;
    }

    public void addABlankVote() {
        numberOfBlankVotes++;
    }

    public void addVote(Player player) {
        if (result.get(player) == null) {
            result.put(player, 1);
            return;
        }
        result.put(player, result.get(player) + 1);
    }
}
