package com.github.sveyrat.spaceoutbreak.dao.repository;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.dao.DatabaseOpenHelper;
import com.github.sveyrat.spaceoutbreak.dao.dto.VoteResult;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Round;
import com.github.sveyrat.spaceoutbreak.domain.Vote;

import java.sql.SQLException;
import java.util.Map;

public class VoteRepository extends AbstractRepository {

    public VoteRepository(DatabaseOpenHelper databaseOpenHelper) {
        super(databaseOpenHelper);
    }

    /**
     * Persists the player votes
     *
     * @return the vote results
     */
    public VoteResult vote(Map<Player, Player> votes) {
        try {
            Round currentRound = currentRound();
            if (currentRound.getVotes() == null || currentRound.getVotes().isEmpty()) {
                String message = "Attempting to persist votes on a round that already has votes";
                Log.e(VoteRepository.class.getName(), message);
                throw new RuntimeException(message);
            }
            VoteResult voteResult = new VoteResult();
            for (Map.Entry<Player, Player> voteEntry : votes.entrySet()) {
                Player voter = voteEntry.getKey();
                Player votedFor = voteEntry.getValue();
                Vote vote = new Vote(currentRound, voter, votedFor);
                voteDao().create(vote);
                if (voteEntry.getValue() == null) {
                    voteResult.addABlankVote();
                } else {
                    voteResult.addVote(votedFor);
                }
            }
            return voteResult;
        } catch (SQLException e) {
            String message = "Could not save votes";
            Log.e(VoteRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }
}
