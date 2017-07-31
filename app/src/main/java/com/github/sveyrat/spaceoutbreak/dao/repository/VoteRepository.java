package com.github.sveyrat.spaceoutbreak.dao.repository;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.dao.DatabaseOpenHelper;
import com.github.sveyrat.spaceoutbreak.dao.dto.VoteResult;
import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Round;
import com.github.sveyrat.spaceoutbreak.domain.Vote;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VoteRepository extends AbstractRepository {

    public VoteRepository(DatabaseOpenHelper databaseOpenHelper) {
        super(databaseOpenHelper);
    }

    /**
     * Persists the player votes for the current round
     *
     * @return the vote results
     */
    public VoteResult vote(Map<Player, Player> votes) {
        try {
            Round currentRound = currentRound();
            if (currentRound.getVotes() != null && !currentRound.getVotes().isEmpty()) {
                String message = "Attempting to persist votes on a round that already has votes";
                Log.e(VoteRepository.class.getName(), message);
                throw new RuntimeException(message);
            }
            for (Map.Entry<Player, Player> voteEntry : votes.entrySet()) {
                Player voter = voteEntry.getKey();
                Player votedFor = voteEntry.getValue();
                Vote vote = new Vote(currentRound, voter, votedFor);
                voteDao().create(vote);
            }
            VoteResult voteResult = voteResult();
            if (!voteResult.draw()) {
                killMostVotedFor();
            }
            return voteResult;
        } catch (SQLException e) {
            String message = "Could not save votes";
            Log.e(VoteRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Captain vote to decide in case of a draw.
     *
     * @param player the player the captain voted to kill. If null, means that the captain opted for a blank vote.
     */
    public void captainVote(Player player) {
        try {
            Game currentGame = currentGame();
            Round currentRound = currentRound();
            Vote vote = new Vote(currentRound, currentGame.getCaptain(), player);
            voteDao().create(vote);

            killMostVotedFor();
        } catch (SQLException e) {
            String message = "Could not save captain vote";
            Log.e(VoteRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    private void killMostVotedFor() throws SQLException {
        Player mostVotedFor = voteResult().mostVotedFor();
        if (mostVotedFor == null) {
            return;
        }
        mostVotedFor.setAlive(false);
        playerDao().update(mostVotedFor);
    }

    public VoteResult voteResult() {
        VoteResult voteResult = new VoteResult();
        Round currentRound = currentRound();
        List<Vote> votes = new ArrayList<>(currentRound.getVotes());
        for (Vote vote : votes) {
            Player votedFor = vote.getVotedFor();
            if (votedFor == null) {
                voteResult.addABlankVote();
            } else {
                voteResult.addVote(votedFor);
            }
        }
        return voteResult;
    }

    public void defineCaptain(Player player) {
        Game currentGame = currentGame();
        if (currentGame.getCaptain() != null && currentGame.getCaptain().isAlive()) {
            String message = "Attempting to change captain when there is already an alive one";
            Log.e(VoteRepository.class.getName(), message);
            throw new RuntimeException(message);
        }
        currentGame.setCaptain(player);
        try {
            gameDao().update(currentGame);
        } catch (SQLException e) {
            String message = "Could not define captain";
            Log.e(VoteRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    public Player getCaptain() {
        Game currentGame = currentGame();
        return currentGame.getCaptain();
    }
}
