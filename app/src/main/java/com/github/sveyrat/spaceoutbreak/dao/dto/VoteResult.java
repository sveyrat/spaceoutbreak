package com.github.sveyrat.spaceoutbreak.dao.dto;

import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteResult {

    private Map<Player, Integer> results;

    public VoteResult() {
        this.results = new HashMap<>();
    }

    public Map<Player, Integer> getResults() {
        return results;
    }

    public void setResults(Map<Player, Integer> result) {
        this.results = result;
    }

    public boolean draw() {
        int maxNumberOfVotes = 0;
        Player mostVotedForPlayer = null;
        for (Map.Entry<Player, Integer> resultEntry : results.entrySet()) {
            Integer numberOfVotes = resultEntry.getValue();
            if (numberOfVotes > maxNumberOfVotes) {
                maxNumberOfVotes = numberOfVotes;
                mostVotedForPlayer = resultEntry.getKey();
            }
        }

        for (Map.Entry<Player, Integer> resultEntry : results.entrySet()) {
            Integer numberOfVotes = resultEntry.getValue();
            Player player = resultEntry.getKey();
            if (numberOfVotes == maxNumberOfVotes && //
                    // Player found is not the same as the already known most voted for
                    ((player == null && mostVotedForPlayer != null)
                            || (player != null && !player.equals(mostVotedForPlayer)))) {
                return true;
            }
        }
        return false;
    }

    public List<Player> getTiedPlayers() {
        List<Player> result = new ArrayList<>();
        int nbVotesMax = results.get(mostVotedFor());
        for (Map.Entry<Player, Integer> resultEntry : results.entrySet()) {
            if (resultEntry.getValue() == nbVotesMax) {
                result.add(resultEntry.getKey());
            }
        }
        return result;
    }

    public void addVote(Player player) {
        if (results.get(player) == null) {
            results.put(player, 1);
            return;
        }
        results.put(player, results.get(player) + 1);
    }

    public Player mostVotedFor() {
        Player mostVotedFor = null;
        int numberOfVotesForMostVoted = 0;
        for (Map.Entry<Player, Integer> resultEntry : results.entrySet()) {
            if (resultEntry.getValue() > numberOfVotesForMostVoted) {
                numberOfVotesForMostVoted = resultEntry.getValue();
                mostVotedFor = resultEntry.getKey();
            }
        }
        return mostVotedFor;
    }
}
